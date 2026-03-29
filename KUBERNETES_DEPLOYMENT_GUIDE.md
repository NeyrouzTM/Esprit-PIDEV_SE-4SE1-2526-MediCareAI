# Kubernetes Deployment Guide for Medicare AI Backend

## Overview

This guide covers deploying the Medicare AI Backend to a Kubernetes cluster created with **KubeAdm**.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      Kubernetes Cluster                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    Ingress (NGINX)                       │  │
│  │         (Handles HTTP/HTTPS traffic routing)            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           ↓                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              LoadBalancer Service                        │  │
│  │           (medicare-ai-service on ports 80/443)         │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           ↓                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         Deployment (3 Replicas - Rolling Update)         │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │  │
│  │  │ Medicare-AI│  │ Medicare-AI│  │ Medicare-AI│  (pods) │  │
│  │  │   Pod 1    │  │   Pod 2    │  │   Pod 3    │         │  │
│  │  └────────────┘  └────────────┘  └────────────┘         │  │
│  │  (HPA: 3-10 replicas based on CPU/Memory)               │  │
│  └──────────────────────────────────────────────────────────┘  │
│         ↙                                    ↙                  │
│  ┌──────────────────┐              ┌──────────────────┐        │
│  │  MySQL Service   │              │  Redis Service   │        │
│  │  (StatefulSet)   │              │  (Deployment)    │        │
│  │                  │              │                  │        │
│  │  - 1 Replica     │              │  - 1 Replica     │        │
│  │  - PVC (10Gi)    │              │  - EmptyDir      │        │
│  │  - Port 3306     │              │  - Port 6379     │        │
│  └──────────────────┘              └──────────────────┘        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Prerequisites

### Software Requirements
- KubeAdm initialized Kubernetes cluster (v1.24+)
- kubectl configured with cluster access
- Docker or container runtime installed
- Helm (optional, for advanced deployments)

### Cluster Requirements
- Minimum 3 nodes (1 master, 2+ workers) for HA
- Each node: 2+ CPU, 4GB+ RAM
- Network connectivity between nodes
- Storage provisioner (local-path, NFS, etc.)

### Installed Operators (Optional but Recommended)
- NGINX Ingress Controller
- Cert-Manager (for TLS certificates)
- Prometheus Operator (for monitoring)
- Metrics Server (for HPA)

## Setup Steps

### 1. Prepare KubeAdm Cluster

```bash
# Initialize KubeAdm (on master node)
sudo kubeadm init --pod-network-cidr=10.244.0.0/16

# Setup kubeconfig
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Install pod network (Flannel example)
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# Join worker nodes
# On each worker, run the command output by kubeadm init:
sudo kubeadm join <control-plane-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>

# Verify cluster
kubectl get nodes
```

### 2. Install NGINX Ingress Controller

```bash
# Using Helm
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --set controller.service.type=LoadBalancer
```

### 3. Install Cert-Manager (for HTTPS)

```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Create ClusterIssuer for Let's Encrypt
kubectl apply -f k8s/cert-issuer.yaml
```

### 4. Deploy Medicare AI Application

```bash
# Make deployment script executable
chmod +x scripts/k8s-deploy.sh

# Run deployment
./scripts/k8s-deploy.sh latest

# Verify deployment
bash scripts/k8s-health-check.sh
```

## Kubernetes Manifest Files

### File Structure

```
k8s/
├── namespace.yaml           # Kubernetes namespace
├── configmap.yaml           # Application configuration
├── secret.yaml              # Sensitive data (passwords, tokens)
├── mysql.yaml               # MySQL StatefulSet
├── redis.yaml               # Redis Deployment
├── deployment.yaml          # Medicare AI Deployment + HPA + PDB
├── ingress-rbac.yaml        # Ingress + RBAC + NetworkPolicy
└── monitoring.yaml          # Prometheus monitoring
```

### 1. **namespace.yaml** - Kubernetes Namespace
- Creates `medicare-ai` namespace for resource isolation

### 2. **configmap.yaml** - Configuration
- Non-sensitive Spring Boot properties
- Database connection pool settings
- Logging configuration
- Actuator endpoints configuration

