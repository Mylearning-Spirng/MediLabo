package com.abernathyclinic.medilabo.controller;

import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public String create(@RequestBody Patient patient) {
        patientService.create(patient);
        return "Patient Record is created";
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAll();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable Long id) {
        patientService.delete(id);
        return "Patient Record is deleted";
    }

    @PutMapping("update/{id}")
    public String update(@PathVariable Long id, @RequestBody Patient patient) {
        patientService.update(id, patient);
        return "Patient Record is updated";
    }
}
