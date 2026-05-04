# Frontend Integration Examples - Angular 17+

## Configuration Base

### Environment Variables (environment.ts)
```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8089/MediCareAI',
  apiTimeout: 10000,
};
```

### Environment Production (environment.prod.ts)
```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.medicare-ai.com',
  apiTimeout: 10000,
};
```

---

## 1. Service de Configuration HTTP (HttpClient)

### api.service.ts
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private apiUrl = environment.apiBaseUrl;
  private tokenSubject = new BehaviorSubject<string | null>(
    this.getStoredToken()
  );
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Set token after login
  setToken(token: string): void {
    localStorage.setItem('authToken', token);
    this.tokenSubject.next(token);
  }

  // Get token from storage
  private getStoredToken(): string | null {
    return localStorage.getItem('authToken');
  }

  // Clear token on logout
  clearToken(): void {
    localStorage.removeItem('authToken');
    this.tokenSubject.next(null);
  }

  // Get Authorization Header
  private getHeaders(): HttpHeaders {
    const token = this.getStoredToken();
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    if (token) {
      return headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  // GET request
  get<T>(endpoint: string, options?: any): Observable<T> {
    return this.http
      .get<T>(`${this.apiUrl}${endpoint}`, {
        headers: this.getHeaders(),
        ...options,
      })
      .pipe(
        catchError((error) => this.handleError(error))
      );
  }

  // POST request
  post<T>(endpoint: string, body: any, options?: any): Observable<T> {
    return this.http
      .post<T>(`${this.apiUrl}${endpoint}`, body, {
        headers: this.getHeaders(),
        ...options,
      })
      .pipe(
        catchError((error) => this.handleError(error))
      );
  }

  // PUT request
  put<T>(endpoint: string, body: any, options?: any): Observable<T> {
    return this.http
      .put<T>(`${this.apiUrl}${endpoint}`, body, {
        headers: this.getHeaders(),
        ...options,
      })
      .pipe(
        catchError((error) => this.handleError(error))
      );
  }

  // DELETE request
  delete<T>(endpoint: string, options?: any): Observable<T> {
    return this.http
      .delete<T>(`${this.apiUrl}${endpoint}`, {
        headers: this.getHeaders(),
        ...options,
      })
      .pipe(
        catchError((error) => this.handleError(error))
      );
  }

  // Error handling
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.status === 401) {
        // Token expired
        this.clearToken();
        window.location.href = '/login';
      }
      errorMessage = error.error?.message || `Error: ${error.status}`;
    }

    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
```

---

## 2. Models/Interfaces

### auth.model.ts
```typescript
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  role: 'PATIENT' | 'DOCTOR' | 'PHARMACIST' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
}

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  role: string;
  enabled: boolean;
}

export interface UserUpdateRequest {
  fullName?: string;
  email?: string;
  password?: string;
  role?: string;
  enabled?: boolean;
}
```

### medical-record.model.ts
```typescript
export interface MedicalRecord {
  id: number;
  patientId: number;
  bloodType: string;
  allergies: string;
  chronicDiseases: string;
  notes: string;
}

export interface MedicalRecordDTO {
  bloodType: string;
  allergies: string;
  chronicDiseases: string;
  notes: string;
}
```

### appointment.model.ts
```typescript
export interface Appointment {
  id: number;
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  reason: string;
  location: string;
}

export interface AppointmentDTO {
  doctorId: number;
  appointmentDate: string;
  reason: string;
  location: string;
}
```

### allergy.model.ts
```typescript
export interface Allergy {
  id: number;
  medicalRecordId: number;
  allergyName: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  reaction: string;
  notes: string;
}

