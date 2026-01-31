package com.abernathyclinic.medilabo_notes.service;

import com.abernathyclinic.medilabo_notes.model.MedicalNote;
import com.abernathyclinic.medilabo_notes.repository.MedicalNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicalNoteServiceTest {

    private MedicalNoteRepository repo;
    private MedicalNoteService service;

    @BeforeEach
    void setUp() {
        repo = mock(MedicalNoteRepository.class);
        service = new MedicalNoteService(repo);
    }

    @Test
    void getByPatientId_callsRepositoryAndReturnsNotes() {
        Long patientId = 1L;

        MedicalNote note = new MedicalNote();
        note.setId("n1");
        note.setPatientId(patientId);
        note.setPatientLastName("TestNone");
        note.setNote("hello");

        when(repo.findByPatientIdOrderByCreatedAtDesc(patientId)).thenReturn(List.of(note));

        List<MedicalNote> result = service.getByPatientId(patientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("n1");
        verify(repo).findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Test
    void add_buildsMedicalNoteAndSaves() {
        when(repo.save(any(MedicalNote.class))).thenAnswer(inv -> {
            MedicalNote m = inv.getArgument(0);
            m.setId("saved123");
            return m;
        });

        MedicalNote saved = service.add(2L, "TestBorderline", "note text");

        assertThat(saved.getId()).isEqualTo("saved123");
        assertThat(saved.getPatientId()).isEqualTo(2L);
        assertThat(saved.getPatientLastName()).isEqualTo("TestBorderline");
        assertThat(saved.getNote()).isEqualTo("note text");
        assertThat(saved.getCreatedAt()).isNotNull();

        ArgumentCaptor<MedicalNote> captor = ArgumentCaptor.forClass(MedicalNote.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getPatientId()).isEqualTo(2L);
    }
}