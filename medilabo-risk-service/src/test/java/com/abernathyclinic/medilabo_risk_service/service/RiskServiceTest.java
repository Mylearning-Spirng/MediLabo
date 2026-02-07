package com.abernathyclinic.medilabo_risk_service.service;

import com.abernathyclinic.medilabo_risk_service.dto.MedicalNoteDto;
import com.abernathyclinic.medilabo_risk_service.dto.PatientDto;
import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RiskServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RiskService riskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // set @Value fields for unit test (no Spring context)
        ReflectionTestUtils.setField(riskService, "patientServiceUrl", "http://patient-service");
        ReflectionTestUtils.setField(riskService, "notesServiceUrl", "http://notes-service");
    }

    @Test
    void assess_returnsNone_whenLessThan2Triggers() {
        Long patientId = 1L;
        String auth = "Bearer test-token";

        // patient age >= 30 doesn't matter here because triggers < 2 => None
        PatientDto patient = new PatientDto();
        patient.id = patientId;
        patient.gender = "M";
        patient.birthdate = "1980-01-01"; // age >= 30

        MedicalNoteDto note = new MedicalNoteDto();
        note.note = "Patient is doing well."; // 0 triggers

        mockPatientCall(patientId, patient);
        mockNotesCall(patientId, List.of(note));

        RiskResponseDto result = riskService.assess(patientId, auth);

        assertEquals(patientId, result.getPatientId());
        assertEquals("None", result.getRiskLevel());
        assertEquals(0, result.getTriggerCount());
    }

    @Test
    void assess_returnsBorderline_whenAgeAtLeast30_andTriggersBetween2And5() {
        Long patientId = 2L;

        PatientDto patient = new PatientDto();
        patient.id = patientId;
        patient.gender = "F";
        patient.birthdate = "1975-01-01"; // age >= 30

        MedicalNoteDto note = new MedicalNoteDto();
        // 2 triggers: "Height", "Weight"
        note.note = "Height and Weight recorded.";

        mockPatientCall(patientId, patient);
        mockNotesCall(patientId, List.of(note));

        RiskResponseDto result = riskService.assess(patientId, "Bearer x");

        assertEquals("Borderline", result.getRiskLevel());
        assertEquals(2, result.getTriggerCount());
    }

    @Test
    void assess_returnsInDanger_forMaleUnder30_with3Triggers() {
        Long patientId = 3L;

        PatientDto patient = new PatientDto();
        patient.id = patientId;
        patient.gender = "M";
        patient.birthdate = "2005-01-01"; // under 30

        MedicalNoteDto note = new MedicalNoteDto();
        // 3 triggers: Smoking, Height, Weight
        note.note = "SMOKING. Height. Weight.";

        mockPatientCall(patientId, patient);
        mockNotesCall(patientId, List.of(note));

        RiskResponseDto result = riskService.assess(patientId, "Bearer x");

        assertEquals("InDanger", result.getRiskLevel());
        assertEquals(3, result.getTriggerCount());
    }

    @Test
    void assess_returnsEarlyOnset_forFemaleUnder30_with6Triggers() {
        Long patientId = 4L;

        PatientDto patient = new PatientDto();
        patient.id = patientId;
        patient.gender = "F";
        patient.birthdate = "2002-01-01"; // under 30

        MedicalNoteDto note = new MedicalNoteDto();
        // 6 triggers: Hemoglobin A1C, Microalbumin, Height, Weight, Smoking, Cholesterol
        note.note = "Hemoglobin A1C, Microalbumin, Height, Weight, Smoking, Cholesterol";

        mockPatientCall(patientId, patient);
        mockNotesCall(patientId, List.of(note));

        RiskResponseDto result = riskService.assess(patientId, "Bearer x");

        assertEquals("EarlyOnset", result.getRiskLevel());
        assertEquals(6, result.getTriggerCount());
    }

    // ---------------- helpers ----------------

    private void mockPatientCall(Long patientId, PatientDto patient) {
        when(restTemplate.exchange(
                eq("http://patient-service/api/patients/" + patientId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(PatientDto.class)
        )).thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));
    }

    private void mockNotesCall(Long patientId, List<MedicalNoteDto> notes) {
        MedicalNoteDto[] arr = notes.toArray(new MedicalNoteDto[0]);

        when(restTemplate.exchange(
                eq("http://notes-service/api/notes/patient/" + patientId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(MedicalNoteDto[].class)
        )).thenReturn(new ResponseEntity<>(arr, HttpStatus.OK));
    }
}
