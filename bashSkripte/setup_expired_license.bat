@echo off
echo ========================================
echo Setup Global License - EXPIRED
echo ========================================

echo.
echo This script will create a global license that is already EXPIRED.
echo This will test what happens when the license expires.
echo.

REM Check if PostgreSQL is available
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL and add it to your PATH
    pause
    exit /b 1
)

echo PostgreSQL found. Proceeding with expired license setup...
echo.

REM Run the SQL script
echo Running SQL script to create EXPIRED license...
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/setup_global_license_expired.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCESS: Expired global license created!
    echo ========================================
    echo.
    echo License details:
    echo - License Key: GLOBAL-LICENSE-2024-EXPIRED
    echo - Status: EXPIRED
    echo - Days since expiry: 1
    echo - Is Active: false
    echo.
    echo Expected behavior:
    echo 1. Backend will return hasValidLicense: false
    echo 2. Frontend will show license expired warning
    echo 3. Interceptor will block API access
    echo 4. Users will see "License expired" message
    echo.
    echo Test the license system:
    echo 1. Start your backend: mvn spring-boot:run
    echo 2. Test endpoint: http://localhost:8080/api/global-license/status
    echo 3. Check frontend for expired license warning
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to create expired global license
    echo ========================================
    echo.
    echo Please check:
    echo 1. Database connection
    echo 2. Table exists (run create_global_license_table.sql first)
    echo 3. User permissions
    echo.
)

pause
