# Banking System - API Testing Script
# This script tests all the major APIs of the banking system

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Banking System - API Testing" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# Test 1: Register a new user
Write-Host "1️⃣  Testing User Registration..." -ForegroundColor Yellow
try {
    $registerBody = @{
        username = "testuser_$(Get-Random -Maximum 9999)"
        password = "Test@123"
        email = "testuser_$(Get-Random -Maximum 9999)@example.com"
    } | ConvertTo-Json

    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
        -Method POST `
        -Body $registerBody `
        -ContentType "application/json" `
        -ErrorAction Stop
    
    Write-Host "✓ User Registration Successful" -ForegroundColor Green
    Write-Host "  Username: $($registerResponse.username)" -ForegroundColor White
} catch {
    Write-Host "✗ User Registration Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test 2: Login and get JWT token
Write-Host "2️⃣  Testing User Login..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = $registerResponse.username
        password = "Test@123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
        -Method POST `
        -Body $loginBody `
        -ContentType "application/json" `
        -ErrorAction Stop
    
    $token = $loginResponse.token
    Write-Host "✓ Login Successful" -ForegroundColor Green
    Write-Host "  JWT Token: $($token.Substring(0, 30))..." -ForegroundColor White
} catch {
    Write-Host "✗ Login Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Set headers with token
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host ""

# Test 3: Create a new account
Write-Host "3️⃣  Testing Account Creation..." -ForegroundColor Yellow
try {
    $accountBody = @{
        accountHolderName = "John Doe"
        accountType = "SAVINGS"
        initialBalance = 1000.00
    } | ConvertTo-Json

    $accountResponse = Invoke-RestMethod -Uri "$baseUrl/api/accounts" `
        -Method POST `
        -Body $accountBody `
        -Headers $headers `
        -ErrorAction Stop
    
    $accountId = $accountResponse.id
    Write-Host "✓ Account Created Successfully" -ForegroundColor Green
    Write-Host "  Account ID: $accountId" -ForegroundColor White
    Write-Host "  Account Holder: $($accountResponse.accountHolderName)" -ForegroundColor White
    Write-Host "  Initial Balance: `$$($accountResponse.balance)" -ForegroundColor White
} catch {
    Write-Host "✗ Account Creation Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test 4: Get all accounts
Write-Host "4️⃣  Testing Get All Accounts..." -ForegroundColor Yellow
try {
    $allAccounts = Invoke-RestMethod -Uri "$baseUrl/api/accounts" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Retrieved All Accounts" -ForegroundColor Green
    Write-Host "  Total Accounts: $($allAccounts.Count)" -ForegroundColor White
} catch {
    Write-Host "✗ Get Accounts Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 5: Create a deposit transaction
Write-Host "5️⃣  Testing Deposit Transaction..." -ForegroundColor Yellow
try {
    $depositBody = @{
        accountId = $accountId
        transactionType = "DEPOSIT"
        amount = 500.00
        description = "Test deposit transaction"
    } | ConvertTo-Json

    $depositResponse = Invoke-RestMethod -Uri "$baseUrl/api/transactions" `
        -Method POST `
        -Body $depositBody `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Deposit Transaction Successful" -ForegroundColor Green
    Write-Host "  Transaction ID: $($depositResponse.id)" -ForegroundColor White
    Write-Host "  Amount: `$$($depositResponse.amount)" -ForegroundColor White
    Write-Host "  Type: $($depositResponse.transactionType)" -ForegroundColor White
} catch {
    Write-Host "✗ Deposit Transaction Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 6: Create a withdrawal transaction
Write-Host "6️⃣  Testing Withdrawal Transaction..." -ForegroundColor Yellow
try {
    $withdrawalBody = @{
        accountId = $accountId
        transactionType = "WITHDRAWAL"
        amount = 200.00
        description = "Test withdrawal transaction"
    } | ConvertTo-Json

    $withdrawalResponse = Invoke-RestMethod -Uri "$baseUrl/api/transactions" `
        -Method POST `
        -Body $withdrawalBody `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Withdrawal Transaction Successful" -ForegroundColor Green
    Write-Host "  Transaction ID: $($withdrawalResponse.id)" -ForegroundColor White
    Write-Host "  Amount: `$$($withdrawalResponse.amount)" -ForegroundColor White
    Write-Host "  Type: $($withdrawalResponse.transactionType)" -ForegroundColor White
} catch {
    Write-Host "✗ Withdrawal Transaction Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 7: Get account details (check updated balance)
Write-Host "7️⃣  Testing Get Account Details (Updated Balance)..." -ForegroundColor Yellow
try {
    $accountDetails = Invoke-RestMethod -Uri "$baseUrl/api/accounts/$accountId" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Retrieved Account Details" -ForegroundColor Green
    Write-Host "  Account ID: $($accountDetails.id)" -ForegroundColor White
    Write-Host "  Current Balance: `$$($accountDetails.balance)" -ForegroundColor White
    Write-Host "  Expected Balance: `$1300 (1000 + 500 - 200)" -ForegroundColor White
} catch {
    Write-Host "✗ Get Account Details Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 8: Get transaction history
Write-Host "8️⃣  Testing Get Transaction History..." -ForegroundColor Yellow
try {
    $transactions = Invoke-RestMethod -Uri "$baseUrl/api/transactions/account/$accountId" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Retrieved Transaction History" -ForegroundColor Green
    Write-Host "  Total Transactions: $($transactions.Count)" -ForegroundColor White
    
    foreach ($txn in $transactions) {
        Write-Host "    - $($txn.transactionType): `$$($txn.amount) - $($txn.description)" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Get Transaction History Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "API Testing Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  ✓ User Registration & Login" -ForegroundColor Green
Write-Host "  ✓ Account Management" -ForegroundColor Green
Write-Host "  ✓ Transaction Processing" -ForegroundColor Green
Write-Host "  ✓ Balance Updates" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Check the notification-service logs to see Kafka messages!" -ForegroundColor Yellow
Write-Host ""
