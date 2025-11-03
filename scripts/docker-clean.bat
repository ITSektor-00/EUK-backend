@echo off
echo Cleaning EUK Backend Docker environment...
echo.

REM Stop and remove all containers, networks, and volumes
echo Stopping and removing all containers...
docker-compose down -v

echo Removing all images...
docker-compose down --rmi all

echo Removing unused Docker resources...
docker system prune -f

echo.
echo Docker environment cleaned!
echo.
echo To start fresh: docker-compose up --build -d
echo.
pause
