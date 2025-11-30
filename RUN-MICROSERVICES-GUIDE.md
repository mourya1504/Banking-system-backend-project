# Banking System - Complete Startup Guide

This guide provides step-by-step instructions to start all database services and microservices, then test all APIs.

---

## 📋 Prerequisites

Before starting, ensure you have:

- ✅ **Docker Desktop** installed and running
- ✅ **Java 17 or higher** installed
- ✅ **Maven** installed (or use Maven wrapper)
- ✅ **PowerShell** (for Windows scripts)

Verify installations:
```powershell
docker --version
java -version
mvn -version
```

---

## 🚀 Step 1: Start Docker Database Services

### Option A: Using the Quick Start Script (Recommended)

```powershell
# Navigate to project root
cd c:\Users\Velampatimourya\Downloads\backend-project\Banking-system-backend-project

# Run the start script
.\start-docker.ps1
```

### Option B: Manual Docker Compose

```powershell
# Start all containers in detached mode
docker-compose up -d

# Wait for containers to be healthy
Start-Sleep -Seconds 10

# Check container status
docker-compose ps
```

### Verify Database Services

You should see 7 containers running:
- `postgres-account-db` (Port 5432)
- `postgres-transaction-db` (Port 5433)
- `postgres-notification-db` (Port 5434)
- `postgres-auth-db` (Port 5435)
- `redis-cache` (Port 6379)
- `kafka` (Port 9092)
- `zookeeper` (Port 2181)

```powershell
# Check all containers are healthy
docker ps
```

---

## 🏗️ Step 2: Build All Microservices

Build all services to ensure they compile without errors:

```powershell
# Build discovery-service
cd discovery-service
mvn clean install -DskipTests
cd ..

# Build config-server
cd config-server
mvn clean install -DskipTests
cd ..

# Build auth-service
cd auth-service
mvn clean install -DskipTests
cd ..

# Build account-service
cd account_service
mvn clean install -DskipTests
cd ..

# Build transaction-service
cd transaction-service
mvn clean install -DskipTests
cd ..

# Build notification-service
cd notification-service
mvn clean install -DskipTests
cd ..

# Build api-gateway
cd api-gateway
mvn clean install -DskipTests
cd ..
```

**Quick Build All** (alternative):
```powershell
# Build all services from root (if parent pom exists)
# Or run a loop
foreach ($service in @("discovery-service", "config-server", "auth-service", "account_service", "transaction-service", "notification-service", "api-gateway")) {
    Write-Host "Building $service..." -ForegroundColor Cyan
    cd $service
    mvn clean install -DskipTests
    cd ..
}
```

---

## ▶️ Step 3: Start Microservices (In Order)

**IMPORTANT:** Start services in this specific order to ensure proper service discovery and configuration:

### 1️⃣ Start Discovery Service (Eureka Server)
```powershell
cd discovery-service
mvn spring-boot:run
```
- Wait until you see: "Started DiscoveryServiceApplication"
- Default Port: **8761**
- URL: http://localhost:8761

### 2️⃣ Start Config Server
Open a **NEW terminal/PowerShell window**:
```powershell
cd config-server
mvn spring-boot:run
```
- Wait until you see: "Started ConfigServerApplication"
- Verify it registers with Eureka

### 3️⃣ Start Auth Service
Open a **NEW terminal/PowerShell window**:
```powershell
cd auth-service
mvn spring-boot:run
```
- Wait until you see: "Started AuthServiceApplication"
- Default Port: **8084**
- This service handles authentication and JWT tokens

### 4️⃣ Start Account Service
Open a **NEW terminal/PowerShell window**:
```powershell
cd account_service
mvn spring-boot:run
```
- Wait until you see: "Started AccountServiceApplication"
- Default Port: **8081**
- This service manages customer accounts

