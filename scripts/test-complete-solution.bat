@echo off
echo ========================================
echo TEST KOMPLETNO REŠENJE
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje kompletno rešenje...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-complete-solution.pdf

if exist test-complete-solution.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-complete-solution.pdf
    echo.
    echo Kompletno rešenje:
    echo - FORCE_EMBEDDED za sve fontove
    echo - PdfEncodings.IDENTITY_H za ćirilicu
    echo - Poseban boldFont za bold tekstove
    echo - Pozicije: 15mm od leve, 20/30/40mm od vrha
    echo - getResourceAsStream() za resources fontove
    echo.
    echo Otvaranje PDF-a...
    start test-complete-solution.pdf
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
