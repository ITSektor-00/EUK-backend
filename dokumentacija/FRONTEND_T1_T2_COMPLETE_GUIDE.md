# 📊 T1 i T2 Tabele - Kompletna Frontend Dokumentacija

## 🎯 Pregled
Kompletna dokumentacija za rad sa T1 i T2 tabelama u EUK sistemu. Svi endpoint-i, filteri, pretrage i primeri implementacije.

---

## 📋 T1 TABELA - Ugrožena Lica T1

### 🔗 Osnovni Endpoint-i

#### 1. Lista svih T1 zapisa
```javascript
GET /api/euk/ugrozena-lica-t1?page=0&size=50
```

#### 2. Pretraga po ID-u
```javascript
GET /api/euk/ugrozena-lica-t1/{id}
```

#### 3. Kompleksna pretraga sa filterima
```javascript
POST /api/euk/ugrozena-lica-t1/search/filters?page=0&size=50
```

### 🔍 Dostupni Filteri za T1

```javascript
const t1Filters = {
  // Osnovne informacije
  "jmbg": "1234567890123",           // JMBG (tačna pretraga)
  "redniBroj": "RB001",              // Redni broj
  "ime": "Marko",                    // Ime (LIKE pretraga)
  "prezime": "Marković",             // Prezime (LIKE pretraga)
  
  // Adresne informacije
  "gradOpstina": "Beograd",          // Grad/Opština (LIKE pretraga)
  "mesto": "Zvezdara",               // Mesto (LIKE pretraga)
  "pttBroj": "11000",                // PTT broj (tačna pretraga)
  
  // Energetski podaci
  "osnovStatusa": "SOC",             // Osnov sticanja statusa (LIKE pretraga)
  "edBroj": "ED123456",              // ED broj/broj mernog uređaja (LIKE pretraga)
  
  // Finansijski podaci
  "brojRacuna": "123-456-789",       // Broj računa (LIKE pretraga)
  "datumOd": "2024-01-01",           // Datum od (LocalDate)
  "datumDo": "2024-12-31",           // Datum do (LocalDate)
  "iznosOd": 1000,                   // Iznos od (BigDecimal)
  "iznosDo": 5000                    // Iznos do (BigDecimal)
};
```

### 🚀 T1 Frontend Implementacija

