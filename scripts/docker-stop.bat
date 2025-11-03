@echo off
echo Stopping EUK Backend services...
echo.

REM Stop services
docker-compose down

echo.
echo Services stopped!
echo.
pause
