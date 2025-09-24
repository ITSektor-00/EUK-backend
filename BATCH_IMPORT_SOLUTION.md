# BATCH IMPORT SOLUTION

## 🚨 **Problem**
- **30,000+ redova** u Excel tabeli
- **Frontend puca** - previše HTTP zahteva
- **Browser se zamrzava** - previše memorije
- **Backend preopterećen** - 30,000+ zahteva

## ✅ **Rešenje - Batch Endpoint**

### **Novi Backend Endpoint:**
```
POST /api/euk/ugrozena-lica-t1/batch
```

### **Kako radi:**
1. **Prima niz podataka** (do 1000 odjednom)
2. **Validira sve odjednom**
3. **Čuva sve odjednom** (`saveAll`)
4. **Vraća rezultate**

## 📊 **Frontend Strategija**

### **Opcija 1: Batch Processing (Preporučeno)**
```javascript
// Podeli 30,000 redova na batch-ove od 1000
const batchSize = 1000;
const totalRows = 30000;
const batches = Math.ceil(totalRows / batchSize);

for (let i = 0; i < batches; i++) {
  const start = i * batchSize;
  const end = Math.min(start + batchSize, totalRows);
  const batch = excelData.slice(start, end);
  
  try {
    const response = await fetch('/api/euk/ugrozena-lica-t1/batch', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(batch)
    });
    
    const result = await response.json();
    console.log(`Batch ${i + 1}/${batches}: ${result.totalProcessed} records processed`);
    
    // Pauza između batch-ova
    await new Promise(resolve => setTimeout(resolve, 100));
  } catch (error) {
    console.error(`Batch ${i + 1} failed:`, error);
  }
}
```

### **Opcija 2: Progress Bar**
```javascript
// Dodaj progress bar za korisnika
const processBatch = async (batch, batchNumber, totalBatches) => {
  // Update progress
  setProgress((batchNumber / totalBatches) * 100);
  
  // Process batch
  const response = await api.batchImport(batch);
  
  // Update status
  setStatus(`Processed ${batchNumber}/${totalBatches} batches`);
};
```

## 🔧 **Backend Features**

### **Validacija:**
- **Maksimalno 1000 zapisa** po batch-u
- **Duplikati se preskaču** (JMBG/Redni broj)
- **Nevalidni zapisi se preskaču**
- **Loguje greške** ali ne zaustavlja proces

### **Performanse:**
- **`saveAll()`** - brže od pojedinačnih `save()`
- **Transakcija** - sve ili ništa
- **Batch processing** - optimizovano za velike količine

## 📈 **Rezultat**

### **Pre:**
- ❌ **30,000 HTTP zahteva**
- ❌ **Browser se zamrzava**
- ❌ **Backend preopterećen**
- ❌ **Import traje satima**

### **Posle:**
- ✅ **30 HTTP zahteva** (30,000 ÷ 1000)
- ✅ **Browser radi normalno**
- ✅ **Backend optimizovan**
- ✅ **Import traje minuta**

## 🚀 **Implementacija**

### **1. Backend (✅ Gotovo)**
- `POST /api/euk/ugrozena-lica-t1/batch` endpoint
- `createBatch()` metoda u servisu
- Validacija i error handling

### **2. Frontend (Treba implementirati)**
```javascript
// Umesto:
for (const row of excelData) {
  await api.create(row); // 30,000 zahteva
}

// Koristi:
const batchSize = 1000;
for (let i = 0; i < excelData.length; i += batchSize) {
  const batch = excelData.slice(i, i + batchSize);
  await api.batchImport(batch); // 30 zahteva
}
```

## 📝 **Datum promene**

**Datum**: $(date)
**Verzija**: 1.0
**Status**: ✅ Backend implementiran, Frontend treba ažurirati
