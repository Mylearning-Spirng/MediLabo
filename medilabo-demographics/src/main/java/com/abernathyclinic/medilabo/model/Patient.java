package com.abernathyclinic.medilabo.model;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient_details")
public class Patient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firstname", nullable = false)
    private String firstname;
    @Column(name = "lastname", nullable = false)
    private String lastname;
    @Column(name = "gender", nullable = false)
    private String gender;
    @Column(name = "birthdate", nullable = false)
    private String birthdate;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
}