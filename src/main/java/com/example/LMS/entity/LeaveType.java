package com.example.LMS.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private int maxDaysPerYear;
    private int maxDaysPerMonth;
    private int totalDaysPerYear;

    public LeaveType(int totalDaysPerYear) {
        this.totalDaysPerYear = totalDaysPerYear;
    }

    public LeaveType(String name, String description, int maxDaysPerYear, int maxDaysPerMonth, int totalDaysPerYear) {
        this.name = name;
        this.description = description;
        this.maxDaysPerYear = maxDaysPerYear;
        this.maxDaysPerMonth = maxDaysPerMonth;
        this.totalDaysPerYear = totalDaysPerYear;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxDaysPerYear() { return maxDaysPerYear; }
    public void setMaxDaysPerYear(int maxDaysPerYear) { this.maxDaysPerYear = maxDaysPerYear; }

    public int getMaxDaysPerMonth() { return maxDaysPerMonth; }
    public void setMaxDaysPerMonth(int maxDaysPerMonth) { this.maxDaysPerMonth = maxDaysPerMonth; }

    public int getTotalDaysPerYear() {
        return totalDaysPerYear;
    }

    public void setTotalDaysPerYear(int totalDaysPerYear) {
        this.totalDaysPerYear = totalDaysPerYear;
    }
}
