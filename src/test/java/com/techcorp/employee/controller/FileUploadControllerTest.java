package com.techcorp.employee.controller;

import com.techcorp.employee.exception.FileNotFoundException;
import com.techcorp.employee.model.DocumentType;
import com.techcorp.employee.model.EmployeeDocument;
import com.techcorp.employee.service.FileStorageService;
import com.techcorp.employee.service.ImportService;
import com.techcorp.employee.service.ReportGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private ImportService importService;

    @MockBean
    private ReportGeneratorService reportGeneratorService;

    // Test 1: Upload pliku CSV
    @Test
    void shouldUploadEmployeeDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "contract.csv", "text/csv",
                "id,name,position\n1,Jan Kowalski,Developer".getBytes());

        when(fileStorageService.saveFile(any(), anyString())).thenReturn("uuid.csv");

        mockMvc.perform(multipart("/api/files/documents/jan@example.com")
                        .file(file)
                        .param("type", "CONTRACT"))
                .andExpect(status().isCreated());
    }

    // Test 2: Upload zbyt dużego pliku
    @Test
    void shouldReturn413ForLargeFile() throws Exception {
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3 MB
        MockMultipartFile file = new MockMultipartFile("file", "large_file.png", "image/png", largeContent);

        mockMvc.perform(multipart("/api/files/photos/jan@example.com")
                        .file(file))
                .andExpect(status().isPayloadTooLarge());
    }

    // Test 3: Upload pliku z nieprawidłowym rozszerzeniem
    @Test
    void shouldReturn400ForInvalidFileExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "invalid_file.txt", "text/plain", "invalid content".getBytes());

        mockMvc.perform(multipart("/api/files/documents/jan@example.com")
                        .file(file)
                        .param("type", "CONTRACT"))
                .andExpect(status().isBadRequest());
    }

    // Test 4: Pobieranie dokumentu
    @Test
    void shouldDownloadEmployeeDocument() throws Exception {
        String documentId = "uuid.pdf";
        String content = "file content";
        ByteArrayResource resource = new ByteArrayResource(content.getBytes());

        when(fileStorageService.loadFile(anyString(), anyString())).thenReturn(resource);

        mockMvc.perform(get("/api/files/documents/jan@example.com/{documentId}", documentId))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content.getBytes()));
    }

    // Test 5: Pobieranie zdjęcia pracownika
    @Test
    void shouldDownloadEmployeePhoto() throws Exception {
        String employeeEmail = "jan@example.com";
        byte[] photoContent = new byte[]{1, 2, 3};
        ByteArrayResource resource = new ByteArrayResource(photoContent);

        when(fileStorageService.loadFile("photos", employeeEmail)).thenReturn(resource);

        mockMvc.perform(get("/api/files/photos/{email}", employeeEmail))
                .andExpect(status().isOk())
                .andExpect(content().bytes(photoContent));
    }

    // Test 6: Pobieranie zdjęcia, gdy brak zdjęcia
    @Test
    void shouldReturn404WhenNoPhotoExists() throws Exception {
        String employeeEmail = "jan@example.com";
        when(fileStorageService.loadFile("photos", employeeEmail)).thenThrow(new FileNotFoundException("Plik nie istnieje"));

        mockMvc.perform(get("/api/files/photos/{email}", employeeEmail))
                .andExpect(status().isNotFound());
    }

    // Test 7: Upload dokumentu pracownika
    @Test
    void shouldUploadEmployeeDocumentAndReturnMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "contract.pdf", "application/pdf", "content".getBytes());

        EmployeeDocument document = new EmployeeDocument("jan@example.com", "contract.pdf", "contract.pdf",
                DocumentType.CONTRACT, "uploads/documents/jan@example.com/contract.pdf");

        when(fileStorageService.saveFile(any(), anyString())).thenReturn("contract.pdf");

        mockMvc.perform(multipart("/api/files/documents/jan@example.com")
                        .file(file)
                        .param("type", "CONTRACT"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeEmail").value("jan@example.com"))
                .andExpect(jsonPath("$.fileName").value("contract.pdf"))
                .andExpect(jsonPath("$.fileType").value("CONTRACT"));
    }
}
