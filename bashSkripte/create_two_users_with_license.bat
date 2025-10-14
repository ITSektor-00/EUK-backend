@echo off
chcp 65001 >nul
echo ==========================================
echo Kreiranje admin korisnika i globalne licence
echo ==========================================
echo.

REM Učitaj .env fajl ako postoji
if exist ..\.env (
    echo Učitavam konekcione parametre iz .env fajla...
    for /f "usebackq tokens=1,2 delims==" %%a in ("..\.env") do (
        if "%%a"=="DB_HOST" set DB_HOST=%%b
        if "%%a"=="DB_PORT" set DB_PORT=%%b
        if "%%a"=="DB_NAME" set DB_NAME=%%b
        if "%%a"=="DB_USER" set DB_USER=%%b
        if "%%a"=="DB_PASSWORD" set DB_PASSWORD=%%b
    )
) else (
    echo .env fajl nije pronađen. Koristim default vrednosti.
    set DB_HOST=localhost
    set DB_PORT=5432
    set DB_NAME=euk
    set DB_USER=postgres
    set DB_PASSWORD=postgres
)

echo.
echo Konekcioni parametri:
echo Host: %DB_HOST%
echo Port: %DB_PORT%
echo Database: %DB_NAME%
echo User: %DB_USER%
echo.

REM Proveri da li je PostgreSQL dostupan
pg_isready -h %DB_HOST% -p %DB_PORT% >nul 2>&1
if errorlevel 1 (
    echo GREŠKA: PostgreSQL nije dostupan na %DB_HOST%:%DB_PORT%
    echo Pokrenite PostgreSQL server prvo.
    pause
    exit /b 1
)

echo PostgreSQL server je dostupan ✓
echo.
echo Kreiram admin korisnika i licencu...
echo.

REM Pokreni SQL upit
set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "..\postgresQuery\create_two_users_with_global_license.sql"

if errorlevel 1 (
    echo.
    echo GREŠKA pri kreiranju admin korisnika i licence!
    pause
    exit /b 1
)

echo.
echo ==========================================
echo ✓ Uspešno kreiran admin korisnik i licenca!
echo ==========================================
echo.
echo Admin korisnik:
echo   - Username: admin
echo   - Email: admin@euk.rs
echo   - Role: ADMIN
echo.
echo Lozinka: (bcrypt hash)
echo   - $2a$12$OTWETUEKamXWIsuPdcdlue0qFKN7BJEEhXhPY1bdkp77sH9hawddi
echo.
echo Globalna licenca: EUK-GLOBAL-LICENSE-2025-2026
echo   - Važi od danas
echo   - Ističe za godinu dana
echo.
pause

