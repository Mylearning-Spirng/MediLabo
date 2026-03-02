package com.abernathyclinic.medilabo_notes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedilaboNotesApplication {

    private static final Logger logger = LoggerFactory.getLogger(MedilaboNotesApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MedilaboNotesApplication.class, args);
        logger.info("Medilabo notes service started (args={})", args == null ? 0 : args.length);
    }

}
