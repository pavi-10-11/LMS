package com.example.LMS.service;

import com.example.LMS.dto.EmployeeLeaveResponseDto;
import com.example.LMS.dto.LeaveSummaryDto;
import com.example.LMS.entity.Employee;
import com.example.LMS.entity.Leave;
import com.example.LMS.entity.LeaveType;
import com.example.LMS.exception.LeavesException;
import com.example.LMS.repository.EmployeeRepository;
import com.example.LMS.repository.LeaveRepository;
import com.example.LMS.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    private static final int MAX_DAYS_PER_MONTH = 2;

    public LeaveService(LeaveRepository leaveRepository,
                        EmployeeRepository employeeRepository,
                        LeaveTypeRepository leaveTypeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Transactional
    public Leave save(Leave leave, String employeeId, Long leaveTypeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        LeaveType lt = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("LeaveType not found: " + leaveTypeId));

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        validatePerMonthLimit(emp, leave);

        leave.setEmployee(emp);
        leave.setLeaveType(lt);
        return leaveRepository.save(leave);
    }

    public List<Leave> findAll() {
        return leaveRepository.findAll();
    }

    public Optional<Leave> findById(Long id) {
        return leaveRepository.findById(id);
    }

    public void deleteById(Long id) {
        leaveRepository.deleteById(id);
    }

    public List<EmployeeLeaveResponseDto> findByEmployeeId(String employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        List<Leave> leaves = leaveRepository.findByEmployeeEmployeeIdOrderByStartDateDesc(employeeId);
        List<EmployeeLeaveResponseDto> result = new ArrayList<>();
        for (Leave l : leaves) {
            String leaveTypeName = l.getLeaveType() != null ? l.getLeaveType().getName() : null;
            result.add(new EmployeeLeaveResponseDto(l.getId(), l.getStartDate(), l.getEndDate(), l.getReason(), leaveTypeName));
        }
        return result;
    }

    private void validatePerMonthLimit(Employee employee, Leave newLeave) {
        LocalDate start = newLeave.getStartDate();
        LocalDate end = newLeave.getEndDate();

        YearMonth cur = YearMonth.from(start);
        YearMonth last = YearMonth.from(end);

        while (!cur.isAfter(last)) {
            LocalDate monthStart = cur.atDay(1);
            LocalDate monthEnd = cur.atEndOfMonth();

            List<Leave> overlapping = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    employee, monthEnd, monthStart);

            int existingDays = 0;
            for (Leave l : overlapping) {
                if (newLeave.getId() != null && newLeave.getId().equals(l.getId())) {
                    continue;
                }
                existingDays += overlapDaysInRange(l.getStartDate(), l.getEndDate(), monthStart, monthEnd);
            }

            int newLeaveDays = overlapDaysInRange(start, end, monthStart, monthEnd);

            if (existingDays + newLeaveDays > MAX_DAYS_PER_MONTH) {
                throw new LeavesException(String.format(
                        "Monthly leave limit exceeded for %s: existing=%d, new=%d, allowed=%d",
                        cur, existingDays, newLeaveDays, MAX_DAYS_PER_MONTH));
            }

            cur = cur.plusMonths(1);
        }
    }

    private int overlapDaysInRange(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        LocalDate start = s1.isAfter(s2) ? s1 : s2;
        LocalDate end = e1.isBefore(e2) ? e1 : e2;
        if (end.isBefore(start)) return 0;
        return (int) (end.toEpochDay() - start.toEpochDay() + 1);
    }


    public List<LeaveSummaryDto> getMonthlyLeaveSummary() {
        List<Leave> leaves = leaveRepository.findAll();
        Map<String, LeaveSummaryDto> monthMap = new LinkedHashMap<>();

        for (Leave leave : leaves) {
            LocalDate start = leave.getStartDate();
            LocalDate end = leave.getEndDate();
            String leaveType = leave.getLeaveType() != null ? leave.getLeaveType().getName() : "Unknown";

            LocalDate current = start;
            while (!current.isAfter(end)) {
                String monthKey = current.getMonth().toString() + current.getYear(); // e.g., JAN2025

                LeaveSummaryDto dto;
                if (monthMap.containsKey(monthKey)) {
                    dto = monthMap.get(monthKey);
                } else {
                    dto = new LeaveSummaryDto(monthKey);
                    monthMap.put(monthKey, dto);
                }

                Map<String, Integer> counts = dto.getLeaveCounts();
                if (counts.containsKey(leaveType)) {
                    counts.put(leaveType, counts.get(leaveType) + 1);
                } else {
                    counts.put(leaveType, 1);
                }

                current = current.plusDays(1);
            }
        }

        return new ArrayList<>(monthMap.values());
    }
}
