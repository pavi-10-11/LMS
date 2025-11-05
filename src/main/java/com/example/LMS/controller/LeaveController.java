package com.example.LMS.controller;

import com.example.LMS.dto.EmployeeLeaveResponseDto;
import com.example.LMS.dto.LeaveBalanceDto;
import com.example.LMS.dto.LeaveSummaryDto;
import com.example.LMS.entity.Leave;
import com.example.LMS.entity.Employee;
import com.example.LMS.service.LeaveService;
import com.example.LMS.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {
    private final LeaveService leaveService;
    private final EmployeeRepository employeeRepository;

    public LeaveController(LeaveService leaveService, EmployeeRepository employeeRepository) {
        this.leaveService = leaveService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Leave>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) {
        return leaveService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeLeaveResponseDto>> getLeavesByEmployee(@PathVariable String employeeId) {
        List<EmployeeLeaveResponseDto> leaves = leaveService.findByEmployeeId(employeeId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<LeaveSummaryDto>> getLeaveSummary() {
        List<LeaveSummaryDto> summary = leaveService.getMonthlyLeaveSummary();
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<Leave> createLeave(
            @RequestBody Leave leave,
            @RequestParam String employeeId,
            @RequestParam Long leaveTypeId) {
        Leave saved = leaveService.save(leave, employeeId, leaveTypeId);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leave> updateLeave(@PathVariable Long id,
                                             @RequestBody Leave leave,
                                             @RequestParam String employeeId,
                                             @RequestParam Long leaveTypeId) {
        return leaveService.findById(id)
                .map(existing -> {
                    existing.setStartDate(leave.getStartDate());
                    existing.setEndDate(leave.getEndDate());
                    existing.setReason(leave.getReason());
                    existing.setId(id);
                    Leave saved = leaveService.save(existing, employeeId, leaveTypeId);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<?> getBalance(@PathVariable String employeeId,
                                        @RequestParam(required = false) String referenceDate) {
        Employee emp = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        LocalDate ref = (referenceDate == null) ? LocalDate.now() : LocalDate.parse(referenceDate);
        int monthRemaining = leaveService.getRemainingMonthlyBalance(emp, ref);
        int yearRemaining = leaveService.getRemainingAnnualBalance(emp, ref);

        return ResponseEntity.ok(java.util.Map.of(
                "monthlyRemaining", monthRemaining,
                "annualRemaining", yearRemaining
        ));
    }

    @GetMapping("/balance/summary/{employeeId}")
    public ResponseEntity<LeaveBalanceDto> getLeaveBalanceSummary(@PathVariable String employeeId,
                                                                  @RequestParam(required = false) String referenceDate) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        LocalDate ref = (referenceDate == null) ? LocalDate.now() : LocalDate.parse(referenceDate);
        LeaveBalanceDto dto = leaveService.getLeaveBalanceSummary(employee, ref);
        return ResponseEntity.ok(dto);
    }
}
