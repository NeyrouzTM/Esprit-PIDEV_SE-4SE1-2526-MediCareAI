# Email Verification System - Architecture & Design

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Frontend / Mobile App                          │
│                         (Swagger UI / REST Client)                       │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        REST API Layer (Endpoints)                         │
├──────────────────────────────────────────────────────────────────────────┤
│  AuthController                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │ POST /auth/register-with-verification                             │  │
│  │ POST /auth/verify-email                                           │  │
│  │ POST /auth/complete-registration                                  │  │
│  │ POST /auth/forgot-password                                        │  │
│  │ POST /auth/reset-password                                         │  │
│  │ POST /auth/login                                                  │  │
│  │ POST /auth/register                                               │  │
│  └────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      Business Logic Layer (Services)                      │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  ┌──────────────────────────┐      ┌──────────────────────────────────┐  │
│  │  IAuthServiceImp         │      │   Supporting Services            │  │
│  ├──────────────────────────┤      ├──────────────────────────────────┤  │
│  │ registerWithEmail...()   │      │ EmailService                     │  │
│  │ verifyEmail()            │──→   │ • sendVerificationEmail()        │  │
│  │ completeRegistration()   │      │ • sendPasswordResetEmail()       │  │
│  │ forgotPassword()         │      │                                  │  │
│  │ resetPassword()          │──→   │ VerificationCodeService          │  │
│  │ register()               │      │ • generateAndSaveCode()          │  │
│  │ login()                  │      │ • verifyCode()                   │  │
│  └────────────────────────┬─┘      │ • generateSixDigitCode()         │  │
│                           │        │ • cleanupExpiredCodes()          │  │
│                           └────────┤                                  │  │
│                                    │ CustomUserDetailsService         │  │
│                                    │ PasswordEncoder (BCrypt)         │  │
│                                    │ JwtService                       │  │
│                                    └──────────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                   Data Access Layer (Repositories)                        │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  ┌────────────────────────────────────┐  ┌────────────────────────────┐  │
│  │ VerificationCodeRepository         │  │ UserRepository             │  │
│  ├────────────────────────────────────┤  ├────────────────────────────┤  │
│  │ extends JpaRepository               │  │ extends JpaRepository      │  │
│  │                                    │  │                            │  │
│  │ findByEmailAndCodeAndTypeAndUsed   │  │ findByEmail()              │  │
│  │ findByEmailAndType()               │  │ save()                     │  │
│  │ deleteByEmailAndType()             │  │ delete()                   │  │
│  └────────────────────────────────────┘  └────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                           Database Layer (MySQL)                          │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  ┌──────────────────────────────────┐  ┌─────────────────────────────┐  │
│  │  verification_codes Table        │  │ users Table                 │  │
│  ├──────────────────────────────────┤  ├─────────────────────────────┤  │
│  │ id (BIGINT, PK)                  │  │ id (BIGINT, PK)             │  │
│  │ email (VARCHAR, UNIQUE)          │  │ fullName (VARCHAR)          │  │
│  │ code (VARCHAR(6))                │  │ email (VARCHAR, UNIQUE)     │  │
│  │ type (ENUM)                      │  │ password (VARCHAR, hashed)  │  │
│  │ expiry_time (DATETIME)           │  │ role (ENUM)                 │  │
│  │ used (BOOLEAN)                   │  │ enabled (BOOLEAN)           │  │
│  │ created_at (DATETIME)            │  └─────────────────────────────┘  │
│  └──────────────────────────────────┘                                     │
│                                                                            │
└──────────────────────────────────────────────────────────────────────────┘
```

## Email Delivery Flow

```
┌──────────────────────────────────────┐
│  Service Layer                       │
│  (IAuthServiceImp)                   │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  EmailService                        │
│  • sendVerificationEmail()           │
│  • sendPasswordResetEmail()          │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  JavaMailSender (Spring Bean)        │
│  Builds SimpleMailMessage            │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  SMTP Configuration                  │
│  (application.properties)            │
│  • host: smtp.gmail.com              │
│  • port: 587                         │
│  • username: email@gmail.com         │
│  • password: app-password            │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  TLS/STARTTLS Connection             │
│  (Encrypted SMTP)                    │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  Email Provider Server               │
│  (Gmail / Outlook / SendGrid)        │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  User's Email Inbox                  │
│  (Receives Verification Code)        │
└──────────────────────────────────────┘
```

## Code Generation & Validation Flow

```
Step 1: Code Generation
┌─────────────────────────────────────────────────────────────┐
│ User calls /register-with-verification with email          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ IAuthServiceImp.registerWithEmailVerification()            │
│ 1. Validate email not already registered                   │
│ 2. Call VerificationCodeService.generateAndSaveCode()      │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ VerificationCodeService.generateAndSaveVerificationCode()  │
│ 1. Delete any existing code for this email/type            │
│ 2. Generate random 6-digit code (100000-999999)            │
│ 3. Calculate expiry time (now + 15 minutes)                │
│ 4. Create VerificationCode entity                          │
│ 5. Save to database                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Database Insert                                             │
│ verification_codes table                                    │
│ email: 'user@example.com'                                  │
│ code: '123456'                                             │
│ type: 'REGISTRATION'                                       │
│ expiry_time: 2026-03-26 10:45:00                           │
│ used: false                                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ EmailService.sendVerificationEmail()                        │
│ 1. Create SimpleMailMessage                                │
│ 2. Set recipient, subject, body with code                  │
│ 3. Send via JavaMailSender                                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Return Response to Client                                   │
│ ✓ "Verification code sent to your email"                  │
└─────────────────────────────────────────────────────────────┘