export interface AllergyDTO {
  medicalRecordId: number;
  allergyName: string;
  severity: string;
  reaction: string;
  notes: string;
}
```

---

## 3. Service d'Authentification

### auth.service.ts
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { ApiService } from './api.service';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  UserResponse,
  UserUpdateRequest,
} from '../models/auth.model';
import { PageResponse } from '../models/page.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isLoggedInSubject = new BehaviorSubject<boolean>(
    !!localStorage.getItem('authToken')
  );
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private apiService: ApiService) {}

  // Register
  register(userData: RegisterRequest): Observable<any> {
    return this.apiService.post('/auth/register', userData);
  }

  // Login
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/login', credentials).pipe(
      tap((response) => {
        this.apiService.setToken(response.token);
        localStorage.setItem('userRole', response.role);
        localStorage.setItem('userEmail', response.username);
        this.isLoggedInSubject.next(true);
      })
    );
  }

  // Logout
  logout(): void {
    this.apiService.clearToken();
    localStorage.removeItem('userRole');
    localStorage.removeItem('userEmail');
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
  }

  // Search Doctors
  searchDoctors(
    query: string = '',
    page: number = 0,
    size: number = 20
  ): Observable<PageResponse<UserResponse>> {
    return this.apiService.get<PageResponse<UserResponse>>(
      `/auth/doctors?query=${query}&page=${page}&size=${size}`
    );
  }

  // Get User by ID
  getUserById(id: number): Observable<UserResponse> {
    return this.apiService.get<UserResponse>(`/auth/users/${id}`);
  }

  // Get All Users (Admin)
  getAllUsers(
    query: string = '',
    role: string = '',
    page: number = 0,
    size: number = 20
  ): Observable<PageResponse<UserResponse>> {
    return this.apiService.get<PageResponse<UserResponse>>(
      `/auth/users?query=${query}&role=${role}&page=${page}&size=${size}`
    );
  }

  // Update User (Admin)
  updateUser(id: number, userData: UserUpdateRequest): Observable<UserResponse> {
    return this.apiService.put<UserResponse>(`/auth/users/${id}`, userData);
  }

  // Delete User (Admin)
  deleteUser(id: number): Observable<void> {
    return this.apiService.delete<void>(`/auth/users/${id}`);
  }

  // Get current user role
  getUserRole(): string | null {
    return localStorage.getItem('userRole');
  }

  // Check if user has role
  hasRole(role: string): boolean {
    const userRole = this.getUserRole();
    return userRole?.includes(role) || false;
  }

  // Get current user email
  getUserEmail(): string | null {
    return localStorage.getItem('userEmail');
  }
}
```

---

## 4. Service des Dossiers Médicaux

### medical-record.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { MedicalRecord, MedicalRecordDTO } from '../models/medical-record.model';

@Injectable({
  providedIn: 'root',
})
export class MedicalRecordService {
  constructor(private apiService: ApiService) {}

  // Create Medical Record
  createMedicalRecord(data: MedicalRecordDTO): Observable<MedicalRecord> {
    return this.apiService.post<MedicalRecord>('/medical-records', data);
  }

  // Get All Medical Records
  getAllMedicalRecords(): Observable<MedicalRecord[]> {
    return this.apiService.get<MedicalRecord[]>('/medical-records');
  }

  // Get Medical Record by ID
  getMedicalRecordById(id: number): Observable<MedicalRecord> {
    return this.apiService.get<MedicalRecord>(`/medical-records/${id}`);
  }

  // Get My Medical Record (Patient)
  getMyMedicalRecord(): Observable<MedicalRecord> {
    return this.apiService.get<MedicalRecord>('/medical-records/me');
  }

  // Get Medical Records by Patient ID
  getMedicalRecordsByPatientId(patientId: number): Observable<MedicalRecord[]> {
    return this.apiService.get<MedicalRecord[]>(
      `/medical-records/patient/${patientId}`
    );
  }

  // Update Medical Record
  updateMedicalRecord(
    id: number,
    data: MedicalRecordDTO
  ): Observable<MedicalRecord> {
    return this.apiService.put<MedicalRecord>(`/medical-records/${id}`, data);
  }

