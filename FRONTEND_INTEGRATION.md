# Frontend Integration Examples

## Configuration Base

### Environment Variables (.env)
```env
REACT_APP_API_BASE_URL=http://localhost:8089/MediCareAI
REACT_APP_API_TIMEOUT=10000
```

---

## 1. Service de Configuration HTTP

### axios.config.js ou api.service.ts
```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8089/MediCareAI';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré, rediriger vers login
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 2. Service d'Authentification

### AuthService.js
```javascript
import api from './api.service';

class AuthService {
  // Register
  async register(userData) {
    const response = await api.post('/auth/register', {
      email: userData.email,
      password: userData.password,
      fullName: userData.fullName,
      role: userData.role, // PATIENT, DOCTOR, PHARMACIST, ADMIN
    });
    return response.data;
  }

  // Login
  async login(email, password) {
    const response = await api.post('/auth/login', { email, password });
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('userRole', response.data.role);
      localStorage.setItem('userEmail', response.data.username);
    }
    return response.data;
  }

  // Logout
  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userEmail');
  }

  // Search Doctors
  async searchDoctors(query = '', page = 0, size = 20) {
    const response = await api.get('/auth/doctors', {
      params: { query, page, size },
    });
    return response.data;
  }

  // Get User by ID
  async getUserById(id) {
    const response = await api.get(`/auth/users/${id}`);
    return response.data;
  }

  // Get All Users (Admin)
  async getAllUsers(query = '', role = '', page = 0, size = 20) {
    const response = await api.get('/auth/users', {
      params: { query, role, page, size },
    });
    return response.data;
  }

  // Update User (Admin)
  async updateUser(id, userData) {
    const response = await api.put(`/auth/users/${id}`, {
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password,
      role: userData.role,
      enabled: userData.enabled,
    });
    return response.data;
  }

  // Delete User (Admin)
  async deleteUser(id) {
    await api.delete(`/auth/users/${id}`);
  }
}

export default new AuthService();
```

---

## 3. Service des Dossiers Médicaux

### MedicalRecordService.js
```javascript
import api from './api.service';

class MedicalRecordService {
  // Create Medical Record (Patient)
  async createMedicalRecord(data) {
    const response = await api.post('/medical-records', {
      bloodType: data.bloodType,
      allergies: data.allergies,
      chronicDiseases: data.chronicDiseases,
      notes: data.notes,
    });
    return response.data;
  }

  // Get All Medical Records
  async getAllMedicalRecords() {
    const response = await api.get('/medical-records');
    return response.data;
  }

  // Get Medical Record by ID
  async getMedicalRecordById(id) {
    const response = await api.get(`/medical-records/${id}`);
    return response.data;
  }

  // Get My Medical Record (Patient)
  async getMyMedicalRecord() {
    const response = await api.get('/medical-records/me');
    return response.data;
  }

  // Get Medical Records by Patient ID
  async getMedicalRecordsByPatientId(patientId) {
    const response = await api.get(`/medical-records/patient/${patientId}`);
    return response.data;
  }

  // Update Medical Record
  async updateMedicalRecord(id, data) {
    const response = await api.put(`/medical-records/${id}`, {
      bloodType: data.bloodType,
      allergies: data.allergies,
      chronicDiseases: data.chronicDiseases,
      notes: data.notes,
    });
    return response.data;
  }

  // Delete Medical Record
  async deleteMedicalRecord(id) {
    await api.delete(`/medical-records/${id}`);
  }
}

export default new MedicalRecordService();
```

---

## 4. Service des Rendez-vous

### AppointmentService.js
```javascript
import api from './api.service';

class AppointmentService {
  // Create Appointment (Patient)
  async createAppointment(data) {
    const response = await api.post('/appointments', {
      doctorId: data.doctorId,
      appointmentDate: data.appointmentDate,
      reason: data.reason,
      location: data.location,
    });
    return response.data;
  }

  // Get All Appointments
  async getAllAppointments() {
    const response = await api.get('/appointments');
    return response.data;
  }

  // Get Appointment by ID
  async getAppointmentById(id) {
    const response = await api.get(`/appointments/${id}`);
    return response.data;
  }

  // Get Appointments by Patient
  async getAppointmentsByPatient(patientId) {
    const response = await api.get(`/appointments/patient/${patientId}`);
    return response.data;
  }

  // Get Appointments by Doctor
  async getAppointmentsByDoctor(doctorId) {
    const response = await api.get(`/appointments/doctor/${doctorId}`);
    return response.data;
  }

