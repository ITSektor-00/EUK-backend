@echo off
echo ========================================
echo EUK Backend - License Test Data Setup
echo ========================================
echo.

echo Creating test users with licenses that expire in different time periods...
echo.

REM Set database connection parameters
set DATABASE_URL=jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres
set DATABASE_USERNAME=postgres.wynfrojhkzddzjbrpdcr
set DATABASE_PASSWORD=a*Xxk3B7?HF8&3r

echo Database URL: %DATABASE_URL%
echo Database Username: %DATABASE_USERNAME%
echo.

REM Check if psql is available
where psql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: psql command not found. Please install PostgreSQL client tools.
    echo Download from: https://www.postgresql.org/download/windows/
    pause
    exit /b 1
)

echo Running SQL script to create test license data...
echo.

REM Run the SQL script
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/test_license_expiration.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS: Test license data created!
    echo ========================================
    echo.
    echo Test users created:
    echo - testuser_license_expires_30_days (license expires in 30 days)
    echo - testuser_license_expires_15_days (license expires in 15 days)  
    echo - testuser_license_expires_5_days (license expires in 5 days)
    echo - testuser_license_expired (license already expired)
    echo.
    echo You can now test the license system with these users.
    echo.
    echo Test endpoints:
    echo - GET /api/licenses/status?userId=<user_id>
    echo - GET /api/licenses/check/<user_id>
    echo - GET /api/licenses/admin/expiring
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to create test data!
    echo ========================================
    echo.
    echo Please check:
    echo 1. Database connection parameters
    echo 2. Network connectivity
    echo 3. Database permissions
    echo.
)

echo Press any key to continue...
pause >nul