#### 1. Osnovna pretraga
```javascript
const searchT1 = async (filters, page = 0, size = 50) => {
  try {
    const response = await fetch(`/api/euk/ugrozena-lica-t1/search/filters?page=${page}&size=${size}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(filters)
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T1 search error:', error);
    throw error;
  }
};
```

#### 2. Pretraga po ED broju
```javascript
const searchT1ByEdBroj = async (edBroj) => {
  const filters = { edBroj: edBroj };
  return await searchT1(filters);
};
```

#### 3. Pretraga po imenu i prezimenu
```javascript
const searchT1ByName = async (ime, prezime) => {
  const filters = { ime: ime, prezime: prezime };
  return await searchT1(filters);
};
```

#### 4. Pretraga po kategoriji
```javascript
const searchT1ByKategorija = async (kategorijaSkracenica, page = 0, size = 50) => {
  try {
    const response = await fetch(`/api/euk/ugrozena-lica-t1/search/kategorija/${kategorijaSkracenica}?page=${page}&size=${size}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T1 kategorija search error:', error);
    throw error;
  }
};
```

### 📊 T1 Odgovor API-ja
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "redniBroj": "RB001",
      "ime": "Marko",
      "prezime": "Marković",
      "jmbg": "1234567890123",
      "pttBroj": "11000",
      "gradOpstina": "Beograd",
      "mesto": "Zvezdara",
      "ulicaIBroj": "Kralja Milana 1",
      "brojClanovaDomacinstva": 3,
      "osnovSticanjaStatusa": "SOC",
      "edBrojBrojMernogUredjaja": "ED123456",
      "potrosnjaIPovrsinaCombined": "100kW/50m²",
      "iznosUmanjenjaSaPdv": 1500.00,
      "brojRacuna": "123-456-789",
      "datumIzdavanjaRacuna": "2024-01-15",
      "datumTrajanjaPrava": "2024-12-31",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 50
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

## 📋 T2 TABELA - Ugrožena Lica T2

### 🔗 Osnovni Endpoint-i

#### 1. Lista svih T2 zapisa
```javascript
GET /api/euk/ugrozena-lica-t2?page=0&size=50
```

#### 2. Pretraga po ID-u
```javascript
GET /api/euk/ugrozena-lica-t2/{id}
```

#### 3. Pretraga po ED broju
```javascript
GET /api/euk/ugrozena-lica-t2/ed-broj/{edBroj}
```

#### 4. Pretraga po gradu/opštini
```javascript
GET /api/euk/ugrozena-lica-t2/grad-opstina/{gradOpstina}
```

#### 5. Pretraga po mestu
```javascript
GET /api/euk/ugrozena-lica-t2/mesto/{mesto}
```

### 🔍 Dostupni Filteri za T2

```javascript
const t2Filters = {
  // Osnovne informacije
  "jmbg": "1234567890123",           // JMBG (tačna pretraga)
  "redniBroj": "RB001",              // Redni broj
  "ime": "Marko",                    // Ime (LIKE pretraga)
  "prezime": "Marković",             // Prezime (LIKE pretraga)
  
  // Adresne informacije
  "gradOpstina": "Beograd",          // Grad/Opština (LIKE pretraga)
  "mesto": "Zvezdara",               // Mesto (LIKE pretraga)
  "pttBroj": "11000",                // PTT broj (tačna pretraga)
  
  // Energetski podaci
  "edBroj": "ED123456",              // ED broj (LIKE pretraga)
  "pokVazenjaResenjaOStatusu": "2024" // Period važenja rešenja (LIKE pretraga)
};
```

### 🚀 T2 Frontend Implementacija

#### 1. Osnovna pretraga
```javascript
const searchT2 = async (filters, page = 0, size = 50) => {
  try {
    const response = await fetch(`/api/euk/ugrozena-lica-t2/search/filters?page=${page}&size=${size}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(filters)
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T2 search error:', error);
    throw error;
  }
};
```

#### 2. Pretraga po ED broju
```javascript
const searchT2ByEdBroj = async (edBroj) => {
  try {
    const response = await fetch(`/api/euk/ugrozena-lica-t2/ed-broj/${edBroj}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T2 ED broj search error:', error);
    throw error;
  }
};
```

#### 3. Pretraga po gradu/opštini
```javascript
const searchT2ByGradOpstina = async (gradOpstina) => {
  try {
    const response = await fetch(`/api/euk/ugrozena-lica-t2/grad-opstina/${gradOpstina}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T2 grad/opština search error:', error);
    throw error;
  }
};
```

### 📊 T2 Odgovor API-ja
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "redniBroj": "RB001",
      "ime": "Marko",
      "prezime": "Marković",
      "jmbg": "1234567890123",
      "pttBroj": "11000",
      "gradOpstina": "Beograd",
      "mesto": "Zvezdara",
      "ulicaIBroj": "Kralja Milana 1",
      "edBroj": "ED123456",
      "pokVazenjaResenjaOStatusu": "2024-2025",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 50
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

## 🎨 React Komponente

### 1. T1 Search Komponenta
```jsx
import React, { useState } from 'react';

const T1SearchComponent = () => {
  const [filters, setFilters] = useState({
    edBroj: '',
    ime: '',
    prezime: '',
    gradOpstina: '',
    osnovStatusa: '',
    brojRacuna: ''
  });
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/euk/ugrozena-lica-t1/search/filters?page=0&size=50', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(filters)
      });
      
      const data = await response.json();
      setResults(data.content || []);
    } catch (error) {
      console.error('T1 search error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="t1-search-container">
      <h2>T1 Ugrožena Lica - Pretraga</h2>
      
      <div className="filters">
        <input
          type="text"
          placeholder="ED Broj"
          value={filters.edBroj}
          onChange={(e) => setFilters({...filters, edBroj: e.target.value})}
        />
        <input
          type="text"
          placeholder="Ime"
          value={filters.ime}
          onChange={(e) => setFilters({...filters, ime: e.target.value})}
        />
        <input
          type="text"
          placeholder="Prezime"
          value={filters.prezime}
          onChange={(e) => setFilters({...filters, prezime: e.target.value})}
        />
        <input
          type="text"
          placeholder="Grad/Opština"
          value={filters.gradOpstina}
          onChange={(e) => setFilters({...filters, gradOpstina: e.target.value})}
        />
        <input
          type="text"
          placeholder="Osnov Statusa"
          value={filters.osnovStatusa}
          onChange={(e) => setFilters({...filters, osnovStatusa: e.target.value})}
        />
        <input
          type="text"
          placeholder="Broj Računa"
          value={filters.brojRacuna}
          onChange={(e) => setFilters({...filters, brojRacuna: e.target.value})}
        />
      </div>
      
      <button onClick={handleSearch} disabled={loading}>
        {loading ? 'Pretražujem...' : 'Pretraži T1'}
      </button>
      
      <div className="results">
        {results.map((lice, index) => (
          <div key={index} className="result-item">
            <h3>{lice.ime} {lice.prezime}</h3>
            <p><strong>JMBG:</strong> {lice.jmbg}</p>
            <p><strong>ED Broj:</strong> {lice.edBrojBrojMernogUredjaja}</p>
            <p><strong>Grad:</strong> {lice.gradOpstina}</p>
            <p><strong>Osnov Statusa:</strong> {lice.osnovSticanjaStatusa}</p>
            <p><strong>Iznos:</strong> {lice.iznosUmanjenjaSaPdv} RSD</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default T1SearchComponent;
```

### 2. T2 Search Komponenta
```jsx
import React, { useState } from 'react';

const T2SearchComponent = () => {
  const [filters, setFilters] = useState({
    edBroj: '',
    ime: '',
    prezime: '',
    gradOpstina: '',
    mesto: ''
  });
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/euk/ugrozena-lica-t2/search/filters?page=0&size=50', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(filters)
      });
      
      const data = await response.json();
      setResults(data.content || []);
    } catch (error) {
      console.error('T2 search error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="t2-search-container">
      <h2>T2 Ugrožena Lica - Pretraga</h2>
      
      <div className="filters">
        <input
          type="text"
          placeholder="ED Broj"
          value={filters.edBroj}
          onChange={(e) => setFilters({...filters, edBroj: e.target.value})}
        />
        <input
          type="text"
          placeholder="Ime"
          value={filters.ime}
          onChange={(e) => setFilters({...filters, ime: e.target.value})}
        />
        <input
          type="text"
          placeholder="Prezime"
          value={filters.prezime}
          onChange={(e) => setFilters({...filters, prezime: e.target.value})}
        />
        <input
          type="text"
          placeholder="Grad/Opština"
          value={filters.gradOpstina}
          onChange={(e) => setFilters({...filters, gradOpstina: e.target.value})}
        />
        <input
          type="text"
          placeholder="Mesto"
          value={filters.mesto}
          onChange={(e) => setFilters({...filters, mesto: e.target.value})}
        />
      </div>
      
      <button onClick={handleSearch} disabled={loading}>
        {loading ? 'Pretražujem...' : 'Pretraži T2'}
      </button>
      
      <div className="results">
        {results.map((lice, index) => (
          <div key={index} className="result-item">
            <h3>{lice.ime} {lice.prezime}</h3>
            <p><strong>JMBG:</strong> {lice.jmbg}</p>
            <p><strong>ED Broj:</strong> {lice.edBroj}</p>
            <p><strong>Grad:</strong> {lice.gradOpstina}</p>
            <p><strong>Mesto:</strong> {lice.mesto}</p>
            <p><strong>Period Važenja:</strong> {lice.pokVazenjaResenjaOStatusu}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default T2SearchComponent;
```

---

## 🔄 Excel Import za T1 i T2

### T1 Import
```javascript
const importT1Excel = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('table', 'euk.ugrozeno_lice_t1'); // Ili 'tableName'
  
  const response = await fetch('/api/import/excel', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  return result;
};
```

### T2 Import
```javascript
const importT2Excel = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('table', 'euk.ugrozeno_lice_t2'); // Ili 'tableName'
  
  const response = await fetch('/api/import/excel', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  return result;
};
```

---

## 📈 Statistike

### T1 Statistike
```javascript
const getT1Statistics = async () => {
  try {
    const response = await fetch('/api/euk/ugrozena-lica-t1/statistics', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T1 statistics error:', error);
    throw error;
  }
};
```

### T2 Statistike
```javascript
const getT2Statistics = async () => {
  try {
    const response = await fetch('/api/euk/ugrozena-lica-t2/statistics', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('T2 statistics error:', error);
    throw error;
  }
};
```

---

## ⚠️ Važne napomene

### T1 Tabela
- **ED broj polje:** `edBrojBrojMernogUredjaja`
- **Finansijski podaci:** Iznos, broj računa, datumi
- **Energetski podaci:** ED broj, osnov sticanja statusa
- **Adresni podaci:** Grad, mesto, PTT broj

### T2 Tabela
- **ED broj polje:** `edBroj`
- **Energetski podaci:** ED broj, period važenja rešenja
- **Adresni podaci:** Grad, mesto, PTT broj
- **Nema finansijskih podataka**

### Opšte napomene
- **Paginacija:** Sve pretrage podržavaju `page` i `size` parametre
- **Sortiranje:** Po ID-u opadajuće (najnoviji prvi)
- **LIKE pretraga:** Ime, prezime, grad, mesto, ED broj
- **Tačna pretraga:** JMBG, redni broj, PTT broj
- **Autorizacija:** Potreban Bearer token u header-u

---

**Status:** ✅ Sve funkcionalnosti su potpuno obezbeđene
**Datum:** 2025-01-03
**Verzija:** 1.0