### 3. **secret.yaml** - Secrets Management
- Database passwords
- JWT secrets
- Email credentials
- Redis password
- **⚠️ DO NOT COMMIT TO GIT - USE SEALED SECRETS OR EXTERNAL SECRETS OPERATOR**

### 4. **mysql.yaml** - MySQL Database
- StatefulSet for persistent data
- PersistentVolumeClaim (10Gi)
- Liveness and readiness probes
- Resource requests/limits

### 5. **redis.yaml** - Redis Cache
- Deployment for caching layer
- Health checks
- Resource management

### 6. **deployment.yaml** - Application Deployment
- 3 replicas by default
- Rolling update strategy
- Resource requests/limits (512Mi memory, 250m CPU)
- Health checks (liveness, readiness, startup)
- Security context (non-root user)
- Pod anti-affinity (spread across nodes)
- HorizontalPodAutoscaler (3-10 replicas)
- PodDisruptionBudget (maintain 2 available)
- Init containers (wait for MySQL and Redis)

### 7. **ingress-rbac.yaml** - Networking & Security
- Ingress controller configuration
- NetworkPolicy for pod-to-pod communication
- RBAC (ServiceAccount, ClusterRole, ClusterRoleBinding)

### 8. **monitoring.yaml** - Prometheus Monitoring
- ServiceMonitor for Prometheus scraping
- PrometheusRule for alerting
- Storage for application logs

## Deployment Workflow

### Initial Deployment

```bash
# 1. Deploy infrastructure
./scripts/k8s-deploy.sh latest

# 2. Verify deployment
bash scripts/k8s-health-check.sh

# 3. Check logs
kubectl logs -f deployment/medicare-ai -n medicare-ai

# 4. Port forward for testing
kubectl port-forward svc/medicare-ai-service 8080:80 -n medicare-ai
curl http://localhost:8080/api/actuator/health
```

### Rolling Update Deployment

```bash
# Update image
kubectl set image deployment/medicare-ai \
  medicare-ai=ghcr.io/your-org/medicare-ai:v1.1.0 \
  -n medicare-ai

# Monitor rollout
kubectl rollout status deployment/medicare-ai -n medicare-ai

# View rollout history
kubectl rollout history deployment/medicare-ai -n medicare-ai

# Rollback if needed
kubectl rollout undo deployment/medicare-ai -n medicare-ai
```

## Managing Secrets

### ⚠️ Important: Secure Secret Management

**Option 1: Using Sealed Secrets** (Recommended for GitOps)

```bash
# Install sealed-secrets
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.24.0/sealed-secrets-0.24.0.yaml

# Create secret
kubectl create secret generic medicare-ai-secrets \
  --from-literal=db-password=mysecurepassword \
  -n medicare-ai --dry-run=client -o yaml | \
  kubeseal --format yaml > k8s/sealed-secret.yaml

# Apply sealed secret
kubectl apply -f k8s/sealed-secret.yaml
```

**Option 2: Using External Secrets Operator** (Best for multiple clusters)

```bash
# Install external-secrets
helm repo add external-secrets https://charts.external-secrets.io
helm install external-secrets external-secrets/external-secrets \
  -n external-secrets-system --create-namespace
```

**Option 3: Manual Secret Creation** (Development only)

```bash
kubectl create secret generic medicare-ai-secrets \
  -n medicare-ai \
  --from-literal=db-password='your-password' \
  --from-literal=jwt-secret='your-jwt-secret'
```

## Scaling & Performance

### Horizontal Scaling

```bash
# Manual scaling
kubectl scale deployment/medicare-ai --replicas=5 -n medicare-ai

# View HPA status
kubectl get hpa -n medicare-ai
kubectl describe hpa medicare-ai-hpa -n medicare-ai

# View metrics
kubectl top pods -n medicare-ai
kubectl top nodes
```

### Resource Limits