  // Update Appointment
  async updateAppointment(id, data) {
    const response = await api.put(`/appointments/${id}`, {
      appointmentDate: data.appointmentDate,
      reason: data.reason,
      location: data.location,
    });
    return response.data;
  }

  // Delete Appointment
  async deleteAppointment(id) {
    await api.delete(`/appointments/${id}`);
  }

  // Start Teleconsultation
  async startTeleconsultation(appointmentId) {
    const response = await api.post(`/appointments/${appointmentId}/teleconsultation/start`);
    return response.data;
  }

  // Join Teleconsultation
  async joinTeleconsultation(appointmentId) {
    const response = await api.post(`/appointments/${appointmentId}/teleconsultation/join`);
    return response.data;
  }

  // Schedule Reminder
  async scheduleReminder(appointmentId) {
    const response = await api.post(`/appointments/${appointmentId}/reminders/schedule`);
    return response.data;
  }
}

export default new AppointmentService();
```

---

## 5. Service Pharmacie

### PharmacyService.js
```javascript
import api from './api.service';

class PharmacyService {
  // Search Medicines
  async searchMedicines(keyword = '', category = '', page = 0, size = 20) {
    const response = await api.get('/api/pharmacy/medicines', {
      params: { keyword, category, page, size },
    });
    return response.data;
  }

  // Get Medicine Details
  async getMedicineDetails(id) {
    const response = await api.get(`/api/pharmacy/medicines/${id}`);
    return response.data;
  }

  // Create Prescription (Doctor)
  async createPrescription(data) {
    const response = await api.post('/api/pharmacy/prescriptions', {
      patientId: data.patientId,
      issueDate: data.issueDate,
      expiryDate: data.expiryDate,
      items: data.items, // Array of {medicineId, dosage, frequency, duration}
    });
    return response.data;
  }

  // Get Prescriptions
  async getPrescriptions(page = 0, size = 20) {
    const response = await api.get('/api/pharmacy/prescriptions', {
      params: { page, size },
    });
    return response.data;
  }

  // Get Prescription by ID
  async getPrescriptionById(id) {
    const response = await api.get(`/api/pharmacy/prescriptions/${id}`);
    return response.data;
  }

  // Place Order (Patient)
  async placeOrder(data) {
    const response = await api.post('/api/pharmacy/orders', {
      prescriptionId: data.prescriptionId || null,
      items: data.items, // Array of {medicineId, quantity}
    });
    return response.data;
  }

  // Get Order History
  async getOrderHistory(page = 0, size = 20) {
    const response = await api.get('/api/pharmacy/orders', {
      params: { page, size },
    });
    return response.data;
  }

  // Get Order by ID
  async getOrderById(id) {
    const response = await api.get(`/api/pharmacy/orders/${id}`);
    return response.data;
  }

  // Cancel Order
  async cancelOrder(id) {
    const response = await api.post(`/api/pharmacy/orders/${id}/cancel`);
    return response.data;
  }

  // Request Refill
  async requestRefill(prescriptionId) {
    const response = await api.post('/api/pharmacy/refills', {
      prescriptionId,
    });
    return response.data;
  }

  // Check Drug Interactions
  async checkDrugInteractions(medicineIds) {
    const response = await api.post('/api/pharmacy/interactions/check', {
      medicineIds,
    });
    return response.data;
  }
}

export default new PharmacyService();
```

---

## 6. Service des Allergies

### AllergyService.js
```javascript
import api from './api.service';

class AllergyService {
  // Create Allergy (Doctor/Admin)
  async createAllergy(data) {
    const response = await api.post('/allergies', {
      medicalRecordId: data.medicalRecordId,
      allergyName: data.allergyName,
      severity: data.severity, // LOW, MEDIUM, HIGH, CRITICAL
      reaction: data.reaction,
      notes: data.notes,
    });
    return response.data;
  }

  // Get Allergy by ID
  async getAllergyById(id) {
    const response = await api.get(`/allergies/${id}`);
    return response.data;
  }

  // Get Allergies by Medical Record
  async getAllergiesByMedicalRecord(medicalRecordId) {
    const response = await api.get(`/allergies/medical-record/${medicalRecordId}`);
    return response.data;
  }

  // Update Allergy
  async updateAllergy(id, data) {
    const response = await api.put(`/allergies/${id}`, {
      allergyName: data.allergyName,
      severity: data.severity,
      reaction: data.reaction,
      notes: data.notes,
    });
    return response.data;
  }

