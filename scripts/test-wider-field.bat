@echo off
echo ========================================
echo TEST ŠIRE POLJE ZA NASLOV
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje šire polje za naslov...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-wider-field.pdf

if exist test-wider-field.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-wider-field.pdf
    echo.
    echo Izmene:
    echo - Širina polja povećana sa 200f na 300f
    echo - setWidth(300f) za veće polje
    echo - setFixedPosition širina 300f
    echo - Naslov treba da bude u jednom redu
    echo.
    echo Otvaranje PDF-a...
    start test-wider-field.pdf
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
