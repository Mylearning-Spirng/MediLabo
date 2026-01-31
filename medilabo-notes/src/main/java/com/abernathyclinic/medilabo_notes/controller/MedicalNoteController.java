package com.abernathyclinic.medilabo_notes.controller;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.service.MedicalNoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class MedicalNoteController {

    private final MedicalNoteService service;

    public MedicalNoteController(MedicalNoteService service) {
        this.service = service;
    }

    // View Patient Medical History (all notes for patient)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalNote>> getNotes(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getByPatientId(patientId));
    }

    // Add a note (formatting preserved)
    @PostMapping
    public ResponseEntity<MedicalNote> add(@RequestBody CreateNoteRequest req) {
        MedicalNote created = service.add(req.patientId, req.patientLastName, req.note);
        return ResponseEntity.created(URI.create("/api/notes/" + created.getId())).body(created);
    }

    public static class CreateNoteRequest {
        public Long patientId;
        public String patientLastName; // optional
        public String note;
    }
}
