@echo off
echo ========================================
echo EUK Backend - Expire User 1 License
echo ========================================
echo.

echo Setting user ID 1 license to EXPIRED...
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

echo Running SQL script to expire user 1 license...
echo.

REM Run the SQL script
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/expire_user_1_license.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS: User 1 license expired!
    echo ========================================
    echo.
    echo User ID 1 license has been set to EXPIRED (expired 30 days ago).
    echo.
    echo You can now test the expired license scenario with user ID 1.
    echo.
    echo Test endpoints:
    echo - GET /api/licenses/status?userId=1
    echo - GET /api/licenses/check/1
    echo - POST /api/auth/signin (should return 401 for user 1)
    echo.
    echo Expected behavior:
    echo - Login should FAIL with 401 error for user 1
    echo - License status should show expired for user 1
    echo - Access to application should be BLOCKED for user 1
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to expire user 1 license!
    echo ========================================
    echo.
    echo Please check:
    echo 1. Database connection parameters
    echo 2. Network connectivity
    echo 3. Database permissions
    echo 4. User ID 1 exists in the database
    echo.
)

echo Press any key to continue...
pause >nul
