# Email Verification System - Command Reference & API Examples

## 🚀 Quick Start Commands

### Build Project
```bash
# Clean build (remove old artifacts)
./mvnw clean package -DskipTests

# Build without tests (faster)
./mvnw clean package -DskipTests -q

# Build with tests
./mvnw clean package
```

### Run Application
```bash
# Option 1: Maven Spring Boot Plugin
./mvnw spring-boot:run

# Option 2: Direct JAR Execution
java -jar target/Medicare_Ai-0.0.1-SNAPSHOT.jar

# Option 3: Run from IDE
# Right-click MedicareAiApplication.java → Run
```

### Check Application Status
```bash
# Test if app is running
curl http://localhost:8089/MediCareAI/health

# Expected response:
# {"status":"UP"}
```

### Access Swagger UI
```
http://localhost:8089/MediCareAI/swagger-ui/index.html
```

## 📧 Email Configuration Commands

### Test SMTP Connection (PowerShell)
```powershell
# Test Gmail SMTP
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12
$client = New-Object System.Net.Sockets.TcpClient
$client.Connect("smtp.gmail.com", 587)
$stream = $client.GetStream()
$client.Close()
Write-Host "Gmail SMTP connection successful!"

# Test Outlook SMTP
$client.Connect("smtp.office365.com", 587)
# ... repeat for other hosts
```

### Verify Gmail App Password
```bash
# Go to https://myaccount.google.com/apppasswords
# Select Mail and Windows Computer
# Copy the 16-character password
# Use in spring.mail.password
```

## 📋 API Endpoints - cURL Examples

All examples assume app is running at `http://localhost:8089/MediCareAI`

### 1️⃣ Register with Email Verification

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/register-with-verification \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }'

# Response:
# {
#   "message": "Verification code sent to your email. It will expire in 15 minutes.",
#   "success": true
# }
```

### 2️⃣ Verify Email

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "code": "123456"
  }'

# Response:
# {
#   "message": "Email verified successfully. You can now complete your registration.",
#   "success": true
# }
```

### 3️⃣ Complete Registration

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/complete-registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "password": "SecurePassword123",
    "role": "PATIENT"
  }'

# Response:
# User account created successfully: john.doe@example.com
```

### 4️⃣ Login

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "email": "john.doe@example.com",
#   "role": "PATIENT"
# }
```

### 5️⃣ Forgot Password

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com"
  }'

# Response:
# {
#   "message": "If this email exists in our system, you will receive a password reset code.",
#   "success": true
# }
```

### 6️⃣ Reset Password

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "code": "654321",
    "newPassword": "NewSecurePassword456"
  }'

# Response:
# {
#   "message": "Password reset successfully. You can now login with your new password.",
#   "success": true
# }
```

### 7️⃣ Simple Registration (Without Email Verification)

```bash
curl -X POST http://localhost:8089/MediCareAI/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Doe",
    "email": "jane.doe@example.com",
    "password": "AnotherPassword123",
    "role": "PATIENT"
  }'

# Response:
# User created: jane.doe@example.com
```

## 🗄️ MySQL Commands

### Connect to Database
```bash
# Login to MySQL
mysql -u root -p

# Enter password when prompted
```

### Database Operations
```sql
-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS medicare_db;

-- Use database
USE medicare_db;

-- Show all tables
SHOW TABLES;

-- Show verification codes
SELECT id, email, code, type, expiry_time, used, created_at 
FROM verification_codes 
ORDER BY created_at DESC;

-- Show users
SELECT id, fullName, email, role, enabled 
FROM users 
ORDER BY id DESC;

-- Count pending verifications
SELECT COUNT(*) as pending_verifications 
FROM verification_codes 
WHERE used = FALSE AND expiry_time > NOW();

-- Find verification codes for email
SELECT * FROM verification_codes 
WHERE email = 'john.doe@example.com' 
ORDER BY created_at DESC;

-- Delete old codes (cleanup)
DELETE FROM verification_codes 
WHERE expiry_time < NOW() AND used = TRUE;

-- Check database size
SELECT 
  table_name, 
  ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables 
WHERE table_schema = 'medicare_db';
```

## 🔍 Debugging Commands

### View Application Logs
```bash
# Run app and capture logs
./mvnw spring-boot:run > app.log 2>&1

# View logs in real-time
tail -f app.log

# Search logs for errors
grep -i error app.log

# Search logs for email-related messages
grep -i "email\|verification\|code" app.log
```

### Check Port Usage
```bash
# Windows PowerShell - Check if port 8089 is in use
Get-NetTCPConnection -LocalPort 8089 -ErrorAction SilentlyContinue

# Windows CMD - List processes using ports
netstat -ano | findstr 8089

# Linux/Mac - Check port
lsof -i :8089
```

### View Maven Properties
```bash
# Show all Maven properties
./mvnw help:describe -Dcmd=package

# Show active profiles
./mvnw help:active-profiles
```

## 🧪 Automated Testing Commands

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=MedicineServiceTest
```

### Run Tests with Specific Pattern
```bash
./mvnw test -Dtest=*Service*
```

### Skip Tests During Build
```bash
./mvnw clean package -DskipTests
```

## 📦 Maven Useful Commands

### Clean Build Artifacts
```bash
./mvnw clean
```

### Compile Only
```bash
./mvnw compile
```

### Dependency Management
```bash
# Show dependency tree
./mvnw dependency:tree

