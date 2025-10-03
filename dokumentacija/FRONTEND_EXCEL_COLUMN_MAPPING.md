# 📊 Frontend Excel Column Mapping Guide

## 🎯 Problem

Frontend čita pogrešne kolone iz Excel fajla - umesto JMBG-a (kolona D) čita PTT broj (kolona E).

## ✅ Ispravno mapiranje kolona za T1 tabelu

Podaci počinju od **REDA 10** (row index 9 u programiranju jer je 0-based).

| Excel Kolona | Index | Naziv polja | Backend DTO property |
|--------------|-------|-------------|----------------------|
| **A10** | 0 | Redni broj | `redniBroj` |
| **B10** | 1 | Ime | `ime` |
| **C10** | 2 | Prezime | `prezime` |
| **D10** | 3 | **JMBG** | `jmbg` ← **VAŽNO!** |
| **E10** | 4 | PTT broj | `pttBroj` |
| **F10** | 5 | Grad/Opština | `gradOpstina` |
| **G10** | 6 | Mesto | `mesto` |
| **H10** | 7 | Ulica i broj | `ulicaIBroj` |
| **I10** | 8 | Broj članova | `brojClanovaDomacinstva` |
| **J10** | 9 | Osnov statusa | `osnovSticanjaStatusa` |
| **K10** | 10 | ED broj | `edBrojBrojMernogUredjaja` |
| **L10** | 11 | Potrošnja/Površina | `potrosnjaIPovrsinaCombined` |
| **M10** | 12 | Iznos umanjenja | `iznosUmanjenjaSaPdv` |
| **N10** | 13 | Broj računa | `brojRacuna` |
| **O10** | 14 | Datum računa | `datumIzdavanjaRacuna` |

## 💻 JavaScript/TypeScript kod za parsiranje

```javascript
import * as XLSX from 'xlsx';

// Funkcija za parsiranje Excel fajla
function parseExcelToT1Data(file: File): Promise<Array<UgrozenoLiceT1Dto>> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const data = e.target?.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
        
        // Parsiranje sa ispravnim mapiranjem kolona
        const parsedData: Array<UgrozenoLiceT1Dto> = [];
        
        // Počni od reda 10 (index 9)
        let rowIndex = 9; // Row 10 u Excel-u
        
        while (true) {
          // Čitaj ćelije za trenutni red
          const redniBroj = firstSheet[`A${rowIndex + 1}`]?.v;
          
          // Ako nema rednog broja, kraj podataka
          if (!redniBroj) break;
          
          const dto: UgrozenoLiceT1Dto = {
            redniBroj: firstSheet[`A${rowIndex + 1}`]?.v?.toString() || '',  // A kolona
            ime: firstSheet[`B${rowIndex + 1}`]?.v?.toString() || '',         // B kolona
            prezime: firstSheet[`C${rowIndex + 1}`]?.v?.toString() || '',     // C kolona
            jmbg: firstSheet[`D${rowIndex + 1}`]?.v?.toString() || '',        // D kolona ← VAŽNO!
            pttBroj: firstSheet[`E${rowIndex + 1}`]?.v?.toString() || '',     // E kolona
            gradOpstina: firstSheet[`F${rowIndex + 1}`]?.v?.toString() || '', // F kolona
            mesto: firstSheet[`G${rowIndex + 1}`]?.v?.toString() || '',       // G kolona
            ulicaIBroj: firstSheet[`H${rowIndex + 1}`]?.v?.toString() || '',  // H kolona
            brojClanovaDomacinstva: parseInt(firstSheet[`I${rowIndex + 1}`]?.v) || 0, // I kolona
            osnovSticanjaStatusa: firstSheet[`J${rowIndex + 1}`]?.v?.toString() || '', // J kolona
            edBrojBrojMernogUredjaja: firstSheet[`K${rowIndex + 1}`]?.v?.toString() || '', // K kolona
            potrosnjaIPovrsinaCombined: firstSheet[`L${rowIndex + 1}`]?.v?.toString() || '', // L kolona
            iznosUmanjenjaSaPdv: parseFloat(firstSheet[`M${rowIndex + 1}`]?.v) || 0, // M kolona
            brojRacuna: firstSheet[`N${rowIndex + 1}`]?.v?.toString() || '',  // N kolona
            datumIzdavanjaRacuna: parseDate(firstSheet[`O${rowIndex + 1}`]?.v), // O kolona
          };
          
          parsedData.push(dto);
          rowIndex++;
        }
        
        resolve(parsedData);
      } catch (error) {
        reject(error);
      }
    };
    
    reader.onerror = () => reject(new Error('Error reading file'));
    reader.readAsBinaryString(file);
  });
}

// Helper funkcija za parsiranje datuma
function parseDate(value: any): string | null {
  if (!value) return null;
  
  if (value instanceof Date) {
    return value.toISOString().split('T')[0]; // Format: YYYY-MM-DD
  }
  
  if (typeof value === 'number') {
    // Excel serijski broj za datum
    const date = XLSX.SSF.parse_date_code(value);
    return `${date.y}-${String(date.m).padStart(2, '0')}-${String(date.d).padStart(2, '0')}`;
  }
  
  return value.toString();
}
```

