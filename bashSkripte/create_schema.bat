@echo off
echo Creating euk schema...

REM Try to connect to PostgreSQL and create the schema
REM You may need to adjust the connection parameters based on your setup

echo Attempting to create euk schema...
psql -U postgres -d postgres -f create_euk_schema.sql

if %errorlevel% equ 0 (
    echo ✅ euk schema created successfully!
) else (
    echo ❌ Failed to create euk schema. Please check your PostgreSQL connection.
    echo.
    echo You may need to:
    echo 1. Make sure PostgreSQL is running
    echo 2. Check your connection parameters
    echo 3. Run this command manually: psql -U postgres -d your_database_name -f create_euk_schema.sql
)

pause
