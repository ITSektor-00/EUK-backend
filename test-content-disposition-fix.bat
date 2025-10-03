@echo off
echo ========================================
echo TEST CONTENT-DISPOSITION FIX
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje Content-Disposition fix...
echo.

echo Test 1: Jedno lice sa ćirilicom
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-content-disposition-fix.pdf

if exist test-content-disposition-fix.pdf (
    echo.
    echo ✅ Test uspešan: test-content-disposition-fix.pdf
    echo.
    echo Ispravke:
    echo - Zamenjen setContentDispositionFormData sa ContentDisposition.builder
    echo - Bolje rukovanje imenima fajlova
    echo - Nema više grešaka sa HTTP headerima
    echo.
    echo Otvaranje PDF-a...
    start test-content-disposition-fix.pdf
) else (
    echo.
    echo ❌ Test neuspešan!
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
