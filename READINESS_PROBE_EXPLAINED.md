# 🔍 Readiness Probe Configuration - Detailed Explanation

## 📋 YOUR READINESS PROBE CONFIG

In your `k8s/deployment.yaml`, the readiness probe is configured as:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 2
```

---

## 🎯 WHAT IT DOES

The readiness probe determines **when a pod is ready to receive traffic**.

### How It Works:

1. **Waits 30 seconds** (initialDelaySeconds) after pod starts
2. **Every 10 seconds** (periodSeconds), checks the health endpoint
3. **Gives 3 seconds** (timeoutSeconds) for response
4. **Allows 2 failures** (failureThreshold) before marking unready
5. **If successful**: Pod is marked READY and receives traffic
6. **If failures exceed threshold**: Pod is marked NOT READY, traffic is removed

---

## 📊 DETAILED BREAKDOWN

### Parameter 1: `httpGet`
```yaml
httpGet:
  path: /actuator/health
  port: 8080
  scheme: HTTP
```

**What it does**: Makes an HTTP GET request to check health
- **Path**: `/actuator/health` - Spring Boot actuator endpoint
- **Port**: `8080` - Container port (same as app)
- **Scheme**: `HTTP` - Plain HTTP (not HTTPS)

**Why this endpoint**: 
- Spring Boot's health endpoint is lightweight
- Returns `{"status":"UP"}` when healthy
- Returns error/timeout if app is unhealthy

**Example request**:
```
GET http://localhost:8080/actuator/health
```

**Healthy response** (HTTP 200):
```json
{
  "status": "UP"
}
```

**Unhealthy response** (HTTP 500 or timeout):
```
Connection refused
```

---

### Parameter 2: `initialDelaySeconds: 30`

**What it does**: Wait 30 seconds after pod starts before first check

**Timeline**:
```
0s   → Pod created and container starts
0-30s → App starting up (no health checks yet)
30s  → First health check happens
31s → Second health check
...
```

**Why 30 seconds?**
- Gives app time to initialize database connections
- Allows Spring Boot to fully start up
- Prevents false "not ready" during startup
- Could be longer if app needs more startup time

**What happens if too short?**
- Checks start before app is ready
- App returns errors (pod marked not ready)
- Pods keep restarting (CrashLoopBackOff)
- Rolling update fails

**What happens if too long?**
- Takes longer for pod to become ready
- Delays traffic during updates
- Deployment takes more time

---

### Parameter 3: `periodSeconds: 10`

**What it does**: Check health every 10 seconds

**Timeline**:
```
30s  → First check
40s  → Second check
50s  → Third check
60s  → Fourth check
...
```

**Why 10 seconds?**
- Regular check to detect failures
- Balances responsiveness vs overhead
- Not too frequent (saves resources)
- Not too slow (detects issues quickly)

**What it means**:
- Pod's readiness is checked every 10 seconds
- If health status changes, readiness updates in ~10s
- If pod becomes unhealthy, traffic removed within 10s

---

### Parameter 4: `timeoutSeconds: 3`

**What it does**: Wait 3 seconds for health response

**Timeline** (per health check):
```
0s   → Send HTTP GET request to /actuator/health
0-3s → Waiting for response
3s   → TIMEOUT (if no response received)
```

**Why 3 seconds?**
- Enough time for local HTTP request
- Not so long that pod seems broken
- Detects network or app issues quickly
- Sensitive to real problems

**What happens if times out?**
- Counts as a **failure**
- Contributes to failureThreshold counter
- If failures = 2, pod marked NOT READY

**What happens if too short (e.g., 1 second)?**
- Health checks might timeout due to slow app
- Pod marked not ready even though app is fine
- False positives

**What happens if too long (e.g., 10 seconds)?**
- Slow detection of real problems
- Broken pods take longer to be removed from traffic
- Poor user experience during issues

---

### Parameter 5: `failureThreshold: 2`

**What it does**: Allow 2 consecutive failures before marking NOT READY

**How it works**:
```
Check 1: Success → Ready (1/2 failures used)
Check 2: Timeout → 1/2 failures
Check 3: Timeout → 2/2 failures → NOT READY ❌

OR

Check 1: Success → Ready
Check 2: Success → Ready (counter resets to 0)
```

**Timeline example** (with failures):
```
30s  → Check 1: FAIL (1 failure)
40s  → Check 2: FAIL (2 failures) → Pod marked NOT READY
```

**Why 2?**
- Tolerates occasional hiccups
- Doesn't remove pod from traffic on single blip
- But responds quickly to real problems
- 2 failures = ~20 seconds (10s interval x 2)

**What happens if threshold is 1?**
- Single timeout = not ready
- Too aggressive (false positives)
- Pod removed from traffic too easily

**What happens if threshold is 5?**
- More tolerant
- But slow to detect problems
- Pod stays in traffic ~50 seconds while broken

---

## 🔄 COMPARISON WITH OTHER PROBES

Your deployment has **3 types of health checks**:

### 1. Readiness Probe (Ready for Traffic?)
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 2
```
**Purpose**: Determines if pod should receive traffic
**Impact**: Pod removed from load balancer if fails
**Usage**: Traffic routing decisions

---

### 2. Liveness Probe (Pod Alive?)
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 60
  periodSeconds: 15
  timeoutSeconds: 5
  failureThreshold: 3
```
**Purpose**: Determines if pod should be restarted
**Impact**: Pod restarted if fails
**Usage**: Automatic recovery from hangs

---

### 3. Startup Probe (App Starting?)
```yaml
startupProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 10
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 12
```
**Purpose**: Gives app time to start before other probes
**Impact**: Other probes don't run until startup succeeds
**Usage**: Support slow-starting applications

---

## 📊 VISUAL TIMELINE

### When Pod Starts

```
Time 0s:    Pod created
            ↓
Time 0-10s: Init containers run
            - wait-for-mysql (connect to mysql-service:3306)
            - wait-for-redis (connect to redis-service:6379)
            ↓
Time 10-30s: Startup probe checks
            - Checks every 5 seconds
            - Waits for app to start
            - Allows 12 failures (60s total)
            ↓
Time 30s:   ← READINESS PROBE STARTS
            First readiness check happens
            
Time 30-40s: App responding to health checks
            Check passes → Pod marked READY ✅
            Pod added to LoadBalancer
            Traffic starts flowing
            ↓
Time 40+:   Regular checks every 10 seconds
            If any check fails:
              - First failure: Keep in traffic
              - Second failure: Remove from traffic
              - Marked as NOT READY
```

---

## 🎯 PRACTICAL EXAMPLES

### Example 1: Normal Healthy Pod

```
30s  → Check /actuator/health → 200 OK → Ready ✅
40s  → Check /actuator/health → 200 OK → Ready ✅
50s  → Check /actuator/health → 200 OK → Ready ✅
60s  → Check /actuator/health → 200 OK → Ready ✅
...
```

**Status**: Pod receives traffic continuously

---

### Example 2: Pod with Database Connection Issue

```
30s  → Check /actuator/health → 200 OK → Ready ✅
40s  → Check /actuator/health → 503 (DB error) → Failure 1/2
50s  → Check /actuator/health → 503 (DB error) → Failure 2/2 → NOT READY ❌
```

**What happens**:
- Pod removed from LoadBalancer
- Traffic routed to other replicas
- Pod keeps running (liveness probe still checks)
- If MySQL comes back: readiness check passes again, traffic restored

---

### Example 3: Pod Getting Overloaded

```
30s  → Check /actuator/health → 200 OK → Ready ✅
40s  → Check /actuator/health → 200 OK → Ready ✅
50s  → Check /actuator/health → TIMEOUT → Failure 1/2
60s  → Check /actuator/health → TIMEOUT → Failure 2/2 → NOT READY ❌
```

**What happens**:
- Pod removed from traffic
- HPA might scale up (more replicas added)
- Pod has chance to recover
- Once healthy again: readiness check passes, traffic restored

---

## 🔧 HOW TO MODIFY IF NEEDED

### If Pod Takes Longer to Start

Increase `initialDelaySeconds`:
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 60  # Changed from 30 to 60
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 2
```

**Apply change**:
```bash
kubectl apply -f k8s/deployment.yaml -n medicare-ai
```

---

### If App is Too Sensitive to Failures

Increase `failureThreshold`:
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3  # Changed from 2 to 3 (more tolerant)
```

**Impact**: Takes ~30 seconds instead of ~20 seconds to mark not ready

---

### If Want Faster Detection of Failures

Decrease `periodSeconds`:
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
    scheme: HTTP
  initialDelaySeconds: 30
  periodSeconds: 5  # Changed from 10 to 5 (checks twice as often)
  timeoutSeconds: 3
  failureThreshold: 2
```

**Impact**: Detects failures in ~10 seconds instead of ~20 seconds

---

## 📈 WHAT HAPPENS DURING DEPLOYMENT

### Rolling Update Process:

1. **New pod starts** (with old pod still running)
   ```
   0-30s: New pod starting (no readiness checks yet)
   ```

2. **New pod ready** (reaches initialDelaySeconds)
   ```
   30s: First readiness check
   Pass → Pod marked READY → Gets traffic
   ```

3. **Old pod removed** (rolling update continues)
   ```
   Waits for new pod to be healthy
   Then terminates old pod
   ```

4. **Next replica starts** (repeat process)
   ```
   By default: 1 new pod, 1 old pod terminating at a time
   ```

**Configuration** (in your deployment.yaml):
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1           # 1 new pod at a time
    maxUnavailable: 1     # 1 old pod terminating at a time
```

---

## ✅ VERIFICATION COMMANDS

### Check Current Readiness Status

```bash
# See if pod is ready
kubectl get pods -n medicare-ai -l app=medicare-ai
# READY column shows 1/1 = ready, 0/1 = not ready

# Get detailed readiness info
kubectl get pod <POD_NAME> -n medicare-ai -o jsonpath='{.status.conditions[?(@.type=="Ready")]}'

# Watch readiness changes in real-time
kubectl get pods -n medicare-ai -l app=medicare-ai -w
```

### Test Health Endpoint Directly

```bash
# From host (if port-forwarded)
curl http://localhost:8080/actuator/health

# From inside pod
kubectl exec -it <POD_NAME> -n medicare-ai -- curl http://localhost:8080/actuator/health

# From another pod in cluster
kubectl exec -it <POD_NAME> -n medicare-ai -- curl http://medicare-ai-service:80/actuator/health
```

### Check Readiness Events

```bash
# See when readiness status changed
kubectl describe pod <POD_NAME> -n medicare-ai
# Look for: "Readiness" section in Conditions

# See all events
kubectl get events -n medicare-ai --field-selector involvedObject.name=<POD_NAME>
```

---

## 🎯 SUMMARY TABLE

| Parameter | Value | Meaning |
|-----------|-------|---------|
| **Probe Type** | httpGet | HTTP request to check health |
| **Path** | /actuator/health | Spring Boot health endpoint |
| **Port** | 8080 | Container port |
| **Scheme** | HTTP | Plain HTTP (not HTTPS) |
| **initialDelaySeconds** | 30 | Wait 30s before first check |
| **periodSeconds** | 10 | Check every 10 seconds |
| **timeoutSeconds** | 3 | 3s timeout per request |
| **failureThreshold** | 2 | Allow 2 failures before not ready |
| **Total time to not ready** | ~20s | 2 failures × 10s interval |

---

## 💡 BEST PRACTICES

✅ **You're doing**:
- Using lightweight health endpoint
- Reasonable delay (30s for Spring Boot)
- Regular checks (10s interval)
- Balanced failure tolerance (2 failures)
- Appropriate timeout (3s)

✅ **Consider**:
- Monitor actual startup time in your logs
- Adjust initialDelaySeconds if pod takes longer
- Monitor readiness failures in Prometheus
- Alert when pods are repeatedly not ready

---

**Your Configuration**: ✅ Well-balanced and appropriate
**Adjustment Needed?**: Only if you see issues in production
**Most Common Issue**: initialDelaySeconds too short (app needs more time)

---

Updated: March 30, 2026

