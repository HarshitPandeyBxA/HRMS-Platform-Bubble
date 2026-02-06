package com.example.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateHrRequestDTO {

    @NotBlank
    private String role;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String department;
    private String designation;
    private String personalEmail;
}
