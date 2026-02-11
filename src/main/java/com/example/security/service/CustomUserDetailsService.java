package com.example.security.service;

import com.example.EmployeeManagement.Model.Employee;
import com.example.EmployeeManagement.Repository.EmployeeRepository;
import com.example.security.model.CustomUserDetails;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) {
//
//        Employee employee = employeeRepository.findByUser_Username(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return new CustomUserDetails(
//                employee.getEmployeeId(),
//                employee.getUser().getUsername(),
//                employee.getUser().getPassword(),
//                getAuthorities(employee)
//        );
//    }

    private Collection<? extends GrantedAuthority> getAuthorities(Employee employee) {

        return employee.getUser()
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }


}
