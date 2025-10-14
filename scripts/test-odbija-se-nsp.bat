@echo off
echo ================================================
echo Test Endpoint: POST /api/dokumenti/odbija-se-nsp/generisi
echo ================================================

curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d "{\"brojPredmeta\":\"123/2025\",\"datumDonosenja\":\"2025-01-15\",\"brojOvlascenja\":\"456/2025\",\"datumOvlascenja\":\"2025-01-10\",\"imeIPrezimeOvlascenog\":\"Марија Петровић\",\"imeIPrezimePodnosioca\":\"Петар Јовановић\",\"jmbg\":\"0101990123456\",\"grad\":\"Београд\",\"ulica\":\"Кнеза Милоша\",\"brojStana\":\"15\",\"opstina\":\"Савски Венац\",\"pttBroj\":\"11000\",\"mestoStanovanja\":\"Београд\",\"datumPodnosenja\":\"2025-01-05\",\"osnovPrava\":\"НСП\",\"brojClanovaDomacinstava\":\"4\",\"prilozenaInfo\":\"1. Потврда о приходима\\n2. Извод из матичне књиге рођених\",\"sluzbeniDokazi\":\"Подаци о приходима из Пореске управе\",\"dodatniTekst\":null,\"vrsilacDuznosti\":true,\"sopstveneRuke\":false,\"dodatakZaPomocOdnosiSe\":false,\"pribavljaDokumentacijuSluzbeno\":true}" ^
  --output "test_odbija_se_nsp.docx"

echo.
echo ================================================
echo Dokument generisan: test_odbija_se_nsp.docx
echo ================================================
pause

