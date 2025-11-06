package com.techcorp.employee.mapper;

import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.model.Employee;

public class EmployeeMapper {

    public static EmployeeDTO toDTO(Employee e) {
        if (e == null) return null;

        return new EmployeeDTO(
                e.getFirstName(),
                e.getLastName(),
                e.getEmailAddress(),
                e.getCompanyName(),
                e.getJobTitle(),
                e.getSalary(),
                e.getStatus()
        );
    }

    public static Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee employee = new Employee(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getCompany(),
                dto.getPosition(),
                dto.getSalary()
        );
        employee.setStatus(dto.getStatus());
        return employee;
    }
}
