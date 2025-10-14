package com.example.LMS.service;


import com.example.LMS.entity.Employee;
import com.example.LMS.entity.Leave;
import com.example.LMS.entity.LeaveType;
import com.example.LMS.repository.EmployeeRepository;
import com.example.LMS.repository.LeaveRepository;
import com.example.LMS.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

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

    public Leave save(Leave leave, Long employeeId, Long leaveTypeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LeaveType lt = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        leave.setEmployee(emp);
        leave.setLeaveType(lt);

        return leaveRepository.save(leave);
    }

    public void deleteById(Long id) {
        leaveRepository.deleteById(id);
    }
}
