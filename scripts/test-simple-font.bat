@echo off
echo ========================================
echo SIMPLE FONT TEST
echo ========================================

echo.
echo 1. Testiranje jednostavnog font endpoint-a...
curl -s http://localhost:8080/api/test-pdf-simple
echo.

echo.
echo 2. Testiranje test PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-simple-font.pdf
echo.

echo.
echo 3. Testiranje glavnog PDF endpoint-a...
curl -X POST http://localhost:8080/api/generate-envelope-pdf ^
     -H "Content-Type: application/json" ^
     -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Marko\",\"prezime\":\"Petrovic\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
     -o koverat-simple.pdf
echo.

echo.
echo ========================================
echo SIMPLE FONT TEST ZAVRSEN
echo ========================================
echo Proverite fajlove:
echo - test-simple-font.pdf
echo - koverat-simple.pdf
echo ========================================
pause
