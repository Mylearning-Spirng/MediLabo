package com.abernathyclinic.medilabo.service;

import com.abernathyclinic.medilabo.exception.PatientNotFoundException;
import com.abernathyclinic.medilabo.model.Patient;
import com.abernathyclinic.medilabo.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private Patient updatedPatient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstname("John");
        patient.setLastname("Doe");
        patient.setGender("M");
        patient.setBirthdate("1990-01-01");
        patient.setAddress("123 Main St");
        patient.setPhone("111-222-3333");

        updatedPatient = new Patient();
        updatedPatient.setFirstname("Jane");
        updatedPatient.setLastname("Smith");
        updatedPatient.setGender("F");
        updatedPatient.setBirthdate("1992-02-02");
        updatedPatient.setAddress("999 New St");
        updatedPatient.setPhone("999-888-7777");
    }

    @Test
    void create_shouldSaveAndReturnPatient() {
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.create(patient);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void getAll_shouldReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.getAll();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstname());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getById_whenFound_shouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getById(1L);

        assertNotNull(result);
        assertEquals("Doe", result.getLastname());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrowPatientNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getById(1L));
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.delete(1L);

        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_whenNotExists_shouldThrowPatientNotFoundException() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> patientService.delete(1L));

        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void update_whenFound_shouldUpdateFieldsAndSave() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient result = patientService.update(1L, updatedPatient);

        assertNotNull(result);
        assertEquals(1L, result.getId()); // unchanged
        assertEquals("Jane", result.getFirstname());
        assertEquals("Smith", result.getLastname());
        assertEquals("F", result.getGender());
        assertEquals("1992-02-02", result.getBirthdate());
        assertEquals("999 New St", result.getAddress());
        assertEquals("999-888-7777", result.getPhone());

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertEquals("Jane", captor.getValue().getFirstname());
    }

    @Test
    void update_whenNotFound_shouldThrowPatientNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.update(1L, updatedPatient));

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }
}
