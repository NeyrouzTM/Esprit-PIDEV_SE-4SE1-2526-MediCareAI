# Quick Start - Email Verification Testing

## Files Created

### Entities
- `VerificationCode.java` - JPA entity for storing verification codes
- `VerificationType.java` - Enum for code types (REGISTRATION, PASSWORD_RESET)

### Repositories
- `VerificationCodeRepository.java` - JPA repository for VerificationCode

### Services
- `EmailService.java` - Sends emails via SMTP
- `VerificationCodeService.java` - Generates, stores, and validates codes

### DTOs
- `RegisterWithVerificationRequest.java` - Initial registration
- `VerifyEmailRequest.java` - Email verification
- `CompleteRegistrationRequest.java` - Complete registration after verification
- `ForgotPasswordRequest.java` - Password reset request
- `ResetPasswordRequest.java` - Password reset with code
- `EmailVerificationResponse.java` - Response DTO

### Exceptions
- `InvalidVerificationCodeException.java` - Invalid code error
- `VerificationCodeExpiredException.java` - Expired code error

### Updated Files
- `IAuthService.java` - Added new method signatures
- `IAuthServiceImp.java` - Implemented email verification methods
- `AuthController.java` - Added new endpoints
- `GlobalExceptionHandler.java` - Added exception handlers
- `application.properties` - Added email configuration
- `pom.xml` - Added spring-boot-starter-mail dependency

## Setup Steps

### 1. Update application.properties (IMPORTANT)

Add your email configuration. For Gmail:

```properties
# Email Configuration (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@medicare-ai.com

# Verification Code Configuration
app.verification.code-expiry-minutes=15
```

**Important:** For Gmail with 2FA:
1. Go to https://myaccount.google.com/
2. Navigate to Security → App passwords
3. Select "Mail" and "Windows Computer"
4. Use the generated password in `spring.mail.password`

### 2. Database Migration

The `VerificationCode` table will be created automatically by Hibernate (JPA) when you run the application since `spring.jpa.hibernate.ddl-auto=update`.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

### 4. Test the Endpoints

Access Swagger UI at:
```
http://localhost:8089/MediCareAI/swagger-ui/index.html
```

## Registration Flow Test

### Step 1: Register with Email Verification

Click **"POST /auth/register-with-verification"** and try it out:

```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

✅ Expected Response (200):
```json
{
  "message": "Verification code sent to your email. It will expire in 15 minutes.",
  "success": true
}
```

📧 **Check your email** - you should see a message like:
```
Welcome to Medicare AI!

Your email verification code is: 123456

This code will expire in 15 minutes.
```

### Step 2: Verify Email

Click **"POST /auth/verify-email"** and try it out:

```json
{
  "email": "john.doe@example.com",
  "code": "123456"
}
```

✅ Expected Response (200):
```json
{
  "message": "Email verified successfully. You can now complete your registration.",
  "success": true
}
```

### Step 3: Complete Registration

Click **"POST /auth/complete-registration"** and try it out:

```json
{
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "password": "SecurePassword123",
  "role": "PATIENT"
}
```

✅ Expected Response (200):
```
User account created successfully: john.doe@example.com
```

### Step 4: Login

Click **"POST /auth/login"** and try it out:

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

✅ Expected Response (200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john.doe@example.com",
  "role": "PATIENT"
}
```

## Password Reset Flow Test

### Step 1: Request Password Reset

Click **"POST /auth/forgot-password"** and try it out:

```json
{
  "email": "john.doe@example.com"
}
```

✅ Expected Response (200):
```json
{
  "message": "If this email exists in our system, you will receive a password reset code.",
  "success": true
}
```

📧 **Check your email** for a message like:
```
Hello,

You requested a password reset for your Medicare AI account.

Your password reset code is: 654321

This code will expire in 15 minutes.
```

### Step 2: Reset Password

Click **"POST /auth/reset-password"** and try it out:

