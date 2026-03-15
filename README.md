# MediLabo

MediLabo is a microservices-based healthcare application designed to help detect **Type 2 diabetes risk** from patient demographics and medical notes.

The system is built with **Spring Boot microservices**, secured with **JWT authentication**, exposed through a **Spring Cloud Gateway**, and uses a **React frontend** for the UI.

---
## GIT HUB REPOSITORY:
https://github.com/Mylearning-Spirng/MediLabo

## Project Architecture

MediLabo is composed of the following services:

- **auth-service** – handles authentication and JWT generation
- **gateway-service** – single entry point for all client requests
- **demographics-service** – manages patient demographic data
- **notes-service** – manages patient medical notes
- **risk-service** – calculates diabetes risk level
- **ui-service** – React frontend
- **postgres** – relational database for patient data
- **mongo** – document database for medical notes

### High-Level Flow

1. User logs in through the UI
2. Auth service returns a JWT token
3. UI sends the token in the `Authorization` header
4. Gateway validates the token
5. Gateway routes requests to the correct backend service
6. Risk service collects patient data and notes to calculate diabetes risk

---

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Cloud Gateway
- Maven

### Frontend
- React
- JavaScript
- Nginx (for frontend container)

### Databases
- PostgreSQL
- MongoDB

### DevOps
- Docker
- Docker Compose

---

## Security

Security is implemented using **Spring Security with JWT**.

- `auth-service` authenticates users
- JWT is signed using RSA keys
- `gateway-service` and other protected services validate the token using the public key
- Protected endpoints require:

```http Token generation
thru postmen or curl:

http://localhost:9000/api/auth/login

Authorization: Bearer <your-jwt-token>

---## Running the Application

1. Clone the repository using git clone in terminal:
https://github.com/Mylearning-Spirng/MediLabo.git
2. Navigate to the project root
3. Build and run with Docker Compose:
```bash
docker-compose up --build
```
4. Access the UI at `http://localhost:3000` or `http://localhost:8080`
5. Use the following credentials to log in:
   - Username: `user`
   - Password: `password`

## Conclusion
MediLabo demonstrates a modern microservices architecture for healthcare applications, with a focus on security, scalability, and maintainability. The system can be extended to include additional features such as patient history, appointment scheduling, and more advanced risk analysis.