package com.example.LMS.controller;

import com.example.LMS.entity.Employee;
import com.example.LMS.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee emp) {
        return ResponseEntity.ok(repo.save(emp));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getByEmployeeId(@PathVariable String employeeId) {
        return repo.findByEmployeeId(employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> update(@PathVariable String employeeId,
                                           @RequestBody Employee details) {
        return repo.findByEmployeeId(employeeId)
                .map(existing -> {
                    existing.setFirstName(details.getFirstName());
                    existing.setLastName(details.getLastName());
                    existing.setEmail(details.getEmail());
                    existing.setDept(details.getDept());
                    existing.setPhoneNumber(details.getPhoneNumber());
                    return ResponseEntity.ok(repo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Object> delete(@PathVariable String employeeId) {
        return repo.findByEmployeeId(employeeId)
                .map(existing -> {
                    repo.delete(existing);
                    return ResponseEntity.<Void>noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}