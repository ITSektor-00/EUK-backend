@echo off
echo Kreiranje tabele ugrozeno_lice_t2 u EUK bazi...

REM Proveri da li postoji psql
where psql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo GREŠKA: psql nije pronađen. Molimo instalirajte PostgreSQL ili dodajte ga u PATH.
    pause
    exit /b 1
)

REM Pokušaj da se povežeš sa bazom i pokreni SQL skriptu
echo Povezivanje sa bazom...
psql -h localhost -U postgres -d euk_db -f "postgresQuery\ugrozeno_lice_t2_table_creation.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Tabela ugrozeno_lice_t2 je uspešno kreirana!
    echo.
    echo Možete sada da testirate API endpointove:
    echo - POST http://localhost:8080/api/ugrozeno-lice-t2
    echo - GET http://localhost:8080/api/ugrozeno-lice-t2
    echo - DELETE http://localhost:8080/api/ugrozeno-lice-t2/{id}
    echo.
) else (
    echo.
    echo ❌ GREŠKA: Neuspešno kreiranje tabele!
    echo Proverite da li je baza pokrenuta i da li su kredencijali ispravni.
    echo.
)

pause
