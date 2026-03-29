# CI/CD Pipeline Documentation - Medicare AI Backend

## Table of Contents
1. [Overview](#overview)
2. [Pipeline Architecture](#pipeline-architecture)
3. [Setup Instructions](#setup-instructions)
4. [Pipeline Stages](#pipeline-stages)
5. [Configuration](#configuration)
6. [Secrets Management](#secrets-management)
7. [Deployment](#deployment)
8. [Monitoring & Troubleshooting](#monitoring--troubleshooting)

---

## Overview

The Medicare AI Backend CI/CD Pipeline is a comprehensive automation system that:

- **Validates** code quality and style
- **Tests** application functionality (unit & integration tests)
- **Scans** for security vulnerabilities
- **Analyzes** code quality with SonarQube
- **Builds** Docker containers
- **Deploys** to staging and production environments
- **Monitors** application health

### Key Benefits

✅ **Automated Testing** - All tests run before deployment  
✅ **Security First** - Dependency scanning and OWASP checks  
✅ **Code Quality** - SonarQube analysis and style checks  
✅ **Zero-Downtime Deploys** - Rolling updates with health checks  
✅ **Complete Traceability** - Every commit is tracked and tested  
✅ **Slack Notifications** - Real-time build status updates  

---

## Pipeline Architecture

### 8 Stages of the Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Code Validation (Lint, Structure, Version)              │
├─────────────────────────────────────────────────────────────┤
│ 2. Build & Unit Tests (Maven Build + Unit Tests)           │
├─────────────────────────────────────────────────────────────┤
│ 3. Security Scanning (Dependency Check, SpotBugs)         │
├─────────────────────────────────────────────────────────────┤
│ 4. Code Quality Analysis (SonarQube)                        │
├─────────────────────────────────────────────────────────────┤
│ 5. Docker Build & Push (Only for main/develop)             │
├─────────────────────────────────────────────────────────────┤
│ 6. Integration Tests (With MySQL Database)                 │
├─────────────────────────────────────────────────────────────┤
│ 7. Deployment (Staging or Production)                      │
├─────────────────────────────────────────────────────────────┤
│ 8. Notifications (Slack)                                   │
└─────────────────────────────────────────────────────────────┘
```

### Trigger Rules

| Branch | Trigger | Deploy To |
|--------|---------|-----------|
| `main` | Push/PR | Production |
| `develop` | Push/PR | Staging |
| `release/*` | Push | Staging |
| Any branch | PR | Tests only |

---

## Setup Instructions

### Prerequisites

- GitHub account with repository access
- Java 17 installed locally
- Maven 3.8.6+ installed
- Docker & Docker Compose installed
- GitHub CLI (`gh`) installed
- Git configured

### Step 1: Repository Setup

```bash
# Clone repository
git clone https://github.com/your-org/Medicare_Ai.git
cd Medicare_Ai

# Create GitHub workflows directory
mkdir -p .github/workflows

# Copy workflow file
cp docs/backend-ci-cd.yml .github/workflows/
```

### Step 2: Configure Secrets

The pipeline requires several secrets to be configured in GitHub. Use the provided script:

```bash
chmod +x scripts/setup-github-secrets.sh
./scripts/setup-github-secrets.sh
```

Or manually configure them in GitHub:

**Settings → Secrets and variables → Actions → New repository secret**

### Step 3: Environment Configuration

```bash
# Copy environment templates
cp .env.example .env
cp env/staging.env env/staging.env.local
cp env/production.env env/production.env.local

# Edit with your actual values
nano .env
nano env/staging.env.local
nano env/production.env.local
```

### Step 4: Local Development Setup

```bash
# Install dependencies
make setup-env

# Start database
make db-start

# Run application
make run

# Application available at http://localhost:8080
```

---

## Pipeline Stages

### Stage 1: Code Validation
**Duration**: ~2 minutes

Validates code structure and configuration:
- Maven project validation
- Code style checks (Checkstyle)
- POM.xml structure verification

**Success Criteria**: No validation errors

### Stage 2: Build & Unit Tests
**Duration**: ~5-8 minutes

Compiles code and runs unit tests:
- Maven clean build
- JUnit 5 tests execution
- Test reports generation
- Code coverage analysis

**Artifacts Generated**:
- `target/*.jar` - Built application JAR
- `target/surefire-reports/` - Test reports
- Coverage reports

**Success Criteria**: All tests pass, build succeeds

### Stage 3: Security Scanning
**Duration**: ~3-5 minutes

Scans for known vulnerabilities:
- OWASP Dependency Check
- SpotBugs (potential bugs)
- CVE detection

**Artifacts Generated**:
- `target/dependency-check-report.json`
- `target/dependency-check-report.xml`
- `target/spotbugsXml.xml`

**Success Criteria**: No CRITICAL vulnerabilities

### Stage 4: Code Quality Analysis
**Duration**: ~2-3 minutes

Analyzes code quality with SonarQube:
- Code complexity
- Code duplication
- Bug detection
- Security hotspots
- Technical debt

**Requires**: 
- `SONAR_HOST_URL` secret
- `SONAR_TOKEN` secret

**Success Criteria**: Quality gate passes (optional)

### Stage 5: Docker Build & Push
**Duration**: ~5-10 minutes

Builds and pushes Docker image:
- Multi-stage build for optimization
- Image tagging with git commit SHA
- Push to GitHub Container Registry (GHCR)

**Artifacts Generated**:
- Docker image tagged with:
  - `latest` (main branch only)
  - Branch name
  - Git commit SHA
  - Semantic version

**Success Criteria**: Image built and pushed successfully

### Stage 6: Integration Tests
**Duration**: ~5-10 minutes

Runs integration tests with real database:
- Starts MySQL 8.0 container
- Runs Spring Boot integration tests
- Full API endpoint testing

**Environment**:
- MySQL: `jdbc:mysql://127.0.0.1:3306/medicare_ai_test`
- Username: `root`
- Password: `root`

**Success Criteria**: All integration tests pass

### Stage 7: Deployment
**Duration**: ~5-10 minutes per environment

Deploys to target environment:

**Staging Deployment** (develop branch):
- SSH to staging server
- Pull Docker image
- Run smoke tests
- Validate application health

**Production Deployment** (main branch):
- SSH to production server
- Pull Docker image
- Run smoke tests
- Create GitHub release
- Trigger notifications

**Pre-deployment Checks**:
- SSH connectivity
- Docker image availability
- Environment variables validation

**Health Checks**:
- Application startup timeout: 40 seconds
- Health endpoint: `/actuator/health`
- Retry attempts: 3 attempts

**Success Criteria**: Application healthy and responding

### Stage 8: Notifications
**Duration**: ~1 minute

Sends status notifications:
- Slack webhook notifications
- Success/failure messages
- Build details and commit info

**Requires**: `SLACK_WEBHOOK` secret

---

## Configuration

### Application Configuration Files

#### `application.properties` (Main)
Default production configuration loaded first.

#### `application-dev.properties`
Development-specific configuration:
- Debug logging enabled
- Schema auto-update (`update`)
- SQL logging enabled

#### `application-prod.properties`
Production-specific configuration:
- Minimal logging
- Schema validation only (`validate`)
- Security headers enabled
- Performance optimizations

#### `application-test.properties`
Test-specific configuration:
- H2 in-memory database
- Schema auto-create/drop
- Mock email service

### Docker Configuration

#### `Dockerfile`
Multi-stage build:
1. **Builder stage**: Compiles application with Maven
2. **Runtime stage**: Minimal JDK 17 image with curl

**Optimization**:
- Dependency pre-download for caching
- Non-root user execution
- Health check endpoint
- Memory optimization (G1GC)

#### `docker-compose.yml`
Local development environment:
- MySQL 8.0 database
- PhpMyAdmin for database access
- Redis for caching
- Application service
- Health checks for all services

### Environment Files

#### `.env.example`
Template with all required variables (safe to commit)

#### `env/staging.env`
Staging-specific configuration (not committed)

#### `env/production.env`
Production-specific configuration (not committed)

---

## Secrets Management

### Required Secrets

All secrets are configured in GitHub Repository Settings:

**Settings → Secrets and variables → Actions**

### General Secrets

| Secret | Description | Example |
|--------|-------------|---------|
| `SLACK_WEBHOOK` | Slack notification webhook | `https://hooks.slack.com/services/...` |
| `SONAR_HOST_URL` | SonarQube server URL | `https://sonarqube.example.com` |
| `SONAR_TOKEN` | SonarQube authentication token | `squ_...` |
| `DOCKER_USERNAME` | Docker registry username | `your-username` |
| `DOCKER_PASSWORD` | Docker registry password/PAT | `...` |
| `DOCKER_REGISTRY` | Docker registry URL | `ghcr.io` |

### Staging Environment Secrets

| Secret | Description |
|--------|-------------|
| `STAGING_HOST` | Staging server hostname |
| `STAGING_USER` | SSH deployment user |
| `STAGING_DEPLOY_KEY` | SSH private key for deployment |
| `STAGING_DB_PASSWORD` | Database password |
| `STAGING_MAIL_USERNAME` | Email service username |
| `STAGING_MAIL_PASSWORD` | Email service password |
| `STAGING_JWT_SECRET` | JWT signing secret (min 32 chars) |

### Production Environment Secrets

| Secret | Description |
|--------|-------------|
| `PROD_HOST` | Production server hostname |
| `PROD_USER` | SSH deployment user |
| `PROD_DEPLOY_KEY` | SSH private key for deployment |
| `PROD_DB_PASSWORD` | Database password |
| `PROD_MAIL_HOST` | Email server hostname |
| `PROD_MAIL_PORT` | Email server port |
| `PROD_MAIL_USERNAME` | Email service username |
| `PROD_MAIL_PASSWORD` | Email service password |
| `PROD_JWT_SECRET` | JWT signing secret (min 32 chars) |
| `PROD_REDIS_PASSWORD` | Redis password |
| `PROD_SENTRY_DSN` | Sentry error tracking DSN |
| `PROD_NEWRELIC_KEY` | NewRelic license key |

### Setting Secrets with GitHub CLI

```bash
# Authenticate
gh auth login

# Set a secret
gh secret set SECRET_NAME -b "secret_value"

# List all secrets
gh secret list

# Remove a secret
gh secret delete SECRET_NAME
```

---

## Deployment

### Manual Deployment

#### Using Make Commands

```bash
# Deploy to staging
make deploy-staging

# Deploy to production
make deploy-prod
```

#### Using Deployment Script

```bash
# Staging deployment
./scripts/deploy.sh staging latest

# Production deployment with version
./scripts/deploy.sh production v1.0.0
```

### Automated Deployment via GitHub Actions

**Staging** - Triggered automatically on `develop` branch push:
```bash
git push origin develop
```

**Production** - Triggered automatically on `main` branch push:
```bash
git push origin main
```

### Deployment Process

1. **Pre-deployment validation**
   - SSH connectivity test
   - Docker image availability check
   - Environment variables validation

2. **Deployment execution**
   - SSH into target server
   - Pull latest Docker image
   - Stop old container
   - Start new container with updated configuration

3. **Health validation**
   - Wait for application startup (40s timeout)
   - Check health endpoint: `/actuator/health`
   - Run smoke tests

4. **Post-deployment**
   - Verify API endpoints
   - Check database connectivity
   - Validate email service

### Rollback Procedure

If deployment fails or issues are detected:

```bash
# Rollback to previous version
docker-compose down
docker pull ghcr.io/your-org/medicare-ai:previous-version
docker-compose up -d

# Verify health
curl -f http://localhost:8080/actuator/health
```

---

## Monitoring & Troubleshooting

### View Pipeline Status

**GitHub UI**:
1. Repository → Actions tab
2. Select workflow run
3. View detailed logs for each stage

**GitHub CLI**:
```bash
# List recent runs
gh run list --workflow=backend-ci-cd.yml

# View specific run
gh run view <run-id> --log

# Watch run in real-time
gh run watch <run-id>
```

### Common Issues

#### Build Fails: "Cannot find or load main class"

**Cause**: JAR not built correctly

**Solution**:
```bash
# Clean and rebuild
mvn clean package -DskipTests
```

#### Tests Fail: "Connection refused" (database)

**Cause**: MySQL container not running

**Solution**:
```bash
# Start database
make db-start

# Run tests
make test
```

#### Docker Build Fails: "Invalid from flag value"

**Cause**: Dockerfile syntax error

**Solution**:
```bash
# Validate Dockerfile
docker build -t test:latest . --progress=plain
```

#### Deployment Fails: "Permission denied" (SSH)

**Cause**: Invalid SSH key or permissions

**Solution**:
```bash
# Verify SSH key
ssh -i /path/to/key user@host "echo test"

# Update secret with correct key
gh secret set PROD_DEPLOY_KEY -b "$(cat /path/to/key)"
```

#### Security Scan Fails: Critical CVE

**Cause**: Dependency with known vulnerability

**Solution**:
1. Identify vulnerable dependency:
   ```bash
   mvn org.owasp:dependency-check-maven:check
   ```

2. Update dependency in `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.example</groupId>
       <artifactId>vulnerable-lib</artifactId>
       <version>2.0.0</version> <!-- Update to patched version -->
   </dependency>
   ```

3. Re-run security check:
   ```bash
   mvn clean dependency-check:check
   ```

### Monitoring Application Health

#### Actuator Endpoints

After deployment, check application health:

```bash
# Health check
curl -f http://localhost:8080/actuator/health

# Get metrics
curl http://localhost:8080/actuator/metrics

# Application info
curl http://localhost:8080/actuator/info
```

#### Logs Monitoring

```bash
# View Docker logs
docker-compose logs -f app

# View specific service
docker logs -f medicare-ai-app

# Follow logs with grep
docker-compose logs app | grep ERROR
```

#### Database Monitoring

Access PhpMyAdmin for database inspection:
- URL: `http://localhost:8081`
- Username: `root`
- Password: `root`

### Performance Optimization Tips

1. **Speed up builds**: Use Maven caching in GitHub Actions
2. **Parallel tests**: Configure Maven Surefire plugin
3. **Docker layer caching**: Optimize Dockerfile layer order
4. **Database pool**: Tune HikariCP settings in properties files

### Security Best Practices

1. **Rotate secrets regularly** (every 90 days)
2. **Never commit secrets** to repository
3. **Use SSH keys** instead of passwords for deployment
4. **Enable branch protection** rules requiring pipeline to pass
5. **Review dependency updates** before merging
6. **Scan Docker images** for vulnerabilities:
   ```bash
   docker scan medicare-ai:latest
   ```

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarQube Documentation](https://docs.sonarqube.org)
- [Docker Documentation](https://docs.docker.com)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)

---

## Support

For pipeline issues:
1. Check workflow logs in GitHub Actions
2. Run local validation: `make analyze`
3. Test Docker build locally: `docker build -t test:latest .`
4. Verify secrets are set: `gh secret list`

---

**Last Updated**: March 2026  
**Pipeline Version**: 1.0.0  
**Maintainer**: DevOps Team

