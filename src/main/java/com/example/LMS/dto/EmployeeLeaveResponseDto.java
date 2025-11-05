package com.example.LMS.dto;

import java.time.LocalDate;

public class EmployeeLeaveResponseDto {
    private Long leaveId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String leaveType;

    public EmployeeLeaveResponseDto() {}

    public EmployeeLeaveResponseDto(Long leaveId, LocalDate startDate, LocalDate endDate, String reason, String leaveType) {
        this.leaveId = leaveId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.leaveType = leaveType;
    }

    public Long getLeaveId() {
        return leaveId;
    }
    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeaveType() {
        return leaveType;
    }
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }
}
