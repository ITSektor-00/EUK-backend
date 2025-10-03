@echo off
echo ========================================
echo DEBUG FONT TEST
echo ========================================

echo.
echo 1. Testiranje jednostavnog PDF endpoint-a...
curl -s http://localhost:8080/api/test-simple-pdf -o test-simple.pdf
echo.

echo.
echo 2. Testiranje osnovnog PDF endpoint-a sa ćirilicom...
curl -s http://localhost:8080/api/test-basic-pdf -o test-basic.pdf
echo.

echo.
echo 3. Testiranje test PDF endpoint-a...
curl -s http://localhost:8080/api/test-envelope-pdf -o test-envelope.pdf
echo.

echo.
echo ========================================
echo DEBUG FONT TEST ZAVRSEN
echo ========================================
echo Proverite fajlove:
echo - test-simple.pdf (jednostavan PDF sa latinicom)
echo - test-basic.pdf (PDF sa ćirilicom i latinicom)
echo - test-envelope.pdf (koverat sa pozicioniranjem)
echo ========================================
pause
