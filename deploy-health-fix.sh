#!/bin/bash

# Medicare AI - Deploy Health Check Fix
# This script handles the deployment of the health check fix

NAMESPACE="medicare-ai"

echo "=========================================="
echo "Medicare AI Health Check Fix Deployment"
echo "=========================================="
echo ""

# Show what we're doing
echo "CHANGES BEING APPLIED:"
echo "1. Health endpoints updated in deployment.yaml"
echo "2. Using /api/actuator/health/liveness for startup/liveness probes"
echo "3. Using /api/actuator/health/readiness for readiness probe"
echo ""

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "Error: kubectl not found. Please ensure kubectl is installed."
    exit 1
fi

# Check cluster connectivity
echo "Checking Kubernetes cluster..."
if ! kubectl cluster-info &> /dev/null; then
    echo "Error: Cannot connect to Kubernetes cluster"
    exit 1
fi
echo "✓ Connected to Kubernetes cluster"
echo ""

# Apply the deployment
echo "Applying updated deployment..."
kubectl apply -f k8s/deployment.yaml -n $NAMESPACE

if [ $? -eq 0 ]; then
    echo "✓ Deployment updated successfully"
    echo ""
    echo "Waiting for rollout (up to 5 minutes)..."
    kubectl rollout status deployment/medicare-ai -n $NAMESPACE --timeout=5m

    if [ $? -eq 0 ]; then
        echo ""
        echo "=========================================="
        echo "✓ DEPLOYMENT SUCCESSFUL!"
        echo "=========================================="
        echo ""
        echo "Pod Status:"
        kubectl get pods -n $NAMESPACE -l app=medicare-ai
        echo ""
        echo "Service Status:"
        kubectl get svc -n $NAMESPACE medicare-ai-service
        echo ""
        echo "Testing health endpoints..."
        sleep 2
        kubectl exec -n $NAMESPACE deployment/medicare-ai -- curl -s http://localhost:8080/api/actuator/health/liveness | grep -q status && \
        echo "✓ Liveness probe is healthy" || echo "⚠ Liveness probe check inconclusive"

        echo ""
        echo "Next steps:"
        echo "1. View logs: kubectl logs -n $NAMESPACE deployment/medicare-ai --tail=100"
        echo "2. Port forward: kubectl port-forward -n $NAMESPACE svc/medicare-ai-service 8080:80"
        echo "3. Test API: curl http://localhost:8080/api/actuator/health"
    else
        echo ""
        echo "⚠ Rollout did not complete in time"
        echo "Showing recent logs..."
        kubectl logs -n $NAMESPACE deployment/medicare-ai --tail=50
        echo ""
        echo "Check pod status: kubectl get pods -n $NAMESPACE"
        exit 1
    fi
else
    echo "✗ Failed to apply deployment"
    exit 1
fi