```json
{
  "email": "john.doe@example.com",
  "code": "654321",
  "newPassword": "NewSecurePassword456"
}
```

✅ Expected Response (200):
```json
{
  "message": "Password reset successfully. You can now login with your new password.",
  "success": true
}
```

### Step 3: Login with New Password

Click **"POST /auth/login"** and try it out:

```json
{
  "email": "john.doe@example.com",
  "password": "NewSecurePassword456"
}
```

✅ Expected Response (200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john.doe@example.com",
  "role": "PATIENT"
}
```

## Error Handling

### Invalid Code

If you use wrong code:
```json
{
  "email": "john.doe@example.com",
  "code": "999999"
}
```

❌ Response (400):
```json
{
  "timestamp": "2026-03-26T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired verification code",
  "path": "/auth/verify-email"
}
```

### Expired Code

If code expires after 15 minutes:

❌ Response (400):
```json
{
  "timestamp": "2026-03-26T10:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired verification code",
  "path": "/auth/verify-email"
}
```

### Email Already Registered

If you try to register with existing email:

❌ Response (400):
```json
{
  "timestamp": "2026-03-26T10:35:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already registered",
  "path": "/auth/register-with-verification"
}
```

## Troubleshooting

### Problem: Emails not sending

**Solution 1: Check Gmail Settings**
- Allow "Less secure app access": https://myaccount.google.com/lesssecureapps
- Or use App Password method (recommended)

**Solution 2: Check Configuration**
- Verify `spring.mail.username` and `spring.mail.password`
- Ensure `spring.mail.host` and `spring.mail.port` are correct

**Solution 3: Check Logs**
- Look for error messages in console/logs
- Error will typically show SMTP connection failure

### Problem: Code validation fails

**Check:**
1. ✅ Code is correct (copy-paste from email)
2. ✅ Email matches exactly
3. ✅ Code hasn't expired (15 minutes)
4. ✅ Code hasn't been used already

### Problem: Database errors

**Solution:**
- Delete the MySQL database and restart app
- Hibernate will recreate tables automatically
- Or manually create database:

```sql
CREATE DATABASE medicare_db;
USE medicare_db;
```

## Next Steps

1. ✅ Setup email configuration
2. ✅ Test all endpoints via Swagger UI
3. ✅ Integrate into frontend application
4. ✅ Consider adding:
   - SMS verification as backup
   - Email template customization
   - Rate limiting on code generation
   - Webhook notifications

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend/API Client                  │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              AuthController                             │
│ • register-with-verification                            │
│ • verify-email                                          │
│ • complete-registration                                │
│ • forgot-password                                       │
│ • reset-password                                        │
└──────┬──────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│           IAuthServiceImp (Service Layer)               │
└──────┬──────────────────────────────────────────────────┘
       │
       ├─────────────────────┬──────────────┬─────────────┐
       ▼                     ▼              ▼             ▼
 ┌──────────────┐  ┌──────────────────┐  ┌────────────┐ ┌──────────────┐
 │EmailService  │  │VerificationCode  │  │UserRepo    │ │PasswordEnc   │
 │• send email  │  │Service           │  │            │ │              │
 └──────────────┘  │• generate code   │  └────────────┘ └──────────────┘
                   │• verify code     │
                   └─────────┬────────┘
                             │
                             ▼
                   ┌──────────────────────┐
                   │VerificationCode      │
                   │ Repository (JPA)     │
                   └──────┬───────────────┘
                          │
                          ▼
                   ┌──────────────────────┐
                   │MySQL Database        │
                   │verification_codes    │
                   │table                 │
                   └──────────────────────┘

Email Flow:
┌──────────────────┐
│JavaMailSender    │
│(Spring Mail)     │
└────────┬─────────┘
         │
         ▼
   ┌──────────────┐
   │SMTP Server   │
   │(Gmail/Other) │
   └────────┬─────┘
            │
            ▼
      ┌──────────────┐
      │User's Email  │
      │Inbox         │
      └──────────────┘
```

Happy Testing! 🎉

