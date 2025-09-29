@echo off
echo ========================================
echo Setup Global License - Expires in 30 Days
echo ========================================

echo.
echo This script will create a global license that expires in 30 days.
echo.

REM Check if PostgreSQL is available
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL and add it to your PATH
    pause
    exit /b 1
)

echo PostgreSQL found. Proceeding with license setup...
echo.

REM Run the SQL script
echo Running SQL script to create 30-day license...
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/setup_global_license_expires_30_days.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCESS: Global license created successfully!
    echo ========================================
    echo.
    echo License details:
    echo - License Key: GLOBAL-LICENSE-2024-30DAYS
    echo - Expires in: 30 days
    echo - Status: VALID
    echo.
    echo You can now test the license system:
    echo 1. Start your backend: mvn spring-boot:run
    echo 2. Test endpoint: http://localhost:8080/api/global-license/status
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to create global license
    echo ========================================
    echo.
    echo Please check:
    echo 1. Database connection
    echo 2. Table exists (run create_global_license_table.sql first)
    echo 3. User permissions
    echo.
)

pause
