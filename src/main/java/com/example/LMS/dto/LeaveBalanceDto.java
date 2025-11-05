package com.example.LMS.dto;

public class LeaveBalanceDto {
    private String employeeId;
    private String employeeName;
    private int totalLeaves;
    private int usedLeavesYear;
    private int remainingLeavesYear;
    private int usedLeavesMonth;
    private int remainingLeavesMonth;

    public LeaveBalanceDto() {}

    public LeaveBalanceDto(String employeeId, String employeeName,
                           int totalLeaves,
                           int usedLeavesYear, int remainingLeavesYear,
                           int usedLeavesMonth, int remainingLeavesMonth) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalLeaves = totalLeaves;
        this.usedLeavesYear = usedLeavesYear;
        this.remainingLeavesYear = remainingLeavesYear;
        this.usedLeavesMonth = usedLeavesMonth;
        this.remainingLeavesMonth = remainingLeavesMonth;
    }


    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public int getTotalLeaves() {
        return totalLeaves;
    }
    public void setTotalLeaves(int totalLeaves) {
        this.totalLeaves = totalLeaves;
    }
    public int getUsedLeavesYear() {
        return usedLeavesYear;
    }
    public void setUsedLeavesYear(int usedLeavesYear) {
        this.usedLeavesYear = usedLeavesYear;
    }
    public int getRemainingLeavesYear() {
        return remainingLeavesYear;
    }
    public void setRemainingLeavesYear(int remainingLeavesYear) {
        this.remainingLeavesYear = remainingLeavesYear;
    }
    public int getUsedLeavesMonth() {
        return usedLeavesMonth;
    }
    public void setUsedLeavesMonth(int usedLeavesMonth) {
        this.usedLeavesMonth = usedLeavesMonth;
    }
    public int getRemainingLeavesMonth() {
        return remainingLeavesMonth;
    }
    public void setRemainingLeavesMonth(int remainingLeavesMonth) {
        this.remainingLeavesMonth = remainingLeavesMonth;
    }
}
