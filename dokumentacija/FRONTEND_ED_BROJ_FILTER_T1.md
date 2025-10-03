# 🔍 ED Broj Filter za T1 Tabelu - Frontend Instrukcije

## ✅ Status
**ED broj filter je POTPUNO OBEZBEĐEN** u backend-u za T1 tabelu!

## 🎯 Dostupni Filteri za T1 Tabelu

### 1. Kompleksna pretraga sa filterima
**Endpoint:** `POST /api/euk/ugrozena-lica-t1/search/filters`

**Parametri:**
```javascript
{
  "jmbg": "string",           // JMBG
  "redniBroj": "string",      // Redni broj
  "ime": "string",           // Ime
  "prezime": "string",       // Prezime
  "gradOpstina": "string",   // Grad/Opština
  "mesto": "string",         // Mesto
  "pttBroj": "string",       // PTT broj
  "osnovStatusa": "string",  // Osnov sticanja statusa
  "edBroj": "string",        // 🎯 ED BROJ FILTER
  "brojRacuna": "string",    // Broj računa
  "datumOd": "2024-01-01",   // Datum od
  "datumDo": "2024-12-31",   // Datum do
  "iznosOd": 1000,           // Iznos od
  "iznosDo": 5000            // Iznos do
}
```

## 🚀 Frontend Implementacija

### 1. Osnovna pretraga po ED broju
```javascript
const searchByEdBroj = async (edBroj, page = 0, size = 50) => {
  try {
    const filters = {
      edBroj: edBroj
    };
    
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
    console.error('Error searching by ED broj:', error);
    throw error;
  }
};
```

### 2. Kombinovana pretraga (ED broj + ostali filteri)
```javascript
const searchWithMultipleFilters = async (searchParams) => {
  try {
    const filters = {
      edBroj: searchParams.edBroj,
      ime: searchParams.ime,
      prezime: searchParams.prezime,
      gradOpstina: searchParams.gradOpstina,
      osnovStatusa: searchParams.osnovStatusa,
      // ... ostali filteri
    };
    
    const response = await fetch('/api/euk/ugrozena-lica-t1/search/filters?page=0&size=50', {
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
    console.error('Error searching with filters:', error);
    throw error;
  }
};
```

### 3. React komponenta sa ED broj filterom
```jsx
import React, { useState } from 'react';

const T1SearchComponent = () => {
  const [filters, setFilters] = useState({
    edBroj: '',
    ime: '',
    prezime: '',
    gradOpstina: '',
    osnovStatusa: ''
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
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="search-container">
      <h2>Pretraga T1 Ugroženih Lica</h2>
      
      {/* ED Broj Filter */}
      <div className="form-group">
        <label htmlFor="edBroj">ED Broj:</label>
        <input
          id="edBroj"
          type="text"
          value={filters.edBroj}
          onChange={(e) => setFilters({...filters, edBroj: e.target.value})}
          placeholder="Unesite ED broj..."
        />
      </div>
      
      {/* Ostali filteri */}
      <div className="form-group">
        <label htmlFor="ime">Ime:</label>
        <input
          id="ime"
          type="text"
          value={filters.ime}
          onChange={(e) => setFilters({...filters, ime: e.target.value})}
          placeholder="Unesite ime..."
        />
      </div>
      
      <div className="form-group">
        <label htmlFor="prezime">Prezime:</label>
        <input
          id="prezime"
          type="text"
          value={filters.prezime}
          onChange={(e) => setFilters({...filters, prezime: e.target.value})}
          placeholder="Unesite prezime..."
        />
      </div>
      
      <button onClick={handleSearch} disabled={loading}>
        {loading ? 'Pretražujem...' : 'Pretraži'}
      </button>
      
      {/* Rezultati */}
      <div className="results">
        {results.map((lice, index) => (
          <div key={index} className="result-item">
            <p><strong>Ime:</strong> {lice.ime} {lice.prezime}</p>
            <p><strong>JMBG:</strong> {lice.jmbg}</p>
            <p><strong>ED Broj:</strong> {lice.edBrojBrojMernogUredjaja}</p>
            <p><strong>Grad/Opština:</strong> {lice.gradOpstina}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default T1SearchComponent;
```

## 📊 Odgovor API-ja

### Uspešan odgovor:
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "ime": "Marko",
      "prezime": "Marković",
      "jmbg": "1234567890123",
      "edBrojBrojMernogUredjaja": "ED123456",
      "gradOpstina": "Beograd",
      "osnovSticanjaStatusa": "SOC",
      "iznosUmanjenjaSaPdv": 1500.00
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

## 🔍 Tipovi pretrage

### 1. Tačna pretraga po ED broju
```javascript
// Traži samo one sa tačno tim ED brojem
const filters = { edBroj: "ED123456" };
```

### 2. Parcijalna pretraga po ED broju
```javascript
// Traži sve koji sadrže "123" u ED broju
const filters = { edBroj: "123" };
```

### 3. Kombinovana pretraga
```javascript
// Traži po ED broju + imenu + prezimenu
const filters = {
  edBroj: "ED123",
  ime: "Marko",
  prezime: "Marković"
};
```

## ⚠️ Važne napomene

1. **ED broj filter radi sa LIKE pretragom** - može da traži i parcijalne rezultate
2. **Case insensitive** - ne pravi razliku između velikih i malih slova
3. **Može se kombinovati sa ostalim filterima** - svi filteri rade zajedno
4. **Paginacija je podržana** - koristite `page` i `size` parametre
5. **Sortiranje je po ID-u opadajuće** - najnoviji zapisi prvi

## 🚀 Testiranje

### 1. Test osnovne pretrage
```javascript
// Test sa ED brojem
const testEdBrojSearch = async () => {
  const filters = { edBroj: "ED123" };
  
  const response = await fetch('/api/euk/ugrozena-lica-t1/search/filters?page=0&size=10', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(filters)
  });
  
  const result = await response.json();
  console.log('ED Broj search results:', result);
};
```

### 2. Test kombinovane pretrage
```javascript
// Test sa više filtera
const testCombinedSearch = async () => {
  const filters = {
    edBroj: "ED",
    ime: "Marko",
    gradOpstina: "Beograd"
  };
  
  const response = await fetch('/api/euk/ugrozena-lica-t1/search/filters?page=0&size=10', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(filters)
  });
  
  const result = await response.json();
  console.log('Combined search results:', result);
};
```

## 📞 Podrška

Ako imate problema:
1. Proverite da li je backend pokrenut
2. Proverite da li šaljete ispravan JSON
3. Proverite da li imate Authorization header
4. Proverite browser console za greške

---

**Status:** ✅ ED broj filter je potpuno funkcionalan
**Datum:** 2025-01-03
**Verzija:** 1.0