  // Delete Allergy
  async deleteAllergy(id) {
    await api.delete(`/allergies/${id}`);
  }
}

export default new AllergyService();
```

---

## 7. Service des Résultats de Labo

### LabResultService.js
```javascript
import api from './api.service';

class LabResultService {
  // Create Lab Result (Doctor/Admin)
  async createLabResult(data) {
    const response = await api.post('/lab-results', {
      medicalRecordId: data.medicalRecordId,
      testName: data.testName,
      result: data.result,
      unit: data.unit,
      normalRange: data.normalRange,
      testDate: data.testDate,
      notes: data.notes,
    });
    return response.data;
  }

  // Get Lab Result by ID
  async getLabResultById(id) {
    const response = await api.get(`/lab-results/${id}`);
    return response.data;
  }

  // Get Lab Results by Medical Record
  async getLabResultsByMedicalRecord(medicalRecordId) {
    const response = await api.get(`/lab-results/medical-record/${medicalRecordId}`);
    return response.data;
  }

  // Update Lab Result
  async updateLabResult(id, data) {
    const response = await api.put(`/lab-results/${id}`, {
      testName: data.testName,
      result: data.result,
      unit: data.unit,
      normalRange: data.normalRange,
      testDate: data.testDate,
      notes: data.notes,
    });
    return response.data;
  }

  // Delete Lab Result
  async deleteLabResult(id) {
    await api.delete(`/lab-results/${id}`);
  }
}

export default new LabResultService();
```

---

## 8. React Component Example

### LoginComponent.jsx
```javascript
import React, { useState } from 'react';
import authService from '../services/AuthService';

function LoginComponent() {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authService.login(formData.email, formData.password);
      console.log('Login successful:', response);
      // Redirect to dashboard or home
      window.location.href = '/dashboard';
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <h2>Login</h2>
      {error && <div className="error">{error}</div>}
      
      <input
        type="email"
        name="email"
        placeholder="Email"
        value={formData.email}
        onChange={handleChange}
        required
      />
      
      <input
        type="password"
        name="password"
        placeholder="Password"
        value={formData.password}
        onChange={handleChange}
        required
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
}

export default LoginComponent;
```

---

## 9. React Hook pour Authentification

### useAuth.js
```javascript
import { useState, useCallback } from 'react';
import authService from '../services/AuthService';

function useAuth() {
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem('authToken');
    const role = localStorage.getItem('userRole');
    const email = localStorage.getItem('userEmail');
    return token ? { token, role, email } : null;
  });

  const login = useCallback(async (email, password) => {
    const response = await authService.login(email, password);
    setUser({
      token: response.token,
      role: response.role,
      email: response.username,
    });
    return response;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const isAuthenticated = !!user?.token;
  const hasRole = (role) => user?.role === `ROLE_${role}`;

  return {
    user,
    login,
    logout,
    isAuthenticated,
    hasRole,
  };
}

export default useAuth;
```

---

## 10. Protected Route Component

### ProtectedRoute.jsx
```javascript
import React from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

function ProtectedRoute({ element, requiredRole }) {
  const { isAuthenticated, hasRole } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (requiredRole && !hasRole(requiredRole)) {
    return <Navigate to="/unauthorized" />;
  }

  return element;
}

export default ProtectedRoute;
```

---

## 11. Utilisation dans App.jsx

```javascript
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginComponent from './components/LoginComponent';
import DashboardComponent from './components/DashboardComponent';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginComponent />} />
        
        <Route
          path="/dashboard"
          element={<ProtectedRoute element={<DashboardComponent />} />}
        />
        
        <Route
          path="/admin"
          element={<ProtectedRoute element={<AdminPanel />} requiredRole="ADMIN" />}
        />
        
        <Route
          path="/pharmacy"
          element={<ProtectedRoute element={<PharmacyPanel />} requiredRole="PHARMACIST" />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

---

## ✅ Checklist d'Intégration Frontend

- [ ] Configurer axios avec baseURL et intercepteurs
- [ ] Créer les services pour chaque module
- [ ] Implémenter useAuth hook
- [ ] Créer ProtectedRoute component
- [ ] Stocker le token JWT dans localStorage
- [ ] Ajouter gestion d'erreur globale
- [ ] Configurer variables d'environnement
- [ ] Tester authentification avec Swagger d'abord
- [ ] Valider chaque endpoint avant intégration
- [ ] Implémenter pagination
- [ ] Ajouter loading states
- [ ] Implémenter error handling

---

**Document Date:** 2026-03-30

