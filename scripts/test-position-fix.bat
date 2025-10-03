@echo off
echo ========================================
echo POSITION FIX TEST
echo ========================================

echo.
echo 1. Testiranje test PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-position.pdf
echo.

echo.
echo 2. Testiranje glavnog PDF endpoint-a...
curl -X POST http://localhost:8080/api/generate-envelope-pdf ^
     -H "Content-Type: application/json" ^
     -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Marko\",\"prezime\":\"Petrovic\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
     -o koverat-position.pdf
echo.

echo.
echo ========================================
echo POSITION FIX TEST ZAVRSEN
echo ========================================
echo Proverite fajlove:
echo - test-position.pdf (ime i prezime na 11cm od leve, 9cm od vrha)
echo - koverat-position.pdf
echo ========================================
pause
