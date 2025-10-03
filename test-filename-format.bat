@echo off
echo ========================================
echo TEST FORMAT IMENA FAJLA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje formata imena fajla...
echo.

curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-filename-format.pdf

if exist test-filename-format.pdf (
    echo.
    echo ✅ PDF je uspešno generisan!
    echo.
    echo Format imena fajla:
    echo - 3 lica = koverti-3-t1.pdf
    echo - 1 lice = koverti-1-t1.pdf
    echo - 5 lica = koverti-5-t1.pdf
    echo - Template T2 = koverti-brojLica-t2.pdf
    echo.
    echo Otvaranje PDF-a...
    start test-filename-format.pdf
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
