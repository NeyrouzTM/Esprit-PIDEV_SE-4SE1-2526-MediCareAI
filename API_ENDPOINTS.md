# Medicare AI - Documentation des Endpoints API

**Base URL:** `http://localhost:8089/MediCareAI`

**Swagger UI:** `http://localhost:8089/MediCareAI/swagger-ui/index.html`

---

## 📋 Table des matières

1. [Authentication](#authentication)
2. [Users Management](#users-management)
3. [Medical Records](#medical-records)
4. [Appointments](#appointments)
5. [Allergies](#allergies)
6. [Lab Results](#lab-results)
7. [Medical Images](#medical-images)
8. [Visit Notes](#visit-notes)
9. [Availabilities](#availabilities)
10. [Pharmacy](#pharmacy)
11. [Prescriptions](#prescriptions)
12. [Patient](#patient)

---

## 🔐 Authentication

### 1. Register
```
POST /auth/register
Content-Type: application/json

Body:
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "role": "PATIENT"  // PATIENT, DOCTOR, PHARMACIST, ADMIN
}

Response: 200 OK
{
  "message": "User created: user@example.com"
}
```

### 2. Login
```
POST /auth/login
Content-Type: application/json

Body:
{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@example.com",
  "role": "ROLE_PATIENT"
}
```

### 3. Search Doctors
```
GET /auth/doctors?query=house&page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [
    {
      "id": 2,
      "fullName": "Dr House",
      "email": "house@med.com",
      "role": "DOCTOR",
      "enabled": true
    }
  ],
  "totalElements": 1,
  "pageable": { ... }
}
```

---

## 👥 Users Management (Admin Only)

### 1. Get All Users
```
GET /auth/users?query=john&role=PATIENT&page=0&size=20
Authorization: Bearer {admin_token}
✓ Requires: ADMIN role

Response: 200 OK
```

### 2. Get User by ID
```
GET /auth/users/{id}
Authorization: Bearer {token}

Example:
GET /auth/users/1
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "PATIENT",
  "enabled": true
}
```

### 3. Update User (Admin Only)
```
PUT /auth/users/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json
✓ Requires: ADMIN role

Body:
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "newpassword123",
  "role": "DOCTOR",
  "enabled": true
}

Response: 200 OK
```

### 4. Delete User (Admin Only)
```
DELETE /auth/users/{id}
Authorization: Bearer {admin_token}
✓ Requires: ADMIN role

Response: 204 No Content
```

---

## 📋 Medical Records

### 1. Create Medical Record
```
POST /medical-records
Authorization: Bearer {patient_token}
Content-Type: application/json
✓ Requires: PATIENT role

Body:
{
  "bloodType": "O+",
  "allergies": "Peanuts, Penicillin",
  "chronicDiseases": "Diabetes",
  "notes": "Patient notes here"
}

Response: 200 OK
{
  "id": 1,
  "patientId": 1,
  "bloodType": "O+",
  ...
}
```

### 2. Get All Medical Records
```
GET /medical-records
Authorization: Bearer {token}
✓ Requires: PATIENT, DOCTOR, or ADMIN role

Response: 200 OK
[
  { "id": 1, ... },
  { "id": 2, ... }
]
```

### 3. Get Medical Record by ID
```
GET /medical-records/{id}
Authorization: Bearer {token}

Example:
GET /medical-records/1

Response: 200 OK
{
  "id": 1,
  "patientId": 1,
  "bloodType": "O+",
  ...
}
```

### 4. Get My Medical Record (Patient)
```
GET /medical-records/me
Authorization: Bearer {patient_token}
✓ Requires: PATIENT role

Response: 200 OK
```

### 5. Get Medical Record by Patient ID
```
GET /medical-records/patient/{patientId}
Authorization: Bearer {token}

Example:
GET /medical-records/patient/1

Response: 200 OK
```

### 6. Update Medical Record
```
PUT /medical-records/{id}
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "bloodType": "B+",
  "allergies": "Updated allergies"
}

Response: 200 OK
```

### 7. Delete Medical Record
```
DELETE /medical-records/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "message": "Medical record deleted"
}
```

---

## 📅 Appointments

### 1. Create Appointment
```
POST /appointments
Authorization: Bearer {patient_token}
Content-Type: application/json
✓ Requires: PATIENT role
✓ Patient ID is automatically taken from JWT token

Body:
{
  "doctorId": 2,
  "appointmentDate": "2026-04-15T10:00:00",
  "reason": "Check-up",
  "location": "Clinic A"
}

Response: 200 OK
{
  "id": 1,
  "patientId": 1,
  "doctorId": 2,
  ...
}
```

### 2. Get All Appointments
```
GET /appointments
Authorization: Bearer {token}

Response: 200 OK
[
  { "id": 1, ... },
  { "id": 2, ... }
]
```

### 3. Get Appointment by ID
```
GET /appointments/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Get Appointments by Patient
```
GET /appointments/patient/{patientId}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Get Appointments by Doctor
```
GET /appointments/doctor/{doctorId}
Authorization: Bearer {doctor_token}
✓ Requires: DOCTOR or ADMIN role

Response: 200 OK
```

### 6. Update Appointment
```
PUT /appointments/{id}
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "appointmentDate": "2026-04-16T11:00:00",
  "reason": "Follow-up"
}

Response: 200 OK
```

### 7. Delete Appointment
```
DELETE /appointments/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 8. Get Appointment Reminders
```
GET /appointments/{appointmentId}/reminders
Authorization: Bearer {token}

Response: 200 OK
{
  "appointmentId": 1,
  "status": "NO_REMINDER_PROVIDER",
  "reminders": []
}
```

### 9. Schedule Reminder
```
POST /appointments/{appointmentId}/reminders/schedule
Authorization: Bearer {token}

Response: 200 OK
{
  "appointmentId": 1,
  "status": "SCHEDULED",
  "scheduledAt": "2026-03-30T10:30:45.123456789Z"
}
```

### 10. Send Reminder
```
POST /appointments/{appointmentId}/reminders/send
Authorization: Bearer {token}

Response: 200 OK
```

### 11. Get Teleconsultation Status
```
GET /appointments/{appointmentId}/teleconsultation
Authorization: Bearer {token}

Response: 200 OK
{
  "appointmentId": 1,
  "status": "NOT_STARTED",
  "joinUrl": ""
}
```

### 12. Start Teleconsultation
```
POST /appointments/{appointmentId}/teleconsultation/start
Authorization: Bearer {token}

Response: 200 OK
{
  "appointmentId": 1,
  "status": "STARTED",
  "sessionId": "tele-1-1711857645123",
  "startedAt": "2026-03-30T10:30:45.123456789Z"
}
```

### 13. Join Teleconsultation
```
POST /appointments/{appointmentId}/teleconsultation/join
Authorization: Bearer {token}

Response: 200 OK
{
  "appointmentId": 1,
  "status": "JOINED",
  "joinUrl": "https://tele.medicare-ai.local/session/1",
  "joinedAt": "2026-03-30T10:30:45.123456789Z"
}
```

---

## 🤧 Allergies

### 1. Create Allergy
```
POST /allergies
Authorization: Bearer {token}
Content-Type: application/json
✓ Requires: ADMIN or DOCTOR role

Body:
{
  "medicalRecordId": 1,
  "allergyName": "Penicillin",
  "severity": "HIGH",
  "reaction": "Anaphylaxis",
  "notes": "Severe allergy"
}

Response: 200 OK
```

### 2. Get Allergy by ID
```
GET /allergies/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 3. Get Allergies by Medical Record
```
GET /allergies/medical-record/{medicalRecordId}
Authorization: Bearer {token}

Response: 200 OK
[
  { "id": 1, ... },
  { "id": 2, ... }
]
```

### 4. Update Allergy
```
PUT /allergies/{id}
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "allergyName": "Penicillin",
  "severity": "CRITICAL"
}

Response: 200 OK
```

### 5. Delete Allergy
```
DELETE /allergies/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 🧪 Lab Results

### 1. Create Lab Result
```
POST /lab-results
Authorization: Bearer {token}
Content-Type: application/json
✓ Requires: ADMIN or DOCTOR role

Body:
{
  "medicalRecordId": 1,
  "testName": "Blood Glucose",
  "result": "120",
  "unit": "mg/dL",
  "normalRange": "70-100",
  "testDate": "2026-03-30T10:00:00",
  "notes": "Test notes"
}

Response: 200 OK
```

### 2. Get Lab Result by ID
```
GET /lab-results/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 3. Get Lab Results by Medical Record
```
GET /lab-results/medical-record/{medicalRecordId}
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Update Lab Result
```
PUT /lab-results/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Delete Lab Result
```
DELETE /lab-results/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 🖼️ Medical Images

### 1. Create Medical Image
```
POST /medical-images
Authorization: Bearer {token}
Content-Type: application/json
✓ Requires: ADMIN or DOCTOR role

Body:
{
  "medicalRecordId": 1,
  "imageType": "XRAY",
  "imageUrl": "https://cdn.example.com/xray.png",
  "uploadDate": "2026-03-30T10:00:00",
  "description": "Chest X-Ray"
}

Response: 200 OK
```

### 2. Get Medical Image by ID
```
GET /medical-images/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 3. Get Medical Images by Medical Record
```
GET /medical-images/medical-record/{medicalRecordId}
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Update Medical Image
```
PUT /medical-images/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Delete Medical Image
```
DELETE /medical-images/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 📝 Visit Notes

### 1. Create Visit Note
```
POST /visit-notes
Authorization: Bearer {token}
Content-Type: application/json
✓ Requires: ADMIN or DOCTOR role

Body:
{
  "medicalRecordId": 1,
  "doctorId": 2,
  "visitDate": "2026-03-30T10:00:00",
  "subjective": "Patient reports...",
  "objective": "Physical exam...",
  "assessment": "Assessment...",
  "plan": "Treatment plan..."
}

Response: 200 OK
```

### 2. Get Visit Note by ID
```
GET /visit-notes/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 3. Get Visit Notes by Medical Record
```
GET /visit-notes/medical-record/{medicalRecordId}
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Update Visit Note
```
PUT /visit-notes/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Delete Visit Note
```
DELETE /visit-notes/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 📅 Availabilities

### 1. Create Availability
```
POST /availabilities
Authorization: Bearer {doctor_token}
Content-Type: application/json
✓ Requires: DOCTOR role

Body:
{
  "doctorId": 2,
  "availableDate": "2026-04-01",
  "startTime": "09:00",
  "endTime": "17:00",
  "maxAppointments": 10
}

Response: 200 OK
```

### 2. Get Availabilities by Doctor
```
GET /availabilities/doctor/{doctorId}
Authorization: Bearer {token}

Response: 200 OK
[
  { "id": 1, ... },
  { "id": 2, ... }
]
```

### 3. Get Available Slots by Doctor
```
GET /availabilities/doctor/{doctorId}/available
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Update Availability
```
PUT /availabilities/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Delete Availability
```
DELETE /availabilities/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 💊 Pharmacy (E-Pharmacy)

### 1. Search Medicines
```
GET /api/pharmacy/medicines?keyword=aspirin&category=PAIN&page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [
    {
      "id": 1,
      "name": "Aspirin",
      "category": "PAIN",
      "price": 5.99,
      ...
    }
  ],
  "totalElements": 1
}
```

### 2. Get Medicine Details
```
GET /api/pharmacy/medicines/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "name": "Aspirin",
  "price": 5.99,
  ...
}
```

### 3. Create Prescription (Doctor)
```
POST /api/pharmacy/prescriptions
Authorization: Bearer {doctor_token}
Content-Type: application/json
✓ Requires: DOCTOR role

Body:
{
  "patientId": 1,
  "issueDate": "2026-03-30",
  "expiryDate": "2026-06-30",
  "items": [
    {
      "medicineId": 1,
      "dosage": "500mg",
      "frequency": "2 times daily",
      "duration": "7 days"
    }
  ]
}

Response: 201 Created
```

### 4. Get Prescriptions
```
GET /api/pharmacy/prescriptions?page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Get Prescription by ID
```
GET /api/pharmacy/prescriptions/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 6. Place Order (Patient)
```
POST /api/pharmacy/orders
Authorization: Bearer {patient_token}
Content-Type: application/json
✓ Requires: PATIENT role

Body:
{
  "prescriptionId": null,
  "items": [
    {
      "medicineId": 1,
      "quantity": 2
    }
  ]
}

Response: 200 OK
```

### 7. Get Orders
```
GET /api/pharmacy/orders?page=0&size=20
Authorization: Bearer {patient_token}
✓ Requires: PATIENT role

Response: 200 OK
```

### 8. Get Order by ID
```
GET /api/pharmacy/orders/{id}
Authorization: Bearer {patient_token}

Response: 200 OK
```

### 9. Cancel Order
```
POST /api/pharmacy/orders/{id}/cancel
Authorization: Bearer {patient_token}

Response: 200 OK
```

### 10. Request Refill
```
POST /api/pharmacy/refills
Authorization: Bearer {patient_token}
Content-Type: application/json

Body:
{
  "prescriptionId": 1
}

Response: 200 OK
```

### 11. Get Refills
```
GET /api/pharmacy/refills?page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
```

### 12. Delete Refill Request
```
DELETE /api/pharmacy/refills/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 13. Check Drug Interactions
```
POST /api/pharmacy/interactions/check
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "medicineIds": [1, 2, 3]
}

Response: 200 OK
{
  "interactions": [
    {
      "medicine1": "Aspirin",
      "medicine2": "Warfarin",
      "severity": "HIGH",
      "description": "..."
    }
  ]
}
```

### 14. Get Inventory
```
GET /api/pharmacy/inventory?page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
```

### 15. Update Inventory
```
PUT /api/pharmacy/inventory
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "medicineId": 1,
  "quantity": 100
}

Response: 200 OK
```

### 16. Delete Inventory Entry
```
DELETE /api/pharmacy/inventory/{medicineId}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 📋 Prescriptions

### 1. Create Prescription
```
POST /prescriptions
Authorization: Bearer {doctor_token}
Content-Type: application/json

Body:
{
  "medicalRecordId": 1,
  "medicationName": "Aspirin",
  "dosage": "500mg",
  "duration": "7 days",
  "instructions": "Take with water",
  "prescriptionDate": "2026-03-30"
}

Response: 200 OK
```

### 2. Get Prescription by ID
```
GET /prescriptions/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 3. Get Prescriptions by Medical Record
```
GET /prescriptions/medical-record/{medicalRecordId}
Authorization: Bearer {token}

Response: 200 OK
```

### 4. Update Prescription
```
PUT /prescriptions/{id}
Authorization: Bearer {token}

Response: 200 OK
```

### 5. Delete Prescription
```
DELETE /prescriptions/{id}
Authorization: Bearer {token}

Response: 200 OK
```

---

## 👤 Patient

### 1. Patient Hello
```
GET /patient/hello
Authorization: Bearer {patient_token}

Response: 200 OK
{
  "message": "Hello PATIENT !"
}
```

---

## 🔒 Security Notes

### Authentication Header
```
Authorization: Bearer <JWT_TOKEN>
```

### Roles
- **PATIENT**: Can create their own records, appointments
- **DOCTOR**: Can create prescriptions, lab results, visit notes
- **PHARMACIST**: Can manage pharmacy orders, inventories
- **ADMIN**: Full access to all endpoints

### Token Generation (from Login)
```javascript
// After login, you receive:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@example.com",
  "role": "ROLE_PATIENT"
}

// Use token in all subsequent requests:
headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 📚 Common Status Codes

- **200 OK**: Successful request
- **201 Created**: Resource created successfully
- **204 No Content**: Successful request with no response body (DELETE)
- **400 Bad Request**: Invalid input
- **401 Unauthorized**: Missing or invalid authentication token
- **403 Forbidden**: Authenticated but not authorized for this resource
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists or business logic conflict
- **500 Internal Server Error**: Server error

---

## 🧪 Testing with Swagger

1. Open **http://localhost:8089/MediCareAI/swagger-ui/index.html**
2. Click **"Authorize"** button
3. Paste your JWT token (without "Bearer" prefix)
4. Click "Authorize" and then "Close"
5. All endpoints are now accessible in Swagger UI

---

**Document Date:** 2026-03-30  
**API Version:** 1.0

---

## 🔎 Advanced Scheduling and Search (Medical Modules)

### Appointments - Upcoming Window (JPQL with joins)
```
GET /appointments/upcoming?doctorKeyword=house&windowMinutes=30
Authorization: Bearer {doctor_or_admin_token}
✓ Requires: DOCTOR or ADMIN role

Response: 200 OK
[
  {
    "id": 21,
    "status": "CONFIRMED",
    "appointmentDate": "2026-04-14T15:00:00",
    "doctor": { "id": 2, "fullName": "Dr House" },
    "patient": { "id": 8, "fullName": "Ali Ben" }
  }
]
```

### Appointments - Keyword Search (multi-table)
```
GET /appointments/search?doctorId=2&patientKeyword=ali&reasonKeyword=follow
Authorization: Bearer {token}
✓ Requires: PATIENT, DOCTOR, or ADMIN role

Behavior:
- PATIENT: returns only current patient's appointments
- DOCTOR: forced to current doctor id
- ADMIN: can filter by all parameters
```

### Visit Notes - Clinical Search (JPQL joins)
```
GET /visit-notes/search?patientKeyword=ali&doctorKeyword=house&clinicalKeyword=fever
Authorization: Bearer {token}
✓ Requires: PATIENT, DOCTOR, or ADMIN role

Response: 200 OK
[
  {
    "id": 15,
    "subjective": "Patient reports fever",
    "assessment": "Viral syndrome",
    "visitDate": "2026-04-10T09:15:00"
  }
]
```

### Background Scheduler (backend)
- Cron key: `app.appointments.reminder.cron`
- Window key: `app.appointments.reminder.window-minutes`
- Current business logic:
  1. Expires past `PENDING` appointments to `EXPIRED`
  2. Computes upcoming appointments count in configured window
  3. Logs execution summary
