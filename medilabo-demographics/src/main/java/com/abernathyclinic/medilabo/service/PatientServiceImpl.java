package com.abernathyclinic.medilabo.service;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient create(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Override
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    public Patient update(Long id, Patient patient) {
        Patient existing = getById(id);

        existing.setFirstname(patient.getFirstname());
        existing.setLastname(patient.getLastname());
        existing.setGender(patient.getGender());
        existing.setBirthdate(patient.getBirthdate());
        existing.setAddress(patient.getAddress());
        existing.setPhone(patient.getPhone());

        return patientRepository.save(existing);
    }
}