  // Delete Medical Record
  deleteMedicalRecord(id: number): Observable<void> {
    return this.apiService.delete<void>(`/medical-records/${id}`);
  }
}
```

---

## 5. Service des Rendez-vous

### appointment.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Appointment, AppointmentDTO } from '../models/appointment.model';

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {
  constructor(private apiService: ApiService) {}

  // Create Appointment
  createAppointment(data: AppointmentDTO): Observable<Appointment> {
    return this.apiService.post<Appointment>('/appointments', data);
  }

  // Get All Appointments
  getAllAppointments(): Observable<Appointment[]> {
    return this.apiService.get<Appointment[]>('/appointments');
  }

  // Get Appointment by ID
  getAppointmentById(id: number): Observable<Appointment> {
    return this.apiService.get<Appointment>(`/appointments/${id}`);
  }

  // Get Appointments by Patient
  getAppointmentsByPatient(patientId: number): Observable<Appointment[]> {
    return this.apiService.get<Appointment[]>(
      `/appointments/patient/${patientId}`
    );
  }

  // Get Appointments by Doctor
  getAppointmentsByDoctor(doctorId: number): Observable<Appointment[]> {
    return this.apiService.get<Appointment[]>(
      `/appointments/doctor/${doctorId}`
    );
  }

  // Update Appointment
  updateAppointment(id: number, data: Partial<AppointmentDTO>): Observable<Appointment> {
    return this.apiService.put<Appointment>(`/appointments/${id}`, data);
  }

  // Delete Appointment
  deleteAppointment(id: number): Observable<void> {
    return this.apiService.delete<void>(`/appointments/${id}`);
  }

  // Start Teleconsultation
  startTeleconsultation(appointmentId: number): Observable<any> {
    return this.apiService.post<any>(
      `/appointments/${appointmentId}/teleconsultation/start`,
      {}
    );
  }

  // Join Teleconsultation
  joinTeleconsultation(appointmentId: number): Observable<any> {
    return this.apiService.post<any>(
      `/appointments/${appointmentId}/teleconsultation/join`,
      {}
    );
  }

  // Schedule Reminder
  scheduleReminder(appointmentId: number): Observable<any> {
    return this.apiService.post<any>(
      `/appointments/${appointmentId}/reminders/schedule`,
      {}
    );
  }
}
```

---

## 6. Service des Allergies

### allergy.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Allergy, AllergyDTO } from '../models/allergy.model';

@Injectable({
  providedIn: 'root',
})
export class AllergyService {
  constructor(private apiService: ApiService) {}

  // Create Allergy
  createAllergy(data: AllergyDTO): Observable<Allergy> {
    return this.apiService.post<Allergy>('/allergies', data);
  }

  // Get Allergy by ID
  getAllergyById(id: number): Observable<Allergy> {
    return this.apiService.get<Allergy>(`/allergies/${id}`);
  }

  // Get Allergies by Medical Record
  getAllergiesByMedicalRecord(medicalRecordId: number): Observable<Allergy[]> {
    return this.apiService.get<Allergy[]>(
      `/allergies/medical-record/${medicalRecordId}`
    );
  }

  // Update Allergy
  updateAllergy(id: number, data: Partial<AllergyDTO>): Observable<Allergy> {
    return this.apiService.put<Allergy>(`/allergies/${id}`, data);
  }

