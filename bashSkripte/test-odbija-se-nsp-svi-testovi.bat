@echo off
chcp 65001 > nul
echo ============================================
echo TEST: ODBIJA SE NSP - SVI TESTOVI
echo ============================================
echo.
echo Testiranje svih scenarija za generisanje dokumenta
echo.

REM Kreiranje direktorijuma ako ne postoji
if not exist "generated_templates" mkdir generated_templates

echo.
echo [1/4] Test SA v.d. i s.r.
echo ============================================
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d @test-sa-vd-sr.json ^
  --output generated_templates\SA_VD_SR_2025.docx ^
  --silent ^
  --show-error ^
  --write-out "Status: %%{http_code} | Vreme: %%{time_total}s\n"
echo.

echo [2/4] Test BEZ v.d. i s.r.
echo ============================================
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d @test-bez-vd-sr.json ^
  --output generated_templates\BEZ_VD_SR_2025.docx ^
  --silent ^
  --show-error ^
  --write-out "Status: %%{http_code} | Vreme: %%{time_total}s\n"
echo.

echo [3/4] Test SA DODATKOM za pomoć
echo ============================================
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d @test-sa-dodatkom.json ^
  --output generated_templates\SA_DODATKOM_2025.docx ^
  --silent ^
  --show-error ^
  --write-out "Status: %%{http_code} | Vreme: %%{time_total}s\n"
echo.

echo [4/4] Test SVE OPCIJE uključene
echo ============================================
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d @test-sve-opcije.json ^
  --output generated_templates\SVE_OPCIJE_2025.docx ^
  --silent ^
  --show-error ^
  --write-out "Status: %%{http_code} | Vreme: %%{time_total}s\n"
echo.

echo.
echo ============================================
echo REZULTATI
echo ============================================
echo.
echo Genеrisani dokumenti:
dir generated_templates\*.docx /B /O:-D 2>nul | findstr /C:"SA_VD_SR" /C:"BEZ_VD_SR" /C:"SA_DODATKOM" /C:"SVE_OPCIJE"
echo.

echo Otvaranje generated_templates foldera...
start "" "generated_templates"

echo.
echo ============================================
echo Testiranje završeno!
echo ============================================
pause

