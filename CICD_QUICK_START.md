# Quick Start Guide - CI/CD Pipeline Setup

> Get your CI/CD pipeline running in 5 minutes!

## 🚀 5-Minute Quick Start

### 1️⃣ Install Prerequisites (2 min)

```bash
# Verify Java 17 is installed
java -version

# Verify Maven is installed
mvn -version

# Install GitHub CLI (if not installed)
# https://cli.github.com

# Verify GitHub CLI
gh auth status
```

### 2️⃣ Setup Development Environment (1 min)

```bash
# Clone and navigate to project
cd Medicare_Ai

# Setup environment
make setup-env
```

### 3️⃣ Configure Secrets (1 min)

```bash
# Interactive secret setup
chmod +x scripts/setup-github-secrets.sh
./scripts/setup-github-secrets.sh

# Or manually in GitHub:
# Settings → Secrets and variables → Actions
# Add the secrets listed below
```

### 4️⃣ Start Local Development (1 min)

```bash
# Start database
make db-start

# Run application
make run

# Application available at http://localhost:8080
```

---

## 📋 Required Secrets

Copy and paste these secrets in GitHub Settings:

### Step-by-Step Secret Setup

**Option A: Using GitHub CLI (Recommended)**

```bash
gh secret set SLACK_WEBHOOK -b "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
gh secret set SONAR_HOST_URL -b "https://sonarqube.example.com"
gh secret set SONAR_TOKEN -b "your-sonar-token"
gh secret set DOCKER_USERNAME -b "your-docker-username"
gh secret set DOCKER_PASSWORD -b "your-docker-password"
gh secret set DOCKER_REGISTRY -b "ghcr.io"

# Staging secrets
gh secret set STAGING_HOST -b "staging-server.example.com"
gh secret set STAGING_USER -b "deploy"
gh secret set STAGING_DEPLOY_KEY -b "$(cat ~/.ssh/id_rsa)"
gh secret set STAGING_DB_PASSWORD -b "secure-password"
gh secret set STAGING_JWT_SECRET -b "min-32-characters-long-secret-key"

# Production secrets (same pattern as staging)
# gh secret set PROD_HOST ...
# gh secret set PROD_USER ...
# etc.
```

**Option B: GitHub UI**

1. Go to: Repository → Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Add each secret listed above

---

## ✅ Verification

After setup, verify everything works:

```bash
# Test build
make build

# Run unit tests
make test-unit

# Run all quality checks
make analyze

# List configured secrets
gh secret list

# View workflow runs
gh run list --workflow=backend-ci-cd.yml
```

---

## 📖 Common Commands

| Command | Description |
|---------|-------------|
| `make help` | Show all available commands |
| `make build` | Build application |
| `make test` | Run all tests |
| `make test-unit` | Run unit tests only |
| `make lint` | Check code style |
| `make security` | Run security scan |
| `make docker-run` | Build and run Docker container |
| `make db-start` | Start MySQL database |
| `make deploy-staging` | Deploy to staging |
| `make deploy-prod` | Deploy to production |

---

## 📊 Pipeline Stages

The pipeline automatically runs these stages:

```
Code Validation
    ↓
Build & Unit Tests
    ↓
Security Scanning (OWASP, SpotBugs)
    ↓
Code Quality (SonarQube)
    ↓
Docker Build & Push
    ↓
Integration Tests
    ↓
Deploy to Staging/Production
    ↓
Notifications (Slack)
```

---

## 🔐 Environment Files

Copy the template and customize:

```bash
# Development
cp .env.example .env
nano .env

# Staging
cp env/staging.env env/staging.env.local
nano env/staging.env.local

# Production
cp env/production.env env/production.env.local
nano env/production.env.local
```

---

## 🐳 Docker Commands

```bash
# Build Docker image
docker build -t medicare-ai:latest .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down

# Access database (PhpMyAdmin)
# http://localhost:8081
# Username: root
# Password: root
```

---

## 📱 Testing Endpoints

Once application is running:

```bash
# Health check
curl http://localhost:8080/actuator/health

# API Documentation
curl http://localhost:8080/swagger-ui.html

# Application metrics
curl http://localhost:8080/actuator/metrics
```

---

## 🆘 Troubleshooting

| Issue | Solution |
|-------|----------|
| Build fails | `mvn clean install` |
| Tests fail | `make db-start` then `make test` |
| Docker fails | `docker system prune` then `docker build .` |
| SSH fails | Verify DEPLOY_KEY secret is correct |
| Pipeline not running | Check workflow file in `.github/workflows/` |

---

## 📚 Full Documentation

For detailed information, see [CICD_PIPELINE_GUIDE.md](./CICD_PIPELINE_GUIDE.md)

---

## 🎯 Next Steps

1. ✅ Setup completed
2. 🔄 Commit changes: `git add . && git commit -m "feat: add CI/CD pipeline"`
3. 🚀 Push to develop: `git push origin develop`
4. 👀 Watch pipeline: GitHub → Actions tab
5. 📊 Monitor logs in GitHub Actions interface

---

**Your CI/CD pipeline is ready! 🎉**

