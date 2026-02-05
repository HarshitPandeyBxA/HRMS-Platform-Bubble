package com.example.time.services;


import java.util.List;

public interface AttendanceService {
    com.example.time.entity.Attendance checkIn(long employeeId);
    com.example.time.entity.Attendance checkOut(long employeeId);

    List<com.example.time.entity.Attendance> getEmployeeAttendance(long employeeId);

}
