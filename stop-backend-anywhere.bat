@echo off
echo Stopping EUK Backend...

docker stop sirus-backend
docker rm sirus-backend

echo.
echo ✅ Backend stopped and removed
echo 🚀 Start again with: start-backend-anywhere.bat
echo.
pause