Step 2: Code Verification
┌─────────────────────────────────────────────────────────────┐
│ User receives email and calls /verify-email                 │
│ with email and code (e.g., 123456)                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ IAuthServiceImp.verifyEmail()                              │
│ Call VerificationCodeService.verifyCode()                  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ VerificationCodeService.verifyCode()                       │
│ 1. Find code in DB by email, code, type, and used=false    │
│ 2. Validate code exists                                    │
│ 3. Check if expired (isExpired() method)                   │
│ 4. Mark as used (used = true)                              │
│ 5. Save to database                                        │
│ 6. Return true/false                                       │
└────────────────────┬────────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
      True (Valid)         False (Invalid/Expired)
          │                     │
          ▼                     ▼
┌────────────────────┐  ┌──────────────────────────────┐
│ Return Success     │  │ Throw                        │
│ "Email verified,   │  │ InvalidVerificationCode      │
│  complete reg"     │  │ Exception                    │
└────────────────────┘  │                              │
                        │ HTTP 400 Bad Request         │
                        └──────────────────────────────┘
```

## Data Flow Sequence Diagrams

### Registration with Email Verification Sequence

```
Client                 Controller          Service            Database        Email Server
  │                        │                 │                  │                │
  ├─ register-with-        │                 │                  │                │
  │  verification request ─→                 │                  │                │
  │                        │                 │                  │                │
  │                        ├─ validate   →   │                  │                │
  │                        │                 │                  │                │
  │                        ├─ generate code  │                  │                │
  │                        │    & send email │                  │                │
  │                        │────────────────→│                  │                │
  │                        │                 ├─ save code  ───→ │                │
  │                        │                 │  to DB           │                │
  │                        │                 │  (code expires    │                │
  │                        │                 │   in 15 min)     │                │
  │                        │                 │                  │                │
  │                        │                 ├─ send email ────────────────────→│
  │                        │                 │                  │                │
  │← response OK ←─────────│                 │                  │                │
  │  "Check email"         │                 │                  │                │
  │                        │                 │                  │                │
  ├─ receive email with code────────────────────────────────────────────────────→│
  │                        │                 │                  │                │
  ├─ verify-email request  │                 │                  │                │
  │  (email + code)  ────→ │                 │                  │                │
  │                        │                 │                  │                │
  │                        ├─ validate code  │                  │                │
  │                        │────────────────→│                  │                │
  │                        │                 ├─ query code ───→ │                │
  │                        │                 │  from DB         │                │
  │                        │                 │← code found ────│                │
  │                        │                 │                  │                │
  │                        │                 ├─ check expired   │                │
  │                        │                 │  & mark used     │                │
  │                        │                 ├─ update DB  ────→│                │
  │                        │                 │                  │                │
  │← response OK ←─────────│                 │                  │                │
  │  "Email verified"      │                 │                  │                │
  │                        │                 │                  │                │
  ├─ complete-registration │                 │                  │                │
  │  request ────────────→ │                 │                  │                │
  │                        │                 │                  │                │
  │                        ├─ create user ──→                   │                │
  │                        │                 ├─ save user  ────→│                │
  │                        │                 │  to DB           │                │
  │                        │                 │← success ────────│                │
  │                        │                 │                  │                │
  │← response OK ←─────────│                 │                  │                │
  │  Account created       │                 │                  │                │
