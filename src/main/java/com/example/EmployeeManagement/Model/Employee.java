
package com.example.EmployeeManagement.Model;

import com.example.EmployeeManagement.audit.entity.AuditableEntity;
import com.example.security.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "employee")
@Getter
@Setter
@EqualsAndHashCode(
        onlyExplicitlyIncluded = true,
        callSuper = false
)
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends AuditableEntity {

//    private Long dummy_id;
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_id_generator"
    )
    @SequenceGenerator(
            name = "employee_id_generator",
            sequenceName = "employee_id_seq",
            allocationSize = 1
    )
    @Column(name = "employee_id")
    private Long employeeId;

    private String firstName;
    private String lastName;
    private String companyEmail;
    private LocalDate dateOfJoining;
    private String status;
    private String employeeType;
    private Long phoneNumber;
    private String currentBand;
    private double currentExperience;
    private String designation;
    private int ctc;
    private String department;

    @Column(name = "created_by_hr_user_id", nullable = false)
    private Long createdByHrUserId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private EmployeePersonal employeePersonal;

    @JsonIgnore
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private Account account;

    @JsonIgnore
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private JobDetails jobDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeAddress> employeeAddress;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeEducation> employeeEducations;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeSkill> employeeSkills;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeBand> employeeBands;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeManagerHistory> employeeManagerHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmploymentContract> employmentContracts;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeEmergency> employeeEmergencies;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> employeeExperiences;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @JsonIgnore
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private Set<Employee> subordinates;
}
