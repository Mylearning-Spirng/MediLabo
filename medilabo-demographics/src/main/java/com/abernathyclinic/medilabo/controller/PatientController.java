package com.abernathyclinic.medilabo.controller;


import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    /**
     * Create a new patient
     */
    @PostMapping
    public String create(@RequestBody Patient patient) {
        patientRepository.save(patient);
        return "Patient Record is created";
    }

    /**
     * Retrieve all patients
     */
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieve a patient by ID with proper error handling
     */
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient Record not found: " + id));
    }

    /**
     * Delete a patient by ID
     */
    @DeleteMapping("/{id}") // cleaner REST style
    public String delete(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return "Patient Record is deleted";
    }

    /**
     * Update a patient by ID
     */
    @PutMapping("/{id}") // cleaner REST style
    public String update(@PathVariable Long id, @RequestBody Patient patient) {
        Patient existingPatient = patientRepository.findById(id).orElse(null);
        if (existingPatient != null) {
            existingPatient.setFirstname(patient.getFirstname());
            existingPatient.setLastname(patient.getLastname());
            existingPatient.setGender(patient.getGender());
            existingPatient.setBirthdate(patient.getBirthdate());
            existingPatient.setAddress(patient.getAddress());
            existingPatient.setPhone(patient.getPhone());
            patientRepository.save(existingPatient);
            return "Patient Record is updated";
        }
        return "Patient Record not found";
    }
}
