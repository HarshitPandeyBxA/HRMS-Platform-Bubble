package com.example.hrms_platform_document.controller;

import com.example.hrms_platform_document.dto.DocumentResponse;
import com.example.hrms_platform_document.dto.PendingDocumentResponse;
import com.example.hrms_platform_document.entity.Document;
import com.example.hrms_platform_document.enums.DocumentAccessAction;
import com.example.hrms_platform_document.service.DocumentAccessLogService;
import com.example.hrms_platform_document.service.DocumentMapper;
import com.example.hrms_platform_document.service.DocumentService;
import com.example.hrms_platform_document.service.DocumentVerificationService;
import com.example.hrms_platform_document.service.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentVerificationService verificationService;
    private final DocumentAccessLogService accessLogService;
    private final StorageService storageService;

    public DocumentController(
            DocumentService documentService,
            DocumentVerificationService verificationService,
            DocumentAccessLogService accessLogService,
            StorageService storageService
    ) {
        this.documentService = documentService;
        this.verificationService = verificationService;
        this.accessLogService = accessLogService;
        this.storageService = storageService;
    }

    /* ============================================================
       1️⃣ Upload document (logged-in employee)
       ============================================================ */

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/upload")
    public DocumentResponse upload(
            @RequestParam MultipartFile file,
            @RequestParam String documentType,
            @RequestParam String documentName
    ) {
        Document doc = documentService.uploadDocument(
                file,
                documentType,
                documentName,
                false
        );
        return DocumentMapper.toResponse(doc);
    }

    /* ============================================================
       2️⃣ Re-upload document (only owner)
       ============================================================ */

    @PostMapping("/{id}/reupload")
    public DocumentResponse reupload(
            @PathVariable Long id,
            @RequestParam MultipartFile file
    ) {
        Document doc = documentService.reuploadDocument(id, file);
        return DocumentMapper.toResponse(doc);
    }

    /* ============================================================
       3️⃣ Verify document (HR / ADMIN)
       ============================================================ */

    @PreAuthorize("hasAnyRole('HR_OPERATIONS','ADMIN')")
    @PostMapping("/{id}/verify")
    public void verify(@PathVariable Long id) {
        verificationService.verifyDocument(id);
    }

    /* ============================================================
       4️⃣ Reject document (HR / ADMIN)
       ============================================================ */

    @PreAuthorize("hasAnyRole('HR_OPERATIONS','ADMIN')")
    @PostMapping("/{id}/reject")
    public void reject(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        verificationService.rejectDocument(id, reason);
    }

    /* ============================================================
       5️⃣ Download document (only owner, only VERIFIED)
       ============================================================ */

    @GetMapping("/{id}/download")
    public String download(
            @PathVariable Long id,
            @RequestHeader(value = "X-IP", required = false) String ip
    ) {
        Document doc = documentService.getDocumentForDownload(id);

        accessLogService.logAccess(
                doc,
                DocumentAccessAction.DOWNLOAD,
                ip != null ? ip : "UNKNOWN"
        );

        return storageService.generatePresignedUrl(
                doc.getCurrentVersion().getS3Key()
        );
    }

    @PreAuthorize("hasAnyRole('HR_OPERATIONS','ADMIN')")
    @GetMapping("/pending")
    public Page<DocumentResponse> listPendingVerifications(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return documentService
                .listPendingVerifications(pageable)
                .map(DocumentMapper::toResponse);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/pending")
    public Page<PendingDocumentResponse> listPendingDocuments(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return documentService
                .listPendingDocuments(pageable)
                .map(DocumentMapper::toPendingResponse);
    }


}
