# 🔌 Guide d'Intégration Frontend Angular 17+ avec Backend Medicare AI

## 📦 Structure des Endpoints Disponibles

### ✅ Endpoints Disponibles

#### **Authentication (Public)**
```
POST   /auth/register              - S'inscrire
POST   /auth/login                 - Se connecter
GET    /auth/users                 - Lister tous les utilisateurs (ADMIN)
GET    /auth/users/{id}            - Récupérer un utilisateur (ADMIN)
PUT    /auth/users/{id}            - Modifier un utilisateur (ADMIN)
DELETE /auth/users/{id}            - Supprimer un utilisateur (ADMIN)
GET    /auth/doctors               - Rechercher des docteurs (Authentifié)
```

#### **Medical Records (Authenticated)**
```
GET    /medical-records            - Lister tous (ADMIN/DOCTOR) ou le sien (PATIENT)
GET    /medical-records/{id}       - Récupérer un dossier
GET    /medical-records/me         - Récupérer mon dossier (PATIENT)
POST   /medical-records            - Créer un dossier (PATIENT)
PUT    /medical-records/{id}       - Modifier un dossier (PATIENT/DOCTOR/ADMIN)
DELETE /medical-records/{id}       - Supprimer un dossier (PATIENT/ADMIN)
GET    /medical-records/patient/{patientId} - Récupérer par patient ID
```

#### **Visit Notes (Authenticated)**
```
GET    /visit-notes/{id}           - Récupérer une note
POST   /visit-notes                - Créer une note
PUT    /visit-notes/{id}           - Modifier une note
DELETE /visit-notes/{id}           - Supprimer une note
GET    /visit-notes/medical-record/{medicalRecordId} - Récupérer les notes d'un dossier
```

#### **Prescriptions (Authenticated)**
```
GET    /prescriptions/{id}         - Récupérer une ordonnance
POST   /prescriptions              - Créer une ordonnance
PUT    /prescriptions/{id}         - Modifier une ordonnance
DELETE /prescriptions/{id}         - Supprimer une ordonnance
GET    /prescriptions/medical-record/{medicalRecordId} - Récupérer les ordonnances
```

#### **Lab Results (Authenticated)**
```
GET    /lab-results/{id}           - Récupérer un résultat
POST   /lab-results                - Créer un résultat
PUT    /lab-results/{id}           - Modifier un résultat
DELETE /lab-results/{id}           - Supprimer un résultat
GET    /lab-results/medical-record/{medicalRecordId} - Récupérer les résultats
```

#### **Medical Images (Authenticated)**
```
GET    /medical-images/{id}        - Récupérer une image
POST   /medical-images             - Créer une image
PUT    /medical-images/{id}        - Modifier une image
DELETE /medical-images/{id}        - Supprimer une image
GET    /medical-images/medical-record/{medicalRecordId} - Récupérer les images
```

#### **Allergies (Authenticated)**
```
GET    /allergies/{id}             - Récupérer une allergie
POST   /allergies                  - Créer une allergie
PUT    /allergies/{id}             - Modifier une allergie
DELETE /allergies/{id}             - Supprimer une allergie
GET    /allergies/medical-record/{medicalRecordId} - Récupérer les allergies
```

#### **Appointments (Authenticated)**
```
GET    /appointments               - Lister tous les rendez-vous
GET    /appointments/{id}          - Récupérer un rendez-vous
GET    /appointments/patient/{patientId} - Rendez-vous d'un patient
GET    /appointments/doctor/{doctorId}   - Rendez-vous d'un docteur
POST   /appointments               - Créer un rendez-vous
PUT    /appointments/{id}          - Modifier un rendez-vous
DELETE /appointments/{id}          - Supprimer un rendez-vous

# Téléconsultation
POST   /appointments/{appointmentId}/teleconsultation/start - Lancer la vidéoconférence
POST   /appointments/{appointmentId}/teleconsultation/join  - Rejoindre la vidéoconférence
GET    /appointments/{appointmentId}/teleconsultation - Récupérer l'état

# Rappels
GET    /appointments/{appointmentId}/reminders - Lister les rappels
POST   /appointments/{appointmentId}/reminders/schedule - Planifier un rappel
POST   /appointments/{appointmentId}/reminders/send - Envoyer un rappel
```

#### **Availabilities (Authenticated)**
```
GET    /availabilities/doctor/{doctorId}       - Disponibilités du docteur
GET    /availabilities/doctor/{doctorId}/available - Disponibilités libres
POST   /availabilities                         - Créer une disponibilité
PUT    /availabilities/{id}                    - Modifier une disponibilité
DELETE /availabilities/{id}                    - Supprimer une disponibilité
```

#### **Pharmacy (PHARMACIST)**
```
GET    /api/pharmacy/medicines                 - Lister les médicaments
GET    /api/pharmacy/medicines/{id}            - Détails d'un médicament
GET    /api/pharmacy/prescriptions             - Lister les ordonnances
GET    /api/pharmacy/prescriptions/{id}        - Détails d'une ordonnance
POST   /api/pharmacy/prescriptions/upload      - Uploader une ordonnance
GET    /api/pharmacy/orders                    - Lister les commandes
POST   /api/pharmacy/orders                    - Créer une commande
GET    /api/pharmacy/orders/{id}               - Détails d'une commande
DELETE /api/pharmacy/orders/{id}               - Annuler une commande
GET    /api/pharmacy/refills                   - Lister les renouvellements
POST   /api/pharmacy/refills                   - Demander un renouvellement
DELETE /api/pharmacy/refills/{id}              - Annuler un renouvellement
POST   /api/pharmacy/interactions/check        - Vérifier les interactions
```