  // Delete Allergy
  deleteAllergy(id: number): Observable<void> {
    return this.apiService.delete<void>(`/allergies/${id}`);
  }
}
```

---

## 7. Service Pharmacie

### pharmacy.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class PharmacyService {
  constructor(private apiService: ApiService) {}

  // Search Medicines
  searchMedicines(
    keyword: string = '',
    category: string = '',
    page: number = 0,
    size: number = 20
  ): Observable<any> {
    return this.apiService.get<any>(
      `/api/pharmacy/medicines?keyword=${keyword}&category=${category}&page=${page}&size=${size}`
    );
  }

  // Get Medicine Details
  getMedicineDetails(id: number): Observable<any> {
    return this.apiService.get<any>(`/api/pharmacy/medicines/${id}`);
  }

  // Create Prescription
  createPrescription(data: any): Observable<any> {
    return this.apiService.post<any>('/api/pharmacy/prescriptions', data);
  }

  // Get Prescriptions
  getPrescriptions(page: number = 0, size: number = 20): Observable<any> {
    return this.apiService.get<any>(
      `/api/pharmacy/prescriptions?page=${page}&size=${size}`
    );
  }

  // Get Prescription by ID
  getPrescriptionById(id: number): Observable<any> {
    return this.apiService.get<any>(`/api/pharmacy/prescriptions/${id}`);
  }

  // Place Order
  placeOrder(data: any): Observable<any> {
    return this.apiService.post<any>('/api/pharmacy/orders', data);
  }

  // Get Order History
  getOrderHistory(page: number = 0, size: number = 20): Observable<any> {
    return this.apiService.get<any>(
      `/api/pharmacy/orders?page=${page}&size=${size}`
    );
  }

  // Get Order by ID
  getOrderById(id: number): Observable<any> {
    return this.apiService.get<any>(`/api/pharmacy/orders/${id}`);
  }

  // Cancel Order
  cancelOrder(id: number): Observable<any> {
    return this.apiService.post<any>(`/api/pharmacy/orders/${id}/cancel`, {});
  }

  // Request Refill
  requestRefill(prescriptionId: number): Observable<any> {
    return this.apiService.post<any>('/api/pharmacy/refills', {
      prescriptionId,
    });
  }

  // Check Drug Interactions
  checkDrugInteractions(medicineIds: number[]): Observable<any> {
    return this.apiService.post<any>(
      '/api/pharmacy/interactions/check',
      { medicineIds }
    );
  }
}
```

---

## 8. Guard d'Authentification

### auth.guard.ts
```typescript
import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const token = localStorage.getItem('authToken');

    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }

    // Check role if required
    const requiredRoles = route.data['roles'] as string[];
    if (requiredRoles && requiredRoles.length > 0) {
      const userHasRole = requiredRoles.some((role) =>
        this.authService.hasRole(role)
      );

      if (!userHasRole) {
        this.router.navigate(['/unauthorized']);
        return false;
      }
    }

    return true;
  }
}
```

---

## 9. Composant de Login

### login.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { LoginRequest } from '../models/auth.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    const credentials: LoginRequest = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.message || 'Login failed';
      },
    });
  }
}
```

### login.component.html
```html
<div class="login-container">
  <form [formGroup]="loginForm" (ngSubmit)="onLogin()">
    <h2>Medicare AI - Login</h2>

    <div *ngIf="error" class="alert alert-danger">
      {{ error }}
    </div>

    <div class="form-group">
      <label for="email">Email:</label>
      <input
        type="email"
        id="email"
        class="form-control"
        formControlName="email"
        [class.is-invalid]="
          loginForm.get('email')?.invalid &&
          loginForm.get('email')?.touched
        "
      />
      <div class="invalid-feedback" *ngIf="loginForm.get('email')?.invalid">
        Please provide a valid email
      </div>
    </div>

    <div class="form-group">
      <label for="password">Password:</label>
      <input
        type="password"
        id="password"
        class="form-control"
        formControlName="password"
        [class.is-invalid]="
          loginForm.get('password')?.invalid &&
          loginForm.get('password')?.touched
        "
      />
      <div class="invalid-feedback" *ngIf="loginForm.get('password')?.invalid">
        Password is required
      </div>
    </div>

    <button
      type="submit"
      class="btn btn-primary"
      [disabled]="loginForm.invalid || loading"
    >
      {{ loading ? 'Logging in...' : 'Login' }}
    </button>
  </form>
