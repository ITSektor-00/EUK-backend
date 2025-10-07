@echo off
echo Zaustavljanje kombinovane EUK aplikacije...

cd /d "%~dp0.."

docker-compose -f docker-compose.combined.yml down

echo EUK aplikacija je zaustavljena!
pause
