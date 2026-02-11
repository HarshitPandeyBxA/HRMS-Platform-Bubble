package com.example.EmployeeManagement.Model;

import com.example.EmployeeManagement.audit.entity.AuditableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "employee_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        onlyExplicitlyIncluded = true,
        callSuper = false
)
public class EmployeeAddress extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long addressId;

    private String houseNumber;
    private String country;
    private String state;
    private String city;
    private String street;
    private String pincode;
    private String landmark;

    private Boolean isPermanent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
