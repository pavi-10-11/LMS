package com.example.LMS.service;

import com.example.LMS.dto.EmployeeRequestDto;
import com.example.LMS.entity.Employee;
import com.example.LMS.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(EmployeeRequestDto dto) {
        if (dto.getEmployeeId() == null || dto.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        
        if (employeeRepository.findByEmployeeId(dto.getEmployeeId()).isPresent()) {
            throw new IllegalArgumentException("Employee with ID " + dto.getEmployeeId() + " already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId().trim());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDept(dto.getDept());

        return employeeRepository.save(employee);
    }
}