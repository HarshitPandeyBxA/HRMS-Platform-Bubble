package com.example.hrms_platform_document.repository;


import com.example.hrms_platform_document.entity.Document;
import com.example.hrms_platform_document.enums.DocumentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByEmployeeEmployeeId(Long employeeId);

    List<Document> findByStatus(DocumentStatus status, Pageable pageable);
}

