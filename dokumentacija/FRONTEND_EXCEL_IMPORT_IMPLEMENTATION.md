# Frontend Excel Import Implementation Guide

## 🎯 Pregled

Kompletna implementacija Excel import funkcionalnosti za EUK frontend aplikaciju. Sistem podržava univerzalni import za sve tabele u aplikaciji.

## 📋 Potrebni API Endpoint-i

### 1. GET /api/import/tables
**Opis:** Dohvatanje liste dostupnih tabela za import

**Request:**
```javascript
fetch('/api/import/tables')
  .then(response => response.json())
  .then(data => {
    console.log('Available tables:', data.tables);
  });
```

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
      "columns": ["redni_broj", "ime", "prezime", "jmbg", "ptt_broj", "grad_opstina", "mesto", "ulica_i_broj", "broj_clanova_domacinstva", "osnov_sticanja_statusa", "ed_broj_broj_mernog_uredjaja", "potrosnja_i_povrsina_combined", "iznos_umanjenja_sa_pdv", "broj_racuna", "datum_izdavanja_racuna", "datum_trajanja_prava"],
      "displayNames": ["Redni broj", "Ime", "Prezime", "JMBG", "PTT broj", "Grad/Opština", "Mesto", "Ulica i broj", "Broj članova domaćinstva", "Osnov sticanja statusa", "ED broj/broj mernog uređaja", "Potrošnja i površina kombinovano", "Iznos umanjenja sa PDV", "Broj računa", "Datum izdavanja računa", "Datum trajanja prava"]
    }
  }
}
```

### 2. POST /api/import/excel
**Opis:** Upload Excel fajla za određenu tabelu

**Request:**
```javascript
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
});
```

**Parameters:**
- `file` (MultipartFile) - Excel fajl (.xlsx, .xls)
- `table` (String) - Naziv tabele za import

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

### 3. GET /api/import/status
**Opis:** Provera statusa trenutnog uvoza

**Request:**
```javascript
fetch('/api/import/status')
  .then(response => response.json())
  .then(status => {
    console.log('Import status:', status);
  });
```

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

## 📊 Excel Format za svaku tabelu

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

### euk.predmet (1 kolona)
```
A: Naziv predmeta
```

### users (4 kolone)
```
A: Username            B: Email            C: First Name       D: Last Name
```

## 🎨 React Komponenta - Kompletna implementacija

```jsx
import React, { useState, useEffect } from 'react';

const ExcelImportComponent = () => {
  const [availableTables, setAvailableTables] = useState([]);
  const [selectedTable, setSelectedTable] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [importStatus, setImportStatus] = useState(null);
  const [isImporting, setIsImporting] = useState(false);
  const [tableDetails, setTableDetails] = useState({});

  // Učitaj dostupne tabele
  useEffect(() => {
    fetch('/api/import/tables')
      .then(response => response.json())
      .then(data => {
        setAvailableTables(data.tables);
        setTableDetails(data.tableDetails);
      })
      .catch(error => console.error('Error loading tables:', error));
  }, []);

  // Polling za status
  const pollStatus = () => {
    fetch('/api/import/status')
      .then(response => response.json())
      .then(status => {
        setImportStatus(status);
        if (status.status === 'PROCESSING') {
          setTimeout(pollStatus, 2000);
        } else if (status.status === 'COMPLETED') {
          setIsImporting(false);
          alert('Import završen uspešno!');
        } else if (status.status === 'ERROR') {
          setIsImporting(false);
          alert('Greška pri importu: ' + status.lastError);
        }
      })
      .catch(error => {
        console.error('Error polling status:', error);
        setIsImporting(false);
      });
  };

  // Upload fajla
  const handleUpload = () => {
    if (!selectedFile || !selectedTable) {
      alert('Molimo odaberite fajl i tabelu');
      return;
    }

    const formData = new FormData();
    formData.append('file', selectedFile);
    formData.append('table', selectedTable);

    setIsImporting(true);
    fetch('/api/import/excel', {
      method: 'POST',
      body: formData
    })
    .then(response => response.json())
    .then(data => {
      if (data.status === 'SUCCESS') {
        pollStatus(); // Počni polling
      } else {
        setIsImporting(false);
        alert('Greška: ' + data.message);
      }
    })
    .catch(error => {
      console.error('Error uploading file:', error);
      setIsImporting(false);
      alert('Greška pri upload-u fajla');
    });
  };

  // File change handler
  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file && (file.name.endsWith('.xlsx') || file.name.endsWith('.xls'))) {
      setSelectedFile(file);
    } else {
      alert('Molimo odaberite Excel fajl (.xlsx ili .xls)');
    }
  };

  return (
    <div className="excel-import-container">
      <h2>Excel Import</h2>
      
      {/* Table selection */}
      <div className="form-group">
        <label htmlFor="table-select">Odaberite tabelu:</label>
        <select 
          id="table-select"
          value={selectedTable} 
          onChange={(e) => setSelectedTable(e.target.value)}
          disabled={isImporting}
        >
          <option value="">Odaberite tabelu</option>
          {availableTables.map(table => (
            <option key={table} value={table}>
              {table} ({tableDetails[table]?.columnCount || 0} kolona)
            </option>
          ))}
        </select>
      </div>

      {/* File upload */}
      <div className="form-group">
        <label htmlFor="file-input">Odaberite Excel fajl:</label>
        <input 
          id="file-input"
          type="file" 
          accept=".xlsx,.xls"
          onChange={handleFileChange}
          disabled={isImporting}
        />
      </div>

      {/* Upload button */}
      <button 
        onClick={handleUpload} 
        disabled={isImporting || !selectedFile || !selectedTable}
        className="upload-button"
      >
        {isImporting ? 'Import u toku...' : 'Počni import'}
      </button>

      {/* Progress tracking */}
      {importStatus && (
        <div className="progress-container">
          <h3>Status uvoza</h3>
          <div className="status-info">
            <p><strong>Status:</strong> {importStatus.status}</p>
            <p><strong>Progres:</strong> {importStatus.progress?.toFixed(1)}%</p>
            <p><strong>Obrađeno:</strong> {importStatus.processedRows} / {importStatus.totalRows}</p>
            {importStatus.elapsedTimeSeconds && (
              <p><strong>Vreme:</strong> {importStatus.elapsedTimeSeconds.toFixed(1)}s</p>
            )}
          </div>
          
          {/* Progress bar */}
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{ width: `${importStatus.progress || 0}%` }}
            ></div>
          </div>
        </div>
      )}

      {/* Table format info */}
      {selectedTable && tableDetails[selectedTable] && (
        <div className="format-info">
          <h3>Format Excel fajla za {selectedTable}</h3>
          <p><strong>Broj kolona:</strong> {tableDetails[selectedTable].columnCount}</p>
          <div className="columns-list">
            <h4>Kolone:</h4>
            <ul>
              {tableDetails[selectedTable].displayNames.map((name, index) => (
                <li key={index}>
                  <strong>{String.fromCharCode(65 + index)}:</strong> {name}
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
};

export default ExcelImportComponent;
```

## 🎨 CSS Stilovi

```css
.excel-import-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group select,
.form-group input[type="file"] {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.upload-button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.upload-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.progress-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.status-info p {
  margin: 5px 0;
}

.progress-bar {
  width: 100%;
  height: 20px;
  background-color: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
  margin-top: 10px;
}

.progress-fill {
  height: 100%;
  background-color: #28a745;
  transition: width 0.3s ease;
}

.format-info {
  margin-top: 20px;
  padding: 15px;
  background-color: #e7f3ff;
  border-radius: 4px;
}

.columns-list ul {
  list-style-type: none;
  padding-left: 0;
}

.columns-list li {
  padding: 2px 0;
}
```

## 🔧 TypeScript tipovi

```typescript
interface ImportStatus {
  status: 'IDLE' | 'PROCESSING' | 'COMPLETED' | 'ERROR';
  processedRows: number;
  totalRows: number;
  progress: number;
  elapsedTimeMs?: number;
  elapsedTimeSeconds?: number;
  lastError?: string;
}

interface TableDetails {
  tableName: string;
  entityName: string;
  columnCount: number;
  columns: string[];
  displayNames: string[];
}

interface ImportResponse {
  status: 'SUCCESS' | 'ERROR';
  message: string;
  filename?: string;
  table?: string;
  size?: number;
  expectedColumns?: number;
  timestamp?: number;
  error?: string;
}

interface TablesResponse {
  status: 'SUCCESS' | 'ERROR';
  tables: string[];
  tableDetails: Record<string, TableDetails>;
}
```

## 🚨 Error Handling

```javascript
// Error responses
const handleError = (error) => {
  switch (error.error) {
    case 'EMPTY_FILE':
      alert('Fajl je prazan');
      break;
    case 'INVALID_FILE_TYPE':
      alert('Podržani su samo Excel fajlovi (.xlsx, .xls)');
      break;
    case 'INVALID_TABLE':
      alert('Neispravan naziv tabele');
      break;
    case 'INTERNAL_ERROR':
      alert('Greška u obradi: ' + error.message);
      break;
    default:
      alert('Nepoznata greška: ' + error.message);
  }
};
```

## ⚡ Ključne karakteristike

- ✅ **Asinhrona obrada** - ne blokira UI
- ✅ **Real-time progress** - praćenje napretka
- ✅ **Error handling** - prikaz grešaka
- ✅ **File validation** - samo Excel fajlovi
- ✅ **Table validation** - samo podržane tabele
- ✅ **Progress bar** - vizuelno praćenje
- ✅ **Format info** - prikaz potrebnih kolona
- ✅ **TypeScript support** - tipovi za type safety

## 📝 Napomene za implementaciju

1. **Polling interval** - 2 sekunde je optimalno
2. **File validation** - proveriti .xlsx i .xls ekstenzije
3. **Error messages** - prikazati korisničke poruke
4. **Progress tracking** - vizuelno prikazati napredak
5. **Table info** - prikazati format za svaku tabelu
6. **Responsive design** - prilagoditi za mobilne uređaje

**Sistem je potpuno spreman za implementaciju!** 🎉