Current configuration:
- **Requests**: 512Mi memory, 250m CPU per pod
- **Limits**: 1Gi memory, 500m CPU per pod
- **Min Replicas**: 3
- **Max Replicas**: 10
- **Scale-up**: 100% increase per 30 seconds
- **Scale-down**: 50% decrease per 60 seconds after 5 min stability

Adjust in `k8s/deployment.yaml` if needed.

## Networking Configuration

### DNS

```bash
# Access services within cluster
mysql-service.medicare-ai.svc.cluster.local:3306
redis-service.medicare-ai.svc.cluster.local:6379
medicare-ai-service.medicare-ai.svc.cluster.local

# From other namespaces
mysql-service.medicare-ai.svc.cluster.local
```

### External Access

```bash
# Get Ingress IP/hostname
kubectl get ingress -n medicare-ai

# Create DNS records pointing to Ingress IP
# A record: medicare-ai.example.com -> <INGRESS_IP>
# A record: api.medicare-ai.example.com -> <INGRESS_IP>
```

## Persistence

### Database Volume

```bash
# Check PVC status
kubectl get pvc -n medicare-ai

# Check volume status
kubectl get pv

# Backup database
kubectl exec -it mysql-0 -n medicare-ai -- \
  mysqldump -u root -p$DB_PASSWORD medicare_ai > backup.sql

# Restore database
kubectl exec -it mysql-0 -n medicare-ai -- \
  mysql -u root -p$DB_PASSWORD medicare_ai < backup.sql
```

## Monitoring & Logs

### Viewing Logs

```bash
# Latest logs
kubectl logs deployment/medicare-ai -n medicare-ai

# Follow logs
kubectl logs -f deployment/medicare-ai -n medicare-ai

# Specific pod
kubectl logs pod/medicare-ai-xxxxx -n medicare-ai

# Previous pod (after restart)
kubectl logs -p pod/medicare-ai-xxxxx -n medicare-ai
```

### Accessing Metrics

```bash
# Prometheus metrics
kubectl port-forward svc/medicare-ai-service 8080:80 -n medicare-ai
curl http://localhost:8080/actuator/prometheus

# Resource usage
kubectl top pods -n medicare-ai
kubectl top nodes
```

## Troubleshooting

### Pod Not Starting

```bash
# Check pod status
kubectl describe pod <pod-name> -n medicare-ai

# Check events
kubectl get events -n medicare-ai --sort-by='.lastTimestamp'

# Check logs
kubectl logs <pod-name> -n medicare-ai
```

### Database Connection Issues

```bash
# Check MySQL is running
kubectl get pods -n medicare-ai -l app=mysql

# Test connection from pod
kubectl exec -it <pod-name> -n medicare-ai -- \
  mysql -h mysql-service -u medicare_user -p$DB_PASSWORD medicare_ai

# Check MySQL logs
kubectl logs mysql-0 -n medicare-ai
```

### Memory/CPU Issues

```bash
# Check resource usage
kubectl top pods -n medicare-ai
kubectl top nodes

# Describe node
kubectl describe node <node-name>

# Check HPA metrics
kubectl get hpa medicare-ai-hpa -n medicare-ai
kubectl describe hpa medicare-ai-hpa -n medicare-ai
```

## Maintenance

### Update Application Image

```bash
./scripts/k8s-deploy.sh v1.2.0
```

### Scale Deployment

```bash
kubectl scale deployment/medicare-ai --replicas=5 -n medicare-ai
```

### Backup and Restore

```bash
# Backup all resources
kubectl get all -n medicare-ai -o yaml > backup.yaml

# Restore from backup
kubectl apply -f backup.yaml
```

### Clean Up Cluster

```bash
# Delete application
kubectl delete namespace medicare-ai

# Verify
kubectl get namespace | grep medicare-ai
```

## Reference Documentation

- [Kubernetes Official Docs](https://kubernetes.io/docs/)
- [KubeAdm Documentation](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)
- [Cert-Manager](https://cert-manager.io/)
- [Pod Disruption Budgets](https://kubernetes.io/docs/tasks/run-application/configure-pdb/)

---

**Last Updated**: March 2026  
**Kubernetes Version**: 1.24+  
**Status**: ✅ Production Ready

