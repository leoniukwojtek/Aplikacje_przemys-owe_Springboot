package com.techcorp.employee.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmployeeDocument {
    private final String id;
    private final String employeeEmail;
    private final String fileName;
    private final String originalFileName;
    private final DocumentType fileType;
    private final LocalDateTime uploadDate;
    private final String filePath;

    public EmployeeDocument(String employeeEmail, String fileName, String originalFileName, DocumentType type, String filePath) {
        this.id = UUID.randomUUID().toString();
        this.employeeEmail = employeeEmail;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileType = type;
        this.uploadDate = LocalDateTime.now();
        this.filePath = filePath;
    }

    public String getId() { return id; }
    public String getEmployeeEmail() { return employeeEmail; }
    public String getFileName() { return fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public DocumentType getFileType() { return fileType; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public String getFilePath() { return filePath; }
}