### 5️⃣ Start Transaction Service
Open a **NEW terminal/PowerShell window**:
```powershell
cd transaction-service
mvn spring-boot:run
```
- Wait until you see: "Started TransactionServiceApplication"
- Default Port: **8082**
- This service handles transactions

### 6️⃣ Start Notification Service
Open a **NEW terminal/PowerShell window**:
```powershell
cd notification-service
mvn spring-boot:run
```
- Wait until you see: "Started NotificationServiceApplication"
- Default Port: **8083**
- This service sends notifications via Kafka

### 7️⃣ Start API Gateway
Open a **NEW terminal/PowerShell window**:
```powershell
cd api-gateway
mvn spring-boot:run
```
- Wait until you see: "Started ApiGatewayApplication"
- Default Port: **8080**
- This is your main entry point for all API requests

---

## ✅ Step 4: Verify All Services Are Running

### Check Eureka Dashboard
1. Open browser: http://localhost:8761
2. You should see all services registered:
   - AUTH-SERVICE
   - ACCOUNT-SERVICE
   - TRANSACTION-SERVICE
   - NOTIFICATION-SERVICE
   - API-GATEWAY
   - CONFIG-SERVER

### Check Service Health
```powershell
# Use curl or Invoke-WebRequest to check each service
Invoke-WebRequest -Uri "http://localhost:8761/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8083/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8084/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
```

---

## 🧪 Step 5: Test All APIs

### 1. Test Auth Service (Register & Login)

#### Register a New User
```powershell
# Using PowerShell Invoke-RestMethod
$registerBody = @{
    username = "testuser"
    password = "Test@123"
    email = "testuser@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method POST `
    -Body $registerBody `
    -ContentType "application/json"
```

#### Login and Get JWT Token
```powershell
$loginBody = @{
    username = "testuser"
    password = "Test@123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -Body $loginBody `
    -ContentType "application/json"

