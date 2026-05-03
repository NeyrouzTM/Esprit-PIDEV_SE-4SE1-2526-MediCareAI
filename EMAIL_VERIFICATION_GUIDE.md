# Email Verification & Password Reset System

## Overview

This document describes the email verification and password reset functionality for the Medicare AI e-pharmacy application. The system generates 6-digit verification codes, stores them temporarily with expiry, and sends them via SMTP email.

## Features

- **Email Registration Verification**: Generate and send verification codes to new users
- **Email Verification**: Validate verification codes and mark emails as verified
- **Password Reset**: Request password reset codes and reset password
- **Automatic Code Expiry**: Codes expire after 15 minutes (configurable)
- **One-time Use**: Codes can only be used once
- **Security**: Codes are randomly generated and encrypted in database

## Configuration

### Email Setup (Gmail Example)

Add the following to `application.properties`:

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

**For Gmail:**
- Use your Gmail address as `spring.mail.username`
- Generate an [App Password](https://support.google.com/accounts/answer/185833) if you have 2FA enabled
- Use the app password in `spring.mail.password`

### Other Email Providers

**Gmail (with 2FA):**
- Host: `smtp.gmail.com`
- Port: `587`
- Use App Password

**Outlook/Office 365:**
```properties
spring.mail.host=smtp.office365.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

**SendGrid:**
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=SG.xxxxxxxxxxxxx
```

## API Endpoints

All endpoints are available at `POST /auth/`

### 1. Register with Email Verification

```http
POST /auth/register-with-verification
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "message": "Verification code sent to your email. It will expire in 15 minutes.",
  "success": true
}
```

### 2. Verify Email

```http
POST /auth/verify-email
Content-Type: application/json

{
  "email": "john@example.com",
  "code": "123456"
}
```

**Response:**
```json
{
  "message": "Email verified successfully. You can now complete your registration.",
  "success": true
}
```

### 3. Complete Registration

After email verification, user must provide registration details:

```http
POST /auth/complete-registration
Content-Type: application/json

{
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "SecurePassword123",
  "role": "PATIENT"
}
```

**Response:**
```
User account created successfully: john@example.com
```

### 4. Forgot Password

Request a password reset code:

```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "message": "If this email exists in our system, you will receive a password reset code.",
  "success": true
}
```

**Note:** Response is same whether email exists or not (security measure)

### 5. Reset Password

Reset password using the code:

```http
POST /auth/reset-password
Content-Type: application/json

{
  "email": "john@example.com",
  "code": "123456",
  "newPassword": "NewSecurePassword456"
}
```

**Response:**
```json
{
  "message": "Password reset successfully. You can now login with your new password.",
  "success": true
}
```

### 6. Traditional Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGc...",
  "email": "john@example.com",
  "role": "PATIENT"
}
```

## User Registration Flow

### Complete Flow with Email Verification

1. **Step 1:** User calls `/auth/register-with-verification`
   - System generates 6-digit code
   - System saves code to database with expiry time (15 minutes)
   - System sends email with code

2. **Step 2:** User receives email and calls `/auth/verify-email`
   - System validates code
   - Code must not be expired
   - Code must not be used before
   - System marks code as used

3. **Step 3:** User calls `/auth/complete-registration`
   - User provides full details (name, email, password, role)
   - System creates user account
   - User can now login

## Database Schema

### VerificationCode Table

```sql
CREATE TABLE verification_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(6) NOT NULL,
    type ENUM('REGISTRATION', 'PASSWORD_RESET') NOT NULL,
    expiry_time DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Exception Handling

The system throws the following exceptions:

### InvalidVerificationCodeException
- Thrown when code is invalid, already used, or doesn't exist
- HTTP Status: 400 Bad Request

### VerificationCodeExpiredException
- Thrown when verification code has expired
- HTTP Status: 400 Bad Request

### IllegalArgumentException
- Email already registered
- Invalid password length
- User not found
- HTTP Status: 400 Bad Request

## Email Templates

### Registration Verification Email

```
Subject: Medicare AI - Email Verification

Body:
Welcome to Medicare AI!

Your email verification code is: 123456

This code will expire in 15 minutes.

If you did not request this code, please ignore this email.

Best regards,
Medicare AI Team
```

### Password Reset Email

```
Subject: Medicare AI - Password Reset

Body:
Hello,

You requested a password reset for your Medicare AI account.

Your password reset code is: 123456

This code will expire in 15 minutes.

If you did not request this, please ignore this email.

Best regards,
Medicare AI Team
```

## Security Considerations

1. **Code Generation**: Uses cryptographically secure random number generator
2. **Code Expiry**: Codes expire after 15 minutes (configurable)
3. **One-time Use**: Codes can only be verified once
4. **Email Enumeration**: Forgot password endpoint returns same message regardless of email existence
5. **Password Hashing**: All passwords are hashed using BCrypt
6. **SMTP Security**: Uses TLS/STARTTLS for email transmission

## Implementation Details

### Service Classes

**VerificationCodeService**
- `generateAndSaveVerificationCode()`: Generate and store code
- `verifyCode()`: Validate and mark code as used
- `generateSixDigitCode()`: Generate random 6-digit code

**EmailService**
- `sendVerificationEmail()`: Send registration verification email
- `sendPasswordResetEmail()`: Send password reset email

**IAuthServiceImp**
- `registerWithEmailVerification()`: Initiate registration with email verification
- `verifyEmail()`: Verify email code
- `completeRegistration()`: Create user after email verification
- `forgotPassword()`: Request password reset
- `resetPassword()`: Reset password with code

### DTOs

- `RegisterWithVerificationRequest`: Initial registration request
- `VerifyEmailRequest`: Email verification with code
- `CompleteRegistrationRequest`: Final registration after verification
- `ForgotPasswordRequest`: Password reset request
- `ResetPasswordRequest`: Password reset with code
- `EmailVerificationResponse`: Response for email operations

## Testing

### Using Swagger UI

1. Open `http://localhost:8089/MediCareAI/swagger-ui/index.html`
2. Navigate to "Authentication" endpoints
3. Test endpoints in order:
   - First: `register-with-verification`
   - Second: `verify-email` (use code from email)
   - Third: `complete-registration`
   - Or: `forgot-password` → `reset-password`

### Using cURL

```bash
# Step 1: Register with verification
curl -X POST http://localhost:8089/MediCareAI/auth/register-with-verification \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123"
  }'

# Step 2: Verify email (use code from email)
curl -X POST http://localhost:8089/MediCareAI/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "code": "123456"
  }'

# Step 3: Complete registration
curl -X POST http://localhost:8089/MediCareAI/auth/complete-registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "fullName": "John Doe",
    "password": "SecurePassword123",
    "role": "PATIENT"
  }'

# Step 4: Login
curl -X POST http://localhost:8089/MediCareAI/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123"
  }'
```

## Troubleshooting

### Emails not being sent

1. **Check SMTP configuration** in `application.properties`
2. **Enable "Less secure app access"** (for Gmail)
3. **Check firewall** - ensure port 587 is open
4. **View logs** - check application logs for email errors

### Code expires too quickly

- Adjust `app.verification.code-expiry-minutes` in properties
- Default is 15 minutes

### Code not validating

1. Ensure code is not already used
2. Check code hasn't expired
3. Verify email matches exactly
4. Check verification type (REGISTRATION vs PASSWORD_RESET)

## Future Enhancements

1. **SMS Verification**: Add SMS as alternative to email
2. **Code Resend**: Allow users to request new code
3. **Email Templates**: Support HTML templates
4. **Multi-language**: Support multiple languages in emails
5. **Rate Limiting**: Limit verification attempts
6. **Webhook Support**: Send webhooks on verification events

