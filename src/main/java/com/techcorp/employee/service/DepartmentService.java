package com.techcorp.employee.service;


import com.techcorp.employee.model.Department;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentService {

    private Map<Long, Department> departments = new HashMap<>();
    private long nextId = 1L;

    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments.values());
    }

    public Department getDepartmentById(Long id) {
        return departments.get(id);
    }

    public Department addDepartment(Department d) {
        d.setId(nextId++);
        departments.put(d.getId(), d);
        return d;
    }

    public void updateDepartment(Department d) {
        departments.put(d.getId(), d);
    }

    public void deleteDepartment(Long id) {
        departments.remove(id);
    }
}
