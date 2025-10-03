@echo off
echo ========================================
echo TEST VELIKA SLOVA I BOLD
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje velika slova i bold za sve tekstove...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-uppercase-bold.pdf

if exist test-uppercase-bold.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-uppercase-bold.pdf
    echo.
    echo Izmene:
    echo - Svi tekstovi su velikim slovima (.toUpperCase())
    echo - Svi tekstovi su boldovani (boldFont)
    echo - PTT broj je takođe boldovan
    echo - Ime, prezime, adresa, grad, mesto - sve velika slova i bold
    echo.
    echo Otvaranje PDF-a...
    start test-uppercase-bold.pdf
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
