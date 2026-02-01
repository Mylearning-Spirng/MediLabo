package com.abernathyclinic.medilabo_risk_service.service;

import com.abernathyclinic.medilabo_risk_service.dto.MedicalNoteDto;
import com.abernathyclinic.medilabo_risk_service.dto.PatientDto;
import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class RiskService {

    private final WebClient webClient;
    private final String patientServiceBaseUrl;
    private final String notesServiceBaseUrl;
    private final long timeoutMs;

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

    public RiskService(
            WebClient webClient,
            @Value("${patient.service.base-url}") String patientServiceBaseUrl,
            @Value("${notes.service.base-url}") String notesServiceBaseUrl,
            @Value("${service.timeout-ms:2000}") long timeoutMs
    ) {
        this.webClient = webClient;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
        this.notesServiceBaseUrl = notesServiceBaseUrl;
        this.timeoutMs = timeoutMs;
    }

    public RiskResponseDto calculateRisk(Long patientId, String authorization) {

        PatientDto patient = fetchPatient(patientId, authorization);
        List<MedicalNoteDto> notes = fetchNotes(patientId, authorization);

        int triggerCount = countTriggers(notes);
        String risk = evaluateRisk(patient, triggerCount);

        return new RiskResponseDto(patientId, risk, triggerCount);
    }

    private PatientDto fetchPatient(Long patientId, String authorization) {
        return webClient.get()
                .uri(patientServiceBaseUrl + "/api/patients/{id}", patientId)
                .headers(h -> setAuthHeader(h, authorization))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Patient service error: HTTP " + resp.statusCode().value() + " " + body)
                                ))
                )
                .bodyToMono(PatientDto.class)
                .timeout(Duration.ofMillis(timeoutMs))
                .block();
    }

    private List<MedicalNoteDto> fetchNotes(Long patientId, String authorization) {
        List<MedicalNoteDto> notes = webClient.get()
                .uri(notesServiceBaseUrl + "/api/notes/patient/{id}", patientId)
                .headers(h -> setAuthHeader(h, authorization))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Notes service error: HTTP " + resp.statusCode().value() + " " + body)
                                ))
                )
                .bodyToFlux(MedicalNoteDto.class)
                .collectList()
                .timeout(Duration.ofMillis(timeoutMs))
                .onErrorReturn(Collections.emptyList())
                .block();

        return notes == null ? Collections.emptyList() : notes;
    }

    private void setAuthHeader(HttpHeaders headers, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }

    /**
     * Counts ALL occurrences of trigger terms across all patient notes (case-insensitive).
     * Repeated triggers are counted repeatedly (e.g., "abnormal" counts as 2 for "Abnormal").
     */
    private int countTriggers(List<MedicalNoteDto> notes) {
        if (notes == null || notes.isEmpty()) return 0;

        int count = 0;

        for (MedicalNoteDto n : notes) {
            if (n == null || n.note == null || n.note.isBlank()) continue;

            String text = n.note.toLowerCase(Locale.ROOT);

            for (String trigger : TRIGGERS) {
                String t = trigger.toLowerCase(Locale.ROOT);

                int idx = 0;
                while ((idx = text.indexOf(t, idx)) != -1) {
                    count++;
                    idx += t.length();
                }
            }
        }

        return count;
    }

    /**
     * Implements the priority rules.
     *
     * Priority order:
     * 1) EARLY_ONSET
     * 2) IN_DANGER
     * 3) BORDERLINE
     * 4) NONE
     */
    private String evaluateRisk(PatientDto patient, int triggers) {
        int age = safeAge(patient);
        String gender = safeGender(patient); // "M" / "F" / ""

        boolean under30 = age < 30;
        boolean age30Plus = age >= 30;

        // 1) EARLY ONSET
        if (age30Plus && triggers >= 8) return "EARLY_ONSET";
        if (under30 && "M".equals(gender) && triggers >= 5) return "EARLY_ONSET";
        if (under30 && "F".equals(gender) && triggers >= 6) return "EARLY_ONSET";

        // 2) IN DANGER
        if (age30Plus && (triggers == 6 || triggers == 7)) return "IN_DANGER";
        if (under30 && "M".equals(gender) && (triggers == 3 || triggers == 4)) return "IN_DANGER";
        if (under30 && "F".equals(gender) && (triggers == 4 || triggers == 5)) return "IN_DANGER";

        // 3) BORDERLINE (spec says "over 30" -> strict > 30)
        if (age > 30 && triggers >= 2 && triggers <= 5) return "BORDERLINE";

        // 4) NONE
        return "NONE";
    }

    private int safeAge(PatientDto patient) {
        if (patient == null || patient.birthdate == null || patient.birthdate.isBlank()) return 0;
        try {
            LocalDate dob = LocalDate.parse(patient.birthdate.trim()); // "YYYY-MM-DD"
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    private String safeGender(PatientDto patient) {
        if (patient == null || patient.gender == null) return "";
        return patient.gender.trim().toUpperCase(Locale.ROOT);
    }
}
