package com.abernathyclinic.medilabo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedilaboApplication {

	private static final Logger logger = LoggerFactory.getLogger(MedilaboApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MedilaboApplication.class, args);
		logger.info("Medilabo demographics service started (args={})", args == null ? 0 : args.length);
	}

}