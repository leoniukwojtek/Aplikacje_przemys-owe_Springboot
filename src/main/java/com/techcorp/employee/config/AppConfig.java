package com.techcorp.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class AppConfig {

    // Bean dla ObjectMapper, aby skonfigurować serializację dat w formacie ISO
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Wyłączenie serializacji dat jako timestampów
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    // Jeśli chcesz mieć także HttpClient
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
