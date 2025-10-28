package com.techcorp.employee.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.techcorp.employee.model.CompanyStatistics;

public class CompanyStatisticsTest {

    // Testuje poprawność zwracanej liczby pracowników
    @Test
    void testGetEmployeeCount() {
        CompanyStatistics stats = new CompanyStatistics(100, 5000.0, 12000.0);
        assertEquals(100, stats.getEmployeeCount(), "Powinno zwrócić liczbę pracowników 100");
    }

    // Testuje poprawność zwracanej średniej pensji
    @Test
    void testGetAverageSalary() {
        CompanyStatistics stats = new CompanyStatistics(100, 5000.0, 12000.0);
        assertEquals(5000.0, stats.getAverageSalary(), "Powinna być średnia pensja 5000.0");
    }

    // Testuje poprawność zwracanej maksymalnej pensji
    @Test
    void testGetMaxSalary() {
        CompanyStatistics stats = new CompanyStatistics(100, 5000.0, 12000.0);
        assertEquals(12000.0, stats.getMaxSalary(), "Powinna być maksymalna pensja 12000.0");
    }

    // Testuje format tekstu zwracanego przez toString()
    @Test
    void testToStringFormat() {
        CompanyStatistics stats = new CompanyStatistics(50, 4500.5, 10000.0);
        String expected = "Employees: 50, Avg Salary: 4500.5, Max Salary: 10000.0";
        assertEquals(expected, stats.toString(), "Format tekstu powinien być zgodny z oczekiwanym");
    }

    // Testuje zachowanie przy zerowej liczbie pracowników
    @Test
    void testZeroEmployeeCount() {
        CompanyStatistics stats = new CompanyStatistics(0, 0.0, 0.0);
        assertEquals(0, stats.getEmployeeCount(), "Powinno zwrócić 0 pracowników");
    }

    // Testuje zachowanie przy zerowej średniej pensji
    @Test
    void testZeroAverageSalary() {
        CompanyStatistics stats = new CompanyStatistics(0, 0.0, 0.0);
        assertEquals(0.0, stats.getAverageSalary(), "Powinna być średnia pensja 0.0");
    }

    // Testuje zachowanie przy zerowej maksymalnej pensji
    @Test
    void testZeroMaxSalary() {
        CompanyStatistics stats = new CompanyStatistics(0, 0.0, 0.0);
        assertEquals(0.0, stats.getMaxSalary(), "Powinna być maksymalna pensja 0.0");
    }

    // Testuje zachowanie przy ujemnej liczbie pracowników
    @Test
    void testNegativeEmployeeCount() {
        CompanyStatistics stats = new CompanyStatistics(-10, -3000.0, -5000.0);
        assertEquals(-10, stats.getEmployeeCount(), "Powinno zwrócić -10 pracowników");
    }

    // Testuje zachowanie przy ujemnej średniej pensji
    @Test
    void testNegativeAverageSalary() {
        CompanyStatistics stats = new CompanyStatistics(-10, -3000.0, -5000.0);
        assertEquals(-3000.0, stats.getAverageSalary(), "Powinna być średnia pensja -3000.0");
    }

    // Testuje zachowanie przy ujemnej maksymalnej pensji
    @Test
    void testNegativeMaxSalary() {
        CompanyStatistics stats = new CompanyStatistics(-10, -3000.0, -5000.0);
        assertEquals(-5000.0, stats.getMaxSalary(), "Powinna być maksymalna pensja -5000.0");
    }

    // Testuje zachowanie przy ekstremalnej liczbie pracowników
    @Test
    void testExtremeEmployeeCount() {
        CompanyStatistics stats = new CompanyStatistics(Long.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, stats.getEmployeeCount(), "Powinno zwrócić Long.MAX_VALUE");
    }

    // Testuje zachowanie przy ekstremalnej średniej pensji
    @Test
    void testExtremeAverageSalary() {
        CompanyStatistics stats = new CompanyStatistics(Long.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, stats.getAverageSalary(), "Powinna być średnia pensja Double.MAX_VALUE");
    }

    // Testuje zachowanie przy ekstremalnej maksymalnej pensji
    @Test
    void testExtremeMaxSalary() {
        CompanyStatistics stats = new CompanyStatistics(Long.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, stats.getMaxSalary(), "Powinna być maksymalna pensja Double.MAX_VALUE");
    }

    // Testuje zachowanie przy wartości NaN dla średniej pensji
    @Test
    void testAverageSalaryIsNaN() {
        CompanyStatistics stats = new CompanyStatistics(10, Double.NaN, Double.POSITIVE_INFINITY);
        assertTrue(Double.isNaN(stats.getAverageSalary()), "Średnia pensja powinna być NaN");
    }

    // Testuje zachowanie przy nieskończonej maksymalnej pensji
    @Test
    void testMaxSalaryIsInfinite() {
        CompanyStatistics stats = new CompanyStatistics(10, Double.NaN, Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(stats.getMaxSalary()), "Maksymalna pensja powinna być nieskończona");
    }
}
