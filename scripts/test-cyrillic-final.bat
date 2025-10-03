@echo off
echo ========================================
echo CYRILLIC FINAL TEST
echo ========================================

echo.
echo 1. Testiranje test PDF endpoint-a sa ćirilicom...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-cyrillic-final.pdf
echo.

echo.
echo 2. Testiranje glavnog PDF endpoint-a sa ćirilicom...
curl -X POST http://localhost:8080/api/generate-envelope-pdf ^
     -H "Content-Type: application/json" ^
     -d "{\"template\":\"T1\",\"ugrozenaLica\":[{\"ugrozenoLiceId\":1,\"ime\":\"Марко\",\"prezime\":\"Петровић\",\"ulicaIBroj\":\"Кнез Михаилова 15\",\"pttBroj\":\"11000\",\"gradOpstina\":\"Београд\",\"mesto\":\"Стари град\"}]}" ^
     -o koverat-cyrillic-final.pdf
echo.

echo.
echo ========================================
echo CYRILLIC FINAL TEST ZAVRSEN
echo ========================================
echo Proverite fajlove:
echo - test-cyrillic-final.pdf (test koverat sa ćirilicom)
echo - koverat-cyrillic-final.pdf (glavni koverat sa ćirilicom)
echo ========================================
echo Proverite backend logove da vidite koji font je učitan!
echo ========================================
pause
