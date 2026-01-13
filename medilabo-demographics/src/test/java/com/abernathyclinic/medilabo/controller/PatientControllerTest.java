package com.abernathyclinic.medilabo.controller;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = getBuild();    }

    private MockMvc getBuild() {
        return MockMvcBuilders
                .standaloneSetup(patientController)
                .build();
    }

    @Test
    void create_shouldReturnSuccessMessage() throws Exception {
        Patient patient = new Patient(
                null, "John", "Doe", "M", "1990-01-01", "123 Main St", "111-222-3333"
        );

        when(patientService.create(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Record is created"));

        verify(patientService, times(1)).create(any(Patient.class));
    }

    @Test
    void getAllPatients_shouldReturnList() throws Exception {
        Patient p1 = new Patient(1L, "John", "Doe", "M", "1990-01-01", "123 Main St", "111-222-3333");
        Patient p2 = new Patient(2L, "Jane", "Smith", "F", "1992-02-02", "999 New St", "999-888-7777");

        when(patientService.getAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstname").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstname").value("Jane"));

        verify(patientService, times(1)).getAll();
    }

    @Test
    void getPatientById_shouldReturnPatient() throws Exception {
        Patient p1 = new Patient(1L, "John", "Doe", "M", "1990-01-01", "123 Main St", "111-222-3333");
        when(patientService.getById(1L)).thenReturn(p1);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"));

        verify(patientService, times(1)).getById(1L);
    }

    @Test
    void delete_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(patientService).delete(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Record is deleted"));

        verify(patientService, times(1)).delete(1L);
    }

    @Test
    void update_shouldReturnSuccessMessage() throws Exception {
        Patient updated = new Patient(
                null, "Jane", "Smith", "F", "1992-02-02", "999 New St", "999-888-7777"
        );

        Patient returned = new Patient(
                1L, "Jane", "Smith", "F", "1992-02-02", "999 New St", "999-888-7777"
        );

        when(patientService.update(eq(1L), any(Patient.class))).thenReturn(returned);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Record is updated"));

        verify(patientService, times(1)).update(eq(1L), any(Patient.class));
    }

    @Test
    void getPatientById_whenNotFound_shouldReturn404_ifExceptionHandled() throws Exception {
        when(patientService.getById(99L)).thenThrow(new PatientNotFoundException(99L));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).getById(99L);
    }

}
