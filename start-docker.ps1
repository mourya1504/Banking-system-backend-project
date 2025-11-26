# Banking System - Docker Database Quick Start Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Banking System - Docker Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "Checking Docker status..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "✓ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker is not running. Please start Docker Desktop first." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Starting all Docker containers..." -ForegroundColor Yellow
docker-compose up -d

Write-Host ""
Write-Host "Waiting for containers to be healthy..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Container Status:" -ForegroundColor Cyan
docker-compose ps

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Docker Database Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Database Connections:" -ForegroundColor Cyan
Write-Host "  Account DB:       localhost:5432 / account_db" -ForegroundColor White
Write-Host "  Transaction DB:   localhost:5433 / transaction_db" -ForegroundColor White
Write-Host "  Notification DB:  localhost:5434 / notification_db" -ForegroundColor White
Write-Host "  Auth DB:          localhost:5435 / auth_db" -ForegroundColor White
Write-Host ""
Write-Host "Other Services:" -ForegroundColor Cyan
Write-Host "  Redis:            localhost:6379" -ForegroundColor White
Write-Host "  Kafka:            localhost:9092" -ForegroundColor White
Write-Host "  Zookeeper:        localhost:2181" -ForegroundColor White
Write-Host ""
Write-Host "Credentials: postgres / postgres" -ForegroundColor Yellow
Write-Host ""
Write-Host "To view logs: docker-compose logs -f" -ForegroundColor Yellow
Write-Host "To stop all:  docker-compose down" -ForegroundColor Yellow
Write-Host ""
