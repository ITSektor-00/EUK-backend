@echo off
echo ========================================
echo TEST DINAMIČKO IMENOVANJE FAJLOVA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje dinamičkog imenovanja fajlova...
echo.

echo Test 1: Više lica (3 lica) - treba koverti-3-t1.pdf
curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-multiple-lice.pdf

echo.
echo Test 2: Jedno lice - treba koverti-Marko-Petrović-t1.pdf
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Marko\",\"prezime\":\"Petrović\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-single-lice.pdf

if exist test-multiple-lice.pdf (
    echo.
    echo ✅ Test 1 uspešan: test-multiple-lice.pdf
    echo.
    echo Format za više lica:
    echo - 3 lica = koverti-3-t1.pdf
    echo - 5 lica = koverti-5-t1.pdf
    echo - 10 lica = koverti-10-t1.pdf
) else (
    echo.
    echo ❌ Test 1 neuspešan!
)

if exist test-single-lice.pdf (
    echo.
    echo ✅ Test 2 uspešan: test-single-lice.pdf
    echo.
    echo Format za jedno lice:
    echo - Marko Petrović = koverti-Marko-Petrović-t1.pdf
    echo - Ana Nikolić = koverti-Ana-Nikolić-t1.pdf
    echo - Petar Jovanović = koverti-Petar-Jovanović-t1.pdf
) else (
    echo.
    echo ❌ Test 2 neuspešan!
)

echo.
echo Zaustavljanje backend aplikacije...
taskkill /F /IM java.exe > nul 2>&1

echo.
echo Test završen!
pause
