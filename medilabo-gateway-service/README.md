# Gateway Service

## Description
The Gateway Service acts as the main entry point for all client requests.  
It routes requests to the correct microservice and validates authentication tokens.

## Responsibilities
- API routing
- JWT validation
- Centralized security
- CORS configuration

## Example Routes

/api/auth/** -> auth-service  
/api/patients/** -> demographics-service  
/api/notes/** -> notes-service  
/api/risk/** -> risk-service

## Green Code Practices
- Centralized routing avoids duplicated logic
- JWT validation performed once at gateway level
- Reduces unnecessary network calls between services

## Run Service

mvn clean package  
mvn spring-boot:run