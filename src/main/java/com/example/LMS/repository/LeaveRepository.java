package com.example.LMS.repository;

import com.example.LMS.entity.Employee;
import com.example.LMS.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {


    List<Leave> findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee, LocalDate rangeEnd, LocalDate rangeStart);


    List<Leave> findByEmployeeEmployeeId(String employeeId);


    List<Leave> findByEmployeeEmployeeIdOrderByStartDateDesc(String employeeId);
    List<Leave> findByEmployee(Employee employee);
}
