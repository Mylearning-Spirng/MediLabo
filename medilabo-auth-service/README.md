# Auth Service

## Description
The Auth Service handles user authentication for the MediLabo system.  
It validates user credentials and generates JWT tokens used to access protected APIs.

## Responsibilities
- Authenticate users
- Generate JWT tokens
- Secure access to other microservices

## Green Code Practices
- Uses stateless JWT authentication (no session storage)
- Lightweight authentication service reduces processing load
- Centralized authentication reduces duplicated logic

## Run Service

mvn clean package  
mvn spring-boot:run