package com.abernathyclinic.medilabo_notes.service;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.repository.MedicalNoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalNoteService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalNoteService.class);

    private final MedicalNoteRepository repo;

    public MedicalNoteService(MedicalNoteRepository repo) {
        this.repo = repo;
    }

    public List<MedicalNote> getByPatientId(Long patientId) {
        logger.debug("Fetching notes for patientId={}", patientId);
        List<MedicalNote> notes = repo.findByPatientIdOrderByCreatedAtDesc(patientId);
        logger.info("Found {} notes for patientId={}", notes == null ? 0 : notes.size(), patientId);
        return notes;
    }

    // This method is called by the controller to add a new medical note for a patient.
    public MedicalNote add(Long patientId, String patientLastName, String noteText) {
        logger.debug("Creating note for patientId={} lastName={}", patientId, patientLastName);
        MedicalNote medicalNote = new MedicalNote();
        medicalNote.setPatientId(patientId);
        medicalNote.setPatientLastName(patientLastName);
        medicalNote.setNote(noteText);
        medicalNote.setCreatedAt(LocalDateTime.now());

        MedicalNote saved = repo.save(medicalNote);
        logger.info("Saved note id={} for patientId={}", saved.getId(), patientId);
        return saved;
    }
    public void deleteNote(String id) {
        logger.debug("Deleting note id={}", id);
        if (!repo.existsById(id)) {
            logger.warn("Attempted to delete non-existent note id={}", id);
            throw new RuntimeException("Note not found with id: " + id);
        }
        repo.deleteById(id);
        logger.info("Deleted note id={}", id);
    }
}
