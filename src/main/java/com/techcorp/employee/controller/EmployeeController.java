package com.techcorp.employee.controller;

import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.dto.StatusUpdateRequest;
import com.techcorp.employee.mapper.EmployeeMapper;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ------------------------ GET WSZYSTKICH ------------------------
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // ------------------------ GET PO EMAILU ------------------------
    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        if (employee == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(employee);
    }

    // ------------------------ PUT UPDATE ------------------------
    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String email, @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updated = employeeService.updateEmployee(email, employeeDTO);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // ------------------------ DELETE ------------------------
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        boolean deleted = employeeService.deleteEmployee(email);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // ------------------------ PATCH STATUS ------------------------
    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(@PathVariable String email, @RequestBody StatusUpdateRequest statusRequest) {
        EmployeeDTO updated = employeeService.updateEmployeeStatus(email, statusRequest.getStatus());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // ------------------------ GET PO STATUSIE ------------------------
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable EmploymentStatus status) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }

    // ------------------------ GET PO FIRMIE ------------------------
    @GetMapping("/company/{company}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCompany(@PathVariable String company) {
        List<EmployeeDTO> employees = employeeService.findEmployeesByCompany(company)
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(employees);
    }

    // ------------------------ POST NOWY ------------------------
    @PostMapping
    public ResponseEntity<Void> addEmployee(@RequestBody EmployeeDTO dto) {
        try {
            boolean created = employeeService.addEmployee(EmployeeMapper.toEntity(dto));
            if (created) {
                return ResponseEntity
                        .created(URI.create("/api/employees/" + dto.getEmail()))
                        .build();
            } else {
                return ResponseEntity.status(409).build(); // konflikt
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("exists")) {
                return ResponseEntity.status(409).build(); // duplikat
            }
            throw e; // inne wyjątki → 500
        }
    }
}
