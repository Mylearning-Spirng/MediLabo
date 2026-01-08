package com.abernathyclinic.medilabo.model;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "patient_details")
public class Patient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "gender")
    private String gender;
    @Column(name = "birthdate")
    private String birthdate;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
}