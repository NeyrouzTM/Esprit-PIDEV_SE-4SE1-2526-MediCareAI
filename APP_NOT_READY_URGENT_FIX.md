# 🚨 URGENT: App Running But Not Ready - Diagnostic Guide

## 🎯 YOUR SITUATION

**Prometheus**: Service is UP (running)
**Readiness Probe**: FAILING (app not ready)
**Status**: 🔴 CRITICAL - Pod not receiving traffic

---

## ⚡ IMMEDIATE DIAGNOSTICS (Run These NOW)

### Command 1: Check Pod Status
```bash
kubectl get pods -n medicare-ai -l app=medicare-ai -o wide
```

**Look for**:
- READY: 0/1 (not ready)
- RESTARTS: How many times has it restarted?
- STATUS: Running but not ready

### Command 2: Check Pod Events
```bash
kubectl describe pod <POD_NAME> -n medicare-ai
```

**Look for** in Conditions section:
```
Ready: False (Reason: Unready)
```

**Look for** in Events section:
- "Readiness probe failed"
- Error messages

### Command 3: Check Pod Logs
```bash
kubectl logs <POD_NAME> -n medicare-ai -f --all-containers=true
```

**Look for errors** like:
- "Cannot get a connection" (Database issue)
- "Unable to connect to Redis" (Cache issue)
- "ERROR" messages
- Exception stack traces

### Command 4: Test Health Endpoint Directly
```bash
kubectl exec -it <POD_NAME> -n medicare-ai -- curl -v http://localhost:8080/actuator/health
```

**What you'll see**:
- HTTP Status Code (200 = good, 500/503 = bad)
- Response body ({"status":"UP"} or {"status":"DOWN"})
- Actual error if any

### Command 5: Check Dependencies
```bash
# Is MySQL running?
kubectl get pods -n medicare-ai -l app=mysql

# Is Redis running?
kubectl get pods -n medicare-ai -l app=redis

# Can pod reach MySQL?
kubectl exec <POD_NAME> -n medicare-ai -- nc -zv mysql-service 3306

# Can pod reach Redis?
kubectl exec <POD_NAME> -n medicare-ai -- nc -zv redis-service 6379
```

---

## 🔴 MOST COMMON CAUSES (Check These First)

### Cause 1: MySQL Not Ready or Not Reachable
**Symptoms**:
- Health endpoint returns 503
- Logs show "Cannot get a connection"
- MySQL pod not running

**Fix**:
```bash
# Check MySQL status
kubectl get pods -n medicare-ai -l app=mysql

# Check MySQL logs
kubectl logs statefulset/mysql -n medicare-ai

# If MySQL pod is stuck "Pending" or "CrashLoopBackOff"
kubectl describe pod mysql-0 -n medicare-ai

# If MySQL is broken, delete and restart it
kubectl delete pod mysql-0 -n medicare-ai

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n medicare-ai --timeout=300s

# Then restart backend pods
kubectl rollout restart deployment/medicare-ai -n medicare-ai
```

### Cause 2: Redis Not Ready or Not Reachable
**Symptoms**:
- Health endpoint returns 503
- Logs show "Unable to connect to Redis"
- Redis pod not running

**Fix**:
```bash
# Check Redis status
kubectl get pods -n medicare-ai -l app=redis

# Check Redis logs
kubectl logs deployment/redis -n medicare-ai

# If Redis is broken
kubectl delete deployment redis -n medicare-ai
kubectl apply -f k8s/redis.yaml -n medicare-ai

# Wait for Redis to be ready
kubectl wait --for=condition=ready pod -l app=redis -n medicare-ai --timeout=300s

# Then restart backend pods
kubectl rollout restart deployment/medicare-ai -n medicare-ai
```

### Cause 3: Secrets Not Set or Incorrect
**Symptoms**:
- Logs show "null pointer" or "missing property"
- App starts but can't authenticate to database
- Logs show "Access denied" errors

