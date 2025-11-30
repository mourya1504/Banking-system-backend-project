# Banking System - Cloud Configuration Helper
# This script helps you update configuration files for cloud database services

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Banking System - Cloud Setup Helper" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "This script will help you configure your services to use cloud databases." -ForegroundColor Yellow
Write-Host "You'll need connection strings from your cloud providers." -ForegroundColor Yellow
Write-Host ""

# Gather cloud database information
Write-Host "📝 Please provide your cloud database connection details:" -ForegroundColor Cyan
Write-Host ""

# PostgreSQL databases
Write-Host "PostgreSQL Databases (from Neon/Supabase/ElephantSQL):" -ForegroundColor Yellow
Write-Host "Format: postgresql://user:pass@host/dbname?sslmode=require" -ForegroundColor Gray
Write-Host ""

$accountDbUrl = Read-Host "Account DB URL"
$transactionDbUrl = Read-Host "Transaction DB URL"
$notificationDbUrl = Read-Host "Notification DB URL"
$authDbUrl = Read-Host "Auth DB URL"

Write-Host ""

# Redis
Write-Host "Redis (from Upstash/Redis Cloud):" -ForegroundColor Yellow
$redisHost = Read-Host "Redis Host (e.g., endpoint.upstash.io)"
$redisPort = Read-Host "Redis Port (default: 6379)"
$redisPassword = Read-Host "Redis Password" -AsSecureString
$redisPasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($redisPassword))

Write-Host ""

# Kafka
Write-Host "Kafka (from Upstash/CloudKarafka):" -ForegroundColor Yellow
$kafkaBootstrap = Read-Host "Kafka Bootstrap Servers (e.g., endpoint.upstash.io:9092)"
$kafkaUsername = Read-Host "Kafka Username"
$kafkaPassword = Read-Host "Kafka Password" -AsSecureString
$kafkaPasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($kafkaPassword))

Write-Host ""
Write-Host "Generating configuration files..." -ForegroundColor Yellow
Write-Host ""

# Extract PostgreSQL connection components
function Parse-PostgresUrl {
    param([string]$url)
    
    # Simple parsing - you might need to adjust based on actual URL format
    if ($url -match "postgresql://([^:]+):([^@]+)@([^/]+)/(.+)") {
        return @{
            username = $Matches[1]
            password = $Matches[2]
            host = $Matches[3]
            database = $Matches[4]
        }
    }
    return $null
}

# Create cloud profile configuration for each service
$services = @(
    @{Name="account_service"; DbUrl=$accountDbUrl},
    @{Name="transaction-service"; DbUrl=$transactionDbUrl},
    @{Name="notification-service"; DbUrl=$notificationDbUrl},
    @{Name="auth-service"; DbUrl=$authDbUrl}
)

foreach ($service in $services) {
    $servicePath = $service.Name
    $configPath = "$servicePath\src\main\resources\application-cloud.yml"
    
    # Parse database URL
    $dbInfo = Parse-PostgresUrl -url $service.DbUrl
    
    # Create cloud configuration
    $cloudConfig = @"
# Cloud Configuration Profile
# Use this profile when running without Docker Desktop
# Run with: mvn spring-boot:run -Dspring-boot.run.profiles=cloud

spring:
  datasource:
    url: jdbc:$($service.DbUrl)
    username: $($dbInfo.username)
    password: $($dbInfo.password)
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false
"@

    # Add Redis configuration for account service
    if ($service.Name -eq "account_service") {
        $cloudConfig += @"


  redis:
    host: $redisHost
    port: $redisPort
    password: $redisPasswordPlain
    ssl: true
    timeout: 60000
"@
    }
    
    # Add Kafka configuration for transaction and notification services
    if ($service.Name -eq "transaction-service" -or $service.Name -eq "notification-service") {
        $cloudConfig += @"


  kafka:
    bootstrap-servers: $kafkaBootstrap
    properties:
      sasl.mechanism: SCRAM-SHA-256
      security.protocol: SASL_SSL
      sasl.jaas.config: org.apache.kafka.common.security.scram.ScramLoginModule required username="$kafkaUsername" password="$kafkaPasswordPlain";
"@
    }
    
    # Write configuration file
    $cloudConfig | Out-File -FilePath $configPath -Encoding UTF8
    Write-Host "✓ Created: $configPath" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Configuration Files Created!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "📁 Created Files:" -ForegroundColor Cyan
Write-Host "  - account_service/src/main/resources/application-cloud.yml" -ForegroundColor White
Write-Host "  - transaction-service/src/main/resources/application-cloud.yml" -ForegroundColor White
Write-Host "  - notification-service/src/main/resources/application-cloud.yml" -ForegroundColor White
Write-Host "  - auth-service/src/main/resources/application-cloud.yml" -ForegroundColor White
Write-Host ""

Write-Host "🚀 How to Run with Cloud Configuration:" -ForegroundColor Cyan
Write-Host ""
Write-Host "Option 1: Using Maven" -ForegroundColor Yellow
Write-Host '  mvn spring-boot:run -Dspring-boot.run.profiles=cloud' -ForegroundColor Gray
Write-Host ""
Write-Host "Option 2: Using Java" -ForegroundColor Yellow
Write-Host '  java -jar target/app.jar --spring.profiles.active=cloud' -ForegroundColor Gray
Write-Host ""

Write-Host "💡 Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Test database connections" -ForegroundColor White
Write-Host "  2. Run services with cloud profile" -ForegroundColor White
Write-Host "  3. No Docker Desktop needed!" -ForegroundColor White
Write-Host ""

Write-Host "⚠️  SECURITY NOTE:" -ForegroundColor Yellow
Write-Host "  - Do NOT commit application-cloud.yml to Git" -ForegroundColor Yellow
Write-Host "  - Add it to .gitignore" -ForegroundColor Yellow
Write-Host "  - These files contain sensitive credentials" -ForegroundColor Yellow
Write-Host ""

# Add to .gitignore
$gitignorePath = ".gitignore"
if (Test-Path $gitignorePath) {
    Add-Content -Path $gitignorePath -Value "`n# Cloud configuration files with credentials`napplication-cloud.yml"
    Write-Host "✓ Updated .gitignore to exclude cloud config files" -ForegroundColor Green
}

Write-Host ""
Write-Host "Setup complete! You can now run your services without Docker Desktop. 🎉" -ForegroundColor Green
Write-Host ""
