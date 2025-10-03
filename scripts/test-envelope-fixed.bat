@echo off
echo ========================================
echo TEST KOVERTA SA POPRAVKAMA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje generisanja koverta sa popravkama...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-koverat-fixed.pdf

if exist test-koverat-fixed.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-koverat-fixed.pdf
    echo.
    echo Popravke:
    echo - FORCE_EMBEDDED umesto PREFER_EMBEDDED
    echo - Uklonjen .setBold() za TrueType fontove
    echo - Pozicije prema specifikaciji: 15mm od leve, 20/30/40mm od vrha
    echo.
    echo Otvaranje PDF-a...
    start test-koverat-fixed.pdf
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
