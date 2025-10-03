@echo off
echo ========================================
echo PDF Debug Test
echo ========================================

echo.
echo 1. Testiranje jednostavnog endpoint-a...
curl -s http://localhost:8080/api/test-pdf-simple
if %errorlevel% neq 0 (
    echo ❌ Jednostavan endpoint nije dostupan
    echo Molimo pokrenite backend aplikaciju
    pause
    exit /b 1
)
echo ✅ Jednostavan endpoint radi

echo.
echo 2. Testiranje PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-koverat.pdf
if %errorlevel% neq 0 (
    echo ❌ PDF endpoint nije dostupan
) else (
    echo ✅ PDF endpoint radi - test-koverat.pdf kreiran
)

echo.
echo 3. Testiranje glavnog PDF endpoint-a...
curl -s -X POST http://localhost:8080/api/generate-envelope-pdf ^
     -H "Content-Type: application/json" ^
     -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Marko\",\"prezime\":\"Petrović\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
     -o koverat.pdf
if %errorlevel% neq 0 (
    echo ❌ Glavni PDF endpoint nije dostupan
) else (
    echo ✅ Glavni PDF endpoint radi - koverat.pdf kreiran
)

echo.
echo ========================================
echo Testiranje završeno!
echo ========================================
pause
