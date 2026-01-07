package com.abernathyclinic.medilabo.repository;

import com.abernathyclinic.medilabo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, String> {

}
