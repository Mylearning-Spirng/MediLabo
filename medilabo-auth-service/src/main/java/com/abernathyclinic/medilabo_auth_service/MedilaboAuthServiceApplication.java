package com.abernathyclinic.medilabo_auth_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedilaboAuthServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(MedilaboAuthServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MedilaboAuthServiceApplication.class, args);
        logger.info("Medilabo auth service started (args={})", args == null ? 0 : args.length);
    }

}
