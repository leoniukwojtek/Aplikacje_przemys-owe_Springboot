package com.techcorp.employee.controller;

import com.techcorp.employee.model.CompanyStatistics;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EmployeeService employeeService;

    @Autowired
    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * GET /api/statistics/salary/average
     * GET /api/statistics/salary/average?company=X
     * Zwraca średnie wynagrodzenie globalnie lub dla wybranej firmy.
     */
    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> getAverageSalary(
            @RequestParam(required = false) String company) {

        double averageSalary;

        if (company != null && !company.isBlank()) {
            // Średnia tylko dla wskazanej firmy
            List<Employee> companyEmployees = employeeService.findEmployeesByCompany(company);
            averageSalary = companyEmployees.stream()
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0.0);
        } else {
            // Globalna średnia
            averageSalary = employeeService.calculateAverageSalary();
        }

        Map<String, Double> response = new HashMap<>();
        response.put("averageSalary", averageSalary);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/company/{companyName}
     * Zwraca szczegółowe statystyki firmy (liczba pracowników, średnia i maks. pensja)
     */
    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatistics> getCompanyStatistics(@PathVariable String companyName) {
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();

        CompanyStatistics companyStats = stats.get(companyName);
        if (companyStats == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(companyStats);
    }

    /**
     * GET /api/statistics/positions
     * Zwraca liczbę pracowników na każdym stanowisku.
     */
    @GetMapping("/positions")
    public ResponseEntity<Map<String, Integer>> getEmployeeCountByPosition() {
        Map<String, Long> counts = employeeService.countEmployeesByJobTitle();

        // Konwersja Long → Integer dla JSON-a
        Map<String, Integer> response = counts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/status
     * Zwraca rozkład pracowników według statusu zatrudnienia.
     * (na razie zwraca pustą mapę, bo Employee nie ma pola status)
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getEmployeeCountByStatus() {
        Map<EmploymentStatus, Long> counts = employeeService.getEmployeesStatusStatistics();

        // Konwertujemy klucze do String dla JSON
        Map<String, Long> response = counts.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));

        return ResponseEntity.ok(response);
    }
}
