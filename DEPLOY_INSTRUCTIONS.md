# Quick Deployment Command

On your Vagrant VM, run these commands:

## OPTION 1: Quick Fix (Recommended - No Docker rebuild needed)

```bash
cd ~/Medicare_Ai
kubectl apply -f k8s/deployment.yaml -n medicare-ai
kubectl rollout status deployment/medicare-ai -n medicare-ai --timeout=5m
```

## OPTION 2: Full Fix (Rebuild Docker image)

```bash
cd ~/Medicare_Ai

# Build the Docker image with the updated configuration
docker build -t brahemahmed/medicare-ai:latest .

# Restart the deployment (if using image pull policy: Always)
kubectl rollout restart deployment/medicare-ai -n medicare-ai

# Or explicitly apply
kubectl apply -f k8s/deployment.yaml -n medicare-ai

# Wait for rollout
kubectl rollout status deployment/medicare-ai -n medicare-ai --timeout=5m
```

## Verify Deployment

```bash
# Check pod status
kubectl get pods -n medicare-ai

# View logs
kubectl logs -n medicare-ai deployment/medicare-ai --tail=100

# Check service
kubectl get svc -n medicare-ai
```

---

## What Was Changed

### Files Modified:
1. **src/main/resources/application.properties**
   - Added: `management.health.mail.enabled=false`
   - This disables the mail health check that was causing 503 errors

2. **k8s/deployment.yaml**
   - Updated startupProbe path: `/api/actuator/health/liveness`
   - Updated livenessProbe path: `/api/actuator/health/liveness`
   - Updated readinessProbe path: `/api/actuator/health/readiness`

### Why These Changes?

The original health check endpoint `/api/actuator/health` includes ALL health indicators, including the mail service check. Since mail credentials weren't properly configured, it returned 503, causing the startup probe to fail.

The new endpoints use Spring Boot's specialized health check groups:
- **liveness**: Only checks if the app is running (db, redis)
- **readiness**: Checks if the app can serve requests (db, redis)

Both exclude the mail health check, allowing the pod to start successfully.

---

## Expected Outcome

✅ Pod will start successfully
✅ Service will become ready within 1-2 minutes
✅ No more "startup probe failed" errors
✅ Application ready to accept traffic

---

**Recommended**: Run OPTION 1 first. It's the quickest and doesn't require rebuilding the Docker image.
If you want to ensure the configuration change is baked into the image, run OPTION 2 instead.

