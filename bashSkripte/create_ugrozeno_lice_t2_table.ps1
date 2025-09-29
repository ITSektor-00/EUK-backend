# PowerShell skripta za kreiranje tabele ugrozeno_lice_t2
Write-Host "Kreiranje tabele ugrozeno_lice_t2 u EUK bazi..." -ForegroundColor Green

# Proveri da li postoji psql
try {
    $psqlPath = Get-Command psql -ErrorAction Stop
    Write-Host "✅ psql pronađen: $($psqlPath.Source)" -ForegroundColor Green
} catch {
    Write-Host "❌ GREŠKA: psql nije pronađen. Molimo instalirajte PostgreSQL ili dodajte ga u PATH." -ForegroundColor Red
    Read-Host "Pritisnite Enter za izlaz"
    exit 1
}

# Pokušaj da se povežeš sa bazom i pokreni SQL skriptu
Write-Host "Povezivanje sa bazom..." -ForegroundColor Yellow

try {
    $sqlFile = "postgresQuery\ugrozeno_lice_t2_table_creation.sql"
    
    if (-not (Test-Path $sqlFile)) {
        Write-Host "❌ GREŠKA: SQL fajl nije pronađen: $sqlFile" -ForegroundColor Red
        Read-Host "Pritisnite Enter za izlaz"
        exit 1
    }
    
    # Pokreni SQL skriptu
    psql -h localhost -U postgres -d euk_db -f $sqlFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Tabela ugrozeno_lice_t2 je uspešno kreirana!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Možete sada da testirate API endpointove:" -ForegroundColor Cyan
        Write-Host "- POST http://localhost:8080/api/ugrozeno-lice-t2" -ForegroundColor White
        Write-Host "- GET http://localhost:8080/api/ugrozeno-lice-t2" -ForegroundColor White
        Write-Host "- DELETE http://localhost:8080/api/ugrozeno-lice-t2/{id}" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "❌ GREŠKA: Neuspešno kreiranje tabele!" -ForegroundColor Red
        Write-Host "Proverite da li je baza pokrenuta i da li su kredencijali ispravni." -ForegroundColor Yellow
        Write-Host ""
    }
} catch {
    Write-Host "❌ GREŠKA: $($_.Exception.Message)" -ForegroundColor Red
}

Read-Host "Pritisnite Enter za izlaz"
