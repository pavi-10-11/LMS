package com.example.LMS.service;

import com.example.LMS.entity.LeaveType;
import com.example.LMS.exception.LeavesException;
import com.example.LMS.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public List<LeaveType> findAll() {
        return leaveTypeRepository.findAll();
    }

    public Optional<LeaveType> findById(Long id) {
        return leaveTypeRepository.findById(id);
    }

    public LeaveType save(LeaveType leaveType) {
        if (leaveType.getName() == null || leaveType.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type name cannot be null or empty");
        }

       
        if (leaveType.getId() == null && leaveTypeRepository.existsByName(leaveType.getName())) {
            throw new LeavesException("Leave type with name '" + leaveType.getName() + "' already exists");
        }

     
        if (leaveType.getId() != null) {
            Optional<LeaveType> existing = leaveTypeRepository.findById(leaveType.getId());
            if (existing.isPresent() && !existing.get().getName().equals(leaveType.getName())) {
                if (leaveTypeRepository.existsByName(leaveType.getName())) {
                    throw new LeavesException("Leave type with name '" + leaveType.getName() + "' already exists");
                }
            }
        }

        return leaveTypeRepository.save(leaveType);
    }

    public void deleteById(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Leave type not found with ID: " + id);
        }
        leaveTypeRepository.deleteById(id);
    }
}