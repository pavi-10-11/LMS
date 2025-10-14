package com.example.LMS.service;


import com.example.LMS.entity.LeaveType;
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

        if (leaveType.getId() == null && leaveTypeRepository.existsByName(leaveType.getName())) {
            throw new RuntimeException("Leave type already exists");
        }
        return leaveTypeRepository.save(leaveType);
    }

    public void deleteById(Long id) {
        leaveTypeRepository.deleteById(id);
    }
}
