# Medicare AI Backend - CI/CD Pipeline

## 📋 Overview

This repository contains the complete CI/CD pipeline configuration for the Medicare AI Backend application. The pipeline automates:

- ✅ Code compilation and testing
- ✅ Security vulnerability scanning
- ✅ Code quality analysis with SonarQube
- ✅ Docker image building and publishing
- ✅ Integration testing
- ✅ Automated deployment to staging and production
- ✅ Slack notifications

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8.6 or higher
- Docker & Docker Compose
- Git with GitHub access
- GitHub CLI (`gh`) for secret management

### 1. Clone Repository

```bash
git clone https://github.com/your-org/Medicare_Ai.git
cd Medicare_Ai
```

### 2. Setup Environment

```bash
# Install dependencies and setup development environment
make setup-env

# Start local database
make db-start

# Run application locally
make run
```

### 3. Configure CI/CD Secrets

```bash
# Interactive secret setup
chmod +x scripts/setup-github-secrets.sh
./scripts/setup-github-secrets.sh

# Or manually add secrets in: GitHub → Settings → Secrets and variables
```

### 4. Trigger Pipeline

```bash
# Commit changes and push to develop (staging) or main (production)
git add .
git commit -m "feat: initialize CI/CD pipeline"
git push origin develop
```

The pipeline will automatically:
1. ✅ Validate code
2. ✅ Build and test
3. ✅ Scan for security issues
4. ✅ Analyze code quality
5. ✅ Build Docker image
6. ✅ Run integration tests
7. ✅ Deploy to appropriate environment
8. ✅ Send Slack notification

## 📁 File Structure

```
Medicare_Ai/
├── .github/workflows/
│   └── backend-ci-cd.yml          # Main CI/CD pipeline definition
├── scripts/
│   ├── deploy.sh                  # Deployment script
│   ├── smoke-tests.sh             # Post-deployment smoke tests
│   └── setup-github-secrets.sh    # Secret configuration script
├── env/
│   ├── staging.env                # Staging environment config
│   └── production.env             # Production environment config
├── src/
│   ├── main/
│   │   ├── java/tn/esprit/        # Application source code
│   │   └── resources/
│   │       ├── application.properties          # Main config
│   │       ├── application-dev.properties      # Dev profile
│   │       ├── application-prod.properties     # Prod profile
│   │       └── application-test.properties     # Test profile
│   └── test/
│       └── java/tn/esprit/        # Test code
├── Dockerfile                     # Container image definition
├── docker-compose.yml             # Local development setup
├── pom.xml                        # Maven configuration
├── Makefile                       # Convenient command shortcuts
├── .env.example                   # Environment template
├── CICD_PIPELINE_GUIDE.md        # Detailed pipeline documentation
└── CICD_QUICK_START.md           # Quick start guide
```

## 🔧 Key Commands

### Development

```bash
make help              # Show all available commands
make build             # Build application
make test              # Run all tests
make test-unit         # Unit tests only
make test-integration  # Integration tests only
make lint              # Code style checks
make security          # Security vulnerability scan
make analyze           # All code analysis
make clean             # Clean build artifacts
```

### Docker

```bash
make docker-build      # Build Docker image
make docker-run        # Start application in Docker
make docker-stop       # Stop Docker containers
make docker-logs       # View application logs
```

### Database

```bash
make db-start          # Start MySQL container
make db-stop           # Stop MySQL container
make db-reset          # Reset database
```

### Deployment

```bash
make deploy-staging    # Deploy to staging environment
make deploy-prod       # Deploy to production environment
```

## 📊 Pipeline Stages

### Stage 1: Validation (2 min)
- Maven project validation
- Code style checks
- POM.xml structure validation

### Stage 2: Build & Tests (5-8 min)
- Maven clean build
- Unit test execution
- Test report generation
- Code coverage analysis

### Stage 3: Security Scanning (3-5 min)
- OWASP Dependency Check for CVEs
- SpotBugs for potential bugs
- No CRITICAL vulnerabilities allowed

### Stage 4: Code Quality (2-3 min)
- SonarQube analysis
- Code complexity assessment
- Technical debt calculation
- Security hotspot detection

### Stage 5: Docker Build & Push (5-10 min)
- Multi-stage Docker build
- Image tagging and versioning
- Push to GitHub Container Registry

### Stage 6: Integration Tests (5-10 min)
- Full API endpoint testing
- Database integration testing
- End-to-end workflow validation

### Stage 7: Deployment (5-10 min)
- Deploy to Staging (develop branch)
- Deploy to Production (main branch)
- Health check validation
- Smoke tests execution

### Stage 8: Notifications (1 min)
- Slack notification with status
- Build details and commit info

## 🔐 Secrets Configuration

