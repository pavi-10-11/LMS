package com.example.LMS.dto;

import java.time.LocalDate;

public class EmployeeLeaveResponseDto {
    private Long leaveId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private int totalDays;

    public EmployeeLeaveResponseDto() {}


    public EmployeeLeaveResponseDto(Long leaveId, String employeeId, String firstName, String lastName,
                                    LocalDate startDate, LocalDate endDate,
                                    String leaveType, int totalDays) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
    }


    public EmployeeLeaveResponseDto(Long leaveId, LocalDate startDate, LocalDate endDate, String reason, String leaveType) {
        this.leaveId = leaveId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
    }

    public Long getLeaveId() {
        return leaveId;
    }
    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getLeaveType() {
        return leaveType;
    }
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public int getTotalDays() {
        return totalDays;
    }
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
}
