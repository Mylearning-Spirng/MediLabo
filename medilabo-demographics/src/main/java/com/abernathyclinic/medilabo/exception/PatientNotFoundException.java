// java
package com.abernathyclinic.medilabo.exception;

public class PatientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PatientNotFoundException(String message) {
        super(message);
    }

    public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id);
    }

    public PatientNotFoundException(long id) {
        this(Long.valueOf(id));
    }
}
