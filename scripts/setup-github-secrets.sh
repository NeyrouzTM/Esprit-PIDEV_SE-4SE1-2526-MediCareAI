#!/bin/bash

# Setup GitHub Secrets for CI/CD Pipeline
# Usage: ./scripts/setup-github-secrets.sh

echo "=========================================="
echo "GitHub Secrets Setup Script"
echo "=========================================="
echo ""
echo "This script will help you set required secrets in GitHub."
echo "Make sure you have GitHub CLI installed: https://cli.github.com/"
echo ""

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI is not installed"
    echo "Please install it from: https://cli.github.com/"
    exit 1
fi

# Check if authenticated with GitHub
if ! gh auth status &> /dev/null; then
    echo "Authenticating with GitHub..."
    gh auth login
fi

echo ""
echo "Setting up secrets..."
echo ""

# Function to set secret
set_secret() {
    local secret_name=$1
    local secret_description=$2

    echo -n "Enter value for ${secret_name} (${secret_description}): "
    read -r secret_value

    if [ -n "$secret_value" ]; then
        gh secret set "${secret_name}" -b "${secret_value}"
        echo "✓ ${secret_name} set successfully"
    else
        echo "⚠ Skipping ${secret_name}"
    fi
}

echo "=== General Secrets ==="
set_secret "SLACK_WEBHOOK" "Slack webhook URL for notifications"
set_secret "SONAR_HOST_URL" "SonarQube server URL"
set_secret "SONAR_TOKEN" "SonarQube authentication token"

echo ""
echo "=== Docker Registry Secrets ==="
set_secret "DOCKER_USERNAME" "Docker registry username"
set_secret "DOCKER_PASSWORD" "Docker registry password"
set_secret "DOCKER_REGISTRY" "Docker registry URL (e.g., ghcr.io)"

echo ""
echo "=== Staging Environment Secrets ==="
set_secret "STAGING_HOST" "Staging server hostname"
set_secret "STAGING_USER" "Staging deployment user"
set_secret "STAGING_DEPLOY_KEY" "Staging SSH private key"
set_secret "STAGING_DB_PASSWORD" "Staging database password"
set_secret "STAGING_MAIL_USERNAME" "Staging mail username"
set_secret "STAGING_MAIL_PASSWORD" "Staging mail password"
set_secret "STAGING_JWT_SECRET" "Staging JWT secret (min 32 chars)"

echo ""
echo "=== Production Environment Secrets ==="
set_secret "PROD_HOST" "Production server hostname"
set_secret "PROD_USER" "Production deployment user"
set_secret "PROD_DEPLOY_KEY" "Production SSH private key"
set_secret "PROD_DB_PASSWORD" "Production database password"
set_secret "PROD_MAIL_HOST" "Production mail server"
set_secret "PROD_MAIL_PORT" "Production mail port"
set_secret "PROD_MAIL_USERNAME" "Production mail username"
set_secret "PROD_MAIL_PASSWORD" "Production mail password"
set_secret "PROD_JWT_SECRET" "Production JWT secret (min 32 chars)"
set_secret "PROD_REDIS_PASSWORD" "Production Redis password"
set_secret "PROD_SENTRY_DSN" "Sentry DSN for error tracking"
set_secret "PROD_NEWRELIC_KEY" "NewRelic license key"

echo ""
echo "=========================================="
echo "✓ Secrets setup completed!"
echo "=========================================="
echo ""
echo "To verify secrets, run:"
echo "  gh secret list"
echo ""

