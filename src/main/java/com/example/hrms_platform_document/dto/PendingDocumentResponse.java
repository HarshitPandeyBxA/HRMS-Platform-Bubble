package com.example.hrms_platform_document.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PendingDocumentResponse {

    // Document info
    private Long documentId;
    private String documentName;
    private String documentType;
    private Integer currentVersion;
    private LocalDateTime uploadedAt;

    // Employee info
    private Long employeeId;
    private String employeeName;
    private String companyEmail;
}

