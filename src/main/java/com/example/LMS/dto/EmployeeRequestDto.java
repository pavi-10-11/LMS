package com.example.LMS.dto;

public class EmployeeRequestDto {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String dept;

    public EmployeeRequestDto() {}


    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }
}