# Save the token for subsequent requests
$token = $response.token
Write-Host "JWT Token: $token"
```

### 2. Test Account Service

#### Create a New Account
```powershell
$accountBody = @{
    accountHolderName = "John Doe"
    accountType = "SAVINGS"
    initialBalance = 1000.00
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" `
    -Method POST `
    -Body $accountBody `
    -Headers $headers
```

#### Get All Accounts
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" `
    -Method GET `
    -Headers $headers
```

#### Get Account by ID
```powershell
# Replace {accountId} with actual account ID from previous response
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/{accountId}" `
    -Method GET `
    -Headers $headers
```

### 3. Test Transaction Service

#### Create a Deposit Transaction
```powershell
$transactionBody = @{
    accountId = 1  # Replace with actual account ID
    transactionType = "DEPOSIT"
    amount = 500.00
    description = "Test deposit"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/transactions" `
    -Method POST `
    -Body $transactionBody `
    -Headers $headers
```

#### Create a Withdrawal Transaction
```powershell
$withdrawalBody = @{
    accountId = 1  # Replace with actual account ID
    transactionType = "WITHDRAWAL"
    amount = 200.00
    description = "Test withdrawal"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/transactions" `
    -Method POST `
    -Body $withdrawalBody `
    -Headers $headers
```

#### Get All Transactions for an Account
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/transactions/account/1" `
    -Method GET `
    -Headers $headers
```

### 4. Test Notification Service

The notification service works via Kafka listeners. When transactions occur, notifications are automatically sent.

#### Check Notification Service Logs
```powershell
# In the notification-service terminal, you should see Kafka message logs
# Example: "Received transaction notification: TransactionId=1, Amount=500.00"
```

---

## 🔍 Useful Testing Tools

### Using Postman (Recommended)

1. **Import Collection:** Create a new Postman collection
2. **Set Environment Variables:**
   - `base_url`: http://localhost:8080
   - `token`: (will be set after login)
3. **Test requests with Postman UI**

### Using curl (Alternative)

#### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123","email":"testuser@example.com"}'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123"}'
```

---

## 📊 Service Ports Summary

| Service | Port | Purpose |
|---------|------|---------|
| Discovery Service (Eureka) | 8761 | Service Registry |
| Config Server | 8888 | Centralized Configuration |
| Auth Service | 8084 | Authentication & Authorization |
| Account Service | 8081 | Account Management |
| Transaction Service | 8082 | Transaction Processing |
| Notification Service | 8083 | Notifications via Kafka |
| API Gateway | 8080 | **Main Entry Point** |

### Database Ports

| Database | Port | Name |
|----------|------|------|
| Account DB | 5432 | account_db |
| Transaction DB | 5433 | transaction_db |
| Notification DB | 5434 | notification_db |
| Auth DB | 5435 | auth_db |
| Redis | 6379 | Cache |
| Kafka | 9092 | Message Broker |

---

## 🛠️ Troubleshooting

### Docker Containers Not Starting
```powershell
# Check Docker status
docker ps -a

# View logs for a specific container
docker logs postgres-account-db
docker logs kafka

# Restart containers
docker-compose restart
```

### Service Not Registering with Eureka
1. Check that Discovery Service is running first
2. Wait at least 30 seconds for registration
3. Check service application.yml for eureka configuration
4. View service logs for connection errors

### Port Already in Use
```powershell
# Find process using a port (e.g., 8080)
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID> /F
```

### Database Connection Issues
```powershell
# Test database connection
docker exec -it postgres-account-db psql -U postgres -d account_db

# Check database logs
docker logs postgres-account-db
```

### Build Failures
```powershell
# Clean Maven cache and rebuild
mvn clean install -U -DskipTests

# Or force update dependencies
mvn clean install -U
```

---

## 🎯 Complete Testing Workflow

Here's a complete end-to-end testing workflow:

```powershell
# 1. Register a user
$register = @{ username="john"; password="Pass@123"; email="john@test.com" } | ConvertTo-Json
$regResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $register -ContentType "application/json"

# 2. Login and get token
$login = @{ username="john"; password="Pass@123" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $login -ContentType "application/json"
$token = $loginResponse.token
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }

# 3. Create an account
$account = @{ accountHolderName="John Doe"; accountType="SAVINGS"; initialBalance=1000 } | ConvertTo-Json
$accountResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method POST -Body $account -Headers $headers
$accountId = $accountResponse.id

# 4. Make a deposit
$deposit = @{ accountId=$accountId; transactionType="DEPOSIT"; amount=500; description="Salary" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transactions" -Method POST -Body $deposit -Headers $headers

# 5. Make a withdrawal
$withdrawal = @{ accountId=$accountId; transactionType="WITHDRAWAL"; amount=200; description="ATM" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transactions" -Method POST -Body $withdrawal -Headers $headers

# 6. Check account balance
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$accountId" -Method GET -Headers $headers

# 7. Check transaction history
Invoke-RestMethod -Uri "http://localhost:8080/api/transactions/account/$accountId" -Method GET -Headers $headers
```

---

## 🛑 Shutdown Procedure

### Stop All Microservices
Press `Ctrl+C` in each terminal window running a service (in reverse order is recommended).

### Stop Docker Containers
```powershell
# Stop all containers
docker-compose down

# Stop and remove volumes (⚠️ This deletes all data)
docker-compose down -v
```

---

## 📝 Notes

- Always start **Discovery Service** first and **API Gateway** last
- Wait for each service to fully start before starting the next one
- All API requests should go through the **API Gateway** (port 8080)
- JWT tokens expire - request a new token if you get 401 errors
- Check individual service logs if APIs are not responding
- Use Eureka dashboard to monitor service registration status

---

## 🔗 Useful URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Account Service (Direct)**: http://localhost:8081
- **Transaction Service (Direct)**: http://localhost:8082
- **Notification Service (Direct)**: http://localhost:8083
- **Auth Service (Direct)**: http://localhost:8084

---

Good luck with your banking system! 🚀
