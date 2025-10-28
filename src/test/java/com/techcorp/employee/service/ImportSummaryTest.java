package com.techcorp.employee.service;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.techcorp.employee.model.ImportSummary;


public class ImportSummaryTest {

    // Test sprawdza, czy liczba zaimportowanych pracowników jest poprawnie ustawiana
    @Test
    void testImportedCountIsCorrect() {
        ImportSummary summary = new ImportSummary(5, Collections.emptyList());
        assertEquals(5, summary.getImportedCount(), "Powinno zwrócić liczbę 5 zaimportowanych pracowników");
    }

    // Test sprawdza, czy lista błędów jest poprawnie ustawiana
    @Test
    void testErrorListIsCorrect() {
        List<String> errors = List.of("Linia 3: nieznane stanowisko", "Linia 7: brak emaila");
        ImportSummary summary = new ImportSummary(2, errors);
        assertEquals(errors, summary.getErrors(), "Lista błędów powinna być zgodna z przekazaną");
    }

    // Test sprawdza, czy lista błędów może być pusta
    @Test
    void testEmptyErrorList() {
        ImportSummary summary = new ImportSummary(10, Collections.emptyList());
        assertTrue(summary.getErrors().isEmpty(), "Lista błędów powinna być pusta");
    }

    // Test sprawdza, czy liczba zaimportowanych może być zero
    @Test
    void testZeroImportedCount() {
        ImportSummary summary = new ImportSummary(0, List.of("Linia 2: brak danych"));
        assertEquals(0, summary.getImportedCount(), "Powinno zwrócić 0 zaimportowanych pracowników");
    }

    // Test sprawdza, czy lista błędów zawiera poprawną liczbę wpisów
    @Test
    void testErrorListSize() {
        List<String> errors = List.of("Błąd 1", "Błąd 2", "Błąd 3");
        ImportSummary summary = new ImportSummary(1, errors);
        assertEquals(3, summary.getErrors().size(), "Lista błędów powinna zawierać 3 wpisy");
    }
}
