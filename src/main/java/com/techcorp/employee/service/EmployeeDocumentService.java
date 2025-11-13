package com.techcorp.employee.service;

import com.techcorp.employee.exception.FileNotFoundException;
import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.EmployeeDocument;
import com.techcorp.employee.model.DocumentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class EmployeeDocumentService {

    private final FileStorageService fileStorageService;
    private final Map<String, List<EmployeeDocument>> employeeDocuments = new HashMap<>();

    public EmployeeDocumentService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public EmployeeDocument saveDocument(String employeeEmail, MultipartFile file, DocumentType type) {
        if (employeeEmail == null || employeeEmail.isBlank()) {
            throw new InvalidDataException("Email pracownika nie może być pusty");
        }

        // zapis pliku w podfolderze email
        String uniqueFileName = fileStorageService.saveFile(file, "documents/" + employeeEmail);

        String filePath = "documents/" + employeeEmail + "/" + uniqueFileName;
        EmployeeDocument doc = new EmployeeDocument(
                employeeEmail,
                uniqueFileName,
                file.getOriginalFilename(),
                type,
                filePath
        );

        employeeDocuments.computeIfAbsent(employeeEmail, k -> new ArrayList<>()).add(doc);
        return doc;
    }

    public List<EmployeeDocument> getDocuments(String employeeEmail) {
        return employeeDocuments.getOrDefault(employeeEmail, Collections.emptyList());
    }

    public EmployeeDocument getDocumentById(String employeeEmail, String documentId) {
        return getDocuments(employeeEmail).stream()
                .filter(d -> d.getId().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Dokument nie znaleziony: " + documentId));
    }

    public void deleteDocument(String employeeEmail, String documentId) {
        List<EmployeeDocument> docs = getDocuments(employeeEmail);
        EmployeeDocument doc = docs.stream()
                .filter(d -> d.getId().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Dokument nie znaleziony: " + documentId));

        fileStorageService.deleteFile("documents/" + employeeEmail, doc.getFileName());
        docs.remove(doc);
    }
}
