# 🏥 MediCare AI – Intelligent Healthcare Platform

> **Smart Health, Connected Life**

## 📋 Description

MediCare AI is an intelligent healthcare platform that serves 
as a 24/7 patient companion and professional support tool.
It uses AI to offer symptom assessment, record management,
teleconsultations, and clinical collaboration — all within 
a secure, unified system.

---

## 👥 Team Members

| Name | Module |
|------|--------|
| Neyrouz Toumi | Medical Records + Appointment |
| Aya Hajji | Chatbot + Events/Feedback |
| Samar Manai | Forum + Collaboration |
| Ahmed Brahem | E-Pharmacy |
| Omaima Sahli | Health Tracking |

---

## 🏗️ Project Architecture
```
medicare-ai/
├── src/main/java/tn/esprit/tn/medicare_ai/
│   ├── controller/     → REST Controllers
│   ├── entity/         → JPA Entities
│   ├── dto/            → Data Transfer Objects
│   ├── repository/     → Spring Data JPA
│   ├── service/        → Business Logic
│   ├── security/       → Spring Security + JWT
│   └── exception/      → Global Exception Handler
└── src/main/resources/
    └── application.properties
```

---

## 🔧 Technologies

### Backend
- Java 21
- Spring Boot 3.5.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Lombok
- Swagger / OpenAPI

### Frontend
- Angular 17
- TypeScript
- Bootstrap

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven
- MySQL 8.0
- Node.js + Angular CLI

### Backend Setup

**1. Clone the repository**
```bash
git clone https://github.com/your-username/Esprit-PIDEV_SE-4SE1-2526-MediCareAI.git
```

**2. Configure database**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medicare_db
spring.datasource.username=root
spring.datasource.password=
```

**3. Run the application**
```bash
mvn spring-boot:run
```

**4. Access Swagger UI**
```
http://localhost:8089/MediCareAI/swagger-ui/index.html
```

---

## 🔐 Security

- JWT Authentication
- Role-based Access Control
- Roles : PATIENT, DOCTOR, PHARMACIST, ADMIN
- BCrypt Password Encoding

---
## 📄 License

This project is developed as part of the 
**PIDEV 2025-2026** program at **ESPRIT School of Engineering**.
