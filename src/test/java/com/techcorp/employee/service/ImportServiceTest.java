package com.techcorp.employee.service;

import com.techcorp.employee.model.EmailSet;
import com.techcorp.employee.model.ImportSummary;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

public class ImportServiceTest {

    private EmailSet emailSet;
    private EmployeeService employeeService;
    private ImportService importService;

    @BeforeEach
    void setUp() {
        emailSet = new EmailSet();
        employeeService = new EmployeeService(emailSet);
    }

    @AfterEach
    void tearDown() {
        emailSet.clear();
    }

    private Path createTempCsvFile(String content) throws Exception {
        Path tempFile = Files.createTempFile("test-import", ".csv");
        Files.writeString(tempFile, content, StandardOpenOption.WRITE);
        return tempFile;
    }

    @Test
    void testImportValidSingleEmployeeCountIsOne() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary
                Jan,Kowalski,jan@firma.com,Firma,Developer,8000
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertEquals(1, summary.getImportedCount(), "Zaimportowano niepoprawną liczbę pracowników.");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImportValidSingleEmployeeHasNoErrors() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary
                Jan,Kowalski,jan@firma.com,Firma,Developer,8000
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertTrue(summary.getErrors().isEmpty(), "Nie powinno być błędów.");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImportInvalidFieldCountAddsError() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary
                Jan,Kowalski,jan@firma.com,Firma,Developer
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertTrue(summary.getErrors().get(0).contains("niepoprawna liczba pól"), "Błąd związany z liczbą pól nie został wykryty.");
        assertEquals(0, summary.getImportedCount(), "Błąd w liczbie pól powinien uniemożliwić import.");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImportUnknownJobTitleAddsError() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary
                Jan,Kowalski,jan@firma.com,Firma,CEO,8000
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertTrue(summary.getErrors().get(0).contains("Nieznane stanowisko 'CEO'"), "Nieznane stanowisko 'CEO' nie zostało wykryte.");
        assertEquals(0, summary.getImportedCount(), "Błąd z nieznanym stanowiskiem powinien uniemożliwić import.");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImportFileNotFoundAddsError() {
        // Przekazujemy nieistniejący plik (np. 'nonexistent.csv')
        importService = new ImportService(employeeService, "nonexistent.csv");

        ImportSummary summary = importService.importFromCsv();

        // Sprawdzamy, czy w wynikach jest odpowiedni komunikat o błędzie
        assertTrue(summary.getErrors().get(0).startsWith("Nie znaleziono pliku CSV"));
    }


    @Test
    void testImportSkipsEmptyLine() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary

                Jan,Kowalski,jan@firma.com,Firma,Developer,8000
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertEquals(1, summary.getImportedCount(), "Pusta linia nie powinna wpłynąć na liczbę zaimportowanych pracowników.");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImportSkipsHeaderLine() throws Exception {
        String csv = """
                firstName,lastName,email,company,position,salary
                """;

        Path tempFile = createTempCsvFile(csv);
        importService = new ImportService(employeeService, tempFile.toString());  // Inicjalizacja wewnątrz testu

        ImportSummary summary = importService.importFromCsv();
        assertEquals(0, summary.getImportedCount(), "Linia nagłówka nie powinna być zaimportowana.");

        Files.deleteIfExists(tempFile);
    }
}