**Fix**:
```bash
# Check if secrets exist
kubectl get secrets -n medicare-ai

# View secret contents (be careful with sensitive data)
kubectl get secret medicare-ai-secrets -n medicare-ai -o yaml

# If secrets missing, recreate them
kubectl create secret generic medicare-ai-secrets \
  -n medicare-ai \
  --from-literal=db-password='your-db-password' \
  --from-literal=db-username='medicare_user' \
  --from-literal=db-url='jdbc:mysql://mysql-service:3306/medicare_ai?useSSL=true&serverTimezone=UTC' \
  --from-literal=jwt-secret='your-32-character-secret-key-here' \
  --from-literal=redis-password='your-redis-password' \
  --from-literal=mail-host='smtp.gmail.com' \
  --from-literal=mail-port='587' \
  --from-literal=mail-username='your-email@gmail.com' \
  --from-literal=mail-password='your-app-password'

# Delete existing secret if needed
kubectl delete secret medicare-ai-secrets -n medicare-ai
# Then recreate with command above

# Restart deployment
kubectl rollout restart deployment/medicare-ai -n medicare-ai
```

### Cause 4: Application Takes Too Long to Start
**Symptoms**:
- Pod running, but readiness checks failing
- Logs show "Starting..." but no errors
- Health endpoint responding after long delay

**Fix**: Increase initialDelaySeconds in deployment
```bash
# Edit deployment
kubectl edit deployment medicare-ai -n medicare-ai

# Find readinessProbe section and change:
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 60  # Change from 30 to 60 (or higher)
  periodSeconds: 10
  timeoutSeconds: 5        # Also increase this from 3 to 5
  failureThreshold: 3      # And this from 2 to 3

# Save and exit (Ctrl+X, Y, Enter in nano editor)
# Deployment will automatically restart pods
```

### Cause 5: Out of Memory or Resource Issues
**Symptoms**:
- Pod restarts frequently
- Logs show "OutOfMemoryError"
- Pod killed with OOMKilled

**Fix**: Increase resource limits
```bash
# Check current usage
kubectl top pod <POD_NAME> -n medicare-ai --containers

# Edit deployment to increase resources
kubectl edit deployment medicare-ai -n medicare-ai

# Find resources section and increase:
resources:
  requests:
    memory: "1Gi"    # Increase from 512Mi
    cpu: "500m"      # Increase from 250m
  limits:
    memory: "2Gi"    # Increase from 1Gi
    cpu: "1000m"     # Increase from 500m

# Save and exit
```

### Cause 6: Database Initialization Not Complete
**Symptoms**:
- MySQL pod running but not ready
- Logs show "Initializing InnoDB"
- Database tables don't exist

**Fix**: Wait for MySQL to fully initialize
```bash
# Check MySQL logs for initialization
kubectl logs statefulset/mysql -n medicare-ai | grep -i "ready\|initialize\|error"

# Wait longer for MySQL
kubectl wait --for=condition=ready pod -l app=mysql -n medicare-ai --timeout=600s

# Check if database exists
kubectl exec mysql-0 -n medicare-ai -- mysql -u root -p$MYSQL_ROOT_PASSWORD -e "SHOW DATABASES;"

# If database doesn't exist, check init script
kubectl logs statefulset/mysql -n medicare-ai | tail -50
```

---

## 🔍 COMPLETE DIAGNOSTIC SEQUENCE

Run these commands in order and share the output:

```bash
# 1. Pod status
echo "=== POD STATUS ==="
kubectl get pods -n medicare-ai -l app=medicare-ai -o wide

# 2. Detailed pod info
echo "=== POD DETAILS ==="
kubectl describe pod <REPLACE_WITH_POD_NAME> -n medicare-ai

# 3. Pod logs
echo "=== POD LOGS ==="
kubectl logs <REPLACE_WITH_POD_NAME> -n medicare-ai --tail=100

# 4. Health endpoint test
echo "=== HEALTH CHECK ==="
kubectl exec <REPLACE_WITH_POD_NAME> -n medicare-ai -- curl -v http://localhost:8080/actuator/health 2>&1

# 5. Dependencies check
echo "=== MYSQL STATUS ==="
kubectl get pods -n medicare-ai -l app=mysql

echo "=== REDIS STATUS ==="
kubectl get pods -n medicare-ai -l app=redis

# 6. Recent events
echo "=== RECENT EVENTS ==="
kubectl get events -n medicare-ai --sort-by='.lastTimestamp' | tail -20
```

---

## ✅ QUICK FIXES (Try These)

### Quick Fix 1: Restart Pods (Sometimes Works)
```bash
kubectl rollout restart deployment/medicare-ai -n medicare-ai
kubectl rollout status deployment/medicare-ai -n medicare-ai --timeout=300s
```

### Quick Fix 2: Check and Fix Database
```bash
# Wait for MySQL to be fully ready
kubectl wait --for=condition=ready pod -l app=mysql -n medicare-ai --timeout=300s

# Restart backend pods
kubectl rollout restart deployment/medicare-ai -n medicare-ai
```

### Quick Fix 3: Increase Startup Time
```bash
# Scale down
kubectl scale deployment medicare-ai --replicas=0 -n medicare-ai

# Wait
sleep 10

# Scale back up
kubectl scale deployment medicare-ai --replicas=3 -n medicare-ai

# Watch with increased delay
kubectl get pods -n medicare-ai -w
```

### Quick Fix 4: Force Full Redeploy
```bash
# Delete and reapply deployment
kubectl delete deployment medicare-ai -n medicare-ai
sleep 10
kubectl apply -f k8s/deployment.yaml -n medicare-ai

# Monitor rollout
kubectl rollout status deployment/medicare-ai -n medicare-ai --timeout=600s
```

---

## 📊 WHAT TO LOOK FOR IN LOGS

### Healthy Log Output
```
Starting MedicareAiApplication
Tomcat started on port(s): 8080
Started MedicareAiApplication in X seconds
Registering Beans...
Initialized database
Connected to MySQL successfully
Connected to Redis successfully
Application is ready
```

### Error Log Patterns

**Database connection error**:
```
Cannot get a connection, pool error
SQLException: Unable to connect
Access denied for user
```

**Redis connection error**:
```
Unable to connect to Redis
Connection refused on localhost:6379
Redis timeout
```

**Memory error**:
```
OutOfMemoryError
Heap space
```

**Network error**:
```
Name or service not known
Connection timeout
Connection refused
```

---

## 🚨 CRITICAL: If Still Not Working After These Steps

Run this comprehensive check:

```bash
# Get ALL pod information
kubectl get all -n medicare-ai

# Get ALL events
kubectl get events -n medicare-ai

# Get secrets
kubectl get secrets -n medicare-ai

# Get configmaps
kubectl get configmaps -n medicare-ai

# Check node resources
kubectl top nodes
kubectl top pods -n medicare-ai --containers

# Check for pod errors
kubectl get pods -n medicare-ai --field-selector=status.phase!=Running

# Full diagnostic dump
kubectl describe all -n medicare-ai > diagnostic-dump.txt
```

Then share:
1. Pod description output
2. Pod logs (last 100 lines minimum)
3. Health endpoint test result
4. MySQL/Redis status
5. Any error messages

---

## 📞 SUMMARY OF COMMANDS

| Issue | Command |
|-------|---------|
| Check pod ready status | `kubectl get pods -n medicare-ai` |
| See why pod not ready | `kubectl describe pod <POD> -n medicare-ai` |
| Check app logs | `kubectl logs <POD> -n medicare-ai` |
| Test health endpoint | `kubectl exec <POD> -n medicare-ai -- curl http://localhost:8080/actuator/health` |
| Check MySQL | `kubectl get pods -l app=mysql -n medicare-ai` |
| Check Redis | `kubectl get pods -l app=redis -n medicare-ai` |
| Restart pods | `kubectl rollout restart deployment/medicare-ai -n medicare-ai` |
| Check resources | `kubectl top pods -n medicare-ai` |
| Increase startup delay | `kubectl edit deployment medicare-ai -n medicare-ai` |

---

**Status**: Ready to help diagnose
**Next Step**: Run diagnostic commands above
**Share Output**: Pod logs, health check result, dependency status

Let's fix this! 🚀

