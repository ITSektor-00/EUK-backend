# 🔧 Frontend Import Fix - Instrukcije za Frontend Tim

## 🚨 Problem
Backend je vraćao grešku 500 "Doslo je do greske na serveru" jer je bio nekompatibilan sa frontend parametrima.

## ✅ Rešenje
Backend je sada ažuriran da prihvata **oba parametra** za kompatibilnost:
- `table` (originalni parametar)
- `tableName` (novi parametar koji frontend koristi)

## 📋 Šta je popravljeno u backend-u

### ImportController.java
```java
// STARO (neispravno):
@RequestParam("table") String tableName

// NOVO (ispravno):
@RequestParam(value = "table", required = false) String table,
@RequestParam(value = "tableName", required = false) String tableName
```

## 🎯 Frontend instrukcije

### 1. Proverite koji parametar koristite

**Opcija A: Koristite `table` (preporučeno)**
```javascript
const formData = new FormData();
formData.append('file', excelFile);
formData.append('table', selectedTable); // ✅ ISPRAVNO
```

**Opcija B: Koristite `tableName` (takođe radi)**
```javascript
const formData = new FormData();
formData.append('file', excelFile);
formData.append('tableName', selectedTable); // ✅ TAKOĐE RADI
```

### 2. Kompletna implementacija

```javascript
const handleImport = async (file, selectedTable) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('table', selectedTable); // Ili 'tableName'
    
    const response = await fetch('/api/import/excel', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    
    if (result.status === 'SUCCESS') {
      console.log('Import uspešan!', result);
      // Refresh podataka
    } else {
      console.error('Import greška:', result.message);
      alert('Greška pri importu: ' + result.message);
    }
  } catch (error) {
    console.error('Import error:', error);
    alert('Greška pri importu!');
  }
};
```

### 3. Validacija endpoint

```javascript
const validateFile = async (file, selectedTable) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('table', selectedTable); // Ili 'tableName'
    
    const response = await fetch('/api/import/excel/validate', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Validation error:', error);
    return { status: 'ERROR', message: 'Greška pri validaciji' };
  }
};
```

## 📊 Dostupne tabele za import

```javascript
const getAvailableTables = async () => {
  try {
    const response = await fetch('/api/import/tables');
    const result = await response.json();
    return result.tables; // ['euk.ugrozeno_lice_t1', 'euk.ugrozeno_lice_t2', ...]
  } catch (error) {
    console.error('Error loading tables:', error);
    return [];
  }
};
```

## 🔍 Testiranje

### 1. Test osnovnog importa
```javascript
// Test sa 'table' parametrom
const testImport = async () => {
  const file = document.getElementById('fileInput').files[0];
  const table = 'euk.ugrozeno_lice_t1';
  
  const formData = new FormData();
  formData.append('file', file);
  formData.append('table', table);
  
  const response = await fetch('/api/import/excel', {
    method: 'POST',
    body: formData
  });
  
  console.log('Response:', await response.json());
};
```

### 2. Test sa 'tableName' parametrom
```javascript
// Test sa 'tableName' parametrom
const testImportWithTableName = async () => {
  const file = document.getElementById('fileInput').files[0];
  const tableName = 'euk.ugrozeno_lice_t1';
  
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tableName', tableName);
  
  const response = await fetch('/api/import/excel', {
    method: 'POST',
    body: formData
  });
  
  console.log('Response:', await response.json());
};
```

## ⚠️ Važne napomene

1. **Backend sada prihvata oba parametra** - možete koristiti bilo koji
2. **Preporučujem `table`** jer je originalni parametar
3. **Greška 500 je rešena** - backend više neće vraćati "Doslo je do greske na serveru"
4. **Validacija je dodana** - backend će vratiti jasnu grešku ako nedostaje parametar

## 🚀 Sledeći koraci

1. **Testirajte import** sa postojećim frontend kodom
2. **Ako i dalje imate greške**, proverite:
   - Da li šaljete ispravan Excel fajl
   - Da li je tabela u listi dostupnih tabela
   - Da li je backend pokrenut na portu 8080
3. **Ako sve radi**, možete opciono promeniti frontend da koristi `table` umesto `tableName`

## 📞 Podrška

Ako imate problema:
1. Proverite browser console za greške
2. Proverite network tab da vidite šta se šalje
3. Proverite da li backend log-ovi pokazuju greške

---

**Status:** ✅ Backend je popravljen i spreman za testiranje
**Datum:** 2025-01-03
**Verzija:** 1.0
