package com.techcorp.employee.service;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.techcorp.employee.model.JobTitle;


public class JobTitleTest {

    // Sprawdza, czy getDisplayName() zwraca "President"
    @Test
    void testPresidentDisplayName() {
        assertEquals("President", JobTitle.PRESIDENT.getDisplayName());
    }

    // Sprawdza, czy getDisplayName() zwraca "Vice President"
    @Test
    void testVicePresidentDisplayName() {
        assertEquals("Vice President", JobTitle.VICE_PRESIDENT.getDisplayName());
    }

    // Sprawdza, czy getDisplayName() zwraca "Manager"
    @Test
    void testManagerDisplayName() {
        assertEquals("Manager", JobTitle.MANAGER.getDisplayName());
    }

    // Sprawdza, czy getDisplayName() zwraca "Developer"
    @Test
    void testDeveloperDisplayName() {
        assertEquals("Developer", JobTitle.DEVELOPER.getDisplayName());
    }

    // Sprawdza, czy getDisplayName() zwraca "Intern"
    @Test
    void testInternDisplayName() {
        assertEquals("Intern", JobTitle.INTERN.getDisplayName());
    }

    // Sprawdza, czy getBaseSalary() zwraca 25000 dla PRESIDENT
    @Test
    void testPresidentBaseSalary() {
        assertEquals(25000, JobTitle.PRESIDENT.getBaseSalary());
    }

    // Sprawdza, czy getBaseSalary() zwraca 18000 dla VICE_PRESIDENT
    @Test
    void testVicePresidentBaseSalary() {
        assertEquals(18000, JobTitle.VICE_PRESIDENT.getBaseSalary());
    }

    // Sprawdza, czy getBaseSalary() zwraca 12000 dla MANAGER
    @Test
    void testManagerBaseSalary() {
        assertEquals(12000, JobTitle.MANAGER.getBaseSalary());
    }

    // Sprawdza, czy getBaseSalary() zwraca 8000 dla DEVELOPER
    @Test
    void testDeveloperBaseSalary() {
        assertEquals(8000, JobTitle.DEVELOPER.getBaseSalary());
    }

    // Sprawdza, czy getBaseSalary() zwraca 3000 dla INTERN
    @Test
    void testInternBaseSalary() {
        assertEquals(3000, JobTitle.INTERN.getBaseSalary());
    }

    // Sprawdza, czy getHierarchyLevel() zwraca 1 dla PRESIDENT
    @Test
    void testPresidentHierarchyLevel() {
        assertEquals(1, JobTitle.PRESIDENT.getHierarchyLevel());
    }

    // Sprawdza, czy getHierarchyLevel() zwraca 2 dla VICE_PRESIDENT
    @Test
    void testVicePresidentHierarchyLevel() {
        assertEquals(2, JobTitle.VICE_PRESIDENT.getHierarchyLevel());
    }

    // Sprawdza, czy getHierarchyLevel() zwraca 3 dla MANAGER
    @Test
    void testManagerHierarchyLevel() {
        assertEquals(3, JobTitle.MANAGER.getHierarchyLevel());
    }

    // Sprawdza, czy getHierarchyLevel() zwraca 4 dla DEVELOPER
    @Test
    void testDeveloperHierarchyLevel() {
        assertEquals(4, JobTitle.DEVELOPER.getHierarchyLevel());
    }

    // Sprawdza, czy getHierarchyLevel() zwraca 5 dla INTERN
    @Test
    void testInternHierarchyLevel() {
        assertEquals(5, JobTitle.INTERN.getHierarchyLevel());
    }

    // Sprawdza, czy toString() zwraca "President"
    @Test
    void testPresidentToString() {
        assertEquals("President", JobTitle.PRESIDENT.toString());
    }

    // Sprawdza, czy toString() zwraca "Vice President"
    @Test
    void testVicePresidentToString() {
        assertEquals("Vice President", JobTitle.VICE_PRESIDENT.toString());
    }

    // Sprawdza, czy toString() zwraca "Manager"
    @Test
    void testManagerToString() {
        assertEquals("Manager", JobTitle.MANAGER.toString());
    }

    // Sprawdza, czy toString() zwraca "Developer"
    @Test
    void testDeveloperToString() {
        assertEquals("Developer", JobTitle.DEVELOPER.toString());
    }

    // Sprawdza, czy toString() zwraca "Intern"
    @Test
    void testInternToString() {
        assertEquals("Intern", JobTitle.INTERN.toString());
    }
}