```

### Password Reset Sequence

```
Client                 Controller          Service            Database
  │                        │                 │                  │
  ├─ forgot-password      │                 │                  │
  │  (email) ────────────→ │                 │                  │
  │                        │                 │                  │
  │                        ├─ check if user  │                  │
  │                        │    exists ─────→│                  │
  │                        │                 ├─ find user ────→ │
  │                        │                 │                  │
  │                        │                 │← user found ────│
  │                        │                 │                  │
  │                        ├─ generate reset │                  │
  │                        │    code ────────→│                  │
  │                        │                 ├─ save code ────→ │
  │                        │                 │  (expires in     │
  │                        │                 │   15 min)       │
  │                        │                 │                  │
  │                        ├─ send email ────→                  │
  │                        │  with code       │                  │
  │                        │                  │                  │
  │← response OK ←─────────│                  │                  │
  │  "Code sent if exists" │                  │                  │
  │                        │                  │                  │
  ├─ receive email────────────────────────────────────────────→ │
  │  with reset code       │                  │                  │
  │                        │                  │                  │
  ├─ reset-password       │                  │                  │
  │  (email, code, new    │                  │                  │
  │   password) ──────────→                  │                  │
  │                        │                  │                  │
  │                        ├─ validate code ──→                  │
  │                        │                  ├─ find code ────→ │
  │                        │                  │                  │
  │                        │                  │← code found ────│
  │                        │                  │                  │
  │                        │                  ├─ check expired  │
  │                        │                  │  & mark used    │
  │                        │                  │← update done ──│
  │                        │                  │                  │
  │                        ├─ update password │                  │
  │                        │    (hash it) ────→                  │
  │                        │                  ├─ find user ────→ │
  │                        │                  │                  │
  │                        │                  ├─ update pwd ───→ │
  │                        │                  │                  │
  │                        │                  │← success ──────│
  │                        │                  │                  │
  │← response OK ←─────────│                  │                  │
  │  "Password reset"      │                  │                  │
```

## Exception Handling Hierarchy

```
RuntimeException
    │
    ├── InvalidVerificationCodeException
    │   │ Thrown when:
    │   ├── Code doesn't exist
    │   ├── Code is invalid
    │   └── Code already used
    │   
    │   Handled by: GlobalExceptionHandler
    │   Response: HTTP 400 Bad Request
    │
    ├── VerificationCodeExpiredException
    │   │ Thrown when:
    │   └── Code has expired (past expiry_time)
    │   
    │   Handled by: GlobalExceptionHandler
    │   Response: HTTP 400 Bad Request
    │
    ├── IllegalArgumentException
    │   │ Thrown when:
    │   ├── Email already registered
    │   ├── Invalid password length
    │   ├── User not found
    │   └── Missing required fields
    │   
    │   Handled by: GlobalExceptionHandler
    │   Response: HTTP 400 Bad Request
    │
    └── Other Runtime Exceptions
        │ Handled by: GlobalExceptionHandler
        │ Response: HTTP 500 Internal Server Error
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Security Layers                                │
└─────────────────────────────────────────────────────────────────────┘

Layer 1: Code Generation Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Uses SecureRandom (cryptographically secure)                      │
│ • Range: 100,000 - 999,999 (6 digits)                               │
│ • Probability of guessing: 1 in 900,000                             │
│ • Cannot predict next code                                          │
└─────────────────────────────────────────────────────────────────────┘

