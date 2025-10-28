package com.techcorp.employee.service;

import com.techcorp.employee.exception.InvalidDataException;
import org.junit.jupiter.api.Test;
import com.techcorp.employee.model.Employee;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testGetFirstName() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("Jan", emp.getFirstName());
    }

    @Test
    void testGetLastName() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("Kowalski", emp.getLastName());
    }

    @Test
    void testGetEmailAddress() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("jan@firma.com", emp.getEmailAddress());
    }

    @Test
    void testGetCompanyName() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("Firma", emp.getCompanyName());
    }

    @Test
    void testGetJobTitle() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("Developer", emp.getJobTitle());
    }

    @Test
    void testGetSalary() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals(5000, emp.getSalary());
    }

    @Test
    void testSetSalary() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        emp.setSalary(6000);
        assertEquals(6000, emp.getSalary());
    }

    @Test
    void testToString() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertEquals("Jan Kowalski", emp.toString());
    }

    @Test
    void testShowFullDetailsContainsName() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertTrue(emp.showFullDetails().contains("Jan Kowalski"));
    }

    @Test
    void testShowFullDetailsContainsEmail() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertTrue(emp.showFullDetails().contains("jan@firma.com"));
    }

    @Test
    void testShowFullDetailsContainsCompany() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertTrue(emp.showFullDetails().contains("Firma"));
    }

    @Test
    void testShowFullDetailsContainsJobTitle() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertTrue(emp.showFullDetails().contains("Developer"));
    }

    @Test
    void testShowFullDetailsContainsSalary() {
        Employee emp = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        assertTrue(emp.showFullDetails().contains("5000"));
    }

    @Test
    void testEqualsSameEmail() {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        Employee emp2 = new Employee("Anna", "Nowak", "jan@firma.com", "Firma", "Manager", 7000);
        assertEquals(emp1, emp2);
    }

    @Test
    void testEqualsDifferentEmail() {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        Employee emp2 = new Employee("Bartek", "Nowak", "bartek@firma.com", "Firma", "Intern", 3000);
        assertNotEquals(emp1, emp2);
    }

    @Test
    void testHashCodeSameEmail() {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        Employee emp2 = new Employee("Anna", "Nowak", "jan@firma.com", "Firma", "Manager", 7000);
        assertEquals(emp1.hashCode(), emp2.hashCode());
    }

    @Test
    void testHashCodeDifferentEmail() {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@firma.com", "Firma", "Developer", 5000);
        Employee emp2 = new Employee("Bartek", "Nowak", "bartek@firma.com", "Firma", "Intern", 3000);
        assertNotEquals(emp1.hashCode(), emp2.hashCode());
    }

    @Test
    void testValidateThrowsExceptionForNullFirstName() {
        Employee emp = new Employee(null, "Kowalski", "email@firma.com", "Firma", "Dev", 5000);
        InvalidDataException ex = assertThrows(InvalidDataException.class, emp::validate);
        assertTrue(ex.getMessage().contains("Imię nie może być puste."));
    }

    @Test
    void testValidateThrowsExceptionForBlankLastName() {
        Employee emp = new Employee("Jan", "  ", "email@firma.com", "Firma", "Dev", 5000);
        InvalidDataException ex = assertThrows(InvalidDataException.class, emp::validate);
        assertTrue(ex.getMessage().contains("Nazwisko nie może być puste."));
    }

    @Test
    void testValidateThrowsExceptionForBlankEmail() {
        Employee emp = new Employee("Jan", "Kowalski", " ", "Firma", "Dev", 5000);
        InvalidDataException ex = assertThrows(InvalidDataException.class, emp::validate);
        assertTrue(ex.getMessage().contains("Email nie może być pusty."));
    }

    @Test
    void testValidateThrowsExceptionForNullCompany() {
        Employee emp = new Employee("Jan", "Kowalski", "email@firma.com", null, "Dev", 5000);
        InvalidDataException ex = assertThrows(InvalidDataException.class, emp::validate);
        assertTrue(ex.getMessage().contains("Nazwa firmy nie może być pusta."));
    }

    @Test
    void testValidateThrowsExceptionForBlankJobTitle() {
        Employee emp = new Employee("Jan", "Kowalski", "email@firma.com", "Firma", "  ", 5000);
        InvalidDataException ex = assertThrows(InvalidDataException.class, emp::validate);
        assertTrue(ex.getMessage().contains("Stanowisko nie może być puste."));
    }
}
