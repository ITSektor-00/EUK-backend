# Excel Import Guide - EUK Backend

## Pregled

Implementiran je sistem za asinhroni uvoz Excel fajlova u `ugrozeno_lice_t1` tabelu.

## API Endpoint-i

### 1. POST /api/import/excel
**Opis:** Uvoz Excel fajla sa asinhronom obradom

**Request:**
- Method: POST
- Content-Type: multipart/form-data
- Parameter: `file` (MultipartFile)

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Uvoz pokrenut u pozadini",
  "filename": "ugrozena_lica.xlsx",
  "size": 1024000,
  "timestamp": 1703123456789
}
```

### 2. GET /api/import/status
**Opis:** Provera statusa uvoza

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

## Excel Format

### Očekivane kolone (redosled):

| Kolona | Naziv | Tip | Opis |
|--------|-------|-----|------|
| 0 | Redni broj | String | Redni broj u evidenciji |
| 1 | Ime | String | Ime osobe |
| 2 | Prezime | String | Prezime osobe |
| 3 | JMBG | String | JMBG (13 cifara) |
| 4 | PTT broj | String | PTT broj |
| 5 | Grad/Opština | String | Grad ili opština |
| 6 | Mesto | String | Mesto |
| 7 | Ulica i broj | String | Ulica i broj |
| 8 | Broj članova domaćinstva | Integer | Broj članova |
| 9 | Osnov sticanja statusa | String | MP, NSP, DD, UDTNP |
| 10 | ED broj/broj mernog uređaja | String | ED broj |
| 11 | Potrošnja i površina kombinovano | String | Kombinovana kolona |
| 12 | Iznos umanjenja sa PDV | Decimal | Iznos umanjenja |
| 13 | Broj računa | String | Broj računa |
| 14 | Datum izdavanja računa | Date | Datum |
| 15 | Datum trajanja prava | Date | Datum |

### Primer Excel fajla:

```
Redni broj | Ime | Prezime | JMBG | PTT broj | Grad/Opština | Mesto | Ulica i broj | Broj članova | Osnov statusa | ED broj | Potrošnja/Površina | Iznos umanjenja | Broj računa | Datum računa | Datum prava
RB001 | Marko | Petrović | 1234567890123 | 11000 | Beograd | Beograd | Knez Mihailova 1 | 3 | MP | ED123 | 1500/80 | 15000.50 | RAC001 | 2024-01-15 | 2024-12-31
```

## Statusi obrade

- **IDLE** - Nema aktivnog uvoza
- **PROCESSING** - Uvoz u toku
- **COMPLETED** - Uvoz završen uspešno
- **ERROR** - Greška pri uvozu

## Batch Processing

- **Batch size:** 1000 redova po batch-u
- **Asinhrona obrada:** Ne blokira API
- **Progress tracking:** Real-time praćenje napretka
- **Error handling:** Nastavlja sa sledećim redom u slučaju greške

## Korišćenje

### 1. Upload Excel fajla
```bash
curl -X POST http://localhost:8080/api/import/excel \
  -F "file=@ugrozena_lica.xlsx"
```

### 2. Provera statusa
```bash
curl http://localhost:8080/api/import/status
```

### 3. Frontend integracija
```javascript
// Upload fajla
const formData = new FormData();
formData.append('file', excelFile);

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

// Polling za status
function pollImportStatus() {
  fetch('/api/import/status')
    .then(response => response.json())
    .then(status => {
      console.log('Import status:', status);
      if (status.status === 'PROCESSING') {
        setTimeout(pollImportStatus, 2000); // Poll svake 2 sekunde
      }
    });
}
```

## Napomene

1. **Validacija je uklonjena** - JMBG i ostale validacije su uklonjene za lakši import
2. **Asinhrona obrada** - API se ne blokira tokom uvoza
3. **Batch processing** - Optimizovano za velike fajlove
4. **Error resilience** - Nastavlja sa sledećim redom u slučaju greške
5. **Progress tracking** - Real-time praćenje napretka

## Troubleshooting

### Česte greške:
- **EMPTY_FILE** - Fajl je prazan
- **INVALID_FILE_TYPE** - Nije Excel fajl
- **INTERNAL_ERROR** - Greška u obradi

### Logovi:
- Sve aktivnosti se loguju u `ImportService` logger
- Progress se loguje svakih 1000 redova
- Greške se loguju sa stack trace-om
