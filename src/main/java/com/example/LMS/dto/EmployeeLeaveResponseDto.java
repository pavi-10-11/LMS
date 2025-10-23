package com.example.LMS.dto;

import java.time.LocalDate;

public class EmployeeLeaveResponseDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String leaveTypeName;

    public EmployeeLeaveResponseDto() {}

    public EmployeeLeaveResponseDto(Long id, LocalDate startDate, LocalDate endDate, String reason, String leaveTypeName) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.leaveTypeName = leaveTypeName;
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

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }
}
