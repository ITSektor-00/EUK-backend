@echo off
echo ========================================
echo TEST OČIŠĆENA VERZIJA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje očišćena verzija bez debug testova...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-clean-version.pdf

if exist test-clean-version.pdf (
    echo.
    echo ✅ PDF je uspešno generisan: test-clean-version.pdf
    echo.
    echo Očišćeno:
    echo - Uklonjeni svi debug testovi
    echo - Uklonjeni debug paragrafi
    echo - Samo čist sadržaj koverta
    echo.
    echo Otvaranje PDF-a...
    start test-clean-version.pdf
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
