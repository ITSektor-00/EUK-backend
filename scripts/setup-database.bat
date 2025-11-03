@echo off
REM EUK Database Setup Script for Windows
REM This script sets up PostgreSQL database for EUK Backend

echo 🐘 Starting EUK Database Setup for Windows...

REM Configuration
set DB_NAME=euk_database
set DB_USER=euk_user
set POSTGRES_USER=postgres

REM Check if PostgreSQL is installed
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL from https://www.postgresql.org/download/windows/
    pause
    exit /b 1
)

REM Check if PostgreSQL service is running
sc query postgresql-x64-15 >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️ PostgreSQL service not found. Please check your installation.
    pause
    exit /b 1
)

REM Get database password
set /p DB_PASSWORD="Enter password for database user '%DB_USER%': "

REM Validate password
if "%DB_PASSWORD%"=="" (
    echo ❌ Password cannot be empty
    pause
    exit /b 1
)

echo.
echo 📊 Setting up database: %DB_NAME%
echo 👤 Database user: %DB_USER%

REM Create database and user
echo 🔧 Creating database and user...

psql -U %POSTGRES_USER% -c "CREATE DATABASE %DB_NAME%;" 2>nul
if %errorlevel% neq 0 (
    echo ⚠️ Database might already exist, continuing...
)

psql -U %POSTGRES_USER% -c "CREATE USER %DB_USER% WITH PASSWORD '%DB_PASSWORD%';" 2>nul
if %errorlevel% neq 0 (
    echo ⚠️ User might already exist, continuing...
)

psql -U %POSTGRES_USER% -c "GRANT ALL PRIVILEGES ON DATABASE %DB_NAME% TO %DB_USER%;"

REM Connect to database and grant schema privileges
psql -U %POSTGRES_USER% -d %DB_NAME% -c "GRANT ALL ON SCHEMA public TO %DB_USER%;"
psql -U %POSTGRES_USER% -d %DB_NAME% -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %DB_USER%;"
psql -U %POSTGRES_USER% -d %DB_NAME% -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %DB_USER%;"
psql -U %POSTGRES_USER% -d %DB_NAME% -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO %DB_USER%;"
psql -U %POSTGRES_USER% -d %DB_NAME% -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO %DB_USER%;"

if %errorlevel% equ 0 (
    echo ✅ Database and user created successfully!
) else (
    echo ❌ Failed to create database and user
    pause
    exit /b 1
)

REM Test connection
echo 🔍 Testing database connection...
psql -h localhost -U %DB_USER% -d %DB_NAME% -c "SELECT version();" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Database connection test successful!
) else (
    echo ❌ Database connection test failed
    pause
    exit /b 1
)

REM Create .env file if it doesn't exist
if not exist .env (
    echo 📝 Creating .env file in euk-backend directory...
    (
        echo # Database Configuration - LOCAL POSTGRESQL
        echo DATABASE_URL=jdbc:postgresql://localhost:5432/%DB_NAME%
        echo DATABASE_USERNAME=%DB_USER%
        echo DATABASE_PASSWORD=%DB_PASSWORD%
        echo.
        echo # Application Configuration
        echo SPRING_PROFILES_ACTIVE=prod
        echo.
        echo # JWT Configuration
        echo JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
        echo JWT_EXPIRATION=86400000
        echo.
        echo # Admin Configuration
        echo ADMIN_PASSWORD=admin123!
        echo.
        echo # Server Configuration
        echo PORT=8080
        echo.
        echo # EUK Domain Configuration
        echo EUK_ALLOWED_DOMAINS=https://your-frontend-domain.com,https://your-other-domain.com
        echo EUK_RATE_LIMIT_ENABLED=true
        echo EUK_RATE_LIMIT_MAX_REQUESTS=150
        echo.
        echo # Security Headers
        echo SECURITY_HEADERS_ENABLED=true
    ) > .env
    echo ✅ .env file created successfully in euk-backend directory!
) else (
    echo ⚠️ .env file already exists. Please update it manually with the new database credentials.
)

REM Final test
echo 🔍 Performing final connection test...
psql -h localhost -U %DB_USER% -d %DB_NAME% -c "SELECT 'Connection successful!' as status;" >nul 2>&1
if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ Database setup completed successfully!
    echo ========================================
    echo.
    echo Database Details:
    echo   - Database: %DB_NAME%
    echo   - User: %DB_USER%
    echo   - Host: localhost
    echo   - Port: 5432
    echo.
    echo Next steps:
    echo   1. Update .env file with your domain names in EUK_ALLOWED_DOMAINS
    echo   2. Change ADMIN_PASSWORD to a secure password
    echo   3. Run: docker-compose -f docker-compose.prod.yml up -d
    echo.
) else (
    echo ❌ Final connection test failed
    pause
    exit /b 1
)

pause
