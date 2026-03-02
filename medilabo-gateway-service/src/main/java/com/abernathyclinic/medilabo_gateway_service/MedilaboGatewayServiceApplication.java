package com.abernathyclinic.medilabo_gateway_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedilaboGatewayServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(MedilaboGatewayServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MedilaboGatewayServiceApplication.class, args);
        logger.info("Medilabo gateway service started (args={})", args == null ? 0 : args.length);
    }
}
