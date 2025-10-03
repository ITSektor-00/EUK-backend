@echo off
echo ========================================
echo TEST KOVERTA SA NOVIM SPECIFIKACIJAMA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje generisanja koverta...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-koverat-final.pdf

if exist test-koverat-final.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-koverat-final.pdf
    echo.
    echo Dimenzije koverta: 245mm x 175mm
    echo Pozicije teksta:
    echo - СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ: 15mm od leve, 20mm od vrha
    echo - 27 МАРТА БР. 43-45: 15mm od leve, 30mm od vrha  
    echo - 11 БЕОГРАД: 15mm od leve, 40mm od vrha
    echo.
    echo Otvaranje PDF-a...
    start test-koverat-final.pdf
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
