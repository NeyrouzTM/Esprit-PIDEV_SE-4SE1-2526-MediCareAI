#!/bin/bash

# Medicare AI Backend Deployment Script
# Usage: ./scripts/deploy.sh [environment] [version]

set -e

ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}
REGISTRY="ghcr.io"
IMAGE_NAME="medicare-ai-api"
FULL_IMAGE="${REGISTRY}/${IMAGE_NAME}:${VERSION}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Medicare AI Backend Deployment${NC}"
echo -e "${YELLOW}Environment: ${ENVIRONMENT}${NC}"
echo -e "${YELLOW}Version: ${VERSION}${NC}"
echo -e "${YELLOW}========================================${NC}"

# Function to log
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

# Function to error
error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

# Load environment configuration
if [ ! -f "env/${ENVIRONMENT}.env" ]; then
    error "Environment file not found: env/${ENVIRONMENT}.env"
fi

source "env/${ENVIRONMENT}.env"

log "Loaded environment configuration from env/${ENVIRONMENT}.env"

# Validate required variables
validate_env() {
    local var_name=$1
    local var_value=${!var_name}
    if [ -z "$var_value" ]; then
        error "Required variable ${var_name} is not set"
    fi
}

log "Validating environment variables..."
validate_env "DEPLOY_HOST"
validate_env "DEPLOY_USER"
validate_env "DEPLOY_PATH"

# Pre-deployment checks
log "Running pre-deployment checks..."

# Check if image exists
if ! docker pull "${FULL_IMAGE}" 2>/dev/null; then
    error "Docker image not found: ${FULL_IMAGE}"
fi
log "✓ Docker image verified"

# SSH connectivity check
if ! ssh -o ConnectTimeout=5 "${DEPLOY_USER}@${DEPLOY_HOST}" "echo 'SSH connection test'" > /dev/null 2>&1; then
    error "Cannot connect to ${DEPLOY_HOST}"
fi
log "✓ SSH connection successful"

# Perform deployment
log "Starting deployment..."

ssh "${DEPLOY_USER}@${DEPLOY_HOST}" <<ENDSSH
    set -e

    # Navigate to deployment directory
    cd ${DEPLOY_PATH}

    # Pull the latest image
    docker pull ${FULL_IMAGE}

    # Stop and remove old container
    docker-compose down || true

    # Update .env file
    echo "DOCKER_IMAGE=${FULL_IMAGE}" > .env

    # Start new container
    docker-compose up -d

    # Wait for application to be ready
    sleep 10

    # Run health check
    max_attempts=30
    attempt=1
    while [ \$attempt -le \$max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✓ Application is healthy"
            exit 0
        fi
        echo "Waiting for application to be ready... (\$attempt/\$max_attempts)"
        sleep 2
        attempt=\$((attempt + 1))
    done

    echo "✗ Application failed to become healthy"
    exit 1
ENDSSH

log "✓ Deployment completed successfully"

# Post-deployment validation
log "Running post-deployment validation..."

# Run smoke tests
if [ -f "scripts/smoke-tests.sh" ]; then
    bash scripts/smoke-tests.sh "${ENVIRONMENT}"
fi

log "✓ Smoke tests passed"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ Deployment to ${ENVIRONMENT} completed successfully${NC}"
echo -e "${GREEN}Version: ${VERSION}${NC}"
echo -e "${GREEN}========================================${NC}"

