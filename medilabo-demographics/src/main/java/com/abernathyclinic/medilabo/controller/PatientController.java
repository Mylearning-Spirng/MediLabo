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

    @PostMapping
    public String create(@RequestBody Patient patient) {
        patientRepository.save(patient);
        return "Patient Record is created";
    }

    @GetMapping
    public List<Patient> getAllStudents() {
        return patientRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        patientRepository.deleteById(id);
        return "Patient Record is deleted";
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable String id, @RequestBody Patient patient) {
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
        } else {
            return "Patient Record not found";
        }
    }
}