Required secrets in GitHub Settings → Secrets and variables:

### Docker Registry
- `DOCKER_USERNAME` - Docker registry username
- `DOCKER_PASSWORD` - Docker registry password
- `DOCKER_REGISTRY` - Registry URL (e.g., ghcr.io)

### Code Quality
- `SONAR_HOST_URL` - SonarQube server URL
- `SONAR_TOKEN` - SonarQube authentication token

### Notifications
- `SLACK_WEBHOOK` - Slack webhook URL for notifications

### Staging Environment
- `STAGING_HOST` - Staging server hostname
- `STAGING_USER` - SSH deployment user
- `STAGING_DEPLOY_KEY` - SSH private key
- `STAGING_DB_PASSWORD` - Database password
- `STAGING_JWT_SECRET` - JWT secret (min 32 chars)
- `STAGING_MAIL_USERNAME` - Email username
- `STAGING_MAIL_PASSWORD` - Email password

### Production Environment
- `PROD_HOST` - Production server hostname
- `PROD_USER` - SSH deployment user
- `PROD_DEPLOY_KEY` - SSH private key
- `PROD_DB_PASSWORD` - Database password
- `PROD_MAIL_HOST` - Email server host
- `PROD_MAIL_PORT` - Email server port
- `PROD_MAIL_USERNAME` - Email username
- `PROD_MAIL_PASSWORD` - Email password
- `PROD_JWT_SECRET` - JWT secret (min 32 chars)
- `PROD_REDIS_PASSWORD` - Redis password
- `PROD_SENTRY_DSN` - Sentry error tracking (optional)
- `PROD_NEWRELIC_KEY` - NewRelic monitoring (optional)

## 🌳 Git Branching Strategy

```
main (Production)
  ↑
  └─ Pull Request from develop
  
develop (Staging)
  ↑
  └─ Feature branches
      ├─ feature/new-api
      ├─ bugfix/security-fix
      └─ release/v1.0.0
```

## 📈 Pipeline Triggers

| Event | Branch | Action |
|-------|--------|--------|
| Push | main | Deploy to Production |
| Push | develop | Deploy to Staging |
| Push | feature/* | Run tests only |
| Pull Request | develop/main | Run tests only |
| Manual | Any | Triggered by workflow_dispatch |

## 🔍 Testing

### Unit Tests
```bash
# Run unit tests
mvn test

# Generate coverage report
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

### Integration Tests
```bash
# Run integration tests
mvn verify

# With specific profile
mvn verify -P integration-tests
```

### Test Coverage
- Unit test code coverage: **Target 80%+**
- Integration test coverage: **Critical paths**

## 📝 Monitoring

### Application Health
```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Metrics endpoint
curl http://localhost:8080/actuator/metrics

# API Documentation
http://localhost:8080/swagger-ui.html
```

### Database Access
- PhpMyAdmin: http://localhost:8081
- Username: root
- Password: root

### Pipeline Logs
- GitHub Actions: Repository → Actions tab
- GitHub CLI: `gh run list --workflow=backend-ci-cd.yml`

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Build fails | `mvn clean install` and check logs |
| Tests fail | `make db-start` then `make test` |
| Docker fails | `docker system prune` then rebuild |
| SSH deployment fails | Verify DEPLOY_KEY secret is valid |
| Pipeline not running | Check `.github/workflows/backend-ci-cd.yml` exists |

## 📚 Documentation

- [CI/CD Pipeline Guide](./CICD_PIPELINE_GUIDE.md) - Detailed documentation
- [Quick Start Guide](./CICD_QUICK_START.md) - 5-minute setup guide
- [Architecture Design](./ARCHITECTURE_DESIGN.md) - System architecture
- [Deployment Checklist](./DEPLOYMENT_CHECKLIST.md) - Pre-deployment verification

## 🤝 Contributing

1. Create feature branch: `git checkout -b feature/my-feature`
2. Make changes and test: `make test`
3. Push to GitHub: `git push origin feature/my-feature`
4. Create Pull Request targeting `develop`
5. Pipeline automatically runs all checks
6. Merge after approval and pipeline success

## 📞 Support

For issues or questions:

1. Check the [Troubleshooting Guide](./CICD_PIPELINE_GUIDE.md#monitoring--troubleshooting)
2. Review GitHub Actions logs
3. Run local validation: `make analyze`
4. Contact DevOps team

## 📄 License

This project is licensed under your organization's license.

## 🙏 Acknowledgments

- GitHub Actions for CI/CD automation
- SonarQube for code quality analysis
- OWASP for security scanning
- Spring Boot team for excellent framework

---

**Last Updated**: March 2026  
**Pipeline Version**: 1.0.0  
**Status**: ✅ Production Ready

