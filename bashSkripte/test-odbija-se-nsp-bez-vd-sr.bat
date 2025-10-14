@echo off
chcp 65001 > nul
echo ============================================
echo TEST: ODBIJA SE NSP - BEZ v.d. i s.r.
echo ============================================
echo.

set OUTPUT_FILE=generated_templates\BEZ_VD_SR_2025.docx

echo Šaljem zahtev na endpoint...
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d @test-bez-vd-sr.json ^
  --output %OUTPUT_FILE% ^
  --silent ^
  --show-error ^
  --write-out "\n\nStatus: %%{http_code}\nVreme: %%{time_total}s\n"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Uspešno generisan dokument!
    echo Lokacija: %OUTPUT_FILE%
    echo.
    if exist %OUTPUT_FILE% (
        echo Otvaranje dokumenta...
        start "" "%OUTPUT_FILE%"
    )
) else (
    echo.
    echo ✗ Greška pri generisanju dokumenta!
    echo ERRORLEVEL: %ERRORLEVEL%
)

echo.
echo ============================================
pause

