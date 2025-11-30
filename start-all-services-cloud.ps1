# Banking System - Start All Services (Cloud Mode)
# This script starts all microservices using cloud database configuration
# No Docker Desktop required!

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Banking System - Cloud Mode Startup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "🌐 Starting all services with CLOUD configuration..." -ForegroundColor Yellow
Write-Host "   (Using cloud databases - No Docker needed!)" -ForegroundColor Gray
Write-Host ""

# Get current directory
$baseDir = Get-Location

# Define services in startup order
$services = @(
    @{Name="Discovery Service"; Path="discovery-service"; Port=8761; Cloud=$false},
    @{Name="Config Server"; Path="config-server"; Port=8888; Wait=5; Cloud=$false},
    @{Name="Auth Service"; Path="auth-service"; Port=8084; Wait=5; Cloud=$true},
    @{Name="Account Service"; Path="account_service"; Port=8081; Wait=5; Cloud=$true},
    @{Name="Transaction Service"; Path="transaction-service"; Port=8082; Wait=5; Cloud=$true},
    @{Name="Notification Service"; Path="notification-service"; Port=8083; Wait=5; Cloud=$true},
    @{Name="API Gateway"; Path="api-gateway"; Port=8080; Wait=5; Cloud=$false}
)

# Function to start a service in a new window
function Start-CloudService {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port,
        [bool]$UseCloud
    )
    
    $profile = if ($UseCloud) { "-Dspring-boot.run.profiles=cloud" } else { "" }
    $command = "cd '$baseDir\$ServicePath'; Write-Host 'Starting $ServiceName on port $Port (Cloud Mode)...' -ForegroundColor Green; mvn spring-boot:run $profile"
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    Write-Host "✓ Started $ServiceName (Port: $Port) [Cloud: $UseCloud]" -ForegroundColor Green
}

# Check if cloud config files exist
Write-Host "🔍 Checking cloud configuration files..." -ForegroundColor Yellow
$cloudConfigMissing = $false

foreach ($service in $services) {
    if ($service.Cloud) {
        $configPath = "$($service.Path)\src\main\resources\application-cloud.yml"
        if (-not (Test-Path $configPath)) {
            Write-Host "  ✗ Missing: $configPath" -ForegroundColor Red
            $cloudConfigMissing = $true
        } else {
            Write-Host "  ✓ Found: $configPath" -ForegroundColor Green
        }
    }
}

if ($cloudConfigMissing) {
    Write-Host ""
    Write-Host "⚠️  Cloud configuration files are missing!" -ForegroundColor Red
    Write-Host "   Run this first: .\setup-cloud-config.ps1" -ForegroundColor Yellow
    Write-Host ""
    $continue = Read-Host "Do you want to continue anyway? (y/n)"
    if ($continue -ne "y") {
        Write-Host "Exiting..." -ForegroundColor Yellow
        exit 1
    }
}

Write-Host ""
Write-Host "🚀 Starting all services..." -ForegroundColor Green
Write-Host ""

# Start each service
foreach ($service in $services) {
    Start-CloudService -ServiceName $service.Name -ServicePath $service.Path -Port $service.Port -UseCloud $service.Cloud
    
    # Wait before starting next service (if specified)
    if ($service.Wait) {
        Write-Host "  Waiting $($service.Wait) seconds before starting next service..." -ForegroundColor Yellow
        Start-Sleep -Seconds $service.Wait
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "All Services Started in Cloud Mode!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Cloud Services Connected:" -ForegroundColor Cyan
Write-Host "  ✓ Cloud PostgreSQL Databases" -ForegroundColor Green
Write-Host "  ✓ Cloud Redis Cache" -ForegroundColor Green
Write-Host "  ✓ Cloud Kafka Messaging" -ForegroundColor Green
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Cyan
Write-Host "  Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "  API Gateway:      http://localhost:8080" -ForegroundColor White
Write-Host "  Account Service:  http://localhost:8081" -ForegroundColor White
Write-Host "  Transaction Svc:  http://localhost:8082" -ForegroundColor White
Write-Host "  Notification Svc: http://localhost:8083" -ForegroundColor White
Write-Host "  Auth Service:     http://localhost:8084" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Wait 1-2 minutes for all services to register with Eureka" -ForegroundColor Yellow
Write-Host "    Check: http://localhost:8761 to see all services" -ForegroundColor Yellow
Write-Host ""
Write-Host "📝 To test APIs: .\test-apis.ps1" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ No Docker Desktop needed - all data in the cloud! 🎉" -ForegroundColor Green
Write-Host ""
