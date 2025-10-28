package com.techcorp.employee.service;

import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmailSet;
import com.techcorp.employee.model.JobTitle;
import com.techcorp.employee.service.EmployeeService;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService manager;
    private Employee emp1;
    private Employee emp2;
    private Employee emp3;
    private EmailSet emailSet;
    private List<Employee> xmlEmployees;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych dla konstruktora
        emailSet = new EmailSet();

        // Tworzenie pracowników w metodzie testowej
        emp1 = new Employee("Jan", "Kowalski", "jan@firma.com", "FirmaA", JobTitle.DEVELOPER.getDisplayName(), 8000);
        emp2 = new Employee("Anna", "Nowak", "anna@firma.com", "FirmaA", JobTitle.MANAGER.getDisplayName(), 12000);
        emp3 = new Employee("Bartek", "Zielinski", "bartek@firma.com", "FirmaB", JobTitle.DEVELOPER.getDisplayName(), 8000);

        // Tworzenie listy pracowników i dodanie ich do XML
        xmlEmployees = new ArrayList<>();
        xmlEmployees.add(emp1);
        xmlEmployees.add(emp2);
        xmlEmployees.add(emp3);

        try {
            // Tworzymy menedżera z emailSet i xmlEmployees
            manager = new EmployeeService(emailSet, xmlEmployees);
        } catch (InvalidDataException e) {
            fail("Nie udało się stworzyć managera: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // Tu można dodać czyszczenie, jeśli potrzebne
    }

    // ================= Add Employee =================
    @Test
    void testAddEmployeeReturnsTrue() {
        Employee emp4 = new Employee("Marta", "Kowalczyk", "marta@firma.com", "FirmaC", JobTitle.INTERN.getDisplayName(), 3000);
        assertTrue(manager.addEmployee(emp4));
    }

    @Test
    void testAddEmployeeIncreasesCount() {
        Employee emp4 = new Employee("Marta", "Kowalczyk", "marta@firma.com", "FirmaC", JobTitle.INTERN.getDisplayName(), 3000);
        manager.addEmployee(emp4);
        assertEquals(4, manager.getEmployeeCount());
    }

    @Test
    void testAddEmployeeIsInList() {
        Employee emp4 = new Employee("Marta", "Kowalczyk", "marta@firma.com", "FirmaC", JobTitle.INTERN.getDisplayName(), 3000);
        manager.addEmployee(emp4);
        assertTrue(manager.getAllEmployees().contains(emp4));
    }

    // ================= Remove Employee =================
    @Test
    void testRemoveEmployeeByEmailReturnsTrue() {
        assertTrue(manager.removeEmployeeByEmail("anna@firma.com"));
    }

    @Test
    void testRemoveEmployeeByEmailDecreasesCount() {
        manager.removeEmployeeByEmail("anna@firma.com");
        assertEquals(2, manager.getEmployeeCount());
    }

    @Test
    void testRemovedEmployeeIsNotInList() {
        manager.removeEmployeeByEmail("anna@firma.com");
        assertFalse(manager.getAllEmployees().contains(emp2));
    }

    @Test
    void testRemoveEmployeeByEmailReturnsFalseIfNotExists() {
        assertFalse(manager.removeEmployeeByEmail("nonexistent@firma.com"));
    }

    @Test
    void testRemoveEmployeeByEmailKeepsCountIfNotExists() {
        manager.removeEmployeeByEmail("nonexistent@firma.com");
        assertEquals(3, manager.getEmployeeCount());
    }

    // ================= Employee Count =================
    @Test
    void testGetEmployeeCountReturnsCorrectValue() {
        assertEquals(3, manager.getEmployeeCount());
    }

    // ================= Find Employees by Company =================
    @Test
    void testFindEmployeesByCompanyFirmaAHasTwoEmployees() {
        List<Employee> firmaAEmployees = manager.findEmployeesByCompany("FirmaA");
        assertEquals(2, firmaAEmployees.size());
    }

    // ================= Group Employees by JobTitle =================
    @Test
    void testGroupEmployeesByJobTitleReturnsTwoGroups() {
        Map<String, List<Employee>> grouped = manager.groupEmployeesByJobTitle();
        assertEquals(2, grouped.size());
    }

    @Test
    void testGroupEmployeesByJobTitleContainsDeveloperKey() {
        Map<String, List<Employee>> grouped = manager.groupEmployeesByJobTitle();
        assertTrue(grouped.containsKey(JobTitle.DEVELOPER.getDisplayName()));
    }

    @Test
    void testGroupEmployeesByJobTitleContainsManagerKey() {
        Map<String, List<Employee>> grouped = manager.groupEmployeesByJobTitle();
        assertTrue(grouped.containsKey(JobTitle.MANAGER.getDisplayName()));
    }

    @Test
    void testGroupEmployeesByJobTitleDeveloperGroupHasTwo() {
        Map<String, List<Employee>> grouped = manager.groupEmployeesByJobTitle();
        assertEquals(2, grouped.get(JobTitle.DEVELOPER.getDisplayName()).size());
    }

    @Test
    void testGroupEmployeesByJobTitleManagerGroupHasOne() {
        Map<String, List<Employee>> grouped = manager.groupEmployeesByJobTitle();
        assertEquals(1, grouped.get(JobTitle.MANAGER.getDisplayName()).size());
    }

    // ================= Count Employees by JobTitle =================
    @Test
    void testCountEmployeesByJobTitleDeveloperCountIsTwo() {
        Map<String, Long> counts = manager.countEmployeesByJobTitle();
        assertEquals(2L, counts.get(JobTitle.DEVELOPER.getDisplayName()));
    }

    @Test
    void testCountEmployeesByJobTitleManagerCountIsOne() {
        Map<String, Long> counts = manager.countEmployeesByJobTitle();
        assertEquals(1L, counts.get(JobTitle.MANAGER.getDisplayName()));
    }

    // ================= Average Salary =================
    @Test
    void testCalculateAverageSalaryReturnsExpectedValue() {
        double expectedAvg = (JobTitle.DEVELOPER.getBaseSalary() + JobTitle.MANAGER.getBaseSalary() + JobTitle.DEVELOPER.getBaseSalary()) / 3.0;
        assertEquals(expectedAvg, manager.calculateAverageSalary(), 0.001);
    }

    @Test
    void testCalculateAverageSalaryEmptyReturnsZero() {
        EmployeeService emptyManager = new EmployeeService(emailSet, new ArrayList<>());
        assertEquals(0.0, emptyManager.calculateAverageSalary());
    }

    // ================= Highest Paid Employee =================
    @Test
    void testFindHighestPaidEmployeeIsPresent() {
        Optional<Employee> highest = manager.findHighestPaidEmployee();
        assertTrue(highest.isPresent());
    }

    @Test
    void testFindHighestPaidEmployeeIsEmp2() {
        Optional<Employee> highest = manager.findHighestPaidEmployee();
        assertEquals(emp2, highest.get());
    }

    @Test
    void testFindHighestPaidEmployeeEmptyReturnsEmpty() {
        EmployeeService emptyManager = new EmployeeService(emailSet, new ArrayList<>());
        Optional<Employee> highest = emptyManager.findHighestPaidEmployee();
        assertTrue(highest.isEmpty());
    }
}
