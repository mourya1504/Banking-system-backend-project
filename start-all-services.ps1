# Banking System - Start All Services Script
# This script starts all microservices in separate PowerShell windows

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Banking System - Starting All Services" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get current directory
$baseDir = Get-Location

# Define services in startup order
$services = @(
    @{Name="Discovery Service"; Path="discovery-service"; Port=8761},
    @{Name="Config Server"; Path="config-server"; Port=8888; Wait=5},
    @{Name="Auth Service"; Path="auth-service"; Port=8084; Wait=5},
    @{Name="Account Service"; Path="account_service"; Port=8081; Wait=5},
    @{Name="Transaction Service"; Path="transaction-service"; Port=8082; Wait=5},
    @{Name="Notification Service"; Path="notification-service"; Port=8083; Wait=5},
    @{Name="API Gateway"; Path="api-gateway"; Port=8080; Wait=5}
)

# Function to start a service in a new window
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port
    )
    
    $command = "cd '$baseDir\$ServicePath'; Write-Host 'Starting $ServiceName on port $Port...' -ForegroundColor Green; mvn spring-boot:run"
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    Write-Host "✓ Started $ServiceName (Port: $Port)" -ForegroundColor Green
}

# Start each service
foreach ($service in $services) {
    Start-Service -ServiceName $service.Name -ServicePath $service.Path -Port $service.Port
    
    # Wait before starting next service (if specified)
    if ($service.Wait) {
        Write-Host "  Waiting $($service.Wait) seconds before starting next service..." -ForegroundColor Yellow
        Start-Sleep -Seconds $service.Wait
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "All Services Started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
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
Write-Host "📝 To test APIs, see: RUN-MICROSERVICES-GUIDE.md" -ForegroundColor Cyan
Write-Host ""
