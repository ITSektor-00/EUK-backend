@echo off
echo Pokretanje kombinovane EUK aplikacije (Backend + Frontend)...

cd /d "%~dp0.."

echo Gradim Docker image...
docker build -f Dockerfile.combined -t euk-combined:latest .

if %ERRORLEVEL% neq 0 (
    echo Greška pri gradnji Docker image-a!
    pause
    exit /b 1
)

echo Pokretam kombinovani kontejner (Backend + Frontend)...
docker-compose -f docker-compose.combined.yml up -d

if %ERRORLEVEL% neq 0 (
    echo Greška pri pokretanju kontejnera!
    pause
    exit /b 1
)

echo.
echo EUK aplikacija je pokrenuta!
echo Frontend: http://localhost
echo Backend API: http://localhost/api
echo Database: PostgreSQL 16 (eksterna)
echo.
echo Za zaustavljanje koristite: docker-compose -f docker-compose.combined.yml down
echo.
pause
