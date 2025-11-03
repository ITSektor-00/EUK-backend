@echo off
echo Starting EUK Backend with Docker...
echo.

REM Check if .env file exists
if not exist .env (
    echo .env file not found! Copying from .env.example...
    copy .env.example .env
    echo Please edit .env file with your configuration before running again.
    pause
    exit /b 1
)

REM Start services
echo Starting services...
docker-compose up -d

echo.
echo Services started!
echo.
echo Backend: http://localhost:8080
echo PostgreSQL: localhost:5432
echo.
echo To view logs: docker-compose logs -f
echo To stop: docker-compose down
echo.
pause
