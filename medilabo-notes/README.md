# Notes Service

## Description
The Notes Service stores patient medical history and notes.

Each note is linked to a patient using the patient ID.

## Database
MongoDB

Collection:
medical_history

## Endpoints

GET /api/notes/patient/{id}  
POST /api/notes  
DELETE /api/notes/{id}

## Green Code Practices
- Uses NoSQL database optimized for flexible data
- Avoids complex relational joins
- Faster data retrieval reduces CPU usage

## Run Service

mvn clean package  
mvn spring-boot:run