package com.techcorp.employee.service;

import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.JobTitle;
import com.techcorp.employee.model.ImportSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final EmployeeService employeeService;
    private final String csvPath;

    public ImportService(EmployeeService employeeService,
                         @Value("${app.import.csv-file}") String csvPath) {
        this.employeeService = employeeService;
        this.csvPath = csvPath;
    }


    /**
     * Importuje pracowników z pliku CSV i zwraca podsumowanie operacji.
     */
    public ImportSummary importFromCsv() {
        List<String> errors = new ArrayList<>();
        int importedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(csvPath)))) {

            if (reader == null) {
                String msg = "Nie znaleziono pliku CSV: " + csvPath;
                logger.error(msg);
                errors.add(msg);
                return new ImportSummary(importedCount, errors);
            }

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Pomijamy nagłówek lub puste linie
                if (lineNumber == 1 || line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 6) {
                    errors.add("Linia " + lineNumber + ": niepoprawna liczba pól (" + parts.length + ")");
                    continue;
                }

                String firstName = parts[0].trim();
                String lastName = parts[1].trim();
                String email = parts[2].trim();
                String company = parts[3].trim();
                String positionStr = parts[4].trim();
                String salaryStr = parts[5].trim();

                // Walidacja stanowiska
                JobTitle jobTitle;
                try {
                    if (positionStr.isBlank()) {
                        throw new InvalidDataException("Stanowisko nie może być puste.");
                    }
                    jobTitle = Arrays.stream(JobTitle.values())
                            .filter(j -> j.getDisplayName().equalsIgnoreCase(positionStr))
                            .findFirst()
                            .orElseThrow(() -> new InvalidDataException("Nieznane stanowisko '" + positionStr + "'"));
                } catch (InvalidDataException e) {
                    errors.add("Linia " + lineNumber + ": błąd danych - " + e.getMessage());
                    continue;
                }

                // Walidacja pensji
                double salary;
                try {
                    salary = Double.parseDouble(salaryStr);
                } catch (NumberFormatException e) {
                    errors.add("Linia " + lineNumber + ": niepoprawna wartość pensji '" + salaryStr + "'");
                    continue;
                }

                // Tworzenie pracownika i dodanie do listy
                try {
                    Employee emp = new Employee(firstName, lastName, email, company, jobTitle.getDisplayName(), salary);
                    employeeService.addEmployee(emp);
                    importedCount++;
                    logger.info("Zaimportowano pracownika: {} {} ({})", firstName, lastName, email);
                } catch (InvalidDataException e) {
                    errors.add("Linia " + lineNumber + ": błąd danych - " + e.getMessage());
                } catch (Exception e) {
                    errors.add("Linia " + lineNumber + ": nieoczekiwany błąd - " + e.getMessage());
                }
            }

        } catch (IOException e) {
            String msg = "Błąd odczytu pliku CSV: " + e.getMessage();
            logger.error(msg);
            errors.add(msg);
        }

        logger.info("Import zakończony. Zaimportowano: {} pracowników, błędów: {}", importedCount, errors.size());
        return new ImportSummary(importedCount, errors);
    }
}
