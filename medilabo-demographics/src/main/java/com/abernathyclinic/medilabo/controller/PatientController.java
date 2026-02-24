package com.abernathyclinic.medilabo.controller;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

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
        Patient saved = patientService.create(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * 1. Retrieve all patients from the service.
     * 2. Return the list of patients with a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        return ResponseEntity.ok(patientService.getAll());
    }

    /**
     * 1. Retrieve a patient by ID from the service.
     * 2. If the patient exists, return it with a 200 OK status.
     * 3. If the patient does not exist, the service will throw PatientNotFoundException,
     * which is handled by the @ExceptionHandler to return a 404 Not Found status.
     */
    @GetMapping("{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Patient p = patientService.getById(id);
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
        patientService.delete(id);
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
        Patient updated = patientService.update(id, patient);
        return ResponseEntity.ok(updated);
    }

    // Handle PatientNotFoundException thrown by service methods and return 404
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PatientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
