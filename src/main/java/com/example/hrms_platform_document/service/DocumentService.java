package com.example.hrms_platform_document.service;

import com.example.EmployeeManagement.Model.Employee;
import com.example.hrms_platform_document.entity.Document;
import com.example.hrms_platform_document.entity.DocumentVersion;
import com.example.hrms_platform_document.enums.DocumentAuditAction;
import com.example.hrms_platform_document.enums.DocumentStatus;
import com.example.hrms_platform_document.exception.DocumentNotFoundException;
import com.example.hrms_platform_document.exception.InvalidDocumentStateException;
import com.example.hrms_platform_document.repository.DocumentRepository;
import com.example.hrms_platform_document.repository.DocumentVersionRepository;
import com.example.hrms_platform_document.service.storage.StorageService;
import com.example.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final StorageService storageService;
    private final DocumentAuditService auditService;
    private final SecurityUtil securityUtil;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            StorageService storageService,
            DocumentAuditService auditService,
            SecurityUtil securityUtil
    ) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.storageService = storageService;
        this.auditService = auditService;
        this.securityUtil = securityUtil;
    }

    /* ============================================================
       INTERNAL HELPER
       ============================================================ */

    private Employee getCurrentEmployee() {
        return securityUtil.getLoggedInEmployee();
    }

    private String buildStagingKey(Long employeeId, Long documentId) {
        return "staging/employee/"
                + employeeId + "/"
                + documentId + "/"
                + UUID.randomUUID();
    }

    /* ============================================================
       UPLOAD DOCUMENT (LOGGED-IN EMPLOYEE)
       ============================================================ */

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    public Document uploadDocument(
            MultipartFile file,
            String documentType,
            String documentName,
            boolean isConfidential
    ) {

        Employee owner = getCurrentEmployee();

        Document document = new Document();
        document.setEmployee(owner);
        document.setUploadedBy(owner);
        document.setDocumentType(documentType);
        document.setDocumentName(documentName);
        document.setIsConfidential(isConfidential);
        document.setStatus(DocumentStatus.PENDING_VERIFICATION);

        document = documentRepository.save(document);

        String s3Key = buildStagingKey(
                owner.getEmployeeId(),   // ✅ business employeeId
                document.getDocumentId()
        );

        storageService.uploadToStaging(file, s3Key);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setUploadedBy(owner);
        version.setVersionNumber(1);
        version.setS3Key(s3Key);

        version = versionRepository.save(version);

        document.setCurrentVersion(version);
        documentRepository.save(document);

        auditService.log(
                document,
                version,
                DocumentAuditAction.UPLOAD,
                owner,
                "Initial document upload"
        );

        return document;
    }

    /* ============================================================
       RE-UPLOAD DOCUMENT (ONLY OWNER, ONLY REJECTED)
       ============================================================ */

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    public Document reuploadDocument(Long documentId, MultipartFile file) {

        Employee currentEmployee = getCurrentEmployee();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        // ownership check
        if (!document.getEmployee().getEmployeeId()
                .equals(currentEmployee.getEmployeeId())) {
            throw new RuntimeException("You are not allowed to re-upload this document");
        }

        if (document.getStatus() != DocumentStatus.REJECTED) {
            throw new InvalidDocumentStateException(
                    "Only REJECTED documents can be re-uploaded"
            );
        }

        int nextVersion = versionRepository
                .findTopByDocumentDocumentIdOrderByVersionNumberDesc(documentId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        String s3Key = "staging/employee/"
                + currentEmployee.getEmployeeId()
                + "/" + documentId
                + "/v" + nextVersion;

        storageService.uploadToStaging(file, s3Key);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setUploadedBy(currentEmployee);
        version.setVersionNumber(nextVersion);
        version.setS3Key(s3Key);

        versionRepository.save(version);

        document.setCurrentVersion(version);
        document.setStatus(DocumentStatus.PENDING_VERIFICATION);
        documentRepository.save(document);

        auditService.log(
                document,
                version,
                DocumentAuditAction.REUPLOAD,
                currentEmployee,
                "Document re-uploaded after rejection"
        );

        return document;
    }

    /* ============================================================
       DOWNLOAD (ONLY OWNER, ONLY VERIFIED)
       ============================================================ */


    @Transactional(readOnly = true)
    public Document getDocumentForDownload(Long documentId) {

        Employee currentEmployee = securityUtil.getLoggedInEmployee();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        // ADMIN override
        if (securityUtil.hasRole("ADMIN")) {
            return document;
        }

        // EMPLOYEE: only own + VERIFIED
        if (securityUtil.hasRole("EMPLOYEE")) {

            if (!document.getEmployee().getEmployeeId()
                    .equals(currentEmployee.getEmployeeId())) {
                throw new RuntimeException("You are not allowed to access this document");
            }

            if (document.getStatus() != DocumentStatus.VERIFIED) {
                throw new InvalidDocumentStateException(
                        "Only VERIFIED documents can be downloaded"
                );
            }

            return document;
        }

        throw new RuntimeException("Access denied");
    }


    /* ============================================================
       GENERIC FETCH (ADMIN / INTERNAL)
       ============================================================ */

    @Transactional(readOnly = true)
    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    @PreAuthorize("hasAnyRole('HR_OPERATIONS','ADMIN')")
    @Transactional(readOnly = true)
    public Page<Document> listPendingVerifications(Pageable pageable) {
        return (Page<Document>) documentRepository.findByStatus(
                DocumentStatus.PENDING_VERIFICATION,
                pageable
        );
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @Transactional(readOnly = true)
    public Page<Document> listPendingDocuments(Pageable pageable) {
        return (Page<Document>) documentRepository.findByStatus(
                DocumentStatus.PENDING_VERIFICATION,
                pageable
        );
    }


}
