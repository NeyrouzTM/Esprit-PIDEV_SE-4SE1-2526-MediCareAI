# 📋 Guide Complet de Test Swagger - Medicare AI

## 🔐 Étape 1 : Authentification (Obligatoire)

### 1.1 S'inscrire (Register)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "Password123",
    "role": "PATIENT"
  }'
```

**Réponse attendue:** `"User created: john@example.com"`

---

### 1.2 Se Connecter (Login)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "john@example.com",
    "password": "Password123"
  }'
```

**Réponse attendue:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

**⚠️ Copiez ce token - vous en aurez besoin pour les autres requêtes!**

---

## 🏥 Étape 2 : Créer un Dossier Médical

### 2.1 POST /medical-records (Créer le dossier)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/medical-records' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "bloodType": "O+",
    "height": 180.5,
    "weight": 75.0,
    "dateOfBirth": "1990-01-15",
    "medicalHistory": "Hypertension, Diabète",
    "chronicDiseases": "Hypertension"
  }'
```

**Note:** Ne incluez PAS `patientId` - il est automatiquement pris de votre token !

---

## 📝 Étape 3 : Ajouter des Données Cliniques

### 3.1 POST /visit-notes (Ajouter une note de visite)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/visit-notes' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "medicalRecordId": 1,
    "doctorId": 2,
    "visitDate": "2026-03-31",
    "notes": "Consultation générale",
    "diagnosis": "Tension artérielle élevée",
    "treatment": "Repos recommandé"
  }'
```

### 3.2 POST /prescriptions (Ajouter une ordonnance)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/prescriptions' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "medicalRecordId": 1,
    "doctorId": 2,
    "medicationName": "Amoxicilline",
    "dosage": "500mg",
    "duration": "7 jours",
    "instructions": "3 fois par jour",
    "prescriptionDate": "2026-03-31"
  }'
```

### 3.3 POST /lab-results (Ajouter des résultats de labo)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/lab-results' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "medicalRecordId": 1,
    "testName": "Analyse de sang",
    "testDate": "2026-03-30",
    "results": "Tous les résultats normaux",
    "doctorId": 2
  }'
```

### 3.4 POST /medical-images (Ajouter des images médicales)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/medical-images' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "medicalRecordId": 1,
    "imageType": "RADIOGRAPHY",
    "uploadDate": "2026-03-31",
    "description": "Radiographie pulmonaire",
    "doctorId": 2
  }'
```

### 3.5 POST /allergies (Ajouter des allergies)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/allergies' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "medicalRecordId": 1,
    "allergen": "Pénicilline",
    "severity": "SEVERE",
    "reaction": "Anaphylaxie"
  }'
```

---

## 📅 Étape 4 : Rendez-vous et Disponibilités

### 4.1 POST /availabilities (Ajouter disponibilités - Docteur)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/availabilities' \
  -H 'Authorization: Bearer DOCTOR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "doctorId": 2,
    "date": "2026-04-05",
    "startTime": "09:00",
    "endTime": "12:00",
    "isAvailable": true
  }'
```

### 4.2 POST /appointments (Créer un rendez-vous - Patient)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/appointments' \
  -H 'Authorization: Bearer PATIENT_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "doctorId": 2,
    "appointmentDate": "2026-04-05",
    "appointmentTime": "10:00",
    "reason": "Consultation générale"
  }'
```

### 4.3 GET /appointments/{appointmentId}/teleconsultation/start (Lancer vidéoconférence)
```bash
curl -X 'POST' \
  'http://localhost:8089/MediCareAI/appointments/1/teleconsultation/start' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

---

## 🔍 Étape 5 : Récupérer les Données (GET)

### 5.1 Récupérer VOTRE dossier médical
```bash
curl -X 'GET' \
  'http://localhost:8089/MediCareAI/medical-records/me' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

### 5.2 Récupérer tous les dossiers (Admin seulement)
```bash
curl -X 'GET' \
  'http://localhost:8089/MediCareAI/medical-records' \
  -H 'Authorization: Bearer ADMIN_TOKEN_HERE'
```

### 5.3 Récupérer dossier par ID
```bash
curl -X 'GET' \
  'http://localhost:8089/MediCareAI/medical-records/1' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

### 5.4 Récupérer les notes de visite
```bash
curl -X 'GET' \
  'http://localhost:8089/MediCareAI/visit-notes/1' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

### 5.5 Rechercher des docteurs
```bash
curl -X 'GET' \
  'http://localhost:8089/MediCareAI/auth/doctors?query=smith' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

---

## 🔧 Étape 6 : Mettre à Jour/Supprimer

### 6.1 PUT /medical-records/{id} (Modifier)
```bash
curl -X 'PUT' \
  'http://localhost:8089/MediCareAI/medical-records/1' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "weight": 76.5,
    "bloodType": "AB+"
  }'
```

### 6.2 DELETE /medical-records/{id} (Supprimer)
```bash
curl -X 'DELETE' \
  'http://localhost:8089/MediCareAI/medical-records/1' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE'
```

---

## 🎯 Accès Swagger UI

**URL:** http://localhost:8089/MediCareAI/swagger-ui/index.html

### Comment utiliser Swagger :

1. **Ouvrir Swagger UI**
2. **Cliquer sur "Authorize"** (en haut à droite)
3. **Coller votre Bearer token** dans le champ "bearerAuth"
4. **Tester chaque endpoint** en cliquant sur "Try it out"

---

## 🗝️ Permissions par Rôle

| Endpoint | PATIENT | DOCTOR | PHARMACIST | ADMIN |
|----------|---------|--------|-----------|-------|
| POST /medical-records | ✅ | ❌ | ❌ | ✅ |
| GET /medical-records | ✅* | ✅ | ❌ | ✅ |
| POST /appointments | ✅ | ✅ | ❌ | ✅ |
| GET /auth/users | ❌ | ❌ | ❌ | ✅ |
| PUT /auth/users/{id} | ❌ | ❌ | ❌ | ✅ |

*PATIENT ne peut voir que son propre dossier

---

## ❌ Erreurs Courantes

### 401 Unauthorized
**Cause:** Token manquant ou expiré
**Solution:** Réenregistrez-vous et obtenez un nouveau token

### 403 Forbidden
**Cause:** Vous n'avez pas les permissions requises
**Solution:** Vérifiez votre rôle et les permissions

### 400 Bad Request
**Cause:** Données invalides ou dossier médical déjà existant
**Solution:** Vérifiez le format des données

---

## 📌 Points Importants

- ✅ **SecurityConfig corrigée** pour exposer tous les endpoints
- ✅ **JWT Token requis** pour tous les endpoints (sauf /auth/**)
- ✅ **Autorités ROLE_PATIENT, ROLE_DOCTOR** etc ajoutées au token
- ✅ **Tous les endpoints disponibles** dans Swagger
- ✅ **CORS activé** pour localhost:4200 (Angular)

