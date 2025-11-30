# 🚀 Quick Start Guide - Banking System

## Simple 3-Step Startup Process

### Step 1: Start Docker Databases
```powershell
.\start-docker.ps1
```
This starts all PostgreSQL databases, Redis, Kafka, and Zookeeper.

### Step 2: Start All Microservices
```powershell
.\start-all-services.ps1
```
This automatically starts all 7 microservices in separate windows in the correct order.

### Step 3: Test All APIs
```powershell
.\test-apis.ps1
```
This runs automated tests for all major API endpoints.

---

## 📚 Detailed Documentation

For comprehensive step-by-step instructions, see: **[RUN-MICROSERVICES-GUIDE.md](./RUN-MICROSERVICES-GUIDE.md)**

---

## 🔗 Service URLs

- **Eureka Dashboard**: http://localhost:8761 (Check service registration)
- **API Gateway**: http://localhost:8080 (Main entry point for all APIs)

---

## 🛑 Shutdown

1. **Stop Microservices**: Press `Ctrl+C` in each service window
2. **Stop Docker**: 
   ```powershell
   docker-compose down
   ```

---

## ⚠️ Important Notes

- Services start in this order: Discovery → Config → Auth → Account → Transaction → Notification → Gateway
- Wait 1-2 minutes for all services to register with Eureka before testing
- All API calls should go through the API Gateway (port 8080)
- JWT tokens are required for authenticated endpoints

---

## 📋 Checklist

- [ ] Docker Desktop is running
- [ ] Java 17+ is installed
- [ ] Maven is installed
- [ ] All services built successfully (`mvn clean install`)
- [ ] Docker containers are healthy (`docker ps`)
- [ ] All services registered in Eureka (`http://localhost:8761`)
- [ ] API tests pass successfully

---

## 🆘 Need Help?

See the complete guide: **[RUN-MICROSERVICES-GUIDE.md](./RUN-MICROSERVICES-GUIDE.md)**
