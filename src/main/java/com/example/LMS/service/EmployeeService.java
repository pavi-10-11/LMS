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
        if (dto.getEmployeeId() == null || dto.getEmployeeId().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDept(dto.getDept());

        return employeeRepository.save(employee);
    }
}
