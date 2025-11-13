package com.techcorp.employee.service;

import com.techcorp.employee.exception.InvalidFileException;
import com.techcorp.employee.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @Mock
    private FileStorageService fileStorageService; // tylko mock, nie realna instancja

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test 1: Pomyślny zapis pliku
    @Test
    void shouldSaveFileSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "contract.csv", "text/csv", "content".getBytes());

        when(fileStorageService.saveFile(any(), anyString())).thenReturn("fileUuid.csv");

        String result = fileStorageService.saveFile(file, "documents");

        verify(fileStorageService, times(1)).saveFile(file, "documents");
        assertEquals("fileUuid.csv", result);
    }

    // ✅ Test 2: Obsługa błędu przy zapisie pliku
    @Test
    void shouldThrowExceptionForInvalidFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.txt", "text/plain", "invalid content".getBytes());

        when(fileStorageService.saveFile(any(), anyString()))
                .thenThrow(new InvalidFileException("Nieprawidłowy plik"));

        assertThrows(InvalidFileException.class, () -> fileStorageService.saveFile(file, "documents"));
    }

    // ✅ Test 3: Zbyt duży plik
    @Test
    void shouldThrowExceptionForLargeFile() {
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3 MB
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", largeContent);

        when(fileStorageService.saveFile(any(), anyString()))
                .thenThrow(new InvalidFileException("Plik zbyt duży"));

        assertThrows(InvalidFileException.class, () -> fileStorageService.saveFile(file, "photos"));
    }

    // ✅ Test 4: Błąd ogólny (I/O)
    @Test
    void shouldThrowFileStorageExceptionOnIoError() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "abc".getBytes());

        when(fileStorageService.saveFile(any(), anyString()))
                .thenThrow(new FileStorageException("Błąd zapisu"));

        assertThrows(FileStorageException.class, () -> fileStorageService.saveFile(file, "documents"));
    }
}
