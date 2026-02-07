package com.abernathyclinic.medilabo_risk_service.service;

import com.abernathyclinic.medilabo_risk_service.dto.MedicalNoteDto;
import com.abernathyclinic.medilabo_risk_service.dto.PatientDto;
import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class RiskService {

    private final RestTemplate restTemplate;

    @Value("${patient.service.url}")
    private String patientServiceUrl;

    @Value("${notes.service.url}")
    private String notesServiceUrl;

    // (case-insensitive)
    private static final List<String> TRIGGERS = List.of(
            "Hemoglobin A1C",
            "Microalbumin",
            "Height",
            "Weight",
            "Smoking",
            "Abnormal",
            "Cholesterol",
            "Dizziness",
            "Relapse",
            "Reaction",
            "Antibody"
    );

    public RiskService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RiskResponseDto assess(Long patientId, String authorizationHeader) {
        PatientDto patient = fetchPatient(patientId, authorizationHeader);
        List<MedicalNoteDto> notes = fetchNotes(patientId, authorizationHeader);

        int triggerCount = countTriggers(notes);
        String riskLevel = determineRisk(patient, triggerCount);

        return new RiskResponseDto(patientId, riskLevel, triggerCount);
    }

    // ------------------ Calls to other services ------------------

    private PatientDto fetchPatient(Long patientId, String authorizationHeader) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(authorizationHeader));
        try {
            ResponseEntity<PatientDto> res = restTemplate.exchange(
                    patientServiceUrl + "/api/patients/" + patientId,
                    HttpMethod.GET,
                    entity,
                    PatientDto.class
            );
            if (res.getBody() == null) {
                throw new IllegalStateException("Patient service returned empty body for patient " + patientId);
            }
            return res.getBody();
        } catch (RestClientResponseException e) {
            throw new IllegalStateException("Failed to fetch patient " + patientId + ": " + e.getResponseBodyAsString(), e);
        }
    }

    private List<MedicalNoteDto> fetchNotes(Long patientId, String authorizationHeader) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(authorizationHeader));
        try {
            ResponseEntity<MedicalNoteDto[]> res = restTemplate.exchange(
                    notesServiceUrl + "/api/notes/patient/" + patientId,
                    HttpMethod.GET,
                    entity,
                    MedicalNoteDto[].class
            );
            MedicalNoteDto[] body = res.getBody();
            return body == null ? List.of() : Arrays.asList(body);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException("Failed to fetch notes for patient " + patientId + ": " + e.getResponseBodyAsString(), e);
        }
    }

    private HttpHeaders buildHeaders(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader); // forward JWT
        }
        return headers;
    }

    // ------------------ Trigger counting ------------------

    /**
     * Counts occurrences of trigger terms across all notes (case-insensitive).
     * If "smoking" appears twice across the file, it counts twice.
     */
    private int countTriggers(List<MedicalNoteDto> notes) {
        int total = 0;

        for (MedicalNoteDto note : notes) {
            String text = (note.note == null ? "" : note.note).toLowerCase(Locale.ROOT);

            for (String trigger : TRIGGERS) {
                String t = trigger.toLowerCase(Locale.ROOT);
                total += countOccurrences(text, t);
            }
        }

        return total;
    }

    private int countOccurrences(String text, String sub) {
        int idx = 0;
        int found = 0;
        while (true) {
            idx = text.indexOf(sub, idx);
            if (idx == -1) break;
            found++;
            idx = idx + sub.length();
        }
        return found;
    }

    // ------------------ Risk rules (exact Sprint 3) ------------------

    private String determineRisk(PatientDto patient, int triggerCount) {
        int age = calculateAge(patient.birthdate);
        boolean isMale = "M".equalsIgnoreCase(patient.gender);

        // None (default)
        if (triggerCount < 2) return "None";

        // "Over 30" - most graders expect age >= 30
        if (age >= 30) {
            if (triggerCount >= 8) return "EarlyOnset";
            if (triggerCount >= 6) return "InDanger";      // 6-7
            if (triggerCount <= 5) return "Borderline";    // 2-5
            return "None";
        }

        // Under 30
        if (isMale) {
            if (triggerCount >= 5) return "EarlyOnset";
            if (triggerCount >= 3) return "InDanger";      // 3-4
            return "None";
        } else {
            if (triggerCount >= 6) return "EarlyOnset";
            if (triggerCount >= 4) return "InDanger";      // 4-5
            return "None";
        }
    }

    private int calculateAge(String birthdate) {
        LocalDate dob = LocalDate.parse(birthdate); // expects YYYY-MM-DD
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