</div>
```

---

## 10. Configuration du Routing

### app.routes.ts (Angular 17+)
```typescript
import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] },
  },
  {
    path: 'pharmacy',
    component: PharmacyComponent,
    canActivate: [AuthGuard],
    data: { roles: ['PHARMACIST'] },
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
];
```

---

## 11. Configuration du Module Principal

### app.config.ts (Angular 17+)
```typescript
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
  ],
};
```

### main.ts
```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, appConfig).catch((err) =>
  console.error(err)
);
```

---

## 12. Composant Dashboard Exemple

### dashboard.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { MedicalRecordService } from '../services/medical-record.service';
import { MedicalRecord } from '../models/medical-record.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  medicalRecords: MedicalRecord[] = [];
  loading = true;
  userEmail: string | null = '';

  constructor(
    private authService: AuthService,
    private medicalRecordService: MedicalRecordService
  ) {}

  ngOnInit(): void {
    this.userEmail = this.authService.getUserEmail();
    this.loadMedicalRecords();
  }

  loadMedicalRecords(): void {
    this.medicalRecordService.getAllMedicalRecords().subscribe({
      next: (records) => {
        this.medicalRecords = records;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading medical records', err);
        this.loading = false;
      },
    });
  }

  onLogout(): void {
    this.authService.logout();
  }
}
```

### dashboard.component.html
```html
<div class="dashboard-container">
  <header class="header">
    <h1>Dashboard</h1>
    <div class="user-info">
      <span>{{ userEmail }}</span>
      <button (click)="onLogout()" class="btn btn-danger">Logout</button>
    </div>
  </header>

  <main class="content">
    <section class="medical-records">
      <h2>Medical Records</h2>

      <div *ngIf="loading" class="spinner">
        Loading...
      </div>

      <div *ngIf="!loading && medicalRecords.length === 0" class="alert">
        No medical records found
      </div>

      <div *ngIf="!loading && medicalRecords.length > 0" class="records-list">
        <div *ngFor="let record of medicalRecords" class="record-card">
          <h3>Record #{{ record.id }}</h3>
          <p><strong>Blood Type:</strong> {{ record.bloodType }}</p>
          <p><strong>Allergies:</strong> {{ record.allergies }}</p>
          <p><strong>Chronic Diseases:</strong> {{ record.chronicDiseases }}</p>
          <p><strong>Notes:</strong> {{ record.notes }}</p>
        </div>
      </div>
    </section>
  </main>
</div>
```

---

## 13. Composant Standalone avec Signals (Angular 17+)

### appointment-list.component.ts
```typescript
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppointmentService } from '../services/appointment.service';
import { Appointment } from '../models/appointment.model';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="appointment-list">
      <h2>Appointments</h2>

      <div *ngIf="loading()" class="spinner">Loading...</div>

      <div
        *ngIf="!loading() && appointments().length === 0"
        class="alert"
      >
        No appointments found
      </div>

      <div *ngIf="!loading() && appointments().length > 0" class="list">
        <div
          *ngFor="let appointment of appointments()"
          class="appointment-card"
        >
          <p><strong>Date:</strong> {{ appointment.appointmentDate }}</p>
          <p><strong>Doctor:</strong> Dr. #{{ appointment.doctorId }}</p>
          <p><strong>Reason:</strong> {{ appointment.reason }}</p>
          <p><strong>Location:</strong> {{ appointment.location }}</p>
        </div>
      </div>
    </div>
  `,
})
export class AppointmentListComponent implements OnInit {
  appointments = signal<Appointment[]>([]);
  loading = signal(true);

  constructor(private appointmentService: AppointmentService) {}

