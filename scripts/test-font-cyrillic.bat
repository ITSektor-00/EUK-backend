@echo off
echo ========================================
echo TEST FONT ĆIRILICA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje font-a za ćirilicu...
echo.

curl -X GET "http://localhost:8080/api/test-cyrillic-font" -H "Accept: application/json"

echo.
echo.
echo Testiranje jednostavan ćirilica PDF...
echo.

curl -X GET "http://localhost:8080/api/test-basic-pdf" -H "Accept: application/pdf" --output test-cyrillic-font.pdf

if exist test-cyrillic-font.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-cyrillic-font.pdf
    echo.
    echo Otvaranje PDF-a...
    start test-cyrillic-font.pdf
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
