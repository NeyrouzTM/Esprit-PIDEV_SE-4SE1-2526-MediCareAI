#!/bin/bash

# Medicare AI Backend Smoke Tests
# Usage: ./scripts/smoke-tests.sh [environment]

ENVIRONMENT=${1:-staging}

# Determine API URL based on environment
case "${ENVIRONMENT}" in
    development)
        API_URL="http://localhost:8080"
        ;;
    staging)
        API_URL="${STAGING_API_URL:-https://staging-api.medicare-ai.com}"
        ;;
    production)
        API_URL="${PROD_API_URL:-https://api.medicare-ai.com}"
        ;;
    *)
        API_URL="http://localhost:8080"
        ;;
esac

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASSED=0
FAILED=0

# Function to run a test
run_test() {
    local test_name=$1
    local test_url=$2
    local expected_status=${3:-200}

    echo -n "Testing: ${test_name}... "

    response=$(curl -s -o /dev/null -w "%{http_code}" "${test_url}")

    if [ "${response}" -eq "${expected_status}" ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ FAILED (Expected: ${expected_status}, Got: ${response})${NC}"
        ((FAILED++))
    fi
}

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Medicare AI Backend Smoke Tests${NC}"
echo -e "${YELLOW}Environment: ${ENVIRONMENT}${NC}"
echo -e "${YELLOW}API URL: ${API_URL}${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Health check
run_test "Health Check" "${API_URL}/actuator/health" 200

# API Documentation
run_test "Swagger UI" "${API_URL}/swagger-ui.html" 200

# API Docs
run_test "OpenAPI Docs" "${API_URL}/api-docs" 200

# Sample API endpoints (adjust based on your actual endpoints)
run_test "GET /api/health-events" "${API_URL}/api/health-events" 200
run_test "GET /api/appointments" "${API_URL}/api/appointments" 200

# Metrics endpoint
run_test "Metrics Endpoint" "${API_URL}/actuator/metrics" 200

# Info endpoint
run_test "Info Endpoint" "${API_URL}/actuator/info" 200

echo ""
echo -e "${YELLOW}========================================${NC}"
echo -e "Test Results: ${GREEN}${PASSED} passed${NC}, ${RED}${FAILED} failed${NC}"
echo -e "${YELLOW}========================================${NC}"

if [ ${FAILED} -gt 0 ]; then
    exit 1
fi

exit 0

