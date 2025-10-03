# Universal Excel Import Guide - EUK Backend

## 🎯 Pregled

Implementiran je **univerzalni Excel import sistem** koji radi za **sve tabele** u EUK aplikaciji. Sistem je potpuno asinhron i optimizovan za velike fajlove.

## 📊 Podržane tabele

### 1. **euk.ugrozeno_lice_t1** (Glavna tabela)
- **16 kolona** - Kompletna struktura sa energetskim podacima
- **Kolone:** Redni broj, Ime, Prezime, JMBG, PTT broj, Grad/Opština, Mesto, Ulica i broj, Broj članova domaćinstva, Osnov sticanja statusa, ED broj, Potrošnja i površina kombinovano, Iznos umanjenja sa PDV, Broj računa, Datum izdavanja računa, Datum trajanja prava

### 2. **euk.ugrozeno_lice_t2** (Pojednostavljena verzija)
- **8 kolona** - Osnovne informacije
- **Kolone:** Redni broj, Ime, Prezime, JMBG, PTT broj, Grad/Opština, Mesto, Ulica i broj

### 3. **euk.kategorija** (Kategorije)
- **1 kolona** - Naziv kategorije
- **Kolone:** Naziv

### 4. **euk.predmet** (Predmeti)
- **2 kolone** - Osnovne informacije
- **Kolone:** Naziv predmeta, Opis

### 5. **users** (Korisnici)
- **4 kolone** - Osnovne informacije
- **Kolone:** Username, Email, First Name, Last Name

## 🚀 API Endpoint-i

### 1. POST /api/import/excel
**Opis:** Uvoz Excel fajla u određenu tabelu

**Request:**
- Method: POST
- Content-Type: multipart/form-data
- Parameters:
  - `file` (MultipartFile) - Excel fajl
  - `table` (String) - Naziv tabele

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Uvoz pokrenut u pozadini",
  "filename": "ugrozena_lica.xlsx",
  "table": "euk.ugrozeno_lice_t1",
  "size": 1024000,
  "expectedColumns": 16,
  "timestamp": 1703123456789
}
```

### 2. GET /api/import/tables
**Opis:** Lista dostupnih tabela za import

**Response:**
```json
{
  "status": "SUCCESS",
  "tables": [
    "euk.ugrozeno_lice_t1",
    "euk.ugrozeno_lice_t2", 
    "euk.kategorija",
    "euk.predmet",
    "users"
  ],
  "tableDetails": {
    "euk.ugrozeno_lice_t1": {
      "tableName": "euk.ugrozeno_lice_t1",
      "entityName": "EukUgrozenoLiceT1",
      "columnCount": 16,
      "columns": ["redni_broj", "ime", "prezime", ...],
      "displayNames": ["Redni broj", "Ime", "Prezime", ...]
    }
  }
}
```

### 3. GET /api/import/status
**Opis:** Status trenutnog uvoza

**Response:**
```json
{
  "status": "PROCESSING",
  "processedRows": 1500,
  "totalRows": 3000,
  "progress": 50.0,
  "elapsedTimeMs": 30000,
  "elapsedTimeSeconds": 30.0
}
```

## 📋 Excel Format za svaku tabelu

### euk.ugrozeno_lice_t1 (16 kolona)
```
A: Redni broj          B: Ime              C: Prezime           D: JMBG
E: PTT broj            F: Grad/Opština     G: Mesto            H: Ulica i broj
I: Broj članova        J: Osnov statusa    K: ED broj          L: Potrošnja/Površina
M: Iznos umanjenja     N: Broj računa      O: Datum računa     P: Datum prava
```

### euk.ugrozeno_lice_t2 (8 kolona)
```
A: Redni broj          B: Ime              C: Prezime           D: JMBG
E: PTT broj            F: Grad/Opština     G: Mesto            H: Ulica i broj
```

### euk.kategorija (1 kolona)
```
A: Naziv
```

### euk.predmet (2 kolone)
```
A: Naziv predmeta      B: Opis
```

### users (4 kolone)
```
A: Username            B: Email            C: First Name       D: Last Name
```

## 🔧 Korišćenje

### 1. Upload Excel fajla
```bash
curl -X POST http://localhost:8080/api/import/excel \
  -F "file=@ugrozena_lica.xlsx" \
  -F "table=euk.ugrozeno_lice_t1"
```

### 2. Lista dostupnih tabela
```bash
curl http://localhost:8080/api/import/tables
```

### 3. Status uvoza
```bash
curl http://localhost:8080/api/import/status
```

### 4. Frontend integracija
```javascript
// 1. Dohvati dostupne tabele
fetch('/api/import/tables')
  .then(response => response.json())
  .then(data => {
    console.log('Available tables:', data.tables);
    // Prikaži dropdown sa tabelama
  });

// 2. Upload fajla
const formData = new FormData();
formData.append('file', excelFile);
formData.append('table', selectedTable);

fetch('/api/import/excel', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('Import started:', data);
  // Pokreni polling za status
  pollImportStatus();
});

// 3. Polling za status
function pollImportStatus() {
  fetch('/api/import/status')
    .then(response => response.json())
    .then(status => {
      console.log('Import status:', status);
      if (status.status === 'PROCESSING') {
        setTimeout(pollImportStatus, 2000);
      }
    });
}
```

## ⚡ Karakteristike

- ✅ **Univerzalni** - radi sa svim tabelama
- ✅ **Asinhrona obrada** - ne blokira API
- ✅ **Batch processing** - 1000 redova po batch-u
- ✅ **Progress tracking** - real-time status
- ✅ **Error resilience** - nastavlja sa sledećim redom
- ✅ **Validacija uklonjena** - lakši import
- ✅ **Dinamičko mapiranje** - automatsko prepoznavanje kolona
- ✅ **JPA saveAll** - optimizovano čuvanje

## 🎯 Statusi obrade

- **IDLE** - Nema aktivnog uvoza
- **PROCESSING** - Uvoz u toku
- **COMPLETED** - Uvoz završen uspešno
- **ERROR** - Greška pri uvozu

## 📝 Napomene

1. **Validacija je uklonjena** - JMBG i ostale validacije su uklonjene za lakši import
2. **Asinhrona obrada** - API se ne blokira tokom uvoza
3. **Batch processing** - Optimizovano za velike fajlove
4. **Error resilience** - Nastavlja sa sledećim redom u slučaju greške
5. **Progress tracking** - Real-time praćenje napretka
6. **Univerzalni** - Jedan endpoint za sve tabele

## 🚨 Troubleshooting

### Česte greške:
- **EMPTY_FILE** - Fajl je prazan
- **INVALID_FILE_TYPE** - Nije Excel fajl
- **INVALID_TABLE** - Neispravan naziv tabele
- **INTERNAL_ERROR** - Greška u obradi

### Logovi:
- Sve aktivnosti se loguju u `ImportService` logger
- Progress se loguje svakih 1000 redova
- Greške se loguju sa stack trace-om
