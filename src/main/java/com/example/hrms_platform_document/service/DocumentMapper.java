package com.example.hrms_platform_document.service;

import com.example.EmployeeManagement.Model.Employee;
import com.example.hrms_platform_document.dto.DocumentResponse;
import com.example.hrms_platform_document.dto.PendingDocumentResponse;
import com.example.hrms_platform_document.entity.Document;

public class DocumentMapper {

    public static DocumentResponse toResponse(Document document) {
        DocumentResponse dto = new DocumentResponse();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentType(document.getDocumentType());
        dto.setDocumentName(document.getDocumentName());
        dto.setStatus(document.getStatus());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());

        if (document.getCurrentVersion() != null) {
            dto.setCurrentVersion(document.getCurrentVersion().getVersionNumber());
        }

        return dto;
    }
    public static PendingDocumentResponse toPendingResponse(Document document) {

        Employee emp = document.getEmployee();

        PendingDocumentResponse dto = new PendingDocumentResponse();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentName(document.getDocumentName());
        dto.setDocumentType(document.getDocumentType());
        dto.setCurrentVersion(
                document.getCurrentVersion().getVersionNumber()
        );
        dto.setUploadedAt(document.getCreatedAt());

        dto.setEmployeeId(emp.getEmployeeId());
        dto.setEmployeeName(
                emp.getFirstName() + " " + emp.getLastName()
        );
        dto.setCompanyEmail(emp.getCompanyEmail());

        return dto;
    }
}
