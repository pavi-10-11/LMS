package com.example.LMS.dto;

public class LeaveTypeDto {

    private Long id;
    private String name;
    private String description;
    private int totalDaysPerYear;
    private int maxConsecutiveDays;
    private int carryForwardLimit;



    public LeaveTypeDto() {
    }


    public LeaveTypeDto(Long id, String name, String description,
                        int totalDaysPerYear, int maxConsecutiveDays, int carryForwardLimit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalDaysPerYear = totalDaysPerYear;
        this.maxConsecutiveDays = maxConsecutiveDays;
        this.carryForwardLimit = carryForwardLimit;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalDaysPerYear() {
        return totalDaysPerYear;
    }

    public void setTotalDaysPerYear(int totalDaysPerYear) {
        this.totalDaysPerYear = totalDaysPerYear;
    }

    public int getMaxConsecutiveDays() {
        return maxConsecutiveDays;
    }

    public void setMaxConsecutiveDays(int maxConsecutiveDays) {
        this.maxConsecutiveDays = maxConsecutiveDays;
    }

    public int getCarryForwardLimit() {
        return carryForwardLimit;
    }

    public void setCarryForwardLimit(int carryForwardLimit) {
        this.carryForwardLimit = carryForwardLimit;
    }
}
