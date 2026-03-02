package com.abernathyclinic.medilabo.controller;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 1. Create a new patient using the provided JSON body.
     * 2. Return the created patient with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        logger.info("Received request to create patient: {} {}", patient.getFirstname(), patient.getLastname());
        Patient saved = patientService.create(patient);
        logger.debug("Created patient with id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * 1. Retrieve all patients from the service.
     * 2. Return the list of patients with a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        logger.info("Received request to list all patients");
        List<Patient> patients = patientService.getAll();
        logger.debug("Returning {} patients", patients == null ? 0 : patients.size());
        return ResponseEntity.ok(patients);
    }

    /**
     * 1. Retrieve a patient by ID from the service.
     * 2. If the patient exists, return it with a 200 OK status.
     * 3. If the patient does not exist, the service will throw PatientNotFoundException,
     * which is handled by the @ExceptionHandler to return a 404 Not Found status.
     */
    @GetMapping("{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        logger.info("Received request to get patient by id={}", id);
        Patient p = patientService.getById(id);
        logger.debug("Found patient id={}", p.getId());
        return ResponseEntity.ok(p);
    }

    /**
     * 1. Delete a patient by ID using the service.
     * 2. If the patient exists and is deleted, return a 204 No Content status.
     * 3. If the patient does not exist, the service will throw PatientNotFoundException,
     * which is handled by the @ExceptionHandler to return a 404 Not Found status.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Received request to delete patient id={}", id);
        patientService.delete(id);
        logger.debug("Deleted patient id={}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 1. Update a patient by ID using the provided JSON body.
     * 2. If the patient exists and is updated, return the updated patient with a 200 OK status.
     * 3. If the patient does not exist, the service will throw PatientNotFoundException,
     * which is handled by the @ExceptionHandler to return a 404 Not Found status.
     */
    @PutMapping("{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody Patient patient) {
        logger.info("Received request to update patient id={} with data for {} {}", id, patient.getFirstname(), patient.getLastname());
        Patient updated = patientService.update(id, patient);
        logger.debug("Updated patient id={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    // Handle PatientNotFoundException thrown by service methods and return 404
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PatientNotFoundException ex) {
        logger.warn("Patient not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
