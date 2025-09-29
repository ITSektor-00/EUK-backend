@echo off
echo ========================================
echo EUK Backend - Expired License Test
echo ========================================
echo.

echo Creating test user with EXPIRED license...
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

echo Running SQL script to create expired license test data...
echo.

REM Run the SQL script
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/test_expired_license.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS: Expired license test data created!
    echo ========================================
    echo.
    echo Test user created:
    echo - Username: testuser_expired_license
    echo - Email: test.expired@example.com
    echo - Password: testpass123
    echo - License Status: EXPIRED (expired 30 days ago)
    echo.
    echo You can now test the expired license scenario.
    echo.
    echo Test endpoints:
    echo - GET /api/licenses/status?userId=<user_id>
    echo - GET /api/licenses/check/<user_id>
    echo - POST /api/auth/signin (should return 401)
    echo.
    echo Expected behavior:
    echo - Login should FAIL with 401 error
    echo - License status should show expired
    echo - Access to application should be BLOCKED
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
