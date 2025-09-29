# EUK Backend - Expire User 1 License
# PowerShell script to set user ID 1 license to expired

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "EUK Backend - Expire User 1 License" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Setting user ID 1 license to EXPIRED..." -ForegroundColor Yellow
Write-Host ""

# Set database connection parameters
$DATABASE_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres"
$DATABASE_USERNAME = "postgres.wynfrojhkzddzjbrpdcr"
$DATABASE_PASSWORD = "a*Xxk3B7?HF8&3r"

Write-Host "Database URL: $DATABASE_URL" -ForegroundColor Green
Write-Host "Database Username: $DATABASE_USERNAME" -ForegroundColor Green
Write-Host ""

# Check if psql is available
try {
    $null = Get-Command psql -ErrorAction Stop
    Write-Host "PostgreSQL client found." -ForegroundColor Green
} catch {
    Write-Host "ERROR: psql command not found. Please install PostgreSQL client tools." -ForegroundColor Red
    Write-Host "Download from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Running SQL script to expire user 1 license..." -ForegroundColor Yellow
Write-Host ""

# Set environment variable for password
$env:PGPASSWORD = $DATABASE_PASSWORD

try {
    # Run the SQL script
    $result = psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U $DATABASE_USERNAME -d postgres -f "postgresQuery/expire_user_1_license.sql" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "SUCCESS: User 1 license expired!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "User ID 1 license has been set to EXPIRED (expired 30 days ago)." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "You can now test the expired license scenario with user ID 1." -ForegroundColor Green
        Write-Host ""
        Write-Host "Test endpoints:" -ForegroundColor Yellow
        Write-Host "- GET /api/licenses/status?userId=1" -ForegroundColor White
        Write-Host "- GET /api/licenses/check/1" -ForegroundColor White
        Write-Host "- POST /api/auth/signin (should return 401 for user 1)" -ForegroundColor White
        Write-Host ""
        Write-Host "Expected behavior:" -ForegroundColor Yellow
        Write-Host "- Login should FAIL with 401 error for user 1" -ForegroundColor Red
        Write-Host "- License status should show expired for user 1" -ForegroundColor Red
        Write-Host "- Access to application should be BLOCKED for user 1" -ForegroundColor Red
        Write-Host ""
        
        # Show the SQL output
        if ($result) {
            Write-Host "SQL Output:" -ForegroundColor Cyan
            Write-Host $result -ForegroundColor White
        }
        
    } else {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Red
        Write-Host "ERROR: Failed to expire user 1 license!" -ForegroundColor Red
        Write-Host "========================================" -ForegroundColor Red
        Write-Host ""
        Write-Host "Please check:" -ForegroundColor Yellow
        Write-Host "1. Database connection parameters" -ForegroundColor White
        Write-Host "2. Network connectivity" -ForegroundColor White
        Write-Host "3. Database permissions" -ForegroundColor White
        Write-Host "4. User ID 1 exists in the database" -ForegroundColor White
        Write-Host ""
        
        # Show error output
        if ($result) {
            Write-Host "Error Output:" -ForegroundColor Red
            Write-Host $result -ForegroundColor Red
        }
    }
} catch {
    Write-Host "ERROR: Exception occurred while running SQL script" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
} finally {
    # Clear the password from environment
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "Press any key to continue..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
