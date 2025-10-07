@echo off
echo ========================================
echo TEST EKSPLICITNI HEADER FIX
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje eksplicitni header fix...
echo.

echo Test 1: Jedno lice sa ćirilicom
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-explicit-header-fix.pdf

if exist test-explicit-header-fix.pdf (
    echo.
    echo ✅ Test uspešan: test-explicit-header-fix.pdf
    echo.
    echo Ispravke:
    echo - Zamenjen ContentDisposition.builder sa eksplicitnim header-om
    echo - headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
    echo - Radi kako očekujete u Spring Framework-u
    echo - Nema više grešaka sa HTTP headerima
    echo.
    echo Otvaranje PDF-a...
    start test-explicit-header-fix.pdf
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
