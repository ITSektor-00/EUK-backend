# Frontend Excel Export Implementation Guide

## 🎯 Pregled

Backend sistem za izvoz podataka u Excel sa template-om. Koristi postojeće Excel template fajlove sa zaglavljem, tabelom i footer-om.

## 📋 Backend Template Logika

### Template struktura:
- **Zaglavlje** - fiksno na vrhu
- **Tabela** - počinje od reda 10
- **Footer** - redovi 36 i 39 (pomeraju se naniže automatski)

### Backend logika:
1. Učitava template Excel fajl
2. Dohvata podatke iz baze (`repository.findAll()`)
3. Upisuje podatke od reda 10 nadole
4. Popunjava kolone A-O (indeks 0-14)
5. Footer ostaje netaknut i pomera se naniže

## 🚀 API Endpoint-i

### 1. GET /api/export/excel/t1
**Opis:** Izvoz ugrozeno_lice_t1 tabele u Excel sa template-om

**Request:**
```javascript
fetch('/api/export/excel/t1')
  .then(response => response.blob())
  .then(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'ugrozena_lica_t1.xlsx';
    a.click();
  });
```

**Response:**
- Content-Type: `application/octet-stream`
- Content-Disposition: `attachment; filename="ugrozena_lica_t1_YYYYMMDD_HHMMSS.xlsx"`
- Body: Excel fajl sa podacima

### 2. GET /api/export/excel/t2
**Opis:** Izvoz ugrozeno_lice_t2 tabele u Excel sa template-om

**Request:**
```javascript
fetch('/api/export/excel/t2')
  .then(response => response.blob())
  .then(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'ugrozena_lica_t2.xlsx';
    a.click();
  });
```

**Response:**
- Content-Type: `application/octet-stream`
- Content-Disposition: `attachment; filename="ugrozena_lica_t2_YYYYMMDD_HHMMSS.xlsx"`
- Body: Excel fajl sa podacima

### 3. GET /api/export/status
**Opis:** Status izvoza (za buduće asinhrone operacije)

**Response:**
```json
{
  "message": "Export operations are synchronous"
}
```

## 📊 Excel Format za svaku tabelu

### ugrozeno_lice_t1 (15 kolona A-O)
```
A10: Redni broj        B10: Ime            C10: Prezime         D10: JMBG
E10: PTT broj          F10: Grad/Opština   G10: Mesto          H10: Ulica i broj
I10: Broj članova      J10: Osnov statusa  K10: ED broj        L10: Potrošnja/Površina
M10: Iznos umanjenja   N10: Broj računa    O10: Datum računa
```

### ugrozeno_lice_t2 (15 kolona A-O)
```
A10: Redni broj        B10: Ime            C10: Prezime         D10: JMBG
E10: PTT broj          F10: Grad/Opština   G10: Mesto          H10: Ulica i broj
I10: ED broj           J10: Period važenja  K10: Prazno          L10: Prazno
M10: Prazno            N10: Prazno         O10: Prazno
```

## 🎨 React Komponenta - Kompletna implementacija

```jsx
import React, { useState } from 'react';

const ExcelExportComponent = () => {
  const [isExportingT1, setIsExportingT1] = useState(false);
  const [isExportingT2, setIsExportingT2] = useState(false);

  // Export T1 tabela
  const handleExportT1 = async () => {
    setIsExportingT1(true);
    try {
      const response = await fetch('/api/export/excel/t1');
      
      if (!response.ok) {
        throw new Error('Greška pri izvozu T1 tabele');
      }
      
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ugrozena_lica_t1_${new Date().toISOString().slice(0,19).replace(/:/g, '-')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      
    } catch (error) {
      console.error('Error exporting T1:', error);
      alert('Greška pri izvozu T1 tabele: ' + error.message);
    } finally {
      setIsExportingT1(false);
    }
  };

  // Export T2 tabela
  const handleExportT2 = async () => {
    setIsExportingT2(true);
    try {
      const response = await fetch('/api/export/excel/t2');
      
      if (!response.ok) {
        throw new Error('Greška pri izvozu T2 tabele');
      }
      
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ugrozena_lica_t2_${new Date().toISOString().slice(0,19).replace(/:/g, '-')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      
    } catch (error) {
      console.error('Error exporting T2:', error);
      alert('Greška pri izvozu T2 tabele: ' + error.message);
    } finally {
      setIsExportingT2(false);
    }
  };

  return (
    <div className="excel-export-container">
      <h2>Excel Export</h2>
      
      <div className="export-buttons">
        <button 
          onClick={handleExportT1} 
          disabled={isExportingT1}
          className="export-button t1-button"
        >
          {isExportingT1 ? 'Izvoz u toku...' : 'Izvezi T1 tabelu'}
        </button>
        
        <button 
          onClick={handleExportT2} 
          disabled={isExportingT2}
          className="export-button t2-button"
        >
          {isExportingT2 ? 'Izvoz u toku...' : 'Izvezi T2 tabelu'}
        </button>
      </div>
      
      <div className="export-info">
        <h3>Informacije o izvozu</h3>
        <ul>
          <li><strong>T1 tabela:</strong> Kompletna struktura sa energetskim podacima (15 kolona)</li>
          <li><strong>T2 tabela:</strong> Pojednostavljena struktura (15 kolona)</li>
          <li><strong>Template:</strong> Koristi se postojeći Excel template sa zaglavljem i footer-om</li>
          <li><strong>Format:</strong> Podaci se upisuju od reda 10, footer ostaje netaknut</li>
        </ul>
      </div>
    </div>
  );
};

export default ExcelExportComponent;
```

