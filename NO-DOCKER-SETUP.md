# Banking System - No Docker Setup Guide

This guide shows how to run your banking system **WITHOUT Docker Desktop** - perfect for library/public computers where you can't install software.

---

## 🌐 Option 1: Use Free Cloud Database Services (Recommended)

### Advantages:
✅ No installation required  
✅ Works on any computer with internet  
✅ No admin rights needed  
✅ Free tiers available  
✅ Can access from anywhere  

### Setup Steps

#### 1. Create Free PostgreSQL Databases

**Using Neon (Recommended - Best Free Tier):**

1. Visit: https://neon.tech/
2. Sign up with GitHub/Google (free)
3. Create **4 databases** (one for each service):
   - `account_db`
   - `transaction_db`
   - `notification_db`
   - `auth_db`

4. Get connection strings (format):
   ```
   postgresql://username:password@ep-xxx.region.neon.tech/dbname?sslmode=require
   ```

**Alternative: Supabase**
- Visit: https://supabase.com/
- Better UI and includes auth features built-in
- Free tier: 500MB storage per database

**Alternative: ElephantSQL**
- Visit: https://www.elephantsql.com/
- Simpler, more lightweight
- Free tier: 20MB per database

#### 2. Create Free Redis Cache

**Using Upstash (Recommended):**

1. Visit: https://upstash.com/
2. Sign up (free)
3. Create a Redis database
4. Get connection URL:
   ```
   redis://default:password@region.upstash.io:port
   ```

**Alternative: Redis Cloud**
- Visit: https://redis.com/try-free/
- Free tier: 30MB

#### 3. Create Free Kafka Service

**Using Upstash Kafka:**

1. Visit: https://upstash.com/kafka
2. Create a Kafka cluster
3. Get bootstrap servers and credentials

**Alternative: CloudKarafka**
- Visit: https://www.cloudkarafka.com/
- Free tier available

---

## 🔧 Update Application Configuration

### Account Service Configuration

Edit: `account_service/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-neon-endpoint.neon.tech/account_db?sslmode=require
    username: your-username
    password: your-password
    driver-class-name: org.postgresql.Driver
  
  redis:
    host: your-redis-endpoint.upstash.io
    port: 6379
    password: your-redis-password
    ssl: true
```

### Transaction Service Configuration

Edit: `transaction-service/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-neon-endpoint.neon.tech/transaction_db?sslmode=require
    username: your-username
    password: your-password
  
  kafka:
    bootstrap-servers: your-kafka-endpoint.upstash.io:9092
    properties:
      sasl.mechanism: SCRAM-SHA-256
      security.protocol: SASL_SSL
      sasl.jaas.config: org.apache.kafka.common.security.scram.ScramLoginModule required username="your-username" password="your-password";
```

### Notification Service Configuration

Edit: `notification-service/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-neon-endpoint.neon.tech/notification_db?sslmode=require
    username: your-username
    password: your-password
  
  kafka:
    bootstrap-servers: your-kafka-endpoint.upstash.io:9092
    properties:
      sasl.mechanism: SCRAM-SHA-256
      security.protocol: SASL_SSL
      sasl.jaas.config: org.apache.kafka.common.security.scram.ScramLoginModule required username="your-username" password="your-password";
```

### Auth Service Configuration

Edit: `auth-service/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-neon-endpoint.neon.tech/auth_db?sslmode=require
    username: your-username
    password: your-password
```

---

## 🌐 Option 2: Use GitHub Codespaces (Full Cloud Environment)

### Advantages:
✅ Entire development environment in the cloud  
✅ Docker works inside Codespaces  
✅ 60 hours free per month  
✅ Access from any computer  

### Setup Steps:

1. Push your code to GitHub
2. Open repository on GitHub.com
3. Click **Code** → **Codespaces** → **Create codespace**
4. Wait for environment to load
5. Run your `docker-compose up -d` command there
6. Run all microservices
7. Use port forwarding to access APIs

---

## 💻 Option 3: Use Local Embedded Databases (Testing Only)

For local testing when offline, use embedded databases.

### H2 Database (Embedded PostgreSQL Alternative)

Add to each service's `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Create `application-local.yml` in each service:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

Run with: `mvn spring-boot:run -Dspring-boot.run.profiles=local`

**Limitations:**
- Data lost on restart
- No Redis/Kafka (would need to disable these features)
- Only for basic testing

