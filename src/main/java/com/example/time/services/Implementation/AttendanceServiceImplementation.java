package com.example.time.services.Implementation;

import com.example.EmployeeManagement.Model.Employee;
import com.example.EmployeeManagement.Repository.EmployeeRepository;
import com.example.time.entity.Attendance;
import com.example.time.repository.AttendanceRepository;
import com.example.time.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceServiceImplementation implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Attendance checkIn(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalTime.now());
        attendance.setStatus("PRESENT");

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Check-in not found"));

        attendance.setCheckOut(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getEmployeeAttendance(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
        return attendanceRepository.findByEmployee(employee);
    }
}
