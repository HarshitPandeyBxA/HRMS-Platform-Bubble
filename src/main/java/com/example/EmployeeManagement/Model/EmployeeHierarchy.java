package com.example.EmployeeManagement.Model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_hierarchy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "hierarchyId"
//)

public class EmployeeHierarchy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hierarchyId;

    // FK → employee.employee_id (manager)
    private Long managerId;

    private String level;          // L1, L2, L3 or Reporting Level

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}

