package com.techcorp.employee.service;

import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.JobTitle;
import com.techcorp.employee.model.ImportSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    // -------------------- Istniejący import z resources --------------------
    public ImportSummary importFromCsv() {
        return importFromCsvFilePath(null); // null -> użyj domyślnej ścieżki z resources
    }

    // -------------------- Nowa metoda importu z dowolnej ścieżki --------------------
    public ImportSummary importFromCsv(String filePath) {
        return importFromCsvFilePath(filePath);
    }

    private ImportSummary importFromCsvFilePath(String filePath) {
        List<String> errors = new ArrayList<>();
        List<Employee> importedEmployees = new ArrayList<>();
        int importedCount = 0;

        try (BufferedReader reader = filePath == null ?
                new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(csvPath))) :
                new BufferedReader(new FileReader(filePath))) {

            if (reader == null) {
                String msg = "Nie znaleziono pliku CSV: " + (filePath != null ? filePath : csvPath);
                logger.error(msg);
                errors.add(msg);
                return new ImportSummary(importedCount, errors, importedEmployees);
            }

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (lineNumber == 1 || line.trim().isEmpty()) continue; // nagłówek lub puste linie

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
                    if (positionStr.isBlank()) throw new InvalidDataException("Stanowisko nie może być puste.");
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
                    importedEmployees.add(emp); // <-- dodanie do listy
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

        logger.info("Import CSV zakończony. Zaimportowano: {} pracowników, błędów: {}", importedCount, errors.size());
        return new ImportSummary(importedCount, errors, importedEmployees);
    }

    // -------------------- Import XML --------------------
    public ImportSummary importFromXml(String filePath) {
        List<String> errors = new ArrayList<>();
        List<Employee> importedEmployees = new ArrayList<>();
        int importedCount = 0;

        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            String msg = "Nie znaleziono pliku XML: " + filePath;
            logger.error(msg);
            errors.add(msg);
            return new ImportSummary(importedCount, errors, importedEmployees);
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList employeeNodes = doc.getElementsByTagName("bean");
            for (int i = 0; i < employeeNodes.getLength(); i++) {
                Node beanNode = employeeNodes.item(i);

                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;

                    if (!"com.techcorp.employee.model.Employee".equals(beanElement.getAttribute("class"))) {
                        continue; // pomiń inne beany
                    }

                    NodeList args = beanElement.getElementsByTagName("constructor-arg");
                    if (args.getLength() != 6) {
                        errors.add("Bean " + beanElement.getAttribute("id") + ": niepoprawna liczba argumentów (" + args.getLength() + ")");
                        continue;
                    }

                    try {
                        String firstName = getArgValue(args.item(0));
                        String lastName = getArgValue(args.item(1));
                        String email = getArgValue(args.item(2));
                        String company = getArgValue(args.item(3));
                        String positionStr = getArgValue(args.item(4));
                        double salary = Double.parseDouble(getArgValue(args.item(5)));

                        JobTitle jobTitle = Arrays.stream(JobTitle.values())
                                .filter(j -> j.getDisplayName().equalsIgnoreCase(positionStr))
                                .findFirst()
                                .orElseThrow(() -> new InvalidDataException("Nieznane stanowisko '" + positionStr + "'"));

                        Employee emp = new Employee(firstName, lastName, email, company, jobTitle.getDisplayName(), salary);
                        employeeService.addEmployee(emp);
                        importedEmployees.add(emp); // <-- dodanie do listy
                        importedCount++;
                        logger.info("Zaimportowano pracownika z XML: {} {} ({})", firstName, lastName, email);

                    } catch (Exception e) {
                        errors.add("Bean " + beanElement.getAttribute("id") + ": błąd - " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            String msg = "Błąd odczytu lub parsowania pliku XML: " + e.getMessage();
            logger.error(msg);
            errors.add(msg);
        }

        logger.info("Import XML zakończony. Zaimportowano: {} pracowników, błędów: {}", importedCount, errors.size());
        return new ImportSummary(importedCount, errors, importedEmployees);
    }

    // -------------------- Pomocnicza metoda --------------------
    private String getArgValue(Node constructorArgNode) {
        if (constructorArgNode.getNodeType() != Node.ELEMENT_NODE) return "";
        Element elem = (Element) constructorArgNode;
        return elem.getAttribute("value").trim();
    }
}
