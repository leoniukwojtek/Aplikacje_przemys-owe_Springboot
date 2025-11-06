package com.techcorp.employee.service;

import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.mapper.EmployeeMapper;
import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmailSet emailSet;
    private final List<Employee> employees;

    @Autowired
    public EmployeeService(EmailSet emailSet, @Qualifier("xmlEmployees") List<Employee> xmlEmployees) throws InvalidDataException {
        this.emailSet = emailSet;
        this.employees = new ArrayList<>();

        if (xmlEmployees != null && !xmlEmployees.isEmpty()) {
            for (Employee e : xmlEmployees) {
                addEmployee(e);
            }
            logger.info("Dodano pracowników z beana:");
            printAllEmployees(); // pozostaje dokładnie tak jak było
        }
    }

    public EmployeeService(EmailSet emailSet) {
        this.emailSet = emailSet;
        this.employees = new ArrayList<>();
        logger.info("EmployeeService utworzony z pustą listą pracowników.");
    }

    public EmployeeService() {
        this.emailSet = new EmailSet();
        this.employees = new ArrayList<>();
        logger.info("EmployeeService utworzony z pustą listą pracowników.");
    }

    // --- Metody z EmployeeDTO ---
    public List<EmployeeDTO> getAllEmployees() {
        return employees.stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeByEmail(String email) {
        return employees.stream()
                .filter(e -> e.getEmailAddress().equalsIgnoreCase(email))
                .findFirst()
                .map(EmployeeMapper::toDTO)
                .orElse(null);
    }

    public EmployeeDTO updateEmployee(String email, EmployeeDTO updatedDTO) {
        for (Employee e : employees) {
            if (e.getEmailAddress().equalsIgnoreCase(email)) {
                e.setSalary(updatedDTO.getSalary());
                // pozostawiamy przypisanie jobTitle dokładnie jak w poprzednim kodzie
                // e.setJobTitle(updatedDTO.getPosition());
                logger.info("Zaktualizowano dane pracownika z emailem: {}", email);
                return EmployeeMapper.toDTO(e);
            }
        }
        logger.warn("Nie znaleziono pracownika do aktualizacji: {}", email);
        return null;
    }

    public boolean deleteEmployee(String email) {
        return removeEmployeeByEmail(email);
    }

    public EmployeeDTO updateEmployeeStatus(String email, EmploymentStatus status) {
        Employee employee = employees.stream()
                .filter(e -> e.getEmailAddress().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (employee == null) return null;

        // poprawione: faktycznie zmieniamy status w modelu Employee
        employee.setStatus(status);

        EmployeeDTO dto = EmployeeMapper.toDTO(employee);
        dto.setStatus(status);
        logger.info("Zaktualizowano status pracownika {} na {}", email, status);
        return dto;
    }

    public List<EmployeeDTO> getEmployeesByStatus(EmploymentStatus status) {
        // teraz poprawnie zwraca listę pracowników o podanym statusie
        return employees.stream()
                .filter(e -> e.getStatus() == status)
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // --- Metody operujące na modelu Employee ---
    public boolean addEmployee(Employee employee) throws InvalidDataException {
        if (employee == null) throw new InvalidDataException("Pracownik nie może być null.");
        validateEmployeeData(employee);

        if (emailSet.containsEmail(employee.getEmailAddress())) {
            throw new InvalidDataException("Email " + employee.getEmailAddress() + " już istnieje!");
        }
        if (employee.getStatus() == null) employee.setStatus(EmploymentStatus.ACTIVE);

        employees.add(employee);
        emailSet.addEmail(employee.getEmailAddress());
        logger.info("Dodano pracownika: {} {}", employee.getFirstName(), employee.getLastName());
        return true;
    }

    private void validateEmployeeData(Employee employee) throws InvalidDataException {
        if (employee.getFirstName() == null || employee.getFirstName().isBlank())
            throw new InvalidDataException("Imię nie może być puste.");
        if (employee.getLastName() == null || employee.getLastName().isBlank())
            throw new InvalidDataException("Nazwisko nie może być puste.");
        if (employee.getEmailAddress() == null || employee.getEmailAddress().isBlank())
            throw new InvalidDataException("Email nie może być pusty.");
        if (employee.getCompanyName() == null || employee.getCompanyName().isBlank())
            throw new InvalidDataException("Nazwa firmy nie może być pusta.");
        if (employee.getJobTitle() == null || employee.getJobTitle().isBlank())
            throw new InvalidDataException("Stanowisko nie może być puste.");
    }

    public boolean removeEmployeeByEmail(String email) {
        boolean removed = employees.removeIf(e -> e.getEmailAddress().equalsIgnoreCase(email));
        if (removed) {
            emailSet.removeEmail(email);
            logger.info("Usunięto pracownika z emailem: {}", email);
        } else {
            logger.warn("Nie znaleziono pracownika z emailem: {}", email);
        }
        return removed;
    }

    // --- Pozostałe metody, statystyki, grupowania ---
    public void printAllEmployees() {
        if (employees.isEmpty()) {
            logger.info("Brak pracowników do wyświetlenia.");
            return;
        }
        employees.sort(Employee.getAlphabeticalComparator());
        for (int i = 0; i < employees.size(); i++) {
            logger.info("{}. {}", i + 1, employees.get(i));
        }
    }

    public List<Employee> findEmployeesByCompany(String companyName) {
        return employees.stream()
                .filter(e -> e.getCompanyName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    public Map<String, List<Employee>> groupEmployeesByJobTitle() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getJobTitle));
    }

    public Map<String, Long> countEmployeesByJobTitle() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getJobTitle, Collectors.counting()));
    }

    public double calculateAverageSalary() {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Optional<Employee> findHighestPaidEmployee() {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getCompanyName,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            long count = list.size();
                            double avgSalary = list.stream().mapToDouble(Employee::getSalary).average().orElse(0);
                            double maxSalary = list.stream().mapToDouble(Employee::getSalary).max().orElse(0);
                            return new CompanyStatistics(count, avgSalary, maxSalary);
                        })
                ));
    }

    public Map<EmploymentStatus, Long> getEmployeesStatusStatistics() {
        // Tworzymy mapę z każdym statusem i liczbą pracowników
        return Arrays.stream(EmploymentStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> employees.stream()
                                .filter(e -> e.getStatus() == status)
                                .count()
                ));
    }


}
