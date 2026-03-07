# Demographics Service

## Description
The Demographics Service manages patient demographic data such as name, birthdate, gender, address, and phone number.

## Database
PostgreSQL

## Green Code Practices
- Database designed using 3NF normalization
- Reduces duplicate data storage
- Efficient queries reduce processing time

## Run Service

mvn clean package  
mvn spring-boot:run