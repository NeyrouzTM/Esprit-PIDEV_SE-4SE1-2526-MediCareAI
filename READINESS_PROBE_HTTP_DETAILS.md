# 🔬 Readiness Probe - HTTP Requests & Responses

## 📡 ACTUAL HTTP REQUEST KUBERNETES SENDS

Every 10 seconds (after initial 30-second delay), Kubernetes sends:

```http
GET /actuator/health HTTP/1.1
Host: <POD_IP>:8080
User-Agent: kube-probe/1.28
Connection: close
Accept: */*
Accept-Encoding: gzip
```

**To**: `http://<POD_IP>:8080/actuator/health`

---

## ✅ HEALTHY RESPONSE (Pod is READY)

**HTTP Response** (200 OK):
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 27
Connection: close

{"status":"UP"}
```

**What Kubernetes sees**:
- HTTP Status: **200** ✅
- Response body: `{"status":"UP"}`
- **Pod marked READY**: ✅

---

## ❌ UNHEALTHY RESPONSE (Pod is NOT READY)

### Case 1: Database Connection Failed

**HTTP Response** (503 Service Unavailable):
```http
HTTP/1.1 503 Service Unavailable
Content-Type: application/json
Content-Length: 25
Connection: close

{"status":"DOWN"}
```

**What Kubernetes sees**:
- HTTP Status: **503** ❌
- Response body: `{"status":"DOWN"}`
- **Count as failure** (1/2)
- On second failure: **Pod marked NOT READY**

---

### Case 2: Request Timeout

**HTTP Response** (timeout after 3 seconds):
```
[No response - connection times out]
```

**What Kubernetes sees**:
- No response after 3 seconds ❌
- Timeout error
- **Count as failure** (1/2)
- On second failure: **Pod marked NOT READY**

---

### Case 3: Connection Refused

**HTTP Response** (connection error):
```
[Connection refused - no process listening on 8080]
```

**What Kubernetes sees**:
- Can't connect to pod ❌
- Connection error
- **Count as failure** (1/2)
- On second failure: **Pod marked NOT READY**

---

## 🔄 REAL-TIME EXAMPLE: HEALTHY POD

### Timeline of HTTP Requests

```
=== Pod Starts (30 second delay) ===
Time: 0s    Container starts
Time: 0-30s No health checks (initialDelaySeconds: 30)
Time: 30s   App fully initialized

=== Readiness Checks Begin ===
Time: 30s
  GET /actuator/health
  ← HTTP 200 {"status":"UP"}
  → Pod marked READY ✅
  → Pod added to LoadBalancer

Time: 40s
  GET /actuator/health
  ← HTTP 200 {"status":"UP"}
  → Pod still READY ✅

Time: 50s
  GET /actuator/health
  ← HTTP 200 {"status":"UP"}
  → Pod still READY ✅

Time: 60s
  GET /actuator/health
  ← HTTP 200 {"status":"UP"}
  → Pod still READY ✅

[Continues every 10 seconds...]
```

**Status**: Pod receives traffic continuously ✅

---

## 🔄 REAL-TIME EXAMPLE: DATABASE GOES DOWN

### Timeline When MySQL Becomes Unavailable

```
Time: 30s   GET /actuator/health → HTTP 200 → READY ✅
Time: 40s   GET /actuator/health → HTTP 200 → READY ✅
Time: 50s   GET /actuator/health → HTTP 200 → READY ✅

=== MySQL Database Suddenly Stops ===

Time: 60s   GET /actuator/health → HTTP 503 → FAILURE 1
            {"status":"DOWN"}
            Pod still in LoadBalancer (1 failure allowed)
            Traffic still flowing to this pod

Time: 70s   GET /actuator/health → HTTP 503 → FAILURE 2
            {"status":"DOWN"}
            → Pod marked NOT READY ❌
            → Pod removed from LoadBalancer
            → Traffic routed to other healthy pods

Time: 80s   GET /actuator/health → HTTP 503 → FAILURE 3
            Pod not checked for readiness
            (Already marked not ready)

=== MySQL Comes Back Online ===

Time: 90s   GET /actuator/health → HTTP 200 → SUCCESS
            {"status":"UP"}
            → Pod marked READY ✅
            → Pod added back to LoadBalancer
            → Traffic flows to this pod again
```

**Timeline Summary**:
- Database down at: ~55s
- Pod removed from traffic: ~70s (15 seconds later)
- Database back at: ~85s
- Pod serving traffic again: ~90s

---

## 🔄 REAL-TIME EXAMPLE: APP OVERLOADED

### Timeline When App Gets Overloaded

```
Time: 30s   GET /actuator/health → HTTP 200 → READY ✅
Time: 40s   GET /actuator/health → HTTP 200 → READY ✅

=== High Load Hits Pod ===

Time: 50s   GET /actuator/health → TIMEOUT (after 3 sec)
            Application very slow, doesn't respond
            → FAILURE 1
            Pod still in LoadBalancer

