@echo off
echo ========================================
echo TEST SAMO FONT ĆIRILICA
echo ========================================
echo.

echo Pokretanje backend aplikacije...
start /B mvn spring-boot:run > backend.log 2>&1

echo Čekam da se aplikacija pokrene...
timeout /t 15 /nobreak > nul

echo.
echo Testiranje font-a za ćirilicu...
echo.

curl -X GET "http://localhost:8080/api/test-cyrillic-font" -H "Accept: application/json"

echo.
echo.
echo Zaustavljanje backend aplikacije...
taskkill /F /IM java.exe > nul 2>&1

echo.
echo Test završen!
pause