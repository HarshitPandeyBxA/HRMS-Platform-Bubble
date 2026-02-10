//package com.example.hrms_platform_document.mapper;
//
//import com.example.EmployeeManagement.Model.Employee;
//import com.example.hrms_platform_document.dto.PendingDocumentResponse;
//import com.example.hrms_platform_document.entity.Document;
//
//public class DocumentMapper {
//
//
//    public static PendingDocumentResponse toPendingResponse(Document document) {
//
//        Employee emp = document.getEmployee();
//
//        PendingDocumentResponse dto = new PendingDocumentResponse();
//        dto.setDocumentId(document.getDocumentId());
//        dto.setDocumentName(document.getDocumentName());
//        dto.setDocumentType(document.getDocumentType());
//        dto.setCurrentVersion(
//                document.getCurrentVersion().getVersionNumber()
//        );
//        dto.setUploadedAt(document.getCreatedAt());
//
//        dto.setEmployeeId(emp.getEmployeeId());
//        dto.setEmployeeName(
//                emp.getFirstName() + " " + emp.getLastName()
//        );
//        dto.setCompanyEmail(emp.getCompanyEmail());
//
//        return dto;
//    }
//}
