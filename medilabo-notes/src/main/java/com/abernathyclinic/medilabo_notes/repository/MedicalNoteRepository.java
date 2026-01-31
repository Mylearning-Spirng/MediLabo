package com.abernathyclinic.medilabo_notes.repository;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalNoteRepository extends MongoRepository<MedicalNote, String> {
    List<MedicalNote> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
