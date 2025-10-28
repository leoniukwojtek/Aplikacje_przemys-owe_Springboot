package com.techcorp.employee.service;

import com.techcorp.employee.model.EmailSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailSetTest {

    @Autowired
    private EmailSet emailSet;

    @BeforeEach
    void setUp() {
        emailSet.clear(); // czyścimy przed każdym testem, by testy były niezależne
    }

    @AfterEach
    void tearDown() {
        emailSet.clear(); // czyścimy po każdym teście
    }

    // Dodawanie nowego emaila
    @Test
    void testAddEmailReturnsTrueIfNew() {
        boolean added = emailSet.addEmail("test@example.com");
        assertTrue(added, "Dodanie nowego emaila powinno zwrócić true");
    }

    @Test
    void testAddEmailIncreasesSize() {
        emailSet.addEmail("test@example.com");
        assertEquals(1, emailSet.size(), "Rozmiar zbioru powinien wynosić 1");
    }

    // Dodanie duplikatu
    @Test
    void testAddEmailReturnsFalseIfDuplicate() {
        emailSet.addEmail("test@example.com");
        boolean secondAdd = emailSet.addEmail("TEST@example.com");
        assertFalse(secondAdd, "Dodanie duplikatu powinno zwrócić false");
    }

    @Test
    void testDuplicateEmailDoesNotIncreaseSize() {
        emailSet.addEmail("test@example.com");
        emailSet.addEmail("TEST@example.com");
        assertEquals(1, emailSet.size(), "Rozmiar zbioru powinien pozostać 1");
    }

    // Usuwanie istniejącego emaila
    @Test
    void testRemoveEmailReturnsTrueIfExists() {
        emailSet.addEmail("remove@example.com");
        boolean removed = emailSet.removeEmail("REMOVE@example.com");
        assertTrue(removed, "Usunięcie istniejącego emaila powinno zwrócić true");
    }

    @Test
    void testRemoveEmailDecreasesSize() {
        emailSet.addEmail("remove@example.com");
        emailSet.removeEmail("REMOVE@example.com");
        assertEquals(0, emailSet.size(), "Rozmiar zbioru powinien wynosić 0 po usunięciu");
    }

    // Usuwanie nieistniejącego emaila
    @Test
    void testRemoveEmailReturnsFalseIfNotExists() {
        assertFalse(emailSet.removeEmail("nonexistent@example.com"), "Usunięcie nieistniejącego emaila powinno zwrócić false");
    }

    // Sprawdzanie obecności emaila (case insensitive)
    @Test
    void testContainsEmailLowercase() {
        emailSet.addEmail("Check@Example.com");
        assertTrue(emailSet.containsEmail("check@example.com"), "Email powinien być rozpoznawany niezależnie od wielkości liter");
    }

    @Test
    void testContainsEmailUppercase() {
        emailSet.addEmail("Check@Example.com");
        assertTrue(emailSet.containsEmail("CHECK@example.com"), "Email powinien być rozpoznawany niezależnie od wielkości liter");
    }

    // Sprawdzanie nieobecności emaila
    @Test
    void testContainsEmailReturnsFalseIfNotPresent() {
        assertFalse(emailSet.containsEmail("absent@example.com"), "Email nie powinien być obecny w zbiorze");
    }

    // Rozmiar zbioru
    @Test
    void testSizeIsZeroInitially() {
        assertEquals(0, emailSet.size(), "Rozmiar początkowy powinien wynosić 0");
    }

    @Test
    void testSizeAfterAddingTwoEmails() {
        emailSet.addEmail("a@example.com");
        emailSet.addEmail("b@example.com");
        assertEquals(2, emailSet.size(), "Rozmiar powinien wynosić 2 po dodaniu dwóch emaili");
    }

    @Test
    void testSizeAfterRemovingOneEmail() {
        emailSet.addEmail("a@example.com");
        emailSet.addEmail("b@example.com");
        emailSet.removeEmail("a@example.com");
        assertEquals(1, emailSet.size(), "Rozmiar powinien wynosić 1 po usunięciu jednego emaila");
    }

    // toString zawiera email
    @Test
    void testToStringContainsEmail() {
        emailSet.addEmail("string@example.com");
        String str = emailSet.toString();
        assertTrue(str.contains("string@example.com"), "toString powinno zawierać dodany email");
    }

    // Czyszczenie zbioru
    @Test
    void testClearDoesNotLeaveSizeZeroBeforeClear() {
        emailSet.addEmail("clear@example.com");
        assertNotEquals(0, emailSet.size(), "Rozmiar powinien być większy niż 0 przed czyszczeniem");
    }

    @Test
    void testClearEmptiesSet() {
        emailSet.addEmail("clear@example.com");
        emailSet.clear();
        assertEquals(0, emailSet.size(), "Rozmiar powinien wynosić 0 po czyszczeniu");
    }
}