Time: 60s   GET /actuator/health → TIMEOUT (after 3 sec)
            Still overloaded
            → FAILURE 2
            → Pod marked NOT READY ❌
            → Pod removed from traffic
            → HPA might scale up (add more replicas)

=== Load Decreases ===

Time: 70s   GET /actuator/health → HTTP 200 (quick response)
            {"status":"UP"}
            → Pod marked READY ✅
            → Pod back in LoadBalancer
```

---

## 💾 WHAT SPRING BOOT RETURNS

### When App is Healthy

```bash
$ curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

HTTP Status: **200**

---

### When Database is Down

```bash
$ curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "DOWN",
  "components": {
    "db": {
      "status": "DOWN",
      "details": {
        "error": "Cannot get a connection, pool error Timeout waiting for idle object"
      }
    }
  }
}
```

HTTP Status: **503**

---

### When Redis is Down

```bash
$ curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "DOWN",
  "components": {
    "redis": {
      "status": "DOWN",
      "details": {
        "error": "Unable to connect to Redis"
      }
    }
  }
}
```

HTTP Status: **503**

---

### When App is Starting Up

```bash
$ curl http://localhost:8080/actuator/health
```

Response (before app fully starts):
```
[Connection refused / 500 error]
```

Or:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "DOWN",
      "details": {
        "error": "Initializing..."
      }
    }
  }
}
```

HTTP Status: **503** or **Connection Error**

---

## 🔄 KUBERNETES DECISION LOGIC

```
HTTP Request sent to /actuator/health
         ↓
Wait for response (max 3 seconds)
         ↓
    Response received?
         ↓
    YES ─────→ HTTP status 200-399 ?
               ├─ YES → SUCCESS (reset failure counter)
               └─ NO  → FAILURE (increment counter)
         ↓
    NO  ──────→ TIMEOUT → FAILURE (increment counter)
         ↓
Failure counter >= failureThreshold (2)?
         ├─ YES → Pod marked NOT READY ❌
         └─ NO  → Pod still READY ✅
         ↓
    Wait 10 seconds
    Repeat check
```

---

## 📊 RESPONSE CODES & MEANINGS

| HTTP Status | Meaning | Kubernetes Interpretation |
|-------------|---------|--------------------------|
| 200-399 | Success | ✅ Pod is healthy |
| 400 | Bad Request | ❌ Pod is unhealthy |
| 500-599 | Server Error | ❌ Pod is unhealthy |
| Timeout | No response in 3s | ❌ Pod is unhealthy |
| Connection Refused | Port closed | ❌ Pod is unhealthy |

---

## 🔍 TESTING READINESS LOCALLY

### If Port-Forwarded

```bash
# Forward port
kubectl port-forward svc/medicare-ai-service 8080:80 -n medicare-ai

# Test in another terminal
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

### From Inside Pod

```bash
# Execute curl inside pod
kubectl exec <POD_NAME> -n medicare-ai -- curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

### From Another Pod

```bash
# Execute curl from different pod
kubectl exec <OTHER_POD> -n medicare-ai -- curl http://medicare-ai-service:80/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

## 📝 LOG EXAMPLES

### In Kubernetes Events

```bash
kubectl describe pod <POD_NAME> -n medicare-ai
```

You'll see:

```
Conditions:
  Type                 Status  Reason
  Ready                True    [Pod is ready]
  ContainersReady      True    [Containers are ready]
  PodScheduled         True    [Pod scheduled]
  
Events:
  ...
  Readiness probe succeeded
  Readiness probe succeeded
  Readiness probe succeeded
```

---

### If Readiness Fails

```
Conditions:
  Type                 Status  Reason
  Ready                False   Unready
  
Events:
  ...
  Readiness probe failed: HTTP probe failed with statuscode: 503
  Readiness probe failed: HTTP probe failed with statuscode: 503
  (Pod unready)
```

---

## 🎯 SUMMARY

**What Kubernetes Does Every 10 Seconds**:

1. **Sends HTTP GET** to `http://<POD_IP>:8080/actuator/health`
2. **Waits 3 seconds** for response
3. **Checks HTTP status**:
   - 200-399 = Healthy ✅
   - 400-599 = Unhealthy ❌
   - Timeout = Unhealthy ❌
4. **Counts failures** (up to 2 allowed)
5. **Removes from traffic** after 2 failures
6. **Adds back** when healthy again

**Your Configuration**:
- First check at: 30 seconds (initialDelaySeconds)
- Check frequency: Every 10 seconds (periodSeconds)
- Response timeout: 3 seconds (timeoutSeconds)
- Failures allowed: 2 before not ready (failureThreshold)

---

**Status**: ✅ Readiness probe working as expected
**Configuration**: ✅ Appropriate for healthcare backend
**Adjustment**: Only needed if seeing false failures/positives

---

Updated: March 30, 2026

