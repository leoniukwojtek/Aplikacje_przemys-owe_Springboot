package com.techcorp.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.mapper.EmployeeMapper;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.service.EmployeeService;
import com.techcorp.employee.dto.StatusUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    // ------------------------ TEST GET WSZYSTKICH ------------------------
    @Test
    void testGetAllEmployees() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ACTIVE);
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(emp));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jan@example.com"));

        verify(employeeService).getAllEmployees();
    }

    // ------------------------ TEST GET PO EMAILU ------------------------
    @Test
    void testGetEmployeeByEmail() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ACTIVE);
        when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);

        mockMvc.perform(get("/api/employees/jan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jan@example.com"));

        verify(employeeService).getEmployeeByEmail("jan@example.com");
    }

    // ------------------------ TEST GET NIEISTNIEJĄCEGO ------------------------
    @Test
    void testGetEmployeeNotFound() throws Exception {
        when(employeeService.getEmployeeByEmail("noone@example.com")).thenReturn(null);

        mockMvc.perform(get("/api/employees/noone@example.com"))
                .andExpect(status().isNotFound());

        verify(employeeService).getEmployeeByEmail("noone@example.com");
    }

    // ------------------------ TEST POST NOWEGO PRACOWNIKA ------------------------
    @Test
    void testPostNewEmployee() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ACTIVE);
        when(employeeService.addEmployee(any())).thenReturn(true);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(employeeService).addEmployee(any());
    }

    // ------------------------ TEST POST Z DUPLIKATEM ------------------------
    @Test
    void testPostDuplicateEmployee() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ACTIVE);
        when(employeeService.addEmployee(any())).thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp)))
                .andExpect(status().isConflict());

        verify(employeeService).addEmployee(any());
    }

    // ------------------------ TEST DELETE ------------------------
    @Test
    void testDeleteEmployee() throws Exception {
        when(employeeService.deleteEmployee("jan@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/employees/jan@example.com"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee("jan@example.com");
    }

    // ------------------------ TEST PATCH ZMIANY STATUSU ------------------------
    @Test
    void testPatchUpdateStatus() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ON_LEAVE);
        when(employeeService.updateEmployeeStatus(eq("jan@example.com"), eq(EmploymentStatus.ON_LEAVE))).thenReturn(emp);

        StatusUpdateRequest statusRequest = new StatusUpdateRequest();
        statusRequest.setStatus(EmploymentStatus.ON_LEAVE);

        mockMvc.perform(patch("/api/employees/jan@example.com/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));

        verify(employeeService).updateEmployeeStatus("jan@example.com", EmploymentStatus.ON_LEAVE);
    }

    // ------------------------ TEST FILTROWANIA PO FIRMIE ------------------------
    @Test
    void testFilterEmployeesByCompany() throws Exception {
        EmployeeDTO emp = new EmployeeDTO("Jan","Kowalski","jan@example.com","TechCorp","PROGRAMISTA",8000.0, EmploymentStatus.ACTIVE);
        when(employeeService.findEmployeesByCompany("TechCorp"))
                .thenReturn(Arrays.asList(EmployeeMapper.toEntity(emp)));

        mockMvc.perform(get("/api/employees/company/TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].company").value("TechCorp"));

        verify(employeeService).findEmployeesByCompany("TechCorp");
    }
}
