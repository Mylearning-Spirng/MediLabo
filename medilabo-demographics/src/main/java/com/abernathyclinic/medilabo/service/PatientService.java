package com.abernathyclinic.medilabo.service;

import com.abernathyclinic.medilabo.model.Patient;

import java.util.List;

public interface PatientService {
    Patient create(Patient patient);

    List<Patient> getAll();

    Patient getById(Long id);

    void delete(Long id);

    Patient update(Long id, Patient patient);
}
