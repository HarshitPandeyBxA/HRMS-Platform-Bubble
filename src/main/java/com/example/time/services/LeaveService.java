package com.example.time.services;

import com.example.time.entity.LeaveRequest;

public interface LeaveService {
    LeaveRequest applyLeave(LeaveRequest request);
    LeaveRequest approveLeave(Long leaveRequestId, Long approverId);
    LeaveRequest rejectLeave(Long leaveRequestId, Long approverId);
}
