package com.example.LMS.service;

import com.example.LMS.dto.EmployeeLeaveResponseDto;
import com.example.LMS.dto.LeaveBalanceDto;
import com.example.LMS.dto.LeaveSummaryDto;
import com.example.LMS.entity.Employee;
import com.example.LMS.entity.Leave;
import com.example.LMS.entity.LeaveType;
import com.example.LMS.repository.EmployeeRepository;
import com.example.LMS.repository.LeaveRepository;
import com.example.LMS.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public List<Leave> findAll() {
        return leaveRepository.findAll();
    }

    public Optional<Leave> findById(Long id) {
        return leaveRepository.findById(id);
    }

    public Leave save(Leave leave, String employeeId, Long leaveTypeId) {
        // Validate dates
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found with ID: " + leaveTypeId));

        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);

        long daysRequested = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;

        
        int usedYear = getUsedDaysInYear(employee, leaveType, leave.getStartDate().getYear());
        if (usedYear + daysRequested > leaveType.getTotalDaysPerYear()) {
            throw new IllegalArgumentException("Exceeded annual limit (" + leaveType.getTotalDaysPerYear() + " days) for " + leaveType.getName());
        }

       
        int usedMonth = getUsedDaysInMonth(employee, leaveType, leave.getStartDate());
        if (usedMonth + daysRequested > leaveType.getMaxDaysPerMonth()) {
            throw new IllegalArgumentException("Exceeded monthly limit (" + leaveType.getMaxDaysPerMonth() + " days) for " + leaveType.getName());
        }

        return leaveRepository.save(leave);
    }

    private int getUsedDaysInMonth(Employee employee, LeaveType type, LocalDate date) {
        List<Leave> monthlyLeaves = leaveRepository.findByEmployee_EmployeeId(employee.getEmployeeId());
        return monthlyLeaves.stream()
                .filter(l -> l.getLeaveType().getId().equals(type.getId()))
                .filter(l -> l.getStartDate().getMonthValue() == date.getMonthValue()
                        && l.getStartDate().getYear() == date.getYear())
                .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();
    }

    private int getUsedDaysInYear(Employee employee, LeaveType type, int year) {
        List<Leave> yearlyLeaves = leaveRepository.findByEmployee_EmployeeId(employee.getEmployeeId());
        return yearlyLeaves.stream()
                .filter(l -> l.getLeaveType().getId().equals(type.getId()))
                .filter(l -> l.getStartDate().getYear() == year)
                .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();
    }

    public void deleteById(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new IllegalArgumentException("Leave not found with ID: " + id);
        }
        leaveRepository.deleteById(id);
    }

    public List<EmployeeLeaveResponseDto> findByEmployeeId(String employeeId) {
        List<Leave> leaves = leaveRepository.findByEmployee_EmployeeId(employeeId);
        return leaves.stream().map(l -> new EmployeeLeaveResponseDto(
                l.getId(),
                l.getEmployee().getEmployeeId(),
                l.getEmployee().getFirstName(),
                l.getEmployee().getLastName(),
                l.getStartDate(),
                l.getEndDate(),
                l.getLeaveType().getName(),
                (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1
        )).collect(Collectors.toList());
    }

    public List<LeaveSummaryDto> getMonthlyLeaveSummary() {
        List<Leave> leaves = leaveRepository.findAll();
        Map<String, Long> count = leaves.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getEmployee().getEmployeeId(),
                        Collectors.counting()
                ));
        return count.entrySet().stream()
                .map(e -> new LeaveSummaryDto(e.getKey(), e.getValue().intValue()))
                .collect(Collectors.toList());
    }

    public LeaveBalanceDto getLeaveBalanceSummary(Employee employee, LocalDate ref) {
        List<Leave> leaves = leaveRepository.findByEmployee_EmployeeId(employee.getEmployeeId());
        
       
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        int totalPerYear = leaveTypes.stream()
                .mapToInt(LeaveType::getTotalDaysPerYear)
                .sum();
        
        int usedYear = leaves.stream()
                .filter(l -> l.getStartDate().getYear() == ref.getYear())
                .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();
        
        int remainingYear = Math.max(0, totalPerYear - usedYear);
        
        int maxDaysPerMonth = leaveTypes.stream()
                .mapToInt(LeaveType::getMaxDaysPerMonth)
                .sum();
        
        int usedMonth = leaves.stream()
                .filter(l -> l.getStartDate().getMonthValue() == ref.getMonthValue()
                        && l.getStartDate().getYear() == ref.getYear())
                .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();
        
        int remainingMonth = Math.max(0, maxDaysPerMonth - usedMonth);
        
        return new LeaveBalanceDto(
                employee.getEmployeeId(),
                employee.getFirstName() + " " + employee.getLastName(),
                totalPerYear,
                usedYear,
                remainingYear,
                usedMonth,
                remainingMonth
        );
    }

    public int getRemainingMonthlyBalance(Employee employee, LocalDate ref) {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        int maxMonthlyTotal = leaveTypes.stream()
                .mapToInt(LeaveType::getMaxDaysPerMonth)
                .sum();
        int used = 0;
        for (LeaveType type : leaveTypes) {
            used += getUsedDaysInMonth(employee, type, ref);
        }
        return Math.max(0, maxMonthlyTotal - used);
    }

    public int getRemainingAnnualBalance(Employee employee, LocalDate ref) {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        int maxAnnualTotal = leaveTypes.stream()
                .mapToInt(LeaveType::getTotalDaysPerYear)
                .sum();
        int used = 0;
        for (LeaveType type : leaveTypes) {
            used += getUsedDaysInYear(employee, type, ref.getYear());
        }
        return Math.max(0, maxAnnualTotal - used);
    }
}