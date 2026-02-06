package com.example.EmployeeManagement.Controller;

import com.example.EmployeeManagement.DTO.EmployeeCreateRequestDTO;
import com.example.EmployeeManagement.DTO.EmployeeCreateResponse;
import com.example.EmployeeManagement.DTO.EmployeeDTO;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import com.example.security.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hr/employees")
@RequiredArgsConstructor
public class HrEmployeeController {

    private final UserRepository userRepository;

    private final OnboardingService onboardingService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR_OPERATIONS')")
    public ResponseEntity<EmployeeCreateResponse> createEmployee(
            @RequestBody EmployeeCreateRequestDTO dto) {

        String hrUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User hrUser = userRepository.findByUsername(hrUsername)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        EmployeeCreateResponse response =
                onboardingService.createEmployee(dto, hrUser);

        return ResponseEntity.ok(response);
    }


}

