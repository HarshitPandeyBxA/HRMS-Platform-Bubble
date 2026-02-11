package com.example.security.util;

import com.example.EmployeeManagement.Model.Employee;
import com.example.EmployeeManagement.Repository.EmployeeRepository;
import com.example.security.model.CustomUserDetails;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import com.example.security.util.ApplicationContextProvider.ApplicationContextProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("securityUtil")
@RequiredArgsConstructor
public class SecurityUtil {

    private final EmployeeRepository employeeRepository;

    public boolean isSelf(Long employeeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // email / username from JWT

        return employeeRepository.findById(employeeId)
                .map(emp -> emp.getCompanyEmail().equals(username))
                .orElse(false);
    }

    public Employee getLoggedInEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return employeeRepository.findByCompanyEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in employee not found"));
    }

    public boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }


    public Employee getCurrentEmployee() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName(); // email

        return employeeRepository.findByUser_Username(username)
                .orElseThrow(() ->
                        new RuntimeException("Employee not linked to logged-in user"));
    }



    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return "SYSTEM";
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {
            return cud.getUsername();
        }

        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return user.getUsername();
        }

        return "SYSTEM";
    }


    public static Long getCurrentEmployeeId() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getEmployeeId();
    }




}

