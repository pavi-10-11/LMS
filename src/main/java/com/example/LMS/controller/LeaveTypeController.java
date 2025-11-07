package com.example.LMS.controller;

import com.example.LMS.entity.LeaveType;
import com.example.LMS.service.LeaveTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leavetypes")

public class LeaveTypeController {
    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        return leaveTypeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveType leaveType) {
        LeaveType created = leaveTypeService.save(leaveType);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id,
                                                     @RequestBody LeaveType leaveType) {
        return leaveTypeService.findById(id)
                .map(existing -> {
                    existing.setName(leaveType.getName());
                    existing.setDescription(leaveType.getDescription());
                    LeaveType saved = leaveTypeService.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        leaveTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
