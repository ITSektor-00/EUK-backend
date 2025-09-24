# PowerShell script to insert 3000 test records
# EUK Backend - Test Data Generator

Write-Host "========================================" -ForegroundColor Green
Write-Host "EUK Backend - Insert 3000 Test Records" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Database connection parameters
$host = "localhost"
$port = "5432"
$database = "euk_db"
$username = "postgres"

# Prompt for password
$password = Read-Host "Enter PostgreSQL password" -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

# Set environment variable for password
$env:PGPASSWORD = $plainPassword

Write-Host "Clearing existing test data..." -ForegroundColor Yellow
try {
    psql -h $host -U $username -d $database -c "DELETE FROM euk.ugrozeno_lice_t1 WHERE redni_broj LIKE 'RB%';"
    Write-Host "✓ Existing test data cleared" -ForegroundColor Green
} catch {
    Write-Host "✗ Error clearing existing data: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Inserting 3000 test records..." -ForegroundColor Yellow
Write-Host "This may take a few minutes..." -ForegroundColor Yellow
Write-Host ""

try {
    psql -h $host -U $username -d $database -f "insert_3000_simple.sql"
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✓ Test data insertion completed!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now test the batch import functionality." -ForegroundColor Cyan
} catch {
    Write-Host "✗ Error inserting test data: $_" -ForegroundColor Red
    exit 1
} finally {
    # Clear password from environment
    Remove-Item Env:PGPASSWORD
}

Write-Host ""
Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

