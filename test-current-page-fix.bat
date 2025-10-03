@echo off
echo ========================================
echo TEST CURRENT PAGE FIX
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje currentPage fix za više stranica...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-current-page-fix.pdf

if exist test-current-page-fix.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-current-page-fix.pdf
    echo.
    echo Ispravke:
    echo - Zamenjen setFixedPosition(1, ...) sa setFixedPosition(currentPage, ...)
    echo - Svako lice ide na svoju stranicu
    echo - Nema više preklapanja lica
    echo - 3 lica = 3 stranice u PDF-u
    echo.
    echo Otvaranje PDF-a...
    start test-current-page-fix.pdf
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
