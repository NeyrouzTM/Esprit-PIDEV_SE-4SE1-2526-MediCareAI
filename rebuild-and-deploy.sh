#!/bin/bash

# Medicare AI - Rebuild and Deploy Script
# This script rebuilds the Docker image and redeploys to Kubernetes

set -e

echo "=========================================="
echo "Medicare AI - Rebuild & Deploy"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="brahemahmed/medicare-ai"
IMAGE_TAG="latest"
NAMESPACE="medicare-ai"
DEPLOYMENT_NAME="medicare-ai"

# Step 1: Build Docker image
echo -e "${YELLOW}[1/4]${NC} Building Docker image..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Docker image built successfully${NC}"
else
    echo -e "${RED}✗ Docker build failed${NC}"
    exit 1
fi

# Step 2: Push image to registry (optional - only if pushing to registry)
read -p "Push image to registry? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}[2/4]${NC} Pushing image to registry..."
    docker push ${IMAGE_NAME}:${IMAGE_TAG}
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Image pushed successfully${NC}"
    else
        echo -e "${RED}✗ Image push failed${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}[2/4]${NC} Skipping image push"
fi

# Step 3: Restart the deployment
echo -e "${YELLOW}[3/4]${NC} Restarting deployment..."
kubectl rollout restart deployment/${DEPLOYMENT_NAME} -n ${NAMESPACE}
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Deployment restarted${NC}"
else
    echo -e "${RED}✗ Deployment restart failed${NC}"
    exit 1
fi

# Step 4: Wait for rollout
echo -e "${YELLOW}[4/4]${NC} Waiting for rollout to complete (max 5 minutes)..."
kubectl rollout status deployment/${DEPLOYMENT_NAME} -n ${NAMESPACE} --timeout=5m
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Deployment successful${NC}"
    echo ""
    echo "=========================================="
    echo -e "${GREEN}Deployment completed successfully!${NC}"
    echo "=========================================="
    echo ""
    echo "Service Information:"
    kubectl get svc -n ${NAMESPACE} ${DEPLOYMENT_NAME}-service
    echo ""
    echo "Pod Status:"
    kubectl get pods -n ${NAMESPACE} -l app=${DEPLOYMENT_NAME}
else
    echo -e "${RED}✗ Deployment failed or timed out${NC}"
    echo ""
    echo "Pod logs:"
    kubectl logs -n ${NAMESPACE} deployment/${DEPLOYMENT_NAME} --tail=50
    exit 1
fi

