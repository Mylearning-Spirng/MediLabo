package com.abernathyclinic.medilabo_notes.service;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.repository.MedicalNoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalNoteService {

    private final MedicalNoteRepository repo;

    public MedicalNoteService(MedicalNoteRepository repo) {
        this.repo = repo;
    }

    public List<MedicalNote> getByPatientId(Long patientId) {
        return repo.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public MedicalNote add(Long patientId, String patientLastName, String noteText) {
        MedicalNote medicalNote = new MedicalNote();
        medicalNote.setPatientId(patientId);
        medicalNote.setPatientLastName(patientLastName);
        medicalNote.setNote(noteText);
        medicalNote.setCreatedAt(LocalDateTime.now());

        return repo.save(medicalNote);
    }
}
