# Banking System - Database Setup

## Quick Start

### 1️⃣ Start Docker Containers

```powershell
# Run the quick start script
.\start-docker.ps1

# OR manually
docker-compose up -d
```

### 2️⃣ Verify Containers

```powershell
docker ps
```

You should see 7 containers running:
- `postgres-account-db`
- `postgres-transaction-db`
- `postgres-notification-db`
- `postgres-auth-db`
- `redis-cache`
- `kafka`
- `zookeeper`

### 3️⃣ Database Connections

| Service | Port | Database | Username | Password |
|---------|------|----------|----------|----------|
| Account | 5432 | account_db | postgres | postgres |
| Transaction | 5433 | transaction_db | postgres | postgres |
| Notification | 5434 | notification_db | postgres | postgres |
| Auth | 5435 | auth_db | postgres | postgres |

## Configuration Summary

All Spring Boot services have been configured with the correct database ports:

- ✅ **account-service**: `localhost:5432/account_db`
- ✅ **transaction-service**: `localhost:5433/transaction_db`
- ✅ **notification-service**: `localhost:5434/notification_db`
- ✅ **auth-service**: `localhost:5435/auth_db`

## Useful Commands

```powershell
# Start containers
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f

# Connect to a database
docker exec -it postgres-account-db psql -U postgres -d account_db

# Restart a specific service
docker-compose restart postgres-account
```

## Next Steps

1. Start your Spring Boot services (in order):
   - discovery-service (8761)
   - config-server
   - auth-service (8084)
   - account-service (8081)
   - transaction-service (8082)
   - notification-service (8083)
   - api-gateway

2. Test your APIs

## Troubleshooting

See the detailed guide: `docker-database-setup-guide.md`
