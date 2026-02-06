package com.example.security.controller;

import com.example.EmployeeManagement.DTO.EmployeeCreateResponse;
import com.example.security.dto.CreateHrRequest;
import com.example.security.dto.CreateHrRequestDTO;
import com.example.security.service.AdminHrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/hr")
@RequiredArgsConstructor
public class AdminHrController {

    private final AdminHrService adminHrService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeCreateResponse> createHr(@Valid @RequestBody CreateHrRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("**************************************************************");
        System.out.println("AUTH NAME = " + auth.getName());
        System.out.println("AUTHORITIES = " + auth.getAuthorities());
        EmployeeCreateResponse response = adminHrService.createHr(request);
        return ResponseEntity.ok(response);
    }
}

