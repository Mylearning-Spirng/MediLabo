# MediLabo UI Service

## Description
The UI Service is the frontend application for the MediLabo system.  
It provides a user interface for interacting with the backend microservices through the API Gateway.

The UI allows users to:

- Login to the system
- View patient demographics
- View and manage medical notes
- Calculate diabetes risk

All API requests are sent through the **Gateway Service**.


## Application Pages

### Login Page
Allows users to authenticate and receive a JWT token.

### Patients Page
Displays patient demographic information.

### Notes Page
Displays and manages patient medical notes.

### Risk Page
Calculates and displays diabetes risk level.

---

## API Communication

The UI communicates with the backend through the Gateway.

Example API calls:

POST /api/auth/login  
GET /api/patients  
GET /api/notes/patient/{id}  
GET /api/risk/{patientId}

---

## Green Code Practices

The UI service follows Green Coding principles by:

- Making lightweight REST API calls
- Fetching only required data from backend services
- Using reusable React components to reduce redundant code
- Keeping frontend logic simple to reduce processing overhead

## Run Service
To run the UI service, use the following commands:

```bash
npm install
npm start
```
