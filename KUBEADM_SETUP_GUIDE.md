# KubeAdm Kubernetes Setup Guide for Medicare AI

> Complete guide for setting up a Kubernetes cluster with KubeAdm and deploying Medicare AI Backend

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [KubeAdm Cluster Setup](#kubeadm-cluster-setup)
3. [Installing Essential Components](#installing-essential-components)
4. [Deploying Medicare AI](#deploying-medicare-ai)
5. [Production Considerations](#production-considerations)
6. [Maintenance & Troubleshooting](#maintenance--troubleshooting)

---

## Prerequisites

### Hardware Requirements

**Minimum Configuration**:
- **Master Node**: 2+ CPUs, 4GB RAM, 20GB storage
- **Worker Nodes**: 2+ CPUs, 4GB RAM, 20GB storage
- **Total Cluster**: 3 nodes minimum (1 master, 2 workers)

**Recommended Configuration**:
- **Master Node**: 4 CPUs, 8GB RAM, 50GB storage
- **Worker Nodes**: 4+ CPUs, 8GB RAM, 50GB storage
- **Total Cluster**: 3+ nodes

### Network Requirements

- All nodes must be on the same network (or with proper routing)
- Minimum bandwidth: 1 Mbps between nodes
- Ports to open:
  - **Master**: 6443 (API), 2379-2380 (etcd), 10250-10259 (kubelet)
  - **Workers**: 10250 (kubelet), 30000-32767 (NodePort)

### Software Requirements

All nodes need:
- **OS**: Ubuntu 20.04 LTS or 22.04 LTS (or compatible)
- **Container Runtime**: Docker 20.10+ or containerd
- **Kubernetes**: 1.24+ (will be installed by kubeadm)
- **kubectl**: Command-line tool
- **kubeadm**: Kubernetes bootstrapping tool
- **kubelet**: Node component

---

## KubeAdm Cluster Setup

### Step 1: Prepare All Nodes

Run this on **all nodes** (master and workers):

```bash
#!/bin/bash
# Prepare Kubernetes nodes script

# Update system
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

# Install Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add current user to docker group
sudo usermod -aG docker $USER
newgrp docker

# Disable swap (Kubernetes requirement)
sudo swapoff -a
sudo sed -i '/ swap / s/^/#/' /etc/fstab

# Load kernel modules
sudo modprobe overlay
sudo modprobe br_netfilter

# Configure kernel parameters
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward = 1
EOF

sudo sysctl --system

# Install kubeadm, kubelet, kubectl
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

# Enable kubelet service
sudo systemctl daemon-reload
sudo systemctl enable kubelet

echo "✓ Node preparation completed"
```

### Step 2: Initialize Master Node

On the **master node** only:

```bash
#!/bin/bash
# Initialize Kubernetes master with KubeAdm

# Choose pod network CIDR (Flannel uses 10.244.0.0/16)
POD_NETWORK_CIDR="10.244.0.0/16"

# Choose Kubernetes version
K8S_VERSION="1.28.0"

# Initialize the cluster
sudo kubeadm init \
  --kubernetes-version=$K8S_VERSION \
  --pod-network-cidr=$POD_NETWORK_CIDR \
  --apiserver-advertise-address=$(hostname -I | awk '{print $1}') \
  --apiserver-bind-port=6443

# Setup kubeconfig for current user
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
chmod 600 $HOME/.kube/config

# Export kubeconfig
export KUBECONFIG=$HOME/.kube/config

# Verify cluster
kubectl get nodes
kubectl get pods --all-namespaces

echo "✓ Master node initialized"
echo ""
echo "To join worker nodes, save this command:"
kubeadm token create --print-join-command
```

**⚠️ Important**: Save the output of `kubeadm token create --print-join-command`

### Step 3: Install Pod Network (Flannel)

On the **master node**:

```bash
# Install Flannel as pod network
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# Wait for Flannel to be ready
kubectl wait --for=condition=ready pod -l app=flannel -n kube-flannel --timeout=300s

# Verify CoreDNS and Flannel pods are running
kubectl get pods --all-namespaces
```

### Step 4: Join Worker Nodes

On **each worker node**, run the command from Step 2:

```bash
# This command was output by kubeadm init on master
sudo kubeadm join <master-ip>:6443 \
  --token <token> \
  --discovery-token-ca-cert-hash sha256:<hash>

# Wait for node to join
kubectl get nodes  # Run this on master
```

### Step 5: Verify Cluster

On the **master node**:

```bash
# Check nodes
kubectl get nodes

# Expected output:
# NAME     STATUS   ROLES           AGE   VERSION
# master   Ready    control-plane   5m    v1.28.0
# worker1  Ready    <none>          2m    v1.28.0
# worker2  Ready    <none>          2m    v1.28.0

# Check system pods
kubectl get pods --all-namespaces

# All should be running
```

---

## Installing Essential Components

### 1. Helm (Optional but Recommended)

```bash
# Download Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Verify installation
helm version
```

### 2. NGINX Ingress Controller

**Option A: Using Helm**

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm install nginx-ingress ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --set controller.service.type=LoadBalancer \
  --set controller.metrics.enabled=true
```

**Option B: Using kubectl**

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.0/deploy/static/provider/baremetal/deploy.yaml
```

### 3. Cert-Manager (For TLS/HTTPS)

```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Wait for cert-manager to be ready
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=cert-manager -n cert-manager --timeout=300s

# Create ClusterIssuer for Let's Encrypt
cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: admin@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx
EOF
```

### 4. Metrics Server (For HPA)

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Wait for metrics server
kubectl wait --for=condition=ready pod -l k8s-app=metrics-server -n kube-system --timeout=300s
```

### 5. Prometheus Operator (Optional but Recommended for Monitoring)

```bash
# Using Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace
```

---

## Deploying Medicare AI

### 1. Prepare Secrets

```bash
# Create secrets file (DO NOT COMMIT TO GIT)
cat <<'EOF' > /tmp/deploy-secrets.sh
#!/bin/bash

NAMESPACE="medicare-ai"
DB_PASSWORD="your-secure-db-password-here"
JWT_SECRET="your-very-long-jwt-secret-at-least-32-characters"
MAIL_PASSWORD="your-email-app-password"
REDIS_PASSWORD="your-secure-redis-password"

kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

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
  --from-literal=mail-password="$MAIL_PASSWORD" \
  --from-literal=redis-password="$REDIS_PASSWORD" \
  --from-literal=sentry-dsn="" \
  --from-literal=newrelic-key=""

echo "✓ Secrets created"
EOF

chmod +x /tmp/deploy-secrets.sh
/tmp/deploy-secrets.sh
```

### 2. Deploy Application

```bash
# Ensure you're in the project root directory
cd Medicare_Ai

# Make deployment script executable
chmod +x scripts/k8s-deploy.sh

# Run deployment
./scripts/k8s-deploy.sh latest

# Verify deployment
bash scripts/k8s-health-check.sh
```

### 3. Configure DNS

```bash
# Get LoadBalancer IP
kubectl get ingress -n medicare-ai

# Create DNS records in your DNS provider:
# A record: medicare-ai.example.com -> <INGRESS_IP>
# A record: api.medicare-ai.example.com -> <INGRESS_IP>

# Verify DNS resolution
nslookup api.medicare-ai.example.com
```

---

## Production Considerations

### 1. Persistent Storage

```bash
# For production, use external storage instead of emptyDir

# Example: Using NFS
sudo apt-get install -y nfs-common

# Or cloud provider storage (AWS EBS, GCP Persistent Disk, Azure Disk)
```

### 2. Backup Strategy

```bash
# Backup etcd (Kubernetes state)
sudo cp -r /var/lib/etcd /backup/etcd-$(date +%Y%m%d)

# Backup Kubernetes resources
kubectl get all -n medicare-ai -o yaml > backup-medicare-ai.yaml

# Backup database
kubectl exec -it mysql-0 -n medicare-ai -- \
  mysqldump -u root -p$DB_PASSWORD --all-databases > backup-mysql-$(date +%Y%m%d).sql
```

### 3. Resource Limits & Quotas

```bash
# Set resource quotas per namespace
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ResourceQuota
metadata:
  name: medicare-ai-quota
  namespace: medicare-ai
spec:
  hard:
    requests.cpu: "10"
    requests.memory: "20Gi"
    limits.cpu: "20"
    limits.memory: "40Gi"
    pods: "50"
EOF
```

### 4. Network Policies

Ensure network policies are enabled (already included in k8s/ingress-rbac.yaml)

### 5. RBAC & Security

```bash
# Create least-privilege user for CI/CD
kubectl create serviceaccount cicd -n medicare-ai
kubectl create clusterrolebinding cicd-role --clusterrole=edit --serviceaccount=medicare-ai:cicd

# Get token for CI/CD
kubectl get secret -n medicare-ai $(kubectl get secret -n medicare-ai | grep cicd-token | awk '{print $1}') -o jsonpath='{.data.token}' | base64 -d
```

### 6. Node Affinity & Pod Affinity

Already configured in deployment.yaml with:
- Pod anti-affinity (spread pods across nodes)
- Resource requests/limits
- Health checks

---

## Maintenance & Troubleshooting

### Cluster Maintenance

```bash
# Drain node for maintenance
kubectl drain <node-name> --ignore-daemonsets --delete-emptydir-data

# Upgrade kubelet
sudo apt-get update
sudo apt-get install -y kubelet=<new-version>
sudo systemctl daemon-reload
sudo systemctl restart kubelet

# Uncordon node
kubectl uncordon <node-name>
```

### Troubleshooting Common Issues

**Issue**: Pod stuck in Pending

```bash
kubectl describe pod <pod-name> -n medicare-ai
# Check for: insufficient resources, image pull errors, volume binding issues
```

**Issue**: Pod CrashLoopBackOff

```bash
# Check logs
kubectl logs <pod-name> -n medicare-ai
kubectl logs -p <pod-name> -n medicare-ai  # Previous logs

# Check events
kubectl describe pod <pod-name> -n medicare-ai
```

**Issue**: Network connectivity

```bash
# Test DNS
kubectl run -it --rm debug --image=busybox --restart=Never -- nslookup kubernetes.default

# Test pod-to-service connectivity
kubectl run -it --rm debug --image=busybox --restart=Never -- wget -qO- http://mysql-service.medicare-ai.svc.cluster.local:3306
```

**Issue**: PVC not binding

```bash
# Check available storage classes
kubectl get storageclass

# Check PVC status
kubectl describe pvc <pvc-name> -n medicare-ai

# Check PV status
kubectl get pv
kubectl describe pv <pv-name>
```

### Useful Commands

```bash
# View cluster info
kubectl cluster-info

# View node details
kubectl describe node <node-name>

# View resource usage
kubectl top nodes
kubectl top pods -n medicare-ai

# Watch pod deployment
kubectl get pods -n medicare-ai -w

# Port forward to service
kubectl port-forward -n medicare-ai svc/medicare-ai-service 8080:80

# Execute command in pod
kubectl exec -it <pod-name> -n medicare-ai -- bash

# View pod logs
kubectl logs -f <pod-name> -n medicare-ai

# Get pod/service YAML
kubectl get pod <pod-name> -n medicare-ai -o yaml
```

---

## Reference Resources

- [KubeAdm Official Documentation](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/cluster-administration/manage-deployment/)
- [Container Network Interface (CNI)](https://kubernetes.io/docs/concepts/extend-kubernetes/compute-storage-net/network-plugins/)
- [Ingress Controllers](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)

---

**Last Updated**: March 2026  
**Kubernetes Version**: 1.24+  
**Status**: ✅ Production Ready

