# 🏦 Mortgage Application Portal

A RESTful API for managing mortgage applications in a banking environment, built with **Spring Boot**, **Spring Security**, and **JPA**. The system supports applicants submitting mortgage requests and loan officers managing the applications.

---

## 🚀 Features

- JWT-secured authentication & authorization
- Role-based access: `APPLICANT` and `OFFICER`
- Submit, retrieve, list, update, and delete mortgage applications
- Pagination & filtering
- Validation and permission checks
- Structured logging and clean architecture

---

## 🧰 Tech Stack

- Java 17+
- Spring Boot 3+
- Spring Security
- Spring Data JPA
- Swagger/OpenAPI 3
- PostgreSQL or H2 (configurable)
- Lombok

---

## 📦 Setup Instructions

### ✅ Prerequisites

- Java 17+
- Maven
- PostgreSQL (or use in-memory DB for testing)

### 🛠️ Run Locally

```bash
# Clone the repo
git clone https://github.com/your-username/ms-bank-mortgage-portal.git
cd ms-bank-mortgage-portal

# Build the app
./mvnw clean install

# Run the app
./mvnw spring-boot:run
Authorization: Bearer <your_token_here>
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
        "loanAmount": 150000,
        "loanTypeId": "uuid-of-loan-type"
      }'
{
  "id": "application-id",
  "status": "PENDING",
  ...
}
curl -X GET http://localhost:8080/api/v1/applications/{id} \
  -H "Authorization: Bearer <token>"
curl -X PUT http://localhost:8080/api/v1/applications/{id}/decisions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
        "decision": "APPROVED",
        "comments": "Updated application after reconsideration"
      }'