# Check for outdated dependencies
./mvnw versions:display-dependency-updates

# Download dependencies
./mvnw dependency:resolve
```

### Check Code Quality
```bash
# Compile check
./mvnw validate

# Static analysis
./mvnw verify
```

## 🔐 Security Commands

### Password Hashing Test (Java)
```java
// In a Java test or main method:
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String plainPassword = "SecurePassword123";
String hashedPassword = encoder.encode(plainPassword);
boolean matches = encoder.matches(plainPassword, hashedPassword);

System.out.println("Plain: " + plainPassword);
System.out.println("Hashed: " + hashedPassword);
System.out.println("Matches: " + matches);
```

### Generate Test JWT Token (curl)
```bash
# After login, you get a token
curl -X POST http://localhost:8089/MediCareAI/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }' | jq .token

# Use token in subsequent requests
curl http://localhost:8089/MediCareAI/api/pharmacy/medicines \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 📊 Performance Monitoring

### Check Application Memory Usage
```bash
# Windows - Using tasklist
tasklist | findstr java

# Linux/Mac - Using ps
ps aux | grep java

# View heap memory during runtime
jps -l -m
```

### Monitor Database Connections
```sql
-- Show active connections
SHOW PROCESSLIST;

-- Check connection limit
SHOW VARIABLES LIKE 'max_connections';

-- Kill a specific connection
KILL <connection_id>;
```

### Test Email Performance
```bash
#!/bin/bash
# Send 5 registration requests and measure time

for i in {1..5}; do
  time curl -X POST http://localhost:8089/MediCareAI/auth/register-with-verification \
    -H "Content-Type: application/json" \
    -d '{
      "fullName": "User'$i'",
      "email": "user'$i'@example.com",
      "password": "TestPass123"
    }'
  echo ""
done
```

## 🚀 Production Deployment Commands

### Build Docker Image
```bash
# Create Dockerfile
cat > Dockerfile << EOF
FROM openjdk:17-jdk
COPY target/Medicare_Ai-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EOF

# Build image
docker build -t medicare-ai:latest .

# Run container
docker run -d \
  -e SPRING_MAIL_USERNAME=your-email@gmail.com \
  -e SPRING_MAIL_PASSWORD=your-app-password \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/medicare_db \
  -p 8089:8089 \
  medicare-ai:latest
```

### Deploy with Environment Variables
```bash
# Set environment variables (Linux/Mac)
export SPRING_MAIL_USERNAME="your-email@gmail.com"
export SPRING_MAIL_PASSWORD="your-app-password"
export SPRING_DATASOURCE_URL="jdbc:mysql://prod-db:3306/medicare_db"

# Run app
./mvnw spring-boot:run

# Or with JAR
java -jar target/Medicare_Ai-0.0.1-SNAPSHOT.jar
```

## 🔄 Continuous Integration Commands

### Build & Test (CI/CD Pipeline)
```bash
#!/bin/bash
set -e

echo "Building Medicare AI..."
./mvnw clean package

echo "Running tests..."
./mvnw test

echo "Checking code quality..."
./mvnw verify

echo "Build successful!"
```

## 📝 Useful Configuration Changes

### Increase Code Expiry to 30 Minutes
```properties
app.verification.code-expiry-minutes=30
```

### Enable SQL Logging
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### Disable HTTPS Requirement (Dev Only)
```properties
spring.mail.properties.mail.smtp.starttls.required=false
```

### Change Email From Address
```properties
spring.mail.from=noreply@mycompany.com
```

## 🛠️ Troubleshooting Commands

### Clear Maven Cache
```bash
rm -rf ~/.m2/repository  # Linux/Mac
rmdir /S C:\Users\%USERNAME%\.m2\repository  # Windows
```

### Reset Database
```bash
# Delete and recreate
mysql -u root -p -e "DROP DATABASE medicare_db; CREATE DATABASE medicare_db;"
```

### View System Info
```bash
# Java version
java -version

# Maven version
./mvnw --version

# OS info
uname -a  # Linux/Mac
systeminfo  # Windows
```

## 📱 API Testing Tools

### Postman Collection (Quick Import)
```json
{
  "info": {"name": "Medicare AI Auth API"},
  "item": [
    {
      "name": "Register with Verification",
      "request": {
        "method": "POST",
        "url": "{{host}}/auth/register-with-verification",
        "body": {"email": "test@example.com"}
      }
    }
  ]
}
```

### Using Insomnia
```bash
# Import Swagger JSON
GET http://localhost:8089/MediCareAI/v3/api-docs/e-pharmacy
```

### Using REST Client (VS Code Extension)
```rest
### Register with Verification
POST http://localhost:8089/MediCareAI/auth/register-with-verification
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}

### Verify Email
POST http://localhost:8089/MediCareAI/auth/verify-email
Content-Type: application/json

{
  "email": "john@example.com",
  "code": "123456"
}
```

---

**💡 Pro Tips:**
- Use `| jq` to format JSON responses: `curl ... | jq`
- Save token to variable: `TOKEN=$(curl ... | jq -r .token)`
- Use saved token: `curl -H "Authorization: Bearer $TOKEN"`
- Pipe to file: `curl ... > response.json`
- Time request: `time curl ...`

**📌 Remember:**
- Email codes are 6 digits
- Codes expire after 15 minutes (configurable)
- Each code can only be used once
- Password must be at least 6 characters
- Email format must be valid

