package com.example.EmployeeManagement.Service;

import com.example.EmployeeManagement.DTO.EmployeeCreateRequestDTO;
import com.example.EmployeeManagement.DTO.EmployeeDTO;
import com.example.EmployeeManagement.Exception.EmployeeNotFoundException;
import com.example.EmployeeManagement.Model.Employee;
import com.example.EmployeeManagement.Repository.EmployeeRepository;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import com.example.security.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private EmployeeAccessService employeeAccessService;
    private UserRepository userRepository;
    private SecurityUtil securityUtil;

    public List<EmployeeDTO> getAllEmployee(){
        employeeAccessService.checkHrOrAdmin();
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public EmployeeDTO getEmployeeById(Long employeeId) {

        employeeAccessService.checkOwnerOrHr(employeeId);

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return mapToDto(employee);
    }


    public List<EmployeeDTO> getEmployeeByName(String name){
        return employeeRepository.searchByFullName(name)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public EmployeeDTO addEmployee(EmployeeCreateRequestDTO request) {

        // 🔐 get logged-in HR/Admin
        String username = securityUtil.getCurrentUsername();

        User hrUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        Employee employee = new Employee();

        // Identity
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());

        // Organization
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setEmployeeType(request.getEmployeeType());

        // HR details
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setCurrentBand(request.getCurrentBand());
        employee.setCurrentExperience(
                request.getCurrentExperience() != null ? request.getCurrentExperience() : 0.0
        );
        employee.setCtc(
                request.getCtc() != null ? request.getCtc() : 0
        );

        // Contact
        employee.setPhoneNumber(request.getPhoneNumber());

        // CREATED BY HR
        employee.setCreatedByHrUserId(hrUser.getEmployeeId());

        // System fields
        employee.setStatus("ACTIVE");
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());

        Employee saved = employeeRepository.save(employee);

        return mapToDto(saved);
    }


    public EmployeeDTO updateEmployee(Long id, EmployeeCreateRequestDTO request) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Update only allowed fields
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDepartment(request.getDepartment());
        existing.setDesignation(request.getDesignation());
        existing.setEmployeeType(request.getEmployeeType());
        existing.setDateOfJoining(request.getDateOfJoining());
        existing.setCurrentBand(request.getCurrentBand());
        existing.setCurrentExperience(
                request.getCurrentExperience() != null ? request.getCurrentExperience() : existing.getCurrentExperience()
        );
        existing.setCtc(
                request.getCtc() != null ? request.getCtc() : existing.getCtc()
        );
        existing.setPhoneNumber(request.getPhoneNumber());



        // System-managed field
        existing.setUpdatedAt(LocalDateTime.now());

        Employee saved = employeeRepository.save(existing);
        return mapToDto(saved);
    }


    public void deleteEmployeeById(Long employeeId){
        employeeRepository.deleteById(employeeId);
    }


    public EmployeeDTO mapToDto(Employee employee){
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setCompanyEmail(employee.getCompanyEmail());
        dto.setDesignation(employee.getDesignation());
        dto.setStatus(employee.getStatus());
        dto.setCurrentBand(employee.getCurrentBand());

        if(employee.getJobDetails() != null)
            dto.setDepartment(employee.getJobDetails().getDepartmentName());
        if(employee.getManager()!=null)
            dto.setManagerName(employee.getManager().getFirstName()+" "+employee.getManager().getLastName());

        return dto;
    }

    public List<EmployeeDTO> getEmployeesUnderManager(Long managerId) {

        // HR can view anyone, EMPLOYEE only their own subordinates
        employeeAccessService.checkManagerAccess(managerId);

        return employeeRepository.findByManager_EmployeeId(managerId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    public Employee toEntity(EmployeeCreateRequestDTO dto, Long hrUserId) {

        Employee e = new Employee();

        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setDepartment(dto.getDepartment());
        e.setDesignation(dto.getDesignation());
        e.setEmployeeType(dto.getEmployeeType());
        e.setDateOfJoining(dto.getDateOfJoining());
        e.setPhoneNumber(dto.getPhoneNumber());

        // NULL-SAFE DEFAULTS
        e.setCurrentExperience(
                dto.getCurrentExperience() != null ? dto.getCurrentExperience() : 0.0
        );

        e.setCtc(
                dto.getCtc() != null ? dto.getCtc() : 0
        );

        e.setCurrentBand(dto.getCurrentBand());
        e.setStatus("ACTIVE");

        // Audit
        e.setCreatedByHrUserId(hrUserId);

        return e;
    }



    public Employee addEmployeeInternal(Employee employee) {
        return employeeRepository.save(employee);
    }


}
