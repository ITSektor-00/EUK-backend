@echo off
echo ========================================
echo FONT LOADING DEBUG TEST
echo ========================================

echo.
echo 1. Testiranje font loading endpoint-a...
curl -s http://localhost:8080/api/test-font-loading
echo.

echo.
echo 2. Testiranje ćirilice endpoint-a...
curl -s http://localhost:8080/api/test-cyrillic
echo.

echo.
echo 3. Testiranje PDF font endpoint-a...
curl -s http://localhost:8080/api/test-pdf-font
echo.

echo.
echo 4. Testiranje jednostavnog PDF endpoint-a...
curl -s http://localhost:8080/api/test-pdf-simple
echo.

echo.
echo 5. Testiranje test PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-font-debug.pdf
echo.

echo.
echo ========================================
echo FONT DEBUG TEST ZAVRŠEN
echo ========================================
pause
