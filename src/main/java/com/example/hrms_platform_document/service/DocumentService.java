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
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;
@Data
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final StorageService storageService;
    private final DocumentAuditService auditService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            StorageService storageService,
            DocumentAuditService auditService
    ) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    /**
     * Upload document (initial upload)
     */
    @Transactional
    public Document uploadDocument(
            Employee owner,
            MultipartFile file,
            String documentType,
            String documentName,
            boolean isConfidential
    ) {

        System.out.println("Too Before");

        // 1️⃣ Create Document (logical)
        Document document = new Document();
        document.setEmployee(owner);
        document.setUploadedBy(owner);
        document.setDocumentType(documentType);
        document.setDocumentName(documentName);
        document.setIsConfidential(isConfidential);
        document.setStatus(DocumentStatus.PENDING_VERIFICATION);

        document = documentRepository.save(document);

        // 2️⃣ Create S3 key (STAGING)
        System.out.println("Before");
        System.out.println(owner.getEmployeeId());
        String s3Key = buildStagingKey(owner.getEmployeeId(), document.getDocumentId());

        // 3️⃣ Upload to S3
        storageService.uploadToStaging(file, s3Key);

        // 4️⃣ Create Document Version
        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setUploadedBy(owner);
        version.setVersionNumber(1);
        version.setS3Key(s3Key);

        version = versionRepository.save(version);

        // 5️⃣ Update document with current version
        document.setCurrentVersion(version);
        documentRepository.save(document);

        // 6️⃣ Audit log
        auditService.log(
                document,
                version,
                DocumentAuditAction.UPLOAD,
                owner,
                "Initial document upload"
        );

        return document;
    }

    private String buildStagingKey(Long employeeId, Long documentId) {
        return "staging/employee/"
                + employeeId + "/"
                + documentId + "/"
                + UUID.randomUUID();
    }

    @Transactional
    public Document reuploadDocument(
            Long documentId,
            Employee employee,
            MultipartFile file
    ) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        if (document.getStatus() != DocumentStatus.REJECTED) {
            throw new InvalidDocumentStateException("Only REJECTED documents can be re-uploaded");

        }

        // 1️⃣ Get next version number
        int nextVersion = versionRepository
                .findTopByDocumentDocumentIdOrderByVersionNumberDesc(documentId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        // 2️⃣ Build staging key
        String s3Key = "staging/employee/"
                + document.getEmployee().getEmployeeId()
                + "/" + documentId
                + "/v" + nextVersion;

        // 3️⃣ Upload to S3
        storageService.uploadToStaging(file, s3Key);

        // 4️⃣ Create new version
        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setUploadedBy(employee);
        version.setVersionNumber(nextVersion);
        version.setS3Key(s3Key);

        versionRepository.save(version);

        // 5️⃣ Update document
        document.setCurrentVersion(version);
        document.setStatus(DocumentStatus.PENDING_VERIFICATION);
        documentRepository.save(document);

        // 6️⃣ Audit
        auditService.log(
                document,
                version,
                DocumentAuditAction.REUPLOAD,
                employee,
                "Document re-uploaded after rejection"
        );

        return document;
    }

    @Transactional(readOnly = true)
    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsForCurrentEmployee(Employee employee) {
        return documentRepository.findByEmployee(employee);
    }


}
