@echo off
echo Setting up complete EUK database schema...

REM Check if PostgreSQL is available
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL from https://www.postgresql.org/download/windows/
    pause
    exit /b 1
)

echo.
echo Please enter your database connection details:
echo.

set /p DB_HOST="Database host (default: localhost): "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="Database port (default: 5432): "
if "%DB_PORT%"=="" set DB_PORT=5432

set /p DB_NAME="Database name: "
if "%DB_NAME%"=="" (
    echo ❌ Database name is required
    pause
    exit /b 1
)

set /p DB_USER="Database user (default: postgres): "
if "%DB_USER%"=="" set DB_USER=postgres

echo.
echo Creating euk schema and tables...
echo.

REM Run the complete database schema
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f postgresQuery\complete_database_schema.sql

if %errorlevel% equ 0 (
    echo.
    echo ✅ Complete database schema created successfully!
    echo.
    echo The euk schema and all tables have been created.
    echo You can now start your Spring Boot application.
) else (
    echo.
    echo ❌ Failed to create database schema.
    echo Please check your connection parameters and try again.
)

echo.
pause