---

## ☁️ Option 4: Use Free Cloud Platform Deployments

### Railway.app
1. Visit: https://railway.app/
2. Free tier: $5 credit/month
3. Deploy PostgreSQL, Redis, and services
4. One-click deployments

### Render.com
1. Visit: https://render.com/
2. Free tier for PostgreSQL
3. Auto-deploys from GitHub

### Fly.io
1. Visit: https://fly.io/
2. Free tier available
3. Can run Docker containers

---

## 🔄 Quick Migration Script

I'll create a script to help you migrate configurations:

### Step 1: Set Environment Variables

Create a file: `cloud-config.env`

```bash
# Neon PostgreSQL
ACCOUNT_DB_URL=postgresql://user:pass@endpoint.neon.tech/account_db?sslmode=require
TRANSACTION_DB_URL=postgresql://user:pass@endpoint.neon.tech/transaction_db?sslmode=require
NOTIFICATION_DB_URL=postgresql://user:pass@endpoint.neon.tech/notification_db?sslmode=require
AUTH_DB_URL=postgresql://user:pass@endpoint.neon.tech/auth_db?sslmode=require

# Upstash Redis
REDIS_HOST=endpoint.upstash.io
REDIS_PORT=6379
REDIS_PASSWORD=your-password

# Upstash Kafka
KAFKA_BOOTSTRAP_SERVERS=endpoint.upstash.io:9092
KAFKA_USERNAME=your-username
KAFKA_PASSWORD=your-password
```

### Step 2: Update Application Properties

Use Spring Boot's environment variable substitution:

```yaml
spring:
  datasource:
    url: ${ACCOUNT_DB_URL}
```

---

## 📊 Comparison of Options

| Option | Free Tier | Setup Time | Best For |
|--------|-----------|------------|----------|
| Cloud Databases (Neon/Upstash) | ✅ Yes | 15 mins | **Recommended** |
| GitHub Codespaces | 60h/month | 5 mins | Full dev environment |
| Embedded H2 | Unlimited | 10 mins | Offline testing only |
| Railway/Render | Limited | 20 mins | Full deployment |

---

## 🎯 Recommended Approach

**For your situation (library computer):**

1. **Sign up for free cloud services:**
   - Neon for PostgreSQL (4 databases)
   - Upstash for Redis + Kafka

2. **Update configuration files with cloud URLs**

3. **Run microservices locally using Maven:**
   ```powershell
   # No Docker needed!
   .\start-all-services.ps1
   ```

4. **Services connect to cloud databases** - works from any computer!

---

## 🚀 Quick Start (Cloud Setup)

### 1. Sign Up for Services (10 minutes)

```
1. Neon.tech → Create 4 databases
2. Upstash.com → Create Redis + Kafka
3. Copy connection strings
```

### 2. Update Configuration Files (5 minutes)

Replace localhost URLs with cloud URLs in all `application.yml` files.

### 3. Run Services (2 minutes)

```powershell
.\start-all-services.ps1
```

**That's it!** No Docker needed. ✅

---

## 💡 Pro Tips

- **Use environment variables** instead of hardcoding credentials
- **Create separate profiles** for local/cloud configurations
- **Use .env files** (but don't commit them to git!)
- **Free tiers are generous** - plenty for development
- **Data persists** even when you close your laptop

---

## 🆘 Need Help?

### Common Issues:

**Q: SSL/TLS connection errors?**  
A: Add `?sslmode=require` to PostgreSQL URLs

**Q: Kafka connection timeout?**  
A: Ensure SASL authentication is configured correctly

**Q: Redis connection refused?**  
A: Check if SSL is enabled for cloud Redis

---

## 📝 Example Cloud Configuration

Here's a complete example using Neon + Upstash:

```yaml
# account_service/src/main/resources/application-cloud.yml
spring:
  datasource:
    url: jdbc:postgresql://ep-young-sun-123456.us-east-2.aws.neon.tech/account_db?sslmode=require
    username: neondb_owner
    password: abcd1234
  
  redis:
    host: us1-leading-bat-12345.upstash.io
    port: 6379
    password: xyz789
    ssl: true

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

Run with: `mvn spring-boot:run -Dspring-boot.run.profiles=cloud`

---

**Bottom Line:** You can develop and run your entire banking system without Docker Desktop by using free cloud database services! 🎉
