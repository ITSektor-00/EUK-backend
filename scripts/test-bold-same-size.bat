@echo off
echo ========================================
echo TEST BOLD SA ISTOM VELIČINOM
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje bold naslov sa istom veličinom...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-bold-same-size.pdf

if exist test-bold-same-size.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-bold-same-size.pdf
    echo.
    echo Izmene:
    echo - Font size vraćen na 10pt (ista veličina)
    echo - Bold font zadržan
    echo - setKeepTogether(true) sprečava prelom teksta
    echo - setWidth(200f) ograničava širinu
    echo.
    echo Otvaranje PDF-a...
    start test-bold-same-size.pdf
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
