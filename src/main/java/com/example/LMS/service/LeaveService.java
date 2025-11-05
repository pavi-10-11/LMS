package com.example.LMS.service;

import com.example.LMS.dto.EmployeeLeaveResponseDto;
import com.example.LMS.dto.LeaveBalanceDto;
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
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    private static final int MAX_DAYS_PER_MONTH = 2;
    private static final int MAX_DAYS_PER_YEAR = 12;

    public LeaveService(LeaveRepository leaveRepository,
                        EmployeeRepository employeeRepository,
                        LeaveTypeRepository leaveTypeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
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

    @Transactional
    public Leave save(Leave leave, String employeeId, Long leaveTypeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        LeaveType lt = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("LeaveType not found: " + leaveTypeId));

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        validatePerMonthLimit(employee, leave);
        validatePerYearLimit(employee, leave);

        leave.setEmployee(employee);
        leave.setLeaveType(lt);
        return leaveRepository.save(leave);
    }


    public List<EmployeeLeaveResponseDto> findByEmployeeId(String employeeId) {
        employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        List<Leave> leaves = leaveRepository.findByEmployeeEmployeeIdOrderByStartDateDesc(employeeId);
        List<EmployeeLeaveResponseDto> result = new ArrayList<>();
        for (Leave l : leaves) {
            String leaveTypeName = l.getLeaveType() != null ? l.getLeaveType().getName() : null;
            result.add(new EmployeeLeaveResponseDto(l.getId(), l.getStartDate(), l.getEndDate(), l.getReason(), leaveTypeName));
        }
        return result;
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
                String monthKey = current.getMonth().toString() + current.getYear();
                LeaveSummaryDto dto = monthMap.computeIfAbsent(monthKey, k -> new LeaveSummaryDto(monthKey));
                Map<String, Integer> counts = dto.getLeaveCounts();
                counts.put(leaveType, counts.getOrDefault(leaveType, 0) + 1);
                current = current.plusDays(1);
            }
        }
        return new ArrayList<>(monthMap.values());
    }


    private void validatePerMonthLimit(Employee employee, Leave newLeave) {
        LocalDate start = newLeave.getStartDate();
        LocalDate end = newLeave.getEndDate();
        YearMonth cur = YearMonth.from(start);
        YearMonth last = YearMonth.from(end);
        while (!cur.isAfter(last)) {
            LocalDate monthStart = cur.atDay(1);
            LocalDate monthEnd = cur.atEndOfMonth();
            List<Leave> overlapping = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employee, monthEnd, monthStart);
            int existingDays = 0;
            for (Leave l : overlapping) {
                if (newLeave.getId() != null && newLeave.getId().equals(l.getId())) continue;
                existingDays += overlapDaysInRange(l.getStartDate(), l.getEndDate(), monthStart, monthEnd);
            }
            int newLeaveDays = overlapDaysInRange(start, end, monthStart, monthEnd);
            if (existingDays + newLeaveDays > MAX_DAYS_PER_MONTH) {
                throw new LeavesException(String.format("Monthly leave limit exceeded for %s: existing=%d, new=%d, allowed=%d", cur, existingDays, newLeaveDays, MAX_DAYS_PER_MONTH));
            }
            cur = cur.plusMonths(1);
        }
    }

    private void validatePerYearLimit(Employee employee, Leave newLeave) {
        LocalDate start = newLeave.getStartDate();
        LocalDate end = newLeave.getEndDate();
        int startYear = start.getYear();
        int endYear = end.getYear();
        for (int year = startYear; year <= endYear; year++) {
            LocalDate yearStart = Year.of(year).atDay(1);
            LocalDate yearEnd = Year.of(year).atDay(yearStart.lengthOfYear());
            List<Leave> overlapping = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employee, yearEnd, yearStart);
            int existingDays = 0;
            for (Leave l : overlapping) {
                if (newLeave.getId() != null && newLeave.getId().equals(l.getId())) continue;
                existingDays += overlapDaysInRange(l.getStartDate(), l.getEndDate(), yearStart, yearEnd);
            }
            int newLeaveDays = overlapDaysInRange(start, end, yearStart, yearEnd);
            if (existingDays + newLeaveDays > MAX_DAYS_PER_YEAR) {
                throw new LeavesException(String.format("Yearly leave limit exceeded for %d: existing=%d, new=%d, allowed=%d", year, existingDays, newLeaveDays, MAX_DAYS_PER_YEAR));
            }
        }
    }

    private int overlapDaysInRange(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        LocalDate start = s1.isAfter(s2) ? s1 : s2;
        LocalDate end = e1.isBefore(e2) ? e1 : e2;
        if (end.isBefore(start)) return 0;
        return (int) (end.toEpochDay() - start.toEpochDay() + 1);
    }


    public int getLeaveDaysTakenInMonth(Employee employee, YearMonth month, Long ignoreLeaveId) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        List<Leave> overlapping = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employee, monthEnd, monthStart);
        int days = 0;
        for (Leave l : overlapping) {
            if (ignoreLeaveId != null && ignoreLeaveId.equals(l.getId())) continue;
            days += overlapDaysInRange(l.getStartDate(), l.getEndDate(), monthStart, monthEnd);
        }
        return days;
    }

    public int getLeaveDaysTakenInYear(Employee employee, int year, Long ignoreLeaveId) {
        LocalDate yearStart = Year.of(year).atDay(1);
        LocalDate yearEnd = Year.of(year).atDay(yearStart.lengthOfYear());
        List<Leave> overlapping = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employee, yearEnd, yearStart);
        int days = 0;
        for (Leave l : overlapping) {
            if (ignoreLeaveId != null && ignoreLeaveId.equals(l.getId())) continue;
            days += overlapDaysInRange(l.getStartDate(), l.getEndDate(), yearStart, yearEnd);
        }
        return days;
    }


    public int getRemainingMonthlyBalance(Employee employee, LocalDate referenceDate) {
        int used = getLeaveDaysTakenInMonth(employee, YearMonth.from(referenceDate), null);
        return Math.max(0, MAX_DAYS_PER_MONTH - used);
    }

    public int getRemainingAnnualBalance(Employee employee, LocalDate referenceDate) {
        int used = getLeaveDaysTakenInYear(employee, referenceDate.getYear(), null);
        return Math.max(0, MAX_DAYS_PER_YEAR - used);
    }


    public LeaveBalanceDto getLeaveBalanceSummary(Employee employee, LocalDate referenceDate) {
        if (referenceDate == null) referenceDate = LocalDate.now();
        int usedYear = getLeaveDaysTakenInYear(employee, referenceDate.getYear(), null);
        int usedMonth = getLeaveDaysTakenInMonth(employee, YearMonth.from(referenceDate), null);
        int remainingYear = Math.max(0, MAX_DAYS_PER_YEAR - usedYear);
        int remainingMonth = Math.max(0, MAX_DAYS_PER_MONTH - usedMonth);
        String name = (employee.getFirstName() == null ? "" : employee.getFirstName()) +
                (employee.getLastName() == null ? "" : " " + employee.getLastName());
        return new LeaveBalanceDto(employee.getEmployeeId(), name.trim(), MAX_DAYS_PER_YEAR, usedYear, remainingYear, usedMonth, remainingMonth);
    }
}
