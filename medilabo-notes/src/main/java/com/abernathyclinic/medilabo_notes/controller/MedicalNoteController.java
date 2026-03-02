package com.abernathyclinic.medilabo_notes.controller;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.service.MedicalNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class MedicalNoteController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalNoteController.class);

    private final MedicalNoteService service;

    public MedicalNoteController(MedicalNoteService service) {
        this.service = service;
    }

    // View Patient Medical History (all notes for patient)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalNote>> getNotes(@PathVariable Long patientId) {
        logger.info("Request: list notes for patientId={}", patientId);
        List<MedicalNote> notes = service.getByPatientId(patientId);
        logger.debug("Returning {} notes for patientId={}", notes == null ? 0 : notes.size(), patientId);
        return ResponseEntity.ok(notes);
    }

    // Add a note (formatting preserved)
    @PostMapping
    public ResponseEntity<MedicalNote> add(@RequestBody CreateNoteRequest req) {
        logger.info("Request: add note for patientId={} lastName={}", req.patientId, req.patientLastName);
        MedicalNote created = service.add(req.patientId, req.patientLastName, req.note);
        logger.debug("Created note id={} for patientId={}", created.getId(), req.patientId);
        return ResponseEntity.created(URI.create("/api/notes/" + created.getId())).body(created);
    }

    public static class CreateNoteRequest {
        public Long patientId;
        public String patientLastName; // optional
        public String note;
    }

    // Delete a note by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        logger.info("Request: delete note id={}", id);
        service.deleteNote(id);
        logger.debug("Deleted note id={}", id);
        return ResponseEntity.noContent().build();
    }
}
