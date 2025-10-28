package com.techcorp.employee.service;

import com.google.gson.*;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.JobTitle;
import com.techcorp.employee.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ApiService {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiUrl;

    public ApiService(HttpClient httpClient, Gson gson, @Value("${app.api.url}") String apiUrl) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.apiUrl = apiUrl;
    }

    public List<Employee> fetchEmployeesFromApi() throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException("HTTP error: " + response.statusCode());
            }

            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

            return StreamSupport.stream(array.spliterator(), false)
                    .map(element -> {
                        JsonObject obj = element.getAsJsonObject();
                        String fullName = obj.get("name").getAsString();
                        String[] nameParts = fullName.split(" ", 2);
                        String firstName = nameParts.length > 0 ? nameParts[0] : "";
                        String lastName = nameParts.length > 1 ? nameParts[1] : "";
                        String email = obj.get("email").getAsString();
                        String company = obj.getAsJsonObject("company").get("name").getAsString();
                        String jobTitle = "DEVELOPER";
                        double salary= 8000.0;

                        return new Employee(
                                firstName,
                                lastName,
                                email,
                                company,
                                jobTitle,
                                salary
                        );
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ApiException("API error: " + e.getMessage());
        }
    }
}