## 🎨 CSS Stilovi

```css
.excel-export-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.export-buttons {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.export-button {
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
  min-width: 200px;
}

.export-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.t1-button {
  background-color: #007bff;
  color: white;
}

.t1-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.t2-button {
  background-color: #28a745;
  color: white;
}

.t2-button:hover:not(:disabled) {
  background-color: #1e7e34;
}

.export-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.export-info h3 {
  margin-top: 0;
  color: #007bff;
}

.export-info ul {
  list-style-type: none;
  padding-left: 0;
}

.export-info li {
  padding: 8px 0;
  border-bottom: 1px solid #e9ecef;
}

.export-info li:last-child {
  border-bottom: none;
}

.export-info strong {
  color: #495057;
}
```

## 🔧 TypeScript tipovi

```typescript
interface ExportResponse {
  blob: Blob;
  filename: string;
}

interface ExportStatus {
  message: string;
}

// Helper funkcija za download
const downloadFile = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};
```

## 🚀 Napredna implementacija sa progress tracking-om

```jsx
import React, { useState, useCallback } from 'react';

const AdvancedExcelExportComponent = () => {
  const [exportStatus, setExportStatus] = useState({
    t1: { isExporting: false, progress: 0 },
    t2: { isExporting: false, progress: 0 }
  });

  const exportWithProgress = useCallback(async (type: 't1' | 't2') => {
    setExportStatus(prev => ({
      ...prev,
      [type]: { isExporting: true, progress: 0 }
    }));

    try {
      // Simulacija progress-a (u realnoj implementaciji bi bio server-sent events)
      const progressInterval = setInterval(() => {
        setExportStatus(prev => ({
          ...prev,
          [type]: { 
            isExporting: true, 
            progress: Math.min(prev[type].progress + 10, 90) 
          }
        }));
      }, 200);

      const response = await fetch(`/api/export/excel/${type}`);
      
      if (!response.ok) {
        throw new Error(`Greška pri izvozu ${type} tabele`);
      }

      clearInterval(progressInterval);
      
      const blob = await response.blob();
      const timestamp = new Date().toISOString().slice(0,19).replace(/:/g, '-');
      const filename = `ugrozena_lica_${type}_${timestamp}.xlsx`;
      
      // Download fajl
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

      setExportStatus(prev => ({
        ...prev,
        [type]: { isExporting: false, progress: 100 }
      }));

      setTimeout(() => {
        setExportStatus(prev => ({
          ...prev,
          [type]: { isExporting: false, progress: 0 }
        }));
      }, 1000);

    } catch (error) {
      console.error(`Error exporting ${type}:`, error);
      setExportStatus(prev => ({
        ...prev,
        [type]: { isExporting: false, progress: 0 }
      }));
      alert(`Greška pri izvozu ${type} tabele: ${error.message}`);
    }
  }, []);

  return (
    <div className="advanced-export-container">
      <h2>Napredni Excel Export</h2>
      
      <div className="export-cards">
        <div className="export-card">
          <h3>T1 Tabela</h3>
          <p>Kompletna struktura sa energetskim podacima</p>
          <button 
            onClick={() => exportWithProgress('t1')}
            disabled={exportStatus.t1.isExporting}
            className="export-btn t1-btn"
          >
            {exportStatus.t1.isExporting ? 'Izvoz u toku...' : 'Izvezi T1'}
          </button>
          
          {exportStatus.t1.isExporting && (
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${exportStatus.t1.progress}%` }}
              ></div>
            </div>
          )}
        </div>

        <div className="export-card">
          <h3>T2 Tabela</h3>
          <p>Pojednostavljena struktura</p>
          <button 
            onClick={() => exportWithProgress('t2')}
            disabled={exportStatus.t2.isExporting}
            className="export-btn t2-btn"
          >
            {exportStatus.t2.isExporting ? 'Izvoz u toku...' : 'Izvezi T2'}
          </button>
          
          {exportStatus.t2.isExporting && (
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${exportStatus.t2.progress}%` }}
              ></div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
```

## ⚡ Ključne karakteristike

- ✅ **Template-based** - koristi postojeće Excel template fajlove
- ✅ **Footer preservation** - footer ostaje netaknut
- ✅ **Automatic row insertion** - novi redovi se dodaju ispod postojećih
- ✅ **Column mapping** - precizno mapiranje kolona A-O
- ✅ **Error handling** - prikaz grešaka korisniku
- ✅ **Progress tracking** - vizuelno praćenje napretka
- ✅ **File naming** - automatsko imenovanje sa timestamp-om

## 📝 Napomene za implementaciju

1. **Template lokacije** - template fajlovi su u `src/main/resources/excelTemplate/`
2. **Row insertion** - podaci se upisuju od reda 10 nadole
3. **Footer protection** - footer se nikad ne dira, samo se pomera naniže
4. **Column range** - koristi se opseg A-O (indeks 0-14)
5. **File download** - automatski download sa timestamp-om u imenu
6. **Error handling** - prikaz grešaka korisniku

**Sistem je potpuno spreman za implementaciju!** 🎉