## 🔍 Validacija podataka

Pre slanja na backend, validir

aj da su kolone ispravno pročitane:

```javascript
function validateParsedData(data: Array<UgrozenoLiceT1Dto>) {
  const errors: string[] = [];
  
  data.forEach((dto, index) => {
    const rowNum = index + 10; // Row broj u Excel-u
    
    // JMBG validacija - mora biti 13 cifara
    if (!dto.jmbg || dto.jmbg.length !== 13 || !/^\d{13}$/.test(dto.jmbg)) {
      console.warn(`Red ${rowNum}: JMBG '${dto.jmbg}' nije u ispravnom formatu (13 cifara)`);
    }
    
    // PTT broj validacija - mora biti 5 cifara
    if (dto.pttBroj && !/^\d{5}$/.test(dto.pttBroj)) {
      console.warn(`Red ${rowNum}: PTT broj '${dto.pttBroj}' nije u ispravnom formatu (5 cifara)`);
    }
    
    // Ako JMBG izgleda kao PTT broj (5 cifara), greška!
    if (dto.jmbg && /^\d{5}$/.test(dto.jmbg)) {
      errors.push(`Red ${rowNum}: JMBG '${dto.jmbg}' izgleda kao PTT broj - GREŠKA U MAPIRANJU KOLONA!`);
    }
  });
  
  if (errors.length > 0) {
    console.error('❌ GREŠKA U MAPIRANJU KOLONA:', errors);
    throw new Error('Kolone nisu ispravno mapirane! Proverite Excel fajl.');
  }
  
  console.log('✅ Svi podaci su ispravno parsirani');
}
```

## 📝 Primer korišćenja

```javascript
async function handleExcelUpload(event: React.ChangeEvent<HTMLInputElement>) {
  const file = event.target.files?.[0];
  if (!file) return;
  
  try {
    // Parsiraj Excel fajl
    const parsedData = await parseExcelToT1Data(file);
    
    // Validuj podatke
    validateParsedData(parsedData);
    
    // Pošalji na backend
    const response = await fetch('/api/euk/ugrozena-lica-t1/batch-progress', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(parsedData),
    });
    
    if (!response.ok) {
      throw new Error('Import failed');
    }
    
    const result = await response.json();
    console.log('Import started:', result);
    
  } catch (error) {
    console.error('Error:', error);
    alert('Greška pri učitavanju fajla: ' + error.message);
  }
}
```

## ⚠️ Česte greške

### 1. **Pogrešan row index**
```javascript
// ❌ POGREŠNO - počinje od reda 0
const redniBroj = firstSheet[`A1`]?.v; 

// ✅ ISPRAVNO - počinje od reda 10
const redniBroj = firstSheet[`A10`]?.v;
```

