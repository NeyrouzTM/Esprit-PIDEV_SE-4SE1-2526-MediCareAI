#!/bin/bash

# Medicare AI Kubernetes Deployment Script
# Deploys application to Kubernetes cluster using KubeAdm

set -e

# Configuration
NAMESPACE="medicare-ai"
KUBECONFIG="${KUBECONFIG:-$HOME/.kube/config}"
DOCKER_REGISTRY="${DOCKER_REGISTRY:-docker.io}"
DOCKER_IMAGE="${DOCKER_IMAGE:-brahemahmed/medicare-ai}"
IMAGE_TAG="${1:-latest}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log "Checking prerequisites..."

    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        error "kubectl not found. Please install kubectl."
    fi
    info "✓ kubectl found"

    # Check kubeconfig
    if [ ! -f "$KUBECONFIG" ]; then
        error "kubeconfig not found at $KUBECONFIG"
    fi
    info "✓ kubeconfig found"

    # Check cluster connectivity
    if ! kubectl cluster-info &> /dev/null; then
        error "Cannot connect to Kubernetes cluster. Check your kubeconfig."
    fi
    info "✓ Kubernetes cluster is accessible"

    # Check Kubernetes version
    K8S_VERSION=$(kubectl version --short 2>/dev/null | grep Server | awk '{print $3}')
    info "✓ Kubernetes version: $K8S_VERSION"
}

# Create namespace
create_namespace() {
    log "Creating namespace: $NAMESPACE"

    if kubectl get namespace $NAMESPACE &> /dev/null; then
        info "Namespace already exists"
    else
        kubectl apply -f k8s/namespace.yaml
        info "✓ Namespace created"
    fi
}

# Update Docker image in deployment
update_image() {
    log "Updating Docker image to: $DOCKER_REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"

    # Update the image in deployment.yaml
    sed -i.bak "s|image: .*medicare-ai:.*|image: $DOCKER_REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG|g" k8s/deployment.yaml

    info "✓ Image updated to: $DOCKER_REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"
}

# Deploy configurations
deploy_configs() {
    log "Deploying configurations..."

    # ConfigMap
    kubectl apply -f k8s/configmap.yaml
    info "✓ ConfigMap deployed"

    # Secrets
    log "Deploying secrets..."
    read -sp "Enter database password: " DB_PASSWORD
    echo
    read -sp "Enter JWT secret (min 32 chars): " JWT_SECRET
    echo

    # Create secret from file with values
    kubectl delete secret medicare-ai-secrets -n $NAMESPACE 2>/dev/null || true
    kubectl create secret generic medicare-ai-secrets \
        -n $NAMESPACE \
        --from-literal=db-password="$DB_PASSWORD" \
        --from-literal=jwt-secret="$JWT_SECRET" \
        --from-literal=db-url="jdbc:mysql://mysql-service:3306/medicare_ai?useSSL=true&serverTimezone=UTC" \
        --from-literal=db-username="medicare_user" \
        --from-literal=mail-host="smtp.gmail.com" \
        --from-literal=mail-port="587" \
        --from-literal=mail-username="your-email@gmail.com" \
        --from-literal=mail-password="your-app-password" \
        --from-literal=redis-password="$DB_PASSWORD"

    info "✓ Secrets deployed"
}

# Deploy infrastructure (MySQL, Redis)
deploy_infrastructure() {
    log "Deploying infrastructure services..."

    kubectl apply -f k8s/mysql.yaml
    info "✓ MySQL StatefulSet deployed"

    log "Waiting for MySQL to be ready..."
    kubectl wait --for=condition=ready pod -l app=mysql -n $NAMESPACE --timeout=300s || warning "MySQL not ready yet"

    kubectl apply -f k8s/redis.yaml
    info "✓ Redis Deployment deployed"

    log "Waiting for Redis to be ready..."
    kubectl wait --for=condition=ready pod -l app=redis -n $NAMESPACE --timeout=300s || warning "Redis not ready yet"
}

# Deploy application
deploy_application() {
    log "Deploying Medicare AI application..."

    kubectl apply -f k8s/deployment.yaml
    info "✓ Deployment created"

    # Wait for rollout
    log "Waiting for deployment to be ready (this may take a few minutes)..."
    kubectl rollout status deployment/medicare-ai -n $NAMESPACE --timeout=600s
    info "✓ Deployment is ready"
}

# Deploy networking and security
deploy_networking() {
    log "Deploying networking and security policies..."

    kubectl apply -f k8s/ingress-rbac.yaml
    info "✓ Ingress, RBAC, and NetworkPolicy deployed"
}

# Deploy monitoring
deploy_monitoring() {
    log "Deploying monitoring configuration..."

    kubectl apply -f k8s/monitoring.yaml
    info "✓ Monitoring configuration deployed"
}

# Verify deployment
verify_deployment() {
    log "Verifying deployment..."

    echo
    info "Checking pod status:"
    kubectl get pods -n $NAMESPACE

    echo
    info "Checking services:"
    kubectl get svc -n $NAMESPACE

    echo
    info "Checking deployments:"
    kubectl get deployments -n $NAMESPACE

    # Test connectivity
    log "Testing application health..."
    POD_NAME=$(kubectl get pod -n $NAMESPACE -l app=medicare-ai -o jsonpath='{.items[0].metadata.name}')

    if [ -n "$POD_NAME" ]; then
        kubectl exec -n $NAMESPACE $POD_NAME -- curl -f http://localhost:8080/actuator/health || warning "Health check failed"
        info "✓ Application is responding"
    fi
}

# Main execution
main() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}Medicare AI Kubernetes Deployment${NC}"
    echo -e "${BLUE}Namespace: $NAMESPACE${NC}"
    echo -e "${BLUE}Image: $DOCKER_REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo

    check_prerequisites
    echo

    create_namespace
    echo

    update_image
    echo

    deploy_configs
    echo

    deploy_infrastructure
    echo

    deploy_application
    echo

    deploy_networking
    echo

    deploy_monitoring
    echo

    verify_deployment

    echo
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}✓ Deployment completed successfully!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo
    info "Application endpoints:"
    echo "  API: http://medicare-ai.example.com/api"
    echo "  Health: http://medicare-ai.example.com/actuator/health"
    echo "  Swagger: http://medicare-ai.example.com/swagger-ui.html"
    echo
    info "Useful commands:"
    echo "  View logs: kubectl logs -f deployment/medicare-ai -n $NAMESPACE"
    echo "  Port forward: kubectl port-forward svc/medicare-ai-service 8080:80 -n $NAMESPACE"
    echo "  Describe pod: kubectl describe pod <pod-name> -n $NAMESPACE"
    echo "  Get events: kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp'"
    echo
}

# Run main
main

