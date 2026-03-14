package com.abernathyclinic.medilabo.service;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // This method is called by the controller to create a new patient.
    public Patient create(Patient patient) {
        logger.debug("Saving new patient: {} {}", patient.getFirstname(), patient.getLastname());
        Patient saved = patientRepository.save(patient);
        logger.info("Patient created with id={}", saved.getId());
        return saved;
    }

    // This method is called by the controller to get all patients.
    public List<Patient> getAll() {
        logger.debug("Fetching all patients");
        List<Patient> all = patientRepository.findAll();
        logger.info("Retrieved {} patients", all.size());
        return all;
    }

    // This method is called by the controller to get all patients with pagination.
    public Page<Patient> getAllPaged(Pageable pageable) {
        logger.debug("Fetching patients with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Patient> page = patientRepository.findAll(pageable);
        logger.info("Retrieved page {} with {} patients out of {} total",
                pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    // This method is called by the controller to get a patient by ID.
    public Patient getById(Long id) {
        logger.debug("Fetching patient by id={}", id);
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found with id={}", id);
                    return new PatientNotFoundException("Patient not found with id: " + id);
                });
    }

    // This method is called by the controller to delete a patient by ID.
    public void delete(Long id) {
        logger.debug("Deleting patient id={}", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent patient id={}", id);
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
        logger.info("Deleted patient id={}", id);
    }

    // This method is not required by the controller, but it's a common service method to have.
    public Patient update(Long id, Patient updatedPatient) {
        logger.debug("Updating patient id={} with provided data", id);
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found for update id={}", id);
                    return new PatientNotFoundException("Patient not found with id: " + id);
                });

        // update fields (keep id unchanged)
        existing.setFirstname(updatedPatient.getFirstname());
        existing.setLastname(updatedPatient.getLastname());
        existing.setGender(updatedPatient.getGender());
        existing.setBirthdate(updatedPatient.getBirthdate());
        existing.setAddress(updatedPatient.getAddress());
        existing.setPhone(updatedPatient.getPhone());

        Patient saved = patientRepository.save(existing);
        logger.info("Updated patient id={}", saved.getId());
        return saved;
    }
}
