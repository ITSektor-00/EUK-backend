@echo off
echo Stopping EUK Backend...

docker stop sirus-backend
docker rm sirus-backend

echo Backend stopped and removed.
pause

