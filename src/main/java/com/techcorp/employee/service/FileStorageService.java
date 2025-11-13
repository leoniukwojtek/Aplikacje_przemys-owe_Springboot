package com.techcorp.employee.service;

import com.techcorp.employee.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final Path reportsPath;

    public FileStorageService(
            @Value("${app.upload.directory}") String uploadDir,
            @Value("${app.reports.directory}") String reportsDir) {

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.reportsPath = Paths.get(reportsDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadPath);
            Files.createDirectories(this.reportsPath);
        } catch (IOException e) {
            throw new FileStorageException("Nie można utworzyć katalogów do zapisu plików.", e);
        }
    }

    public String saveFile(MultipartFile file, String subfolder) {
        validateFile(file);

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String uniqueName = UUID.randomUUID() + "." + extension;

        Path targetDir = uploadPath.resolve(subfolder).normalize();
        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(),
                    targetDir.resolve(uniqueName),
                    StandardCopyOption.REPLACE_EXISTING);
            return uniqueName;
        } catch (IOException e) {
            throw new FileStorageException("Błąd zapisu pliku: " + originalName, e);
        }
    }

    public Resource loadFile(String subfolder, String filename) {
        try {
            Path path = uploadPath.resolve(subfolder).resolve(filename).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new FileNotFoundException("Plik nie istnieje: " + filename);
            return resource;
        } catch (Exception e) {
            throw new FileNotFoundException("Nie można odczytać pliku: " + filename);
        }
    }

    public void deleteFile(String subfolder, String filename) {
        try {
            Files.deleteIfExists(uploadPath.resolve(subfolder).resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("Nie udało się usunąć pliku: " + filename, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Plik jest pusty!");
        }

        // --- Walidacja rozszerzenia ---
        String filename = file.getOriginalFilename();
        String extension = getExtension(filename).toLowerCase();

        String[] allowedExtensions = {"csv", "pdf", "txt", "jpg", "jpeg", "png", "gif"};
        boolean allowed = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(extension)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new InvalidFileException("Nieobsługiwany typ pliku: " + extension);
        }

        // --- Walidacja rozmiaru pliku ---
        long maxSizeBytes = 2 * 1024 * 1024; // 2 MB
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidFileException("Plik jest za duży. Maksymalny rozmiar to 2 MB.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
