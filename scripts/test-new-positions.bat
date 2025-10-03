@echo off
echo ========================================
echo TEST NOVE POZICIJE
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje nove pozicije za ugroženo lice...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-new-positions.pdf

if exist test-new-positions.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-new-positions.pdf
    echo.
    echo Nove pozicije:
    echo - Ime i prezime: 90mm od gornje, 110mm od leve
    echo - Ulica i broj: 104mm od gornje, 110mm od leve
    echo - Grad/Opština: 118mm od gornje, 110mm od leve
    echo - Mesto: 134mm od gornje, 155mm od leve
    echo - PTT broj: 135mm od gornje, 110mm od leve (svaki broj 11mm od levog susednog)
    echo.
    echo Otvaranje PDF-a...
    start test-new-positions.pdf
) else (
    echo.
    echo ❌ Greška pri generisanju PDF-a!
    echo.
    echo Proverite backend.log za detalje...
    type backend.log
)

echo.
echo Zaustavljanje backend aplikacije...
taskkill /F /IM java.exe > nul 2>&1

echo.
echo Test završen!
pause
