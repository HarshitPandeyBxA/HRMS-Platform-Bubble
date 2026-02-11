package com.example.EmployeeManagement.audit.listener;

import com.example.EmployeeManagement.Repository.AuditLogRepository;
import com.example.EmployeeManagement.audit.entity.AuditLog;
import com.example.security.util.SecurityUtil;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AuditEntityListener {

    private static AuditLogRepository auditLogRepository;

    // Called by Spring injector
    public static void setAuditLogRepository(AuditLogRepository repository) {
        auditLogRepository = repository;
    }

    @PrePersist
    public void onInsert(Object entity) {
        log(entity, "INSERT");
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        log(entity, "UPDATE");
    }

    @PreRemove
    public void onDelete(Object entity) {
        log(entity, "DELETE");
    }

    private void log(Object entity, String action) {

        AuditLog audit = AuditLog.builder()
                .tableName(entity.getClass().getSimpleName())
                .recordId(extractId(entity))
                .actionType(action)
                .changedBy(SecurityUtil.getCurrentUsername())
                .changedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .oldValue(action.equals("UPDATE") ? "PREVIOUS_STATE" : null)
                .newValue(toJson(entity))
                .approvalRequired(false)
                .build();
        auditLogRepository.save(audit);
    }


    private Long extractId(Object entity) {
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    return (Long) field.get(entity);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String toJson(Object entity) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(entity);
        } catch (Exception e) {
            return null;
        }
    }

}
