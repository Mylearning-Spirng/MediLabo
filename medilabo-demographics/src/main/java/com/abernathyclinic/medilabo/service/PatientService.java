package com.abernathyclinic.medilabo.service;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // This method is called by the controller to create a new patient.
    public Patient create(Patient patient) {
        // return whatever the repository returns (including generated id)
        return patientRepository.save(patient);
    }

    // This method is called by the controller to get all patients.
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    // This method is called by the controller to get a patient by ID.
    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
    }

    // This method is called by the controller to delete a patient by ID.
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    // This method is not required by the controller, but it's a common service method to have.
    public Patient update(Long id, Patient updatedPatient) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        // update fields (keep id unchanged)
        existing.setFirstname(updatedPatient.getFirstname());
        existing.setLastname(updatedPatient.getLastname());
        existing.setGender(updatedPatient.getGender());
        existing.setBirthdate(updatedPatient.getBirthdate());
        existing.setAddress(updatedPatient.getAddress());
        existing.setPhone(updatedPatient.getPhone());

        return patientRepository.save(existing);
    }
}
