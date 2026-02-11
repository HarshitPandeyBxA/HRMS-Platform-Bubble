package com.example.EmployeeManagement.Model;


import com.example.EmployeeManagement.audit.entity.AuditableEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_hierarchy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(
        onlyExplicitlyIncluded = true,
        callSuper = false
)
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "hierarchyId"
//)

public class EmployeeHierarchy extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long hierarchyId;

    // FK → employee.employee_id (manager)
    private Long managerId;

    private String level;          // L1, L2, L3 or Reporting Level

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}

