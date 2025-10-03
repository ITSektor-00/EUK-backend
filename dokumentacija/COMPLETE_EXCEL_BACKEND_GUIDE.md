# 📊 Kompletna Backend Excel Obrada - Vodič za Frontend

## 🎯 Pregled sistema

Backend radi **kompletno obradu Excel fajlova** - čita, validira, obrađuje i izvozi podatke. Frontend samo šalje fajlove i preuzima rezultate.

## 📥 UVOZ (Import) - Backend API

### 1. **POST /api/import/excel** - Uvoz Excel fajla
```javascript
// Frontend šalje fajl i naziv tabele
const formData = new FormData();
formData.append('file', excelFile);
formData.append('table', 'euk.ugrozeno_lice_t1');

const response = await fetch('/api/import/excel', {
    method: 'POST',
    body: formData
});

const result = await response.json();
```

**Backend radi:**
- ✅ Čita Excel fajl pomoću Apache POI
- ✅ Validira podatke (JMBG, prazna polja, duplikati)
- ✅ Batch insert u bazu (1000 redova odjednom)
- ✅ Asinhrona obrada (@Async)
- ✅ Vraća status uvoza

**Response:**
```json
{
    "status": "SUCCESS",
    "message": "Uvoz pokrenut u pozadini",
    "filename": "podaci.xlsx",
    "table": "euk.ugrozeno_lice_t1",
    "size": 12345,
    "expectedColumns": 15,
    "timestamp": 1703123456789
}
```

### 2. **POST /api/import/excel/validate** - Validacija bez uvoza
```javascript
// Frontend može da validira fajl pre uvoza
const formData = new FormData();
formData.append('file', excelFile);
formData.append('table', 'euk.ugrozeno_lice_t1');

const response = await fetch('/api/import/excel/validate', {
    method: 'POST',
    body: formData
});

const validation = await response.json();
```

**Response sa greškama:**
```json
{
    "status": "SUCCESS",
    "totalRows": 100,
    "validRows": 95,
    "errorRows": 5,
    "warningRows": 2,
    "canImport": false,
    "message": "Pronađene greške u 5 redova. Uvoz nije moguć.",
    "errors": [
        {
            "rowNumber": 12,
            "hasErrors": true,
            "errors": ["JMBG mora sadržati tačno 13 cifara u redu 12 (vrednost: 123456789)"],
            "warnings": []
        }
    ],
    "warnings": [
        {
            "rowNumber": 15,
            "hasErrors": false,
            "hasWarnings": true,
            "errors": [],
            "warnings": ["PTT broj nije u ispravnom formatu u redu 15 (vrednost: 1234)"]
        }
    ]
}
```

### 3. **GET /api/import/status** - Status uvoza
```javascript
// Frontend može da proveri status asinhrone obrade
const response = await fetch('/api/import/status');
const status = await response.json();
```

**Response:**
```json
{
    "status": "PROCESSING",
    "processedRows": 500,
    "totalRows": 1000,
    "progress": 50.0,
    "elapsedTimeMs": 5000,
    "elapsedTimeSeconds": 5.0
}
```

### 4. **GET /api/import/tables** - Dostupne tabele
```javascript
// Frontend može da vidi koje tabele su dostupne za import
const response = await fetch('/api/import/tables');
const tables = await response.json();
```

## 📤 IZVOZ (Export) - Backend API

### 1. **GET /api/export/excel** - Izvoz Excel fajla
```javascript
// Frontend poziva endpoint za izvoz
// type: 't1' ili 't2' (default je 't1')
const response = await fetch('/api/export/excel?type=t1');
const blob = await response.blob();

// Kreiranje download linka
const url = window.URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'ugrozena_lica_t1_20231221_143022.xlsx';
a.click();
window.URL.revokeObjectURL(url);
```

**Backend radi:**
- ✅ Učitava template fajl (`ЕУК-T1.xlsx` ili `ЕУК-T2.xlsx`)
- ✅ Čita podatke iz baze
- ✅ Upisuje podatke od reda 10, kolone A-O
- ✅ Pomera footer naniže (redovi 36 i 39)
- ✅ Vraća fajl kao download

### 2. **GET /api/export/status** - Status izvoza
```javascript
// Za buduće asinhrone operacije
const response = await fetch('/api/export/status');
const status = await response.text();
```

## 🗂️ Podržane tabele za import

| Tabela | Opis | Kolone |
|---------|------|--------|
| `euk.ugrozeno_lice_t1` | Ugrožena lica T1 | 15 kolona (A-O) |
| `euk.ugrozeno_lice_t2` | Ugrožena lica T2 | 8 kolona (A-H) |
| `euk.kategorija` | Kategorije | 1 kolona (A) |
| `euk.predmet` | Predmeti | 1 kolona (A) |
| `users` | Korisnici | 4 kolone (A-D) |

## 📋 Excel format za T1 tabelu

**Podaci počinju od REDA 10** (row index 9 u programiranju):

| Excel Kolona | Index | Naziv polja | Validacija |
|--------------|-------|-------------|------------|
| **A10** | 0 | Redni broj | - |
| **B10** | 1 | Ime | ✅ Obavezno |
| **C10** | 2 | Prezime | ✅ Obavezno |
| **D10** | 3 | **JMBG** | ✅ Obavezno, 13 cifara |
| **E10** | 4 | PTT broj | ⚠️ Format: 5 cifara |
| **F10** | 5 | Grad/Opština | - |
| **G10** | 6 | Mesto | - |
| **H10** | 7 | Ulica i broj | - |
| **I10** | 8 | Broj članova | ⚠️ > 0 |
| **J10** | 9 | Osnov statusa | - |
| **K10** | 10 | ED broj | - |
| **L10** | 11 | Potrošnja/Površina | - |
| **M10** | 12 | Iznos umanjenja | - |
| **N10** | 13 | Broj računa | - |
| **O10** | 14 | Datum računa | - |

## 📋 Excel format za T2 tabelu

**Podaci počinju od REDA 10**:

| Excel Kolona | Index | Naziv polja | Validacija |
|--------------|-------|-------------|------------|
| **A10** | 0 | Redni broj | - |
| **B10** | 1 | Ime | ✅ Obavezno |
| **C10** | 2 | Prezime | ✅ Obavezno |
| **D10** | 3 | **JMBG** | ✅ Obavezno, 13 cifara |
| **E10** | 4 | PTT broj | ⚠️ Format: 5 cifara |
| **F10** | 5 | Grad/Opština | - |
| **G10** | 6 | Mesto | - |
| **H10** | 7 | Ulica i broj | - |
| **I10** | 8 | ED broj | - |
| **J10** | 9 | Period važenja | - |
| **K10** | 10 | Prazno | - |
| **L10** | 11 | Prazno | - |
| **M10** | 12 | Prazno | - |
| **N10** | 13 | Prazno | - |
| **O10** | 14 | Prazno | - |

## 🎨 Frontend implementacija

