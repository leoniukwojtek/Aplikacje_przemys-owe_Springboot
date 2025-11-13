package com.techcorp.employee.controller;

import com.techcorp.employee.exception.InvalidFileException;
import com.techcorp.employee.model.DocumentType;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmployeeDocument;
import com.techcorp.employee.model.ImportSummary;
import com.techcorp.employee.service.FileStorageService;
import com.techcorp.employee.service.ImportService;
import com.techcorp.employee.service.ReportGeneratorService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final ImportService importService;
    private final ReportGeneratorService reportGeneratorService;

    // W testach możemy mockować tę mapę
    private final Map<String, List<EmployeeDocument>> documents = new HashMap<>();
    private final Map<String, List<Employee>> employeesByCompany = new HashMap<>();

    public FileUploadController(FileStorageService fileStorageService,
                                ImportService importService,
                                ReportGeneratorService reportGeneratorService) {
        this.fileStorageService = fileStorageService;
        this.importService = importService;
        this.reportGeneratorService = reportGeneratorService;
    }

    // -------------------- Dokumenty pracowników --------------------
    @PostMapping("/documents/{email}")
    public ResponseEntity<EmployeeDocument> uploadDocument(
            @PathVariable String email,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentType type) {

        if (!employeeExists(email)) {
            return ResponseEntity.status(404).body(null);
        }

        String savedFile = fileStorageService.saveFile(file, "documents/" + email);
        EmployeeDocument doc = new EmployeeDocument(
                email, savedFile, file.getOriginalFilename(), type, "documents/" + email + "/" + savedFile
        );

        documents.computeIfAbsent(email, e -> new ArrayList<>()).add(doc);
        return ResponseEntity.status(201).body(doc);
    }

    @GetMapping("/documents/{email}")
    public ResponseEntity<List<EmployeeDocument>> listDocuments(@PathVariable String email) {
        if (!employeeExists(email)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(documents.getOrDefault(email, Collections.emptyList()));
    }

    @GetMapping("/documents/{email}/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String email, @PathVariable String id) throws IOException {
        if (!employeeExists(email)) {
            return ResponseEntity.notFound().build();
        }

        List<EmployeeDocument> list = documents.get(email);
        if (list == null) throw new InvalidFileException("Brak dokumentów dla użytkownika");

        EmployeeDocument doc = list.stream().filter(d -> d.getId().equals(id)).findFirst()
                .orElseThrow(() -> new InvalidFileException("Nie znaleziono dokumentu"));

        Resource resource = fileStorageService.loadFile("documents/" + email, doc.getFileName());
        String contentType = Files.probeContentType(resource.getFile().toPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/documents/{email}/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String email, @PathVariable String id) {
        if (!employeeExists(email)) {
            return ResponseEntity.notFound().build();
        }

        List<EmployeeDocument> list = documents.get(email);
        if (list == null) return ResponseEntity.notFound().build();

        Optional<EmployeeDocument> docOpt = list.stream().filter(d -> d.getId().equals(id)).findFirst();
        if (docOpt.isEmpty()) return ResponseEntity.notFound().build();

        EmployeeDocument doc = docOpt.get();
        fileStorageService.deleteFile("documents/" + email, doc.getFileName());
        list.remove(doc);

        return ResponseEntity.noContent().build();
    }

    // -------------------- Import CSV --------------------
    @PostMapping("/import/csv")
    public ResponseEntity<ImportSummary> importCsv(@RequestParam("file") MultipartFile file) {
        validateImportFile(file, "csv");
        String savedFile = fileStorageService.saveFile(file, "uploads");

        ImportSummary summary = importService.importFromCsv(savedFile);

        summary.getImportedEmployees().forEach(emp ->
                employeesByCompany.computeIfAbsent(emp.getCompanyName().toLowerCase(), k -> new ArrayList<>()).add(emp)
        );

        return ResponseEntity.ok(summary);
    }

    // -------------------- Import XML --------------------
    @PostMapping("/import/xml")
    public ResponseEntity<ImportSummary> importXml(@RequestParam("file") MultipartFile file) {
        validateImportFile(file, "xml");
        String savedFile = fileStorageService.saveFile(file, "uploads");

        ImportSummary summary = importService.importFromXml(savedFile);

        summary.getImportedEmployees().forEach(emp ->
                employeesByCompany.computeIfAbsent(emp.getCompanyName().toLowerCase(), k -> new ArrayList<>()).add(emp)
        );

        return ResponseEntity.ok(summary);
    }

    // -------------------- Eksport CSV --------------------
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportCsv(@RequestParam(value = "company", required = false) String company) {
        List<Employee> employees;
        if (company != null && !company.isBlank()) {
            employees = employeesByCompany.getOrDefault(company.toLowerCase(), Collections.emptyList());
        } else {
            employees = employeesByCompany.values().stream().flatMap(List::stream).toList();
        }

        Resource file = (company != null && !company.isBlank())
                ? reportGeneratorService.generateCsv(employees, company)
                : reportGeneratorService.generateCsv(employees);

        String filename = (company != null && !company.isBlank())
                ? "employees_" + company + ".csv"
                : "employees.csv";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    // -------------------- Eksport PDF --------------------
    @GetMapping("/reports/statistics/{companyName}")
    public ResponseEntity<Resource> exportPdf(@PathVariable String companyName) {
        List<Employee> employees = employeesByCompany.getOrDefault(companyName.toLowerCase(), Collections.emptyList());
        if (employees.isEmpty()) {
            throw new InvalidFileException("Brak danych dla firmy: " + companyName);
        }

        Resource file = reportGeneratorService.generatePdfStatistics(employees, companyName);
        String filename = "statistics_" + companyName + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    // -------------------- Walidacja plików importu --------------------
    private void validateImportFile(MultipartFile file, String expectedExtension) {
        if (file.isEmpty()) throw new InvalidFileException("Plik jest pusty!");
        String ext = getExtension(file.getOriginalFilename());
        if (!ext.equalsIgnoreCase(expectedExtension)) {
            throw new InvalidFileException("Niepoprawne rozszerzenie pliku. Oczekiwano: " + expectedExtension);
        }
        long maxSize = 10 * 1024 * 1024; // 10 MB
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("Plik jest za duży. Maksymalny rozmiar to 10 MB.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    // -------------------- WALIDACJA PRACOWNIKA --------------------
    private boolean employeeExists(String email) {
        // Ułatwienie dla testów – można mockować employeesByCompany
        return employeesByCompany.values().stream()
                .flatMap(List::stream)
                .anyMatch(emp -> emp.getEmailAddress().equalsIgnoreCase(email));
    }
}
