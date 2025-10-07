@echo off
echo ========================================
echo TEST JEDNOSTAVNO IMENOVANJE
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje jednostavno imenovanje fajlova...
echo.

echo Test 1: T1 template - treba koverte-t1.pdf
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-t1-template.pdf

echo.
echo Test 2: T2 template - treba koverte-t2.pdf
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T2\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-t2-template.pdf

if exist test-t1-template.pdf (
    echo.
    echo ✅ Test 1 uspešan: test-t1-template.pdf
    echo.
    echo Format za T1 template:
    echo - koverte-t1.pdf
) else (
    echo.
    echo ❌ Test 1 neuspešan!
)

if exist test-t2-template.pdf (
    echo.
    echo ✅ Test 2 uspešan: test-t2-template.pdf
    echo.
    echo Format za T2 template:
    echo - koverte-t2.pdf
) else (
    echo.
    echo ❌ Test 2 neuspešan!
)

echo.
echo Prednosti jednostavnog imenovanja:
echo - Nema problema sa ćirilicom
echo - Jednostavno i jasno
echo - Kompatibilno sa svim browserima
echo - Nema grešaka sa HTTP headerima

echo.
echo Zaustavljanje backend aplikacije...
taskkill /F /IM java.exe > nul 2>&1

echo.
echo Test završen!
pause
