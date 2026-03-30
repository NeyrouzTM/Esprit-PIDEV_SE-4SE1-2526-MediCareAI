# Medicare AI - Health Check Fix Guide

## Problem
The startup probe was failing with HTTP 503 because the health endpoint was checking the mail service, which was not properly configured with valid credentials.

## Solution
Updated the health check probes to use Spring Boot's specialized health endpoints:

### Before (Failed)
```yaml
startupProbe:
  httpGet:
    path: /api/actuator/health  # Includes ALL health indicators (mail, db, redis, etc.)
```

### After (Fixed)
```yaml
startupProbe:
  httpGet:
    path: /api/actuator/health/liveness  # Only checks critical app startup indicators

readinessProbe:
  httpGet:
    path: /api/actuator/health/readiness  # Checks readiness to serve requests
```

## Changes Made

1. **application.properties** - Disabled mail health indicator:
   ```properties
   management.health.mail.enabled=false
   management.health.defaults.enabled=true
   management.endpoint.health.probes.enabled=true
   ```

2. **k8s/deployment.yaml** - Updated all three probes:
   - `startupProbe`: `/api/actuator/health/liveness`
   - `livenessProbe`: `/api/actuator/health/liveness`
   - `readinessProbe`: `/api/actuator/health/readiness`

## Deployment Steps

### Option 1: Quick Fix (Recommended - No rebuild needed)
```bash
# Apply the updated deployment
kubectl apply -f k8s/deployment.yaml -n medicare-ai

# Watch the rollout
kubectl rollout status deployment/medicare-ai -n medicare-ai --timeout=5m

# Check pod status
kubectl get pods -n medicare-ai
kubectl logs -n medicare-ai deployment/medicare-ai --tail=100
```

### Option 2: Full Rebuild (If you want to rebuild the image)
```bash
# On the Vagrant VM where Docker is available:
cd ~/Medicare_Ai

# Build the image (includes the application.properties changes)
docker build -t brahemahmed/medicare-ai:latest .

# If using Docker registry:
docker push brahemahmed/medicare-ai:latest

# Restart the deployment
kubectl rollout restart deployment/medicare-ai -n medicare-ai

# Or apply the deployment
kubectl apply -f k8s/deployment.yaml -n medicare-ai
```

## Health Check Endpoints

The application now exposes three health endpoints:

1. **Full Health** (`/api/actuator/health`)
   - Includes all indicators (db, redis, mail, etc.)
   - Used for monitoring dashboards

2. **Liveness** (`/api/actuator/health/liveness`)
   - Checks if app is running (db, redis only)
   - Used by: startupProbe, livenessProbe
   - Excludes: mail, external services

3. **Readiness** (`/api/actuator/health/readiness`)
   - Checks if app can handle requests (db, redis)
   - Used by: readinessProbe
   - Used by: Service load balancer

## Verification

After deployment, verify:

```bash
# Check pods are running
kubectl get pods -n medicare-ai

# Check logs for startup success
kubectl logs -n medicare-ai deployment/medicare-ai

# Test the health endpoints
kubectl port-forward -n medicare-ai svc/medicare-ai-service 8080:80

# In another terminal:
curl http://localhost:8080/api/actuator/health/liveness
curl http://localhost:8080/api/actuator/health/readiness
curl http://localhost:8080/api/actuator/health
```

## Files Modified

- `src/main/resources/application.properties` - Mail health indicator disabled
- `k8s/deployment.yaml` - Health check endpoints updated

## Expected Result

✅ Pod should start successfully
✅ No more 503 errors on startup probe
✅ Application should be ready to serve traffic within 1-2 minutes
✅ Full health endpoint still available for monitoring

---
**Status**: Ready to deploy
**Risk**: Low - Only health check configuration changes

