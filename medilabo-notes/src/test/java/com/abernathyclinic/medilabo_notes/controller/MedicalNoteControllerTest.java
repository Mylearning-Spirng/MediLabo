package com.abernathyclinic.medilabo_notes.controller;

import com.abernathyclinic.medilabo_notes.config.SecurityConfig;
import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.service.MedicalNoteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(MedicalNoteController.class)
class MedicalNoteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MedicalNoteService service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MedicalNoteService medicalNoteService() {
            return Mockito.mock(MedicalNoteService.class);
        }
    }

    @Test
    void getNotes_requiresAuth() throws Exception {
        mvc.perform(get("/api/notes/patient/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "physician", roles = {"PHYSICIAN"})
    void getNotes_returnsList() throws Exception {
        MedicalNote note = new MedicalNote();
        note.setId("abc");
        note.setPatientId(1L);
        note.setPatientLastName("TestNone");
        note.setNote("hello");

        when(service.getByPatientId(1L)).thenReturn(List.of(note));

        mvc.perform(get("/api/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("abc"))
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].patientLastName").value("TestNone"))
                .andExpect(jsonPath("$[0].note").value("hello"));
    }

    @Test
    @WithMockUser(username = "physician", roles = {"PHYSICIAN"})
    void add_returns201AndLocationHeader() throws Exception {
        MedicalNote created = new MedicalNote();
        created.setId("n1");
        created.setPatientId(2L);
        created.setPatientLastName("TestBorderline");
        created.setNote("new note");

        when(service.add(eq(2L), eq("TestBorderline"), eq("new note")))
                .thenReturn(created);

        String body = """
                {
                  "patientId": 2,
                  "patientLastName": "TestBorderline",
                  "note": "new note"
                }
                """;

        mvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/notes/n1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("n1"))
                .andExpect(jsonPath("$.patientId").value(2))
                .andExpect(jsonPath("$.note").value("new note"));
    }
}