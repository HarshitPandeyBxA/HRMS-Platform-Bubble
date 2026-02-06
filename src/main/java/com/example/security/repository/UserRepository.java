package com.example.security.repository;

import com.example.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByResetToken(String resetToken);


    @Query(value = "SELECT nextval('employee_id_seq')", nativeQuery = true)
    Long getNextEmployeeId();

    Optional<User> findByEmployeeId(Long employeeId);
}


