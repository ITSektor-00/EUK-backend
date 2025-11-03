@echo off
echo ========================================
echo TEST ASCII IMENA FAJLA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje ASCII imena fajlova...
echo.

echo Test 1: Jedno lice sa ćirilicom - treba koverti-Marko-Petrovic-t1.pdf
curl -X POST "http://localhost:8080/api/generate-envelope-pdf" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/pdf" ^
  -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
  --output test-cyrillic-to-ascii.pdf

if exist test-cyrillic-to-ascii.pdf (
    echo.
    echo ✅ Test uspešan: test-cyrillic-to-ascii.pdf
    echo.
    echo Konverzija ćirilice u ASCII:
    echo - Марко Петровић = Marko-Petrovic
    echo - Име Презиме = Ime-Prezime
    echo - Ћирилица = Cirilica
    echo.
    echo Otvaranje PDF-a...
    start test-cyrillic-to-ascii.pdf
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