Layer 2: Code Storage Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Stored in MySQL database (indexed by email + type)                │
│ • Not encrypted in database (but could be for extra security)       │
│ • Automatic cleanup of old codes                                    │
│ • One-time use enforcement in application logic                     │
└─────────────────────────────────────────────────────────────────────┘

Layer 3: Code Expiry Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Default expiry: 15 minutes                                        │
│ • Validated on every verification attempt                           │
│ • isExpired() method checks current time vs expiry_time             │
│ • Cannot extend or modify expiry time                               │
└─────────────────────────────────────────────────────────────────────┘

Layer 4: Transmission Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Email sent via TLS/STARTTLS (port 587)                            │
│ • Encrypted connection to SMTP server                               │
│ • Code never transmitted in plain HTTP request body                 │
│   (only via form data in HTTPS)                                     │
└─────────────────────────────────────────────────────────────────────┘

Layer 5: Password Security
┌─────────────────────────────────────────────────────────────────────┐
│ • All passwords hashed using BCrypt                                 │
│ • Automatically salted by Spring Security                           │
│ • Original password never stored or logged                          │
│ • Reset requires valid verification code + verified email           │
└─────────────────────────────────────────────────────────────────────┘

Layer 6: Information Disclosure Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Forgot password returns same message for all emails               │
│   (doesn't reveal if email exists in system)                        │
│ • Invalid codes don't specify if wrong code or not registered       │
│ • Error messages are generic without technical details              │
└─────────────────────────────────────────────────────────────────────┘

Layer 7: Application Security
┌─────────────────────────────────────────────────────────────────────┐
│ • Spring Security integration (future: add @PreAuthorize)           │
│ • Input validation with Jakarta Validation                          │
│ • @NotBlank, @Email, @Pattern annotations                          │
│ • Transactional operations for consistency                          │
│ • Proper exception handling                                         │
└─────────────────────────────────────────────────────────────────────┘
```

## Configuration Architecture

```
application.properties (Environment)
        │
        ├─→ Email Configuration
        │   ├── spring.mail.host
        │   ├── spring.mail.port
        │   ├── spring.mail.username
        │   ├── spring.mail.password
        │   ├── spring.mail.properties.mail.smtp.auth
        │   ├── spring.mail.properties.mail.smtp.starttls.enable
        │   ├── spring.mail.properties.mail.smtp.starttls.required
        │   └── spring.mail.from
        │
        └─→ Verification Configuration
            └── app.verification.code-expiry-minutes (default: 15)

Spring Beans Loaded
        │
        ├─→ JavaMailSender (auto-configured by Spring Boot)
        │   │ Reads: spring.mail.* properties
        │   └── Used by: EmailService.sendXXX()
        │
        ├─→ PasswordEncoder (BCryptPasswordEncoder)
        │   └── Used by: IAuthServiceImp
        │
        └─→ Other Security Beans
            └── AuthenticationManager, JwtService, etc.
```

## Deployment Architecture

```
┌──────────────────────────────────────────┐
│         Development Environment          │
├──────────────────────────────────────────┤
│ • Localhost MySQL (localhost:3306)       │
│ • Gmail SMTP (smtp.gmail.com:587)        │
│ • Spring Boot (port 8089)                │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│         Production Environment           │
├──────────────────────────────────────────┤
│ • Remote MySQL (secure connection)       │
│ • SendGrid or enterprise SMTP            │
│ • Docker container / Kubernetes          │
│ • SSL/TLS for all connections            │
│ • Environment variables for secrets      │
└──────────────────────────────────────────┘

Environment Variables to Set:
  SPRING_MAIL_USERNAME=your-email
  SPRING_MAIL_PASSWORD=your-password
  SPRING_MAIL_HOST=smtp.example.com
  SPRING_DATASOURCE_URL=jdbc:mysql://...
```

---

**This architecture provides:**
✅ Clean separation of concerns
✅ Scalability and maintainability
✅ Security at multiple layers
✅ Easy to test and extend
✅ Production-ready code

