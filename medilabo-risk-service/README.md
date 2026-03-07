# Risk Service

## Description
The Risk Service calculates the Type 2 diabetes risk level for a patient.

It retrieves patient demographics and medical notes to evaluate risk triggers.

## Green Code Practices
- Risk calculation isolated in a dedicated service
- Stateless processing reduces memory usage
- Retrieves only required data from other services

## Run Service

mvn clean package  
mvn spring-boot:run