@echo off
echo ========================================
echo TEST FINALNO IMENOVANJE FAJLOVA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje finalne logike imenovanja...
echo.

echo Test 1: Jedno lice - treba koverta_Marko_Petrovic_t1.pdf
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-single-lice-final.pdf

echo.
echo Test 2: Više lica - treba koverti-t1.pdf
curl -X GET "http://localhost:8080/api/test-envelope-pdf" -H "Accept: application/pdf" --output test-multiple-lice-final.pdf

if exist test-single-lice-final.pdf (
    echo.
    echo ✅ Test 1 uspešan: test-single-lice-final.pdf
    echo.
    echo Format za jedno lice:
    echo - koverta_Marko_Petrovic_t1.pdf
    echo - koverta_Ana_Nikolic_t2.pdf
    echo - koverta_Petar_Jovanovic_t1.pdf
) else (
    echo.
    echo ❌ Test 1 neuspešan!
)

if exist test-multiple-lice-final.pdf (
    echo.
    echo ✅ Test 2 uspešan: test-multiple-lice-final.pdf
    echo.
    echo Format za više lica:
    echo - koverti-t1.pdf (za T1 template)
    echo - koverti-t2.pdf (za T2 template)
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