#### **Patient (PATIENT)**
```
GET    /patient/hello              - Vérification de l'accès patient
```

---

## 🎯 Implementation Angular

### 1. Créer un Service d'Authentification

```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8089/MediCareAI';
  private tokenSubject = new BehaviorSubject<string | null>(
    localStorage.getItem('authToken')
  );

  constructor(private http: HttpClient) {}

  register(firstName: string, lastName: string, email: string, 
           password: string, role: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, {
      firstName, lastName, email, password, role
    });
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, {
      email, password
    }).pipe(
      tap(response => {
        localStorage.setItem('authToken', response.token);
        this.tokenSubject.next(response.token);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('authToken');
    this.tokenSubject.next(null);
  }

  getToken(): string | null {
    return this.tokenSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

### 2. Créer un HTTP Interceptor

```typescript
// auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}
```

### 3. Ajouter au module (app.config.ts pour Angular 17)

```typescript
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideHttpClient, withInterceptors, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([])
    ),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
};
```

### 4. Service Medical Records

```typescript
// medical-record.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MedicalRecordService {
  private apiUrl = 'http://localhost:8089/MediCareAI/medical-records';

  constructor(private http: HttpClient) {}

  getMyRecord(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  getAllRecords(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getByPatientId(patientId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/patient/${patientId}`);
  }

  create(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, data);
  }

  update(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
```

### 5. Service Appointments

```typescript
// appointment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private apiUrl = 'http://localhost:8089/MediCareAI/appointments';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getByPatientId(patientId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/patient/${patientId}`);
  }

  getByDoctorId(doctorId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/doctor/${doctorId}`);
  }

  create(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, data);
  }

  update(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // Téléconsultation
  startTeleconsultation(appointmentId: number): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${appointmentId}/teleconsultation/start`, {}
    );
  }

  joinTeleconsultation(appointmentId: number): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${appointmentId}/teleconsultation/join`, {}
    );
  }

  getTeleconsultationStatus(appointmentId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/${appointmentId}/teleconsultation`
    );
  }

  // Reminders
  getReminders(appointmentId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/${appointmentId}/reminders`
    );
  }

  scheduleReminder(appointmentId: number, data: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${appointmentId}/reminders/schedule`, data
    );
  }

  sendReminder(appointmentId: number): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${appointmentId}/reminders/send`, {}
    );
  }
}
```

### 6. Component Exemple - Créer un Rendez-vous

```typescript
import { Component } from '@angular/core';
import { AppointmentService } from './appointment.service';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-book-appointment',
  templateUrl: './book-appointment.component.html'
})
export class BookAppointmentComponent {
  doctorId: number = 1;
  appointmentDate: string = '';
  appointmentTime: string = '';
  reason: string = '';

  constructor(
    private appointmentService: AppointmentService,
    private authService: AuthService
  ) {}

  bookAppointment(): void {
    if (!this.authService.isAuthenticated()) {
      alert('Veuillez vous authentifier');
      return;
    }

    const appointment = {
      doctorId: this.doctorId,
      appointmentDate: this.appointmentDate,
      appointmentTime: this.appointmentTime,
      reason: this.reason
    };

    this.appointmentService.create(appointment).subscribe({
      next: (response) => {
        alert('Rendez-vous créé avec succès!');
        console.log(response);
      },
      error: (error) => {
        console.error('Erreur:', error);
        alert('Erreur lors de la création du rendez-vous');
      }
    });
  }
}
```

---

## 🔒 Configuration CORS

Le backend est déjà configuré pour accepter les requêtes d'Angular (localhost:4200):

```typescript
// SecurityConfig.java
config.setAllowedOrigins(List.of("http://localhost:4200"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(true);
```

---

## 📝 Modèles de Données

### MedicalRecordDTO
```typescript
interface MedicalRecord {
  id: number;
  bloodType: string;
  height: number;
  weight: number;
  dateOfBirth: string; // YYYY-MM-DD
  medicalHistory: string;
  chronicDiseases: string;
}
```

### AppointmentDTO
```typescript
interface Appointment {
  id: number;
  doctorId: number;
  patientId: number;
  appointmentDate: string; // YYYY-MM-DD
  appointmentTime: string; // HH:MM
  reason: string;
  status: string; // PENDING, CONFIRMED, CANCELLED, COMPLETED
}
```

### AvailabilityDTO
```typescript
interface Availability {
  id: number;
  doctorId: number;
  date: string; // YYYY-MM-DD
  startTime: string; // HH:MM
  endTime: string; // HH:MM
  isAvailable: boolean;
}
```

---

## ✅ Checklist d'Intégration

- [ ] Créer AuthService
- [ ] Créer AuthInterceptor
- [ ] Créer MedicalRecordService
- [ ] Créer AppointmentService
- [ ] Ajouter HTTP_INTERCEPTORS au module
- [ ] Configurer l'URL de base du backend
- [ ] Tester l'authentification
- [ ] Tester la création de dossier médical
- [ ] Tester la création de rendez-vous
- [ ] Tester la récupération de données

---

## 🚀 Déploiement

### Variables d'environnement (environment.ts)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8089/MediCareAI'
};
```

### Production (environment.prod.ts)

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-domain.com/MediCareAI'
};
```

