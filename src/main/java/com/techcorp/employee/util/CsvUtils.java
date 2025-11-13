package com.techcorp.employee.util;

import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.exception.InvalidDataException;
import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static List<Employee> parseCsv(MultipartFile file) throws InvalidDataException {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) { // pomijamy nagłówek
                String[] row = rows.get(i);
                if (row.length < 5) continue; // minimalna liczba pól

                double salary = 0.0;
                if (row.length > 5 && !row[5].isBlank()) {
                    try {
                        salary = Double.parseDouble(row[5].trim());
                    } catch (NumberFormatException ex) {
                        salary = 0.0;
                    }
                }

                Employee e = new Employee(
                        row[0].trim(), // firstName
                        row[1].trim(), // lastName
                        row[2].trim(), // emailAddress
                        row[3].trim(), // companyName
                        row[4].trim(), // jobTitle
                        salary         // salary
                );

                // ustawienie statusu jeśli podany
                if (row.length > 6 && !row[6].isBlank()) {
                    try {
                        e.setStatus(EmploymentStatus.valueOf(row[6].trim().toUpperCase()));
                    } catch (IllegalArgumentException ex) {
                        e.setStatus(EmploymentStatus.ACTIVE);
                    }
                } else {
                    e.setStatus(EmploymentStatus.ACTIVE);
                }

                employees.add(e);
            }
        } catch (Exception ex) {
            throw new InvalidDataException("Błąd parsowania CSV: " + ex.getMessage());
        }
        return employees;
    }
}