### 2. **Zamenjene kolone**
```javascript
// ❌ POGREŠNO - JMBG i PTT broj zamenjeni
jmbg: firstSheet[`E${rowIndex + 1}`]?.v,    // E je PTT broj!
pttBroj: firstSheet[`D${rowIndex + 1}`]?.v, // D je JMBG!

// ✅ ISPRAVNO
jmbg: firstSheet[`D${rowIndex + 1}`]?.v,    // D je JMBG
pttBroj: firstSheet[`E${rowIndex + 1}`]?.v, // E je PTT broj
```

### 3. **0-based vs 1-based indexing**
```javascript
// Excel koristi 1-based indexing (red 1, red 2, red 3...)
// JavaScript array koristi 0-based indexing (index 0, 1, 2...)

// Ako počinješ od reda 10:
for (let i = 0; i < dataLength; i++) {
  const excelRow = 10 + i; // Red 10, 11, 12...
  const value = firstSheet[`A${excelRow}`]?.v;
}
```

## 🎯 Backend API

Backend očekuje ovaj format:

```json
POST /api/euk/ugrozena-lica-t1/batch-progress

[
  {
    "redniBroj": "RB001",
    "ime": "Marko",
    "prezime": "Marković",
    "jmbg": "1234567890123",  // ← 13 cifara!
    "pttBroj": "11000",        // ← 5 cifara!
    "gradOpstina": "Beograd",
    "mesto": "Beograd",
    "ulicaIBroj": "Knez Mihailova 1",
    "brojClanovaDomacinstva": 3,
    "osnovSticanjaStatusa": "MP",
    "edBrojBrojMernogUredjaja": "ED001",
    "potrosnjaIPovrsinaCombined": "2500 kWh / 75 m²",
    "iznosUmanjenjaSaPdv": 5000.00,
    "brojRacuna": "RAC001",
    "datumIzdavanjaRacuna": "2024-01-15"
  }
]
```

## 📊 Dijagram toka podataka

```
Excel fajl (ЕУК-T1.xlsx)
  ↓
Row 10: A10=RB001, B10=Marko, C10=Marković, D10=1234567890123, E10=11000, ...
  ↓
Frontend parsiranje (XLSX.js)
  ↓
{
  redniBroj: "RB001",      // A10
  ime: "Marko",            // B10
  prezime: "Marković",     // C10
  jmbg: "1234567890123",   // D10 ← VAŽNO!
  pttBroj: "11000",        // E10
  ...
}
  ↓
POST /api/euk/ugrozena-lica-t1/batch-progress
  ↓
Backend processing
  ↓
Database (euk.ugrozeno_lice_t1)
```

## 🔧 Debugging

Ako se i dalje pojavljuje greška da se čitaju pogrešne kolone:

1. **Loguj prvih 5 redova:**
```javascript
console.log('Parsed data (first 5 rows):');
parsedData.slice(0, 5).forEach((dto, i) => {
  console.log(`Row ${i + 10}:`, {
    redniBroj: dto.redniBroj,
    ime: dto.ime,
    prezime: dto.prezime,
    jmbg: dto.jmbg,
    pttBroj: dto.pttBroj,
  });
});
```

2. **Proveri JMBG format:**
```javascript
parsedData.forEach((dto, i) => {
  if (dto.jmbg && dto.jmbg.length < 10) {
    console.error(`❌ Row ${i + 10}: JMBG '${dto.jmbg}' je suviše kratak - verovatno je pročitana pogrešna kolona!`);
  }
});
```

3. **Proveri Excel fajl:**
```javascript
console.log('Excel sheet preview:');
console.log('A10:', firstSheet['A10']?.v);
console.log('B10:', firstSheet['B10']?.v);
console.log('C10:', firstSheet['C10']?.v);
console.log('D10:', firstSheet['D10']?.v); // Ovde treba da bude JMBG (13 cifara)
console.log('E10:', firstSheet['E10']?.v); // Ovde treba da bude PTT broj (5 cifara)
```

## 📞 Kontakt

Ako i dalje imaš problema, pošalji primer Excel fajla i log output da možemo da debugujemo zajedno.
