package com.example.EmployeeManagement.audit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "record_id")
    private Long recordId;



    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "action_type", nullable = false)
    private String actionType; // INSERT, UPDATE, DELETE

    @Column(name = "changed_by")
    private String changedBy; // employee_id

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "approval_required")
    private Boolean approvalRequired;

    @Column(name = "approved_by")
    private Long approvedBy; // employee_id

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

