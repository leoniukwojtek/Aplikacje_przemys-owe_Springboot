package com.techcorp.employee.service;

import java.util.*;
import java.util.stream.Collectors;

import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmailSet emailSet;
    private final List<Employee> employees;

    // Konstruktor z wstrzykiwaniem zależności: EmailSet oraz lista pracowników z XML
    @Autowired
    public EmployeeService(EmailSet emailSet, @Qualifier("xmlEmployees") List<Employee> xmlEmployees) throws InvalidDataException {
        this.emailSet = emailSet;
        this.employees = new ArrayList<>();

        // Dodanie pracowników z XML do listy
        if (xmlEmployees != null && !xmlEmployees.isEmpty()) {
            for (Employee e : xmlEmployees) {
                addEmployee(e); // Dodanie pracowników z XML do listy
            }
            logger.info("Dodano pracowników z beana:");
            printAllEmployees();
        }
    }

    // Konstruktor jednoargumentowy z tylko EmailSet
    public EmployeeService(EmailSet emailSet) {
        this.emailSet = emailSet;
        this.employees = new ArrayList<>();
        logger.info("EmployeeService utworzony z pustą listą pracowników.");
    }

    // Konstruktor bezargumentowy (do testów lub innych zastosowań)
    public EmployeeService() {
        this.emailSet = new EmailSet();
        this.employees = new ArrayList<>();
        logger.info("EmployeeService utworzony z pustą listą pracowników.");
    }

    // Dodanie pracownika z walidacją i kontrolą unikalności emaila
    public boolean addEmployee(Employee employee) throws InvalidDataException {
        if (employee == null) {
            throw new InvalidDataException("Pracownik nie może być null.");
        }

        // Walidacja danych
        validateEmployeeData(employee);

        // Unikalność emaila
        if (emailSet.containsEmail(employee.getEmailAddress())) {
            throw new InvalidDataException("Email " + employee.getEmailAddress() + " już istnieje!");
        }

        // Dodanie pracownika do listy
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

    // Usunięcie pracownika po emailu
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

    // Liczba pracowników
    public int getEmployeeCount() {
        return employees.size();
    }

    // Metoda toString
    @Override
    public String toString() {
        return String.format("Lista pracowników zawiera %d pracowników.", getEmployeeCount());
    }

    // Metoda do wypisania wszystkich pracowników
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

    // Metoda zwracająca wszystkich pracowników
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    // Znajdowanie pracowników według firmy
    public List<Employee> findEmployeesByCompany(String companyName) {
        return employees.stream()
                .filter(e -> e.getCompanyName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    // Grupowanie pracowników według stanowiska
    public Map<String, List<Employee>> groupEmployeesByJobTitle() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getJobTitle));
    }

    // Liczenie pracowników według stanowiska
    public Map<String, Long> countEmployeesByJobTitle() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getJobTitle, Collectors.counting()));
    }

    // Obliczanie średniej pensji
    public double calculateAverageSalary() {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    // Znajdowanie najlepiej opłacanego pracownika
    public Optional<Employee> findHighestPaidEmployee() {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    // Statystyki dla każdej firmy
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

    // Walidacja niespójnych wynagrodzeń
    public List<Employee> validateSalaryConsistency() {
        return employees.stream()
                .filter(emp -> {
                    JobTitle title = getJobTitleByDisplayName(emp.getJobTitle());
                    return title != null && emp.getSalary() < title.getBaseSalary();
                })
                .collect(Collectors.toList());
    }

    private JobTitle getJobTitleByDisplayName(String displayName) {
        for (JobTitle title : JobTitle.values()) {
            if (title.getDisplayName().equalsIgnoreCase(displayName)) {
                return title;
            }
        }
        return null;
    }
}
