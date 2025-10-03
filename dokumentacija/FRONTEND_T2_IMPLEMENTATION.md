# 📋 Frontend Implementacija za EUK Ugrožena Lica T2

## 🎯 Pregled

Kompletan vodič za implementaciju T2 tabele na frontend-u sa svim API endpoint-ima.

---

## 📡 Backend API Endpoint-i za T2

### **1. Učitavanje podataka**

#### **GET `/api/euk/ugrozena-lica-t2`**
Vraća sve T2 zapise sa paginacijom.

**Parametri:**
- `page` (default: 0) - Broj stranice
- `size` (default: 50000) - Broj zapisa po stranici

**Primer zahteva:**
```javascript
const fetchT2Data = async (page = 0, size = 50000) => {
  const response = await fetch(
    `http://localhost:8080/api/euk/ugrozena-lica-t2?page=${page}&size=${size}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return await response.json();
};
```

**Primer odgovora:**
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "redniBroj": "1",
      "ime": "Marko",
      "prezime": "Marković",
      "jmbg": "0101990123456",
      "pttBroj": "11000",
      "gradOpstina": "Beograd",
      "mesto": "Beograd",
      "ulicaIBroj": "Kneza Miloša 10",
      "createdAt": "2025-10-01T10:00:00",
      "updatedAt": "2025-10-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 50000
  },
  "totalElements": 150,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

### **2. Pretraga po imenu**

#### **GET `/api/euk/ugrozena-lica-t2/search/name`**

**Parametri:**
- `ime` (opciono) - Ime za pretragu
- `prezime` (opciono) - Prezime za pretragu
- `page` (default: 0)
- `size` (default: 50000)

**Primer:**
```javascript
const searchT2ByName = async (ime, prezime) => {
  const response = await fetch(
    `http://localhost:8080/api/euk/ugrozena-lica-t2/search/name?ime=${ime}&prezime=${prezime}`,
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return await response.json();
};
```

---

### **3. Pretraga sa filterima**

#### **POST `/api/euk/ugrozena-lica-t2/search/filters`**

**Parametri (query):**
- `page` (default: 0)
- `size` (default: 50000)

**Body (JSON):**
```json
{
  "jmbg": "0101990123456",
  "redniBroj": "1",
  "ime": "Marko",
  "prezime": "Marković",
  "gradOpstina": "Beograd",
  "mesto": "Beograd",
  "pttBroj": "11000"
}
```

**Primer:**
```javascript
const searchT2WithFilters = async (filters) => {
  const response = await fetch(
    'http://localhost:8080/api/euk/ugrozena-lica-t2/search/filters?page=0&size=50000',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(filters)
    }
  );
  return await response.json();
};
```

---

### **4. CRUD Operacije**

#### **GET `/api/euk/ugrozena-lica-t2/{id}`**
Dohvata jedno T2 lice po ID-u.

```javascript
const getT2ById = async (id) => {
  const response = await fetch(
    `http://localhost:8080/api/euk/ugrozena-lica-t2/${id}`,
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return await response.json();
};
```

#### **POST `/api/euk/ugrozena-lica-t2`**
Kreira novo T2 lice.

```javascript
const createT2 = async (data) => {
  const response = await fetch(
    'http://localhost:8080/api/euk/ugrozena-lica-t2',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(data)
    }
  );
  return await response.json();
};
```

**Primer podataka:**
```json
{
  "redniBroj": "1",
  "ime": "Marko",
  "prezime": "Marković",
  "jmbg": "0101990123456",
  "pttBroj": "11000",
  "gradOpstina": "Beograd",
  "mesto": "Beograd",
  "ulicaIBroj": "Kneza Miloša 10",
  "edBroj": "12345",
  "pokVazenjaResenjaOStatusu": "01.01.2025 - 31.12.2025"
}
```

#### **PUT `/api/euk/ugrozena-lica-t2/{id}`**
Ažurira postojeće T2 lice.

```javascript
const updateT2 = async (id, data) => {
  const response = await fetch(
    `http://localhost:8080/api/euk/ugrozena-lica-t2/${id}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(data)
    }
  );
  return await response.json();
};
```

#### **DELETE `/api/euk/ugrozena-lica-t2/{id}`**
Briše T2 lice.

```javascript
const deleteT2 = async (id) => {
  const response = await fetch(
    `http://localhost:8080/api/euk/ugrozena-lica-t2/${id}`,
    {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return response.ok;
};
```

---

### **5. Excel Import**

#### **POST `/api/import/excel`**

**Parametri:**
- `file` - Excel fajl (MultipartFile)
- `tableName` - **`euk.ugrozeno_lice_t2`** (ključna razlika!)

**Format Excel fajla:**
- **Template:** `excelTemplate/ЕУК-T2.xlsx`
- **Podaci počinju od:** Red A9 (index 9, redni broj 1)
- **Kolone (A-J):**
  - A: Redni broj
  - B: Ime
  - C: Prezime
  - D: JMBG
  - E: PTT broj
  - F: Grad/Opština
  - G: Mesto
  - H: Ulica i broj
  - I: ED broj
  - J: Pokriće važenja rešenja

**Primer:**
```javascript
const handleT2Import = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tableName', 'euk.ugrozeno_lice_t2'); // ⚠️ VAŽNO za T2!
  
  const response = await fetch('http://localhost:8080/api/import/excel', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  const result = await response.json();
  console.log('T2 Import result:', result);
  return result;
};
```

**Odgovor:**
```json
{
  "status": "SUCCESS",
  "message": "Import završen",
  "processedRecords": 150,
  "totalRecords": 150,
  "filename": "ugrozena_lica_t2.xlsx",
  "table": "euk.ugrozeno_lice_t2",
  "processingTimeMs": 5000
}
```

---

### **6. Excel Export**

#### **GET `/api/export/dynamic/t2`**
Izvozi **SVE T2 podatke** u Excel sa template-om.

```javascript
const handleT2ExportAll = async () => {
  const response = await fetch(
    'http://localhost:8080/api/export/dynamic/t2',
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  
  if (!response.ok) {
    throw new Error(`Export failed: ${response.status}`);
  }
  
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `EUK_T2_Izvestaj_${new Date().toISOString().split('T')[0]}.xlsx`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};
```

#### **POST `/api/export/dynamic/t2/filtered`**
Izvozi **filtrirane T2 podatke** (samo odabrane ID-eve).

```javascript
const handleT2ExportFiltered = async (selectedIds) => {
  const response = await fetch(
    'http://localhost:8080/api/export/dynamic/t2/filtered',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ ids: selectedIds })
    }
  );
  
  if (!response.ok) {
    throw new Error(`Export failed: ${response.status}`);
  }
  
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `EUK_T2_Filtered_${new Date().toISOString().split('T')[0]}.xlsx`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};
```

---

### **7. Statistike**

#### **GET `/api/euk/ugrozena-lica-t2/statistics`**

```javascript
const getT2Statistics = async () => {
  const response = await fetch(
    'http://localhost:8080/api/euk/ugrozena-lica-t2/statistics',
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return await response.json();
};
```

**Odgovor:**
```json
{
  "totalCount": 150,
  "uniqueJmbg": 148,
  "uniqueGradOpstina": 25,
  "uniqueMesto": 40
}
```

---

### **8. Brojanje zapisa**

#### **GET `/api/euk/ugrozena-lica-t2/count`**

```javascript
const getT2Count = async () => {
  const response = await fetch(
    'http://localhost:8080/api/euk/ugrozena-lica-t2/count',
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  const result = await response.json();
  return result.count;
};
```

---

## 🎨 Kompletna React/Next.js Komponenta za T2

```typescript
'use client';

import { useState, useEffect } from 'react';

interface UgrozenoLiceT2 {
  ugrozenoLiceId: number;
  redniBroj: string;
  ime: string;
  prezime: string;
  jmbg: string;
  pttBroj: string;
  gradOpstina: string;
  mesto: string;
  ulicaIBroj: string;
  edBroj: string;
  pokVazenjaResenjaOStatusu: string;
  createdAt: string;
  updatedAt: string;
}

export default function UgrozenaLicaT2Page() {
  const [data, setData] = useState<UgrozenoLiceT2[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  
  const API_BASE = 'http://localhost:8080';
  const token = localStorage.getItem('token');

  // Učitaj sve T2 podatke
  const fetchT2Data = async () => {
    setLoading(true);
    try {
      let allData: UgrozenoLiceT2[] = [];
      let page = 0;
      let hasMore = true;

      while (hasMore) {
        const response = await fetch(
          `${API_BASE}/api/euk/ugrozena-lica-t2?page=${page}&size=1000`,
          {
            headers: { 'Authorization': `Bearer ${token}` }
          }
        );
        
        const result = await response.json();
        allData = [...allData, ...result.content];
        
        hasMore = result.content.length === 1000;
        page++;
      }
      
      setData(allData);
      console.log(`Loaded ${allData.length} T2 records`);
    } catch (error) {
      console.error('Error fetching T2 data:', error);
    } finally {
      setLoading(false);
    }
  };

  // Import Excel
  const handleT2Import = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('tableName', 'euk.ugrozeno_lice_t2');

      const response = await fetch(`${API_BASE}/api/import/excel`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.status === 'SUCCESS') {
        alert(`Import uspešan! Uvezeno ${result.processedRecords} zapisa.`);
        fetchT2Data(); // Refresh podataka
      } else {
        alert(`Import greška: ${result.message}`);
      }
    } catch (error) {
      console.error('Import error:', error);
      alert('Greška pri importu!');
    } finally {
      setLoading(false);
    }
  };

  // Export SVE T2 podatke
  const handleExportAll = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/export/dynamic/t2`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`Export failed: ${response.status}`);
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `EUK_T2_Izvestaj_${new Date().toISOString().split('T')[0]}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      
      alert('Export uspešan!');
    } catch (error) {
      console.error('Export error:', error);
      alert('Greška pri exportu!');
    } finally {
      setLoading(false);
    }
  };

  // Export filtrirane T2 podatke
  const handleExportFiltered = async () => {
    if (selectedIds.length === 0) {
      alert('Morate selektovati bar jedan zapis!');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(
        `${API_BASE}/api/export/dynamic/t2/filtered`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ ids: selectedIds })
        }
      );

      if (!response.ok) {
        throw new Error(`Export failed: ${response.status}`);
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `EUK_T2_Filtered_${new Date().toISOString().split('T')[0]}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      
      alert(`Export uspešan! ${selectedIds.length} zapisa.`);
    } catch (error) {
      console.error('Export error:', error);
      alert('Greška pri exportu!');
    } finally {
      setLoading(false);
    }
  };

  // Pretraga sa filterima
  const handleFilterSearch = async (filters: any) => {
    setLoading(true);
    try {
      const response = await fetch(
        `${API_BASE}/api/euk/ugrozena-lica-t2/search/filters?page=0&size=50000`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(filters)
        }
      );
      
      const result = await response.json();
      setData(result.content || []);
      console.log(`Found ${result.content?.length || 0} T2 records`);
    } catch (error) {
      console.error('Filter search error:', error);
    } finally {
      setLoading(false);
    }
  };

  // Kreiranje novog T2 zapisa
  const handleCreate = async (formData: Partial<UgrozenoLiceT2>) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/euk/ugrozena-lica-t2`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      const result = await response.json();
      
      if (response.ok) {
        alert('Lice uspešno kreirano!');
        fetchT2Data();
      } else {
        alert(`Greška: ${result.message}`);
      }
    } catch (error) {
      console.error('Create error:', error);
      alert('Greška pri kreiranju!');
    } finally {
      setLoading(false);
    }
  };

  // Ažuriranje postojećeg T2 zapisa
  const handleUpdate = async (id: number, formData: Partial<UgrozenoLiceT2>) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/euk/ugrozena-lica-t2/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      const result = await response.json();
      
      if (response.ok) {
        alert('Lice uspešno ažurirano!');
        fetchT2Data();
      } else {
        alert(`Greška: ${result.message}`);
      }
    } catch (error) {
      console.error('Update error:', error);
      alert('Greška pri ažuriranju!');
    } finally {
      setLoading(false);
    }
  };

  // Brisanje T2 zapisa
  const handleDelete = async (id: number) => {
    if (!confirm('Da li ste sigurni da želite da obrišete ovaj zapis?')) {
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/euk/ugrozena-lica-t2/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        alert('Lice uspešno obrisano!');
        fetchT2Data();
      } else {
        alert('Greška pri brisanju!');
      }
    } catch (error) {
      console.error('Delete error:', error);
      alert('Greška pri brisanju!');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchT2Data();
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">EUK Ugrožena Lica T2</h1>
      
      {/* Import dugme */}
      <div className="mb-4">
        <label className="bg-blue-500 text-white px-4 py-2 rounded cursor-pointer hover:bg-blue-600">
          📤 Import Excel T2
          <input
            type="file"
            accept=".xlsx,.xls"
            onChange={handleT2Import}
            className="hidden"
          />
        </label>
      </div>

      {/* Export dugmad */}
      <div className="mb-4 flex gap-2">
        <button
          onClick={handleExportAll}
          className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          disabled={loading}
        >
          📥 Export SVE T2 podatke
        </button>
        
        <button
          onClick={handleExportFiltered}
          className="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600"
          disabled={loading || selectedIds.length === 0}
        >
          📥 Export Selektovane ({selectedIds.length})
        </button>
      </div>

      {/* Tabela */}
      <div className="overflow-x-auto">
        <table className="min-w-full border">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">
                <input
                  type="checkbox"
                  onChange={(e) => {
                    if (e.target.checked) {
                      setSelectedIds(data.map(d => d.ugrozenoLiceId));
                    } else {
                      setSelectedIds([]);
                    }
                  }}
                />
              </th>
              <th className="border p-2">R.br</th>
              <th className="border p-2">Ime</th>
              <th className="border p-2">Prezime</th>
              <th className="border p-2">JMBG</th>
              <th className="border p-2">PTT</th>
              <th className="border p-2">Grad/Opština</th>
              <th className="border p-2">Mesto</th>
              <th className="border p-2">Ulica i broj</th>
              <th className="border p-2">ED broj</th>
              <th className="border p-2">Pokriće važenja</th>
              <th className="border p-2">Akcije</th>
            </tr>
          </thead>
          <tbody>
            {data.map((lice) => (
              <tr key={lice.ugrozenoLiceId}>
                <td className="border p-2">
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(lice.ugrozenoLiceId)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedIds([...selectedIds, lice.ugrozenoLiceId]);
                      } else {
                        setSelectedIds(selectedIds.filter(id => id !== lice.ugrozenoLiceId));
                      }
                    }}
                  />
                </td>
                <td className="border p-2">{lice.redniBroj}</td>
                <td className="border p-2">{lice.ime}</td>
                <td className="border p-2">{lice.prezime}</td>
                <td className="border p-2">{lice.jmbg}</td>
                <td className="border p-2">{lice.pttBroj}</td>
                <td className="border p-2">{lice.gradOpstina}</td>
                <td className="border p-2">{lice.mesto}</td>
                <td className="border p-2">{lice.ulicaIBroj}</td>
                <td className="border p-2">{lice.edBroj}</td>
                <td className="border p-2">{lice.pokVazenjaResenjaOStatusu}</td>
                <td className="border p-2">
                  <button
                    onClick={() => handleDelete(lice.ugrozenoLiceId)}
                    className="text-red-500 hover:text-red-700"
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {loading && <div className="mt-4">⏳ Učitavanje...</div>}
      
      <div className="mt-4 text-gray-600">
        Ukupno: {data.length} zapisa | Selektovano: {selectedIds.length}
      </div>
    </div>
  );
}
```

---

## 📊 Razlike između T1 i T2

| Aspekt | T1 | T2 |
|--------|----|----|
| **Endpoint prefix** | `/api/euk/ugrozena-lica-t1` | `/api/euk/ugrozena-lica-t2` |
| **Table name (import)** | `euk.ugrozeno_lice_t1` | `euk.ugrozeno_lice_t2` |
| **Export endpoint** | `/api/export/dynamic` | `/api/export/dynamic/t2` |
| **Template** | `ЕУК-T1.xlsx` | `ЕУК-T2.xlsx` |
| **Broj kolona** | 16 | 10 |
| **Dodatne kolone T1** | Broj članova, Osnov statusa, Potrošnja, Iznos, Datum... | - |
| **Dodatne kolone T2** | - | ED broj, Pokriće važenja rešenja |

---

## ✅ Checklist za Frontend T2

- [ ] Kreiraj `src/app/euk/ugrozena-lica-t2/page.tsx`
- [ ] Implementiraj `fetchT2Data()` funkciju
- [ ] Dodaj Import dugme za T2
- [ ] Dodaj Export SVE dugme
- [ ] Dodaj Export Selektovane dugme
- [ ] Implementiraj search sa filterima
- [ ] Dodaj CRUD forme (Create, Update, Delete)
- [ ] Dodaj paginaciju (ako treba)
- [ ] Dodaj checkbox za selekciju
- [ ] Dodaj statistike display

---

## 🔗 Povezani Endpoint-i

### **Import:**
- `POST /api/import/excel` (sa `tableName: 'euk.ugrozeno_lice_t2'`)

### **Export:**
- `GET /api/export/dynamic/t2` - Export SVE
- `POST /api/export/dynamic/t2/filtered` - Export filtrirane

### **CRUD:**
- `GET /api/euk/ugrozena-lica-t2` - Lista svih
- `GET /api/euk/ugrozena-lica-t2/{id}` - Jedan zapis
- `POST /api/euk/ugrozena-lica-t2` - Kreiranje
- `PUT /api/euk/ugrozena-lica-t2/{id}` - Ažuriranje
- `DELETE /api/euk/ugrozena-lica-t2/{id}` - Brisanje

### **Pretraga:**
- `GET /api/euk/ugrozena-lica-t2/search/name` - Po imenu/prezimenu
- `POST /api/euk/ugrozena-lica-t2/search/filters` - Napredna pretraga

### **Statistike:**
- `GET /api/euk/ugrozena-lica-t2/statistics` - Statistike
- `GET /api/euk/ugrozena-lica-t2/count` - Broj zapisa

---

## 🚀 Quick Start

1. **Kopiraj T1 komponentu** i preimenuj u T2
2. **Zameni sve endpoint-e** sa T2 verzijama
3. **Ukloni dodatne kolone** koje T2 nema (Broj članova, ED broj, itd.)
4. **Ažuriraj tableName** u import-u: `'euk.ugrozeno_lice_t2'`
5. **Ažuriraj export endpoint-e**: `/api/export/dynamic/t2`

---

## 🎯 Sve radi identično kao T1, samo sa T2 podacima!

Template lokacija: `src/main/resources/excelTemplate/ЕУК-T2.xlsx`

