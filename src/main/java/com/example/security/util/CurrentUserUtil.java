//package com.example.security.util;
//
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class CurrentUserUtil {
//
//    private CurrentUserUtil() {}
//
//    public static Long getEmployeeId() {
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new IllegalStateException("No authenticated user found");
//        }
//
//        // CASE 1: principal is employeeId (common simple setup)
//        if (authentication.getPrincipal() instanceof Long employeeId) {
//            return employeeId;
//        }
//
//        // CASE 2: principal is String (JWT subject)
//        if (authentication.getPrincipal() instanceof String principal) {
//            return Long.parseLong(principal);
//        }
//
//        throw new IllegalStateException("Unable to extract employeeId from security context");
//    }
//}
