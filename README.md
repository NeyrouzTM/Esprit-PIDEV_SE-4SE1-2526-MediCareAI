# 🏥 MediCare AI – Intelligent Healthcare Platform

## Overview

This project was developed as part of the PIDEV – 4th Year 
Engineering Program at **Esprit School of Engineering** 
(Academic Year 2025–2026).

MediCare AI is an intelligent healthcare platform that serves 
as a 24/7 patient companion and professional support tool.
It uses AI to offer symptom assessment, record management,
teleconsultations, and clinical collaboration — all within 
a secure, unified system.

> **Slogan:** Smart Health, Connected Life

---

## Features

- ✅ Secure Authentication (JWT + Spring Security)
- ✅ Role-based Access Control (Patient, Doctor, Pharmacist, Admin)
- ✅ Medical Records Management
- ✅ Appointment & Availability Management
- ✅ Teleconsultation
- ✅ AI Chatbot Assistant
- ✅ Professional Collaboration
- ✅ E-Pharmacy
- ✅ Health Tracking & Well-being
- ✅ Community Forum + Subscription

---

## Tech Stack

### Frontend
- Angular 17
- TypeScript
- Bootstrap

### Backend
- Java 21
- Spring Boot 3.5.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Lombok
- Swagger / OpenAPI

---

## Architecture
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

## Contributors

| Name | Module |
|------|--------|
| Neyrouz Toumi | Medical Records + Appointment |
| Aya Hajji | Chatbot + Events/Feedback |
| Samar Manai | Forum + Collaboration |
| Ahmed Brahem | E-Pharmacy |
| Omaima Sahli | Health Tracking |

---

## Academic Context

Developed at **Esprit School of Engineering – Tunisia**

PIDEV – 4th Year | 2025–2026

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- MySQL 8.0
- Node.js + Angular CLI

### Backend Setup

**1. Clone the repository**
```bash
git clone https://github.com/your-username/Esprit-PIDEV-4SE1-2526-MediCareAI.git
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

## Acknowledgments

Special thanks to our professors and supervisors at 
**Esprit School of Engineering** for their guidance 
throughout this project.