  ngOnInit(): void {
    this.appointmentService.getAllAppointments().subscribe({
      next: (data) => {
        this.appointments.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading appointments', err);
        this.loading.set(false);
      },
    });
  }
}
```

---

## 14. Intercepteur HTTP (Optional - Avec RxJS)

### auth.interceptor.ts
```typescript
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('authToken');

    if (token) {
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      return next.handle(cloned);
    }

    return next.handle(req);
  }
}
```

---

## 15. Provider dans app.config.ts (avec intercepteur)

```typescript
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient,
  withInterceptors,
  HTTP_INTERCEPTORS,
} from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
};
```

---

## 16. Advanced Medical Scheduling and Search (Angular 17+)

### appointment-advanced.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Appointment } from '../models/appointment.model';

@Injectable({ providedIn: 'root' })
export class AppointmentAdvancedService {
  constructor(private apiService: ApiService) {}

  getUpcomingAppointments(doctorKeyword = '', windowMinutes = 30): Observable<Appointment[]> {
    return this.apiService.get<Appointment[]>(
      `/appointments/upcoming?doctorKeyword=${encodeURIComponent(doctorKeyword)}&windowMinutes=${windowMinutes}`
    );
  }

  searchAppointments(params: {
    doctorId?: number;
    patientKeyword?: string;
    reasonKeyword?: string;
  }): Observable<Appointment[]> {
    const query = new URLSearchParams();
    if (params.doctorId !== undefined) query.set('doctorId', String(params.doctorId));
    if (params.patientKeyword) query.set('patientKeyword', params.patientKeyword);
    if (params.reasonKeyword) query.set('reasonKeyword', params.reasonKeyword);
    return this.apiService.get<Appointment[]>(`/appointments/search?${query.toString()}`);
  }
}
```

### visit-note-advanced.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { VisitNote } from '../models/visit-note.model';

@Injectable({ providedIn: 'root' })
export class VisitNoteAdvancedService {
  constructor(private apiService: ApiService) {}

  searchClinicalNotes(params: {
    patientKeyword?: string;
    doctorKeyword?: string;
    clinicalKeyword?: string;
  }): Observable<VisitNote[]> {
    const query = new URLSearchParams();
    if (params.patientKeyword) query.set('patientKeyword', params.patientKeyword);
    if (params.doctorKeyword) query.set('doctorKeyword', params.doctorKeyword);
    if (params.clinicalKeyword) query.set('clinicalKeyword', params.clinicalKeyword);
    return this.apiService.get<VisitNote[]>(`/visit-notes/search?${query.toString()}`);
  }
}
```

### AppointmentSearchComponent (usage)
```typescript
this.appointmentAdvancedService.searchAppointments({
  doctorId: 2,
  patientKeyword: 'ali',
  reasonKeyword: 'follow'
}).subscribe(data => this.rows = data);
```

### ClinicalSearchComponent (usage)
```typescript
this.visitNoteAdvancedService.searchClinicalNotes({
  patientKeyword: 'ali',
  doctorKeyword: 'house',
  clinicalKeyword: 'fever'
}).subscribe(data => this.notes = data);
```

### Notes
- `PATIENT`: backend limits results to current patient ownership.
- `DOCTOR`: backend forces doctor scope for appointment search.
- `ADMIN`: backend allows all filters.
- Scheduler runs in backend automatically using:
  - `app.appointments.reminder.cron`
  - `app.appointments.reminder.window-minutes`

---

## ✅ Checklist d'Intégration Angular

- [ ] Créer les models/interfaces TypeScript
- [ ] Implémenter ApiService avec HttpClient
- [ ] Créer les services métier (Auth, MedicalRecord, etc.)
- [ ] Implémenter AuthGuard
- [ ] Créer les composants standalone
- [ ] Configurer le routing
- [ ] Implémenter Signals pour state management
- [ ] Tester avec Swagger en premier
- [ ] Ajouter error handling global
- [ ] Implémenter RxJS pipes (map, catchError, etc.)
- [ ] Créer des pipes personnalisés si nécessaire
- [ ] Configurer environment.ts et environment.prod.ts

---

## 📚 Resources Utiles

- **Angular 17 Docs:** https://angular.io
- **TypeScript:** https://www.typescriptlang.org
- **RxJS:** https://rxjs.dev
- **HttpClient:** https://angular.io/guide/http

---

**Document Date:** 2026-03-30  
**Framework:** Angular 17+

