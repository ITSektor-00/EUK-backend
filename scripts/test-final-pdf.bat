@echo off
echo ========================================
echo FINAL PDF TEST
echo ========================================

echo.
echo 1. Testiranje jednostavnog PDF endpoint-a...
curl -s http://localhost:8080/api/test-simple-pdf -o test-simple.pdf
echo.

echo.
echo 2. Testiranje test PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-envelope.pdf
echo.

echo.
echo 3. Testiranje glavnog PDF endpoint-a...
curl -X POST http://localhost:8080/api/generate-envelope-pdf ^
     -H "Content-Type: application/json" ^
     -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Marko\",\"prezime\":\"Petrovic\",\"ulicaIBroj\":\"Knez Mihailova 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Beograd\",\"mesto\":\"Stari grad\"}]}" ^
     -o koverat-final.pdf
echo.

echo.
echo ========================================
echo FINAL PDF TEST ZAVRSEN
echo ========================================
echo Proverite fajlove:
echo - test-simple.pdf (jednostavan PDF)
echo - test-envelope.pdf (test koverat)
echo - koverat-final.pdf (glavni koverat)
echo ========================================
pause
