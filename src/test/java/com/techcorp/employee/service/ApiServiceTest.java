package com.techcorp.employee.service;

import com.google.gson.Gson;
import com.techcorp.employee.exception.ApiException;
import com.techcorp.employee.model.EmailSet;
import com.techcorp.employee.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ApiServiceTest {

    @Mock
    private HttpClient httpClient; // Mockujemy HttpClient

    @Mock
    private Gson gson; // Mockujemy Gson

    @InjectMocks
    private ApiService apiService; // Wstrzykujemy ApiService z mockami

    private final String testApiUrl = "https://jsonplaceholder.typicode.com/users";

    @Autowired
    private EmailSet emailSet;

    @BeforeEach
    void clearEmailSet() {
        // Clear the EmailSet before each test
        emailSet.clear();
    }

    // Test sprawdza, czy metoda zwraca niepustą listę pracowników
    @Test
    void testFetchEmployeesReturnsNonEmptyList() throws ApiException, IOException, InterruptedException {
        // Przygotowanie mocka odpowiedzi
        String jsonResponse = "[{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"company\": { \"name\": \"TechCorp\" } }]";
        HttpResponse<String> mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(jsonResponse);
        when(mockedResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockedResponse);

        // Wywołanie metody
        List<Employee> employees = apiService.fetchEmployeesFromApi();

        // Assercja, że lista pracowników nie jest pusta
        assertFalse(employees.isEmpty(), "Lista pracowników nie powinna być pusta");
    }

    // Test sprawdza, czy pierwszy pracownik ma poprawnie ustawione imię
    @Test
    void testFirstEmployeeHasFirstName() throws ApiException, IOException, InterruptedException {
        // Przygotowanie mocka odpowiedzi
        String jsonResponse = "[{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"company\": { \"name\": \"TechCorp\" } }]";
        HttpResponse<String> mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(jsonResponse);
        when(mockedResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockedResponse);

        // Wywołanie metody
        List<Employee> employees = apiService.fetchEmployeesFromApi();

        // Assercja, że imię pierwszego pracownika jest ustawione
        assertNotNull(employees.get(0).getFirstName(), "Imię nie powinno być nullem");
    }

    // Test sprawdza, czy metoda rzuca wyjątek przy błędnym URL
    @Test
    void testFetchEmployeesThrowsExceptionOnInvalidUrl() {
        String invalidUrl = "https://invalid.url";
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi();
        }, "Powinien zostać rzucany wyjątek ApiException");
        assertTrue(exception.getMessage().contains("API error"), "Komunikat wyjątku powinien zawierać 'API error'");
    }

    // Test sprawdza, czy metoda rzuca wyjątek przy błędnym kodzie odpowiedzi HTTP
    @Test
    void testFetchEmployeesThrowsExceptionOnHttpError() {
        String errorUrl = "https://jsonplaceholder.typicode.com/invalid-endpoint";
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi();
        }, "Powinien zostać rzucony wyjątek ApiException");
        assertTrue(exception.getMessage().contains("HTTP error"), "Komunikat wyjątku powinien zawierać 'HTTP error'");
    }
}
