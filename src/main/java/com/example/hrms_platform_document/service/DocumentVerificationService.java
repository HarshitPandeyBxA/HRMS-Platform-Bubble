package com.example.hrms_platform_document.service;

import com.example.EmployeeManagement.Model.Employee;
import com.example.hrms_platform_document.entity.Document;
import com.example.hrms_platform_document.entity.DocumentVersion;
import com.example.hrms_platform_document.enums.DocumentAuditAction;
import com.example.hrms_platform_document.enums.DocumentStatus;
import com.example.hrms_platform_document.exception.DocumentNotFoundException;
import com.example.hrms_platform_document.exception.InvalidDocumentStateException;
import com.example.hrms_platform_document.repository.DocumentRepository;
import com.example.hrms_platform_document.service.storage.StorageService;
import com.example.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentVerificationService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final DocumentAuditService auditService;
    private final SecurityUtil securityUtil;

    public DocumentVerificationService(
            DocumentRepository documentRepository,
            StorageService storageService,
            DocumentAuditService auditService,
            SecurityUtil securityUtil
    ) {
        this.documentRepository = documentRepository;
        this.storageService = storageService;
        this.auditService = auditService;
        this.securityUtil = securityUtil;
    }

    /* ============================================================
       VERIFY DOCUMENT (HR / ADMIN)
       ============================================================ */

    @Transactional
    public void verifyDocument(Long documentId) {

        Employee verifier = securityUtil.getLoggedInEmployee();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        if (document.getStatus() != DocumentStatus.PENDING_VERIFICATION) {
            throw new InvalidDocumentStateException(
                    "Only PENDING documents can be verified"
            );
        }

        DocumentVersion version = document.getCurrentVersion();

        String verifiedKey = buildVerifiedKey(
                document.getEmployee().getEmployeeId(), // ✅ business employeeId
                document.getDocumentId(),
                version.getVersionNumber()
        );

        storageService.moveToVerified(version.getS3Key(), verifiedKey);

        version.setS3Key(verifiedKey);

        document.setStatus(DocumentStatus.VERIFIED);
        document.setApprovedBy(verifier);

        documentRepository.save(document);

        auditService.log(
                document,
                version,
                DocumentAuditAction.VERIFY,
                verifier,
                "Document verified successfully"
        );
    }

    /* ============================================================
       REJECT DOCUMENT (HR / ADMIN)
       ============================================================ */

    @Transactional
    public void rejectDocument(Long documentId, String reason) {

        Employee verifier = securityUtil.getLoggedInEmployee();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        if (document.getStatus() != DocumentStatus.PENDING_VERIFICATION) {
            throw new InvalidDocumentStateException(
                    "Only PENDING documents can be rejected"
            );
        }

        document.setStatus(DocumentStatus.REJECTED);
        document.setApprovedBy(verifier);

        documentRepository.save(document);

        auditService.log(
                document,
                document.getCurrentVersion(),
                DocumentAuditAction.REJECT,
                verifier,
                reason
        );
    }

    private String buildVerifiedKey(
            Long employeeId,
            Long documentId,
            Integer version
    ) {
        return "verified/employee/"
                + employeeId + "/"
                + documentId + "/v" + version;
    }
}
