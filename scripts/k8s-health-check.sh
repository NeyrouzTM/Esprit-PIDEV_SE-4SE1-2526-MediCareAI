#!/bin/bash

# Kubernetes Cluster Health Check Script
# Verifies cluster configuration and application deployment

set -e

NAMESPACE="medicare-ai"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${GREEN}[$(date +'%H:%M:%S')]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }
warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
info() { echo -e "${BLUE}[INFO]${NC} $1"; }

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Kubernetes Cluster Health Check${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Cluster status
log "Checking cluster status..."
kubectl cluster-info
echo

# Node status
log "Checking node status..."
kubectl get nodes -o wide
echo

# Namespace
log "Checking namespace: $NAMESPACE"
kubectl get namespace $NAMESPACE || warning "Namespace not found"
echo

# Pods
log "Checking pods in $NAMESPACE..."
kubectl get pods -n $NAMESPACE -o wide
echo

# Services
log "Checking services in $NAMESPACE..."
kubectl get svc -n $NAMESPACE
echo

# Deployments
log "Checking deployments in $NAMESPACE..."
kubectl get deployments -n $NAMESPACE
echo

# StatefulSets
log "Checking StatefulSets in $NAMESPACE..."
kubectl get statefulsets -n $NAMESPACE
echo

# Storage
log "Checking persistent volumes..."
kubectl get pv
echo

log "Checking persistent volume claims in $NAMESPACE..."
kubectl get pvc -n $NAMESPACE
echo

# ConfigMaps
log "Checking ConfigMaps in $NAMESPACE..."
kubectl get configmap -n $NAMESPACE
echo

# Secrets (without showing values)
log "Checking Secrets in $NAMESPACE..."
kubectl get secrets -n $NAMESPACE
echo

# Check resource usage
log "Checking resource usage..."
kubectl top nodes 2>/dev/null || warning "Metrics server not available"
echo

# Check recent events
log "Recent events in $NAMESPACE..."
kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp' | tail -20
echo

# Test application
log "Testing application endpoints..."
POD=$(kubectl get pod -n $NAMESPACE -l app=medicare-ai -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "$POD" ]; then
    log "Found pod: $POD"

    info "Testing health endpoint..."
    kubectl exec -n $NAMESPACE $POD -- curl -s http://localhost:8080/actuator/health | grep -q "UP" && log "✓ Health check passed" || error "Health check failed"

    info "Testing metrics endpoint..."
    kubectl exec -n $NAMESPACE $POD -- curl -s http://localhost:8080/actuator/metrics | grep -q "names" && log "✓ Metrics endpoint working" || error "Metrics endpoint not working"
else
    warning "No medicare-ai pods found"
fi

echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Health check completed${NC}"
echo -e "${GREEN}========================================${NC}"