### React komponenta za import
```jsx
import React, { useState } from 'react';

const ExcelImport = () => {
    const [file, setFile] = useState(null);
    const [table, setTable] = useState('euk.ugrozeno_lice_t1');
    const [status, setStatus] = useState('idle');
    const [progress, setProgress] = useState(0);

    const handleFileUpload = async () => {
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('table', table);

        try {
            // 1. Validacija
            const validateResponse = await fetch('/api/import/excel/validate', {
                method: 'POST',
                body: formData
            });
            const validation = await validateResponse.json();

            if (!validation.canImport) {
                alert(`Greške u fajlu: ${validation.message}`);
                return;
            }

            // 2. Uvoz
            const importResponse = await fetch('/api/import/excel', {
                method: 'POST',
                body: formData
            });
            const result = await importResponse.json();

            if (result.status === 'SUCCESS') {
                setStatus('importing');
                // 3. Praćenje statusa
                const interval = setInterval(async () => {
                    const statusResponse = await fetch('/api/import/status');
                    const statusData = await statusResponse.json();
                    
                    setProgress(statusData.progress);
                    
                    if (statusData.status === 'COMPLETED') {
                        clearInterval(interval);
                        setStatus('completed');
                        alert('Uvoz završen!');
                    }
                }, 1000);
            }
        } catch (error) {
            console.error('Greška pri uvozu:', error);
        }
    };

    return (
        <div>
            <input 
                type="file" 
                accept=".xlsx,.xls" 
                onChange={(e) => setFile(e.target.files[0])} 
            />
            <select value={table} onChange={(e) => setTable(e.target.value)}>
                <option value="euk.ugrozeno_lice_t1">Ugrožena lica T1</option>
                <option value="euk.ugrozeno_lice_t2">Ugrožena lica T2</option>
                <option value="euk.kategorija">Kategorije</option>
                <option value="euk.predmet">Predmeti</option>
                <option value="users">Korisnici</option>
            </select>
            <button onClick={handleFileUpload} disabled={!file}>
                Uvezi Excel
            </button>
            {status === 'importing' && (
                <div>
                    <p>Uvoz u toku... {progress.toFixed(1)}%</p>
                    <div className="progress-bar">
                        <div 
                            className="progress-fill" 
                            style={{width: `${progress}%`}}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};
```

### React komponenta za export
```jsx
import React, { useState } from 'react';

const ExcelExport = () => {
    const [type, setType] = useState('t1');
    const [isExporting, setIsExporting] = useState(false);

    const handleExport = async () => {
        setIsExporting(true);
        
        try {
            const response = await fetch(`/api/export/excel?type=${type}`);
            
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `ugrozena_lica_${type}_${new Date().toISOString().slice(0,19).replace(/:/g,'-')}.xlsx`;
                a.click();
                window.URL.revokeObjectURL(url);
            } else {
                alert('Greška pri izvozu!');
            }
        } catch (error) {
            console.error('Greška pri izvozu:', error);
        } finally {
            setIsExporting(false);
        }
    };

    return (
        <div>
            <select value={type} onChange={(e) => setType(e.target.value)}>
                <option value="t1">Ugrožena lica T1</option>
                <option value="t2">Ugrožena lica T2</option>
            </select>
            <button onClick={handleExport} disabled={isExporting}>
                {isExporting ? 'Izvoz u toku...' : 'Preuzmi Excel'}
            </button>
        </div>
    );
};
```

## 🔧 Backend konfiguracija

### Template fajlovi
Backend koristi template fajlove:
- `src/main/resources/excelTemplate/ЕУК-T1.xlsx` - za T1 izvoz
- `src/main/resources/excelTemplate/ЕУК-T2.xlsx` - za T2 izvoz

### Batch processing
- **Batch size**: 1000 redova
- **Asinhrona obrada**: @Async
- **Progress tracking**: AtomicInteger, AtomicLong

### Validacija
- **JMBG**: 13 cifara
- **PTT broj**: 5 cifara (opciono)
- **Obavezna polja**: Ime, Prezime, JMBG
- **Duplikati**: Proverava se unutar batch-a

## 📝 Napomene za frontend

1. **Frontend ne zna ništa o Excel strukturi** - backend radi sve
2. **Samo 2 dugmeta**: Upload Excel, Download Excel
3. **Validacija je opciona** - može se preskočiti
4. **Progress tracking** - za velike fajlove
5. **Error handling** - detaljne greške po redovima

## 🚀 Testiranje

### Test import
```bash
curl -X POST http://localhost:8080/api/import/excel \
  -F "file=@test_data.xlsx" \
  -F "table=euk.ugrozeno_lice_t1"
```

### Test export
```bash
curl -X GET "http://localhost:8080/api/export/excel?type=t1" \
  -o ugrozena_lica_t1.xlsx
```

### Test validacija
```bash
curl -X POST http://localhost:8080/api/import/excel/validate \
  -F "file=@test_data.xlsx" \
  -F "table=euk.ugrozeno_lice_t1"
```

---

**Backend radi sve - frontend samo šalje fajlove i preuzima rezultate! 🎯**
