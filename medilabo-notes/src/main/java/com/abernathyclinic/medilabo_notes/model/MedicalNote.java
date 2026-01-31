package com.abernathyclinic.medilabo_notes.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "medical_history")
public class MedicalNote {

    @Id
    private String id;

    private Long patientId;
    private String patientLastName;
    private String note;
    private LocalDateTime createdAt = LocalDateTime.now();

}

