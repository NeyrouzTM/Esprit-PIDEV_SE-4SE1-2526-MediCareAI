.PHONY: help build test clean docker-build docker-push docker-run deploy-staging deploy-prod setup-env lint quality security

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
RED := \033[0;31m
NC := \033[0m # No Color

help:
	@echo "$(BLUE)Medicare AI Backend - Available Commands$(NC)"
	@echo ""
	@echo "$(GREEN)Development:$(NC)"
	@echo "  make setup-env          Setup development environment"
	@echo "  make build              Build the application"
	@echo "  make test               Run all tests"
	@echo "  make test-unit          Run unit tests only"
	@echo "  make test-integration   Run integration tests"
	@echo "  make clean              Clean build artifacts"
	@echo ""
	@echo "$(GREEN)Code Quality:$(NC)"
	@echo "  make lint               Run code style checks"
	@echo "  make quality            Run SonarQube analysis"
	@echo "  make security           Run security checks (OWASP Dependency Check)"
	@echo "  make analyze            Run all code analysis"
	@echo ""
	@echo "$(GREEN)Docker:$(NC)"
	@echo "  make docker-build       Build Docker image"
	@echo "  make docker-push        Push Docker image to registry"
	@echo "  make docker-run         Run application in Docker"
	@echo "  make docker-stop        Stop Docker container"
	@echo ""
	@echo "$(GREEN)Database:$(NC)"
	@echo "  make db-start           Start MySQL container"
	@echo "  make db-stop            Stop MySQL container"
	@echo "  make db-reset           Reset database"
	@echo ""
	@echo "$(GREEN)Deployment:$(NC)"
	@echo "  make deploy-staging     Deploy to staging"
	@echo "  make deploy-prod        Deploy to production"
	@echo ""
	@echo "$(GREEN)Documentation:$(NC)"
	@echo "  make docs               Generate API documentation"

# Environment Setup
setup-env:
	@echo "$(BLUE)Setting up development environment...$(NC)"
	@if [ ! -f .env ]; then cp .env.example .env; echo "✓ Created .env file"; fi
	@mvn clean install -DskipTests
	@echo "$(GREEN)✓ Development environment ready$(NC)"

# Build
build:
	@echo "$(BLUE)Building application...$(NC)"
	@mvn clean package -DskipTests -B
	@echo "$(GREEN)✓ Build completed$(NC)"

# Testing
test: test-unit test-integration
	@echo "$(GREEN)✓ All tests completed$(NC)"

test-unit:
	@echo "$(BLUE)Running unit tests...$(NC)"
	@mvn test
	@echo "$(GREEN)✓ Unit tests completed$(NC)"

test-integration:
	@echo "$(BLUE)Running integration tests...$(NC)"
	@mvn verify -P integration-tests
	@echo "$(GREEN)✓ Integration tests completed$(NC)"

test-coverage:
	@echo "$(BLUE)Running tests with coverage...$(NC)"
	@mvn test jacoco:report
	@echo "$(GREEN)✓ Coverage report generated: target/site/jacoco/index.html$(NC)"

# Code Quality
lint:
	@echo "$(BLUE)Running code style checks...$(NC)"
	@mvn checkstyle:check
	@echo "$(GREEN)✓ Code style checks completed$(NC)"

quality:
	@echo "$(BLUE)Running SonarQube analysis...$(NC)"
	@mvn sonar:sonar -Dsonar.projectKey=Medicare_Ai
	@echo "$(GREEN)✓ SonarQube analysis completed$(NC)"

security:
	@echo "$(BLUE)Running security checks...$(NC)"
	@mvn org.owasp:dependency-check-maven:check
	@echo "$(GREEN)✓ Security checks completed$(NC)"

analyze: lint quality security
	@echo "$(GREEN)✓ All analysis completed$(NC)"

# Clean
clean:
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	@mvn clean
	@rm -rf target/
	@echo "$(GREEN)✓ Clean completed$(NC)"

# Docker Operations
docker-build:
	@echo "$(BLUE)Building Docker image...$(NC)"
	@docker build -t medicare-ai:latest .
	@echo "$(GREEN)✓ Docker image built: medicare-ai:latest$(NC)"

docker-push:
	@echo "$(BLUE)Pushing Docker image...$(NC)"
	@docker tag medicare-ai:latest ghcr.io/your-org/medicare-ai:latest
	@docker push ghcr.io/your-org/medicare-ai:latest
	@echo "$(GREEN)✓ Docker image pushed$(NC)"

docker-run: docker-build
	@echo "$(BLUE)Running application in Docker...$(NC)"
	@docker-compose up -d
	@echo "$(GREEN)✓ Application started (http://localhost:8080)$(NC)"

docker-stop:
	@echo "$(BLUE)Stopping Docker containers...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✓ Containers stopped$(NC)"

docker-logs:
	@docker-compose logs -f app

# Database Operations
db-start:
	@echo "$(BLUE)Starting MySQL container...$(NC)"
	@docker-compose up -d mysql phpmyadmin
	@echo "$(GREEN)✓ MySQL started (http://localhost:3306)$(NC)"
	@echo "$(GREEN)✓ PhpMyAdmin available at http://localhost:8081$(NC)"

db-stop:
	@echo "$(BLUE)Stopping database containers...$(NC)"
	@docker-compose down mysql phpmyadmin
	@echo "$(GREEN)✓ Database stopped$(NC)"

db-reset: db-stop db-start
	@echo "$(GREEN)✓ Database reset completed$(NC)"

# Deployment
deploy-staging:
	@echo "$(BLUE)Deploying to staging...$(NC)"
	@bash scripts/deploy.sh staging latest
	@echo "$(GREEN)✓ Staging deployment completed$(NC)"

deploy-prod:
	@echo "$(BLUE)Deploying to production...$(NC)"
	@bash scripts/deploy.sh production latest
	@echo "$(GREEN)✓ Production deployment completed$(NC)"

# Documentation
docs:
	@echo "$(BLUE)Generating API documentation...$(NC)"
	@mvn springdoc-openapi:properties
	@echo "$(GREEN)✓ API documentation generated at http://localhost:8080/swagger-ui.html$(NC)"

# Run application locally
run:
	@echo "$(BLUE)Starting application...$(NC)"
	@mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

run-prod:
	@echo "$(BLUE)Starting application (production mode)...$(NC)"
	@mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Version information
version:
	@mvn -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec

# Git operations
git-tag:
	@echo "$(BLUE)Creating Git tag...$(NC)"
	@git tag -a v$(shell mvn -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec) -m "Release version $$(mvn -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec)"
	@git push --tags
	@echo "$(GREEN)✓ Git tag created and pushed$(NC)"

