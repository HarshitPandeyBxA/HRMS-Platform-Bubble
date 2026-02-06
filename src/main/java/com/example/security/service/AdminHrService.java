package com.example.security.service;

import com.example.EmployeeManagement.DTO.EmployeeCreateRequestDTO;
import com.example.EmployeeManagement.DTO.EmployeeCreateResponse;
import com.example.EmployeeManagement.DTO.EmployeeDTO;
import com.example.security.constants.RoleConstants;
import com.example.security.dto.CreateHrRequest;
import com.example.security.dto.CreateHrRequestDTO;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminHrService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OnboardingService onboardingService;


    @Transactional
    public EmployeeCreateResponse createHr(CreateHrRequestDTO request) {

        // 1️⃣ Build Employee DTO
        EmployeeCreateRequestDTO dto = new EmployeeCreateRequestDTO();
        dto.setFirstName(request.getFirstName());
        dto.setLastName(request.getLastName());
        dto.setDesignation(request.getDesignation());
        dto.setPersonalEmail(request.getPersonalEmail());
        dto.setDepartment(request.getDepartment());
        dto.setEmployeeType("FULL_TIME");


        User systemAdmin = userRepository.findByUsername("admin@bounteous.com")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        EmployeeCreateResponse response =
                onboardingService.createEmployee(dto, systemAdmin);

        // 3️⃣ Assign HR ROLE
        User hrUser = userRepository.findByEmployeeId(response.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("HR user not found"));

//        Role hrRole = roleRepository.findByName(RoleConstants.ROLE_HR_OPERATIONS)
//                .orElseThrow(() -> new RuntimeException("HR role not found"));

        Role hrRole = roleRepository
                .findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("HR role not found"));

        hrUser.getRoles().add(hrRole);
        userRepository.save(hrUser);

        return response;
    }
}

