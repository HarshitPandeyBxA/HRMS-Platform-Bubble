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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentVerificationService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final DocumentAuditService auditService;

    public DocumentVerificationService(
            DocumentRepository documentRepository,
            StorageService storageService,
            DocumentAuditService auditService
    ) {
        this.documentRepository = documentRepository;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    /**
     * APPROVE document
     */
    @Transactional
    public void verifyDocument(Long documentId, Employee verifier) {

        System.out.println("**************VERIFY START******************");

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        System.out.println("DOCUMENT FOUND: " + document.getDocumentId());

        if (document.getStatus() != DocumentStatus.PENDING_VERIFICATION) {
            throw new InvalidDocumentStateException(
                    "Only PENDING documents can be verified"
            );
        }


        DocumentVersion version = document.getCurrentVersion();
        System.out.println("VERSION OBJECT: " + version);

        // 1️⃣ Build verified S3 key
        String verifiedKey = buildVerifiedKey(
                document.getEmployee().getEmployeeId(),
                document.getDocumentId(),
                version.getVersionNumber()
        );

        System.out.println("TARGET KEY: " + verifiedKey);

        // 2️⃣ Move file in S3 (staging → verified)
        storageService.moveToVerified(version.getS3Key(), verifiedKey);

        // 3️⃣ Update version with verified key
        version.setS3Key(verifiedKey);

        // 4️⃣ Update document status
        document.setStatus(DocumentStatus.VERIFIED);
        document.setApprovedBy(verifier);

        documentRepository.save(document);

        // 5️⃣ Audit
        auditService.log(
                document,
                version,
                DocumentAuditAction.VERIFY,
                verifier,
                "Document verified successfully"
        );

        System.out.println("*******************VERIFY END************************");
    }

    /**
     * REJECT document
     */
    @Transactional
    public void rejectDocument(
            Long documentId,
            Employee verifier,
            String reason
    ) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (document.getStatus() != DocumentStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException(
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
