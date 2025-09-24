# 🚀 Frontend Batch Import Implementation Guide

## 📋 Pregled

Ovaj guide objašnjava kako da implementirate batch import sa progress tracking-om za EUK aplikaciju.

## 🎯 Problem koji rešavamo

- **30,000+ redova** u Excel tabeli
- **Frontend puca** - previše HTTP zahteva
- **Browser se zamrzava** - previše memorije
- **Backend preopterećen** - 30,000+ zahteva
- **Nema progress tracking** - korisnik ne zna šta se dešava

## ✅ Rešenje

### **Backend (✅ Implementirano)**
- `POST /api/euk/ugrozena-lica-t1/batch-progress` - Pokreni batch sa progress tracking-om
- `GET /api/euk/ugrozena-lica-t1/batch-progress/{batchId}` - Prati progress
- Asinhrono procesiranje sa real-time progress updates

### **Frontend (📁 Fajlovi za implementaciju)**

#### **1. BatchProgressModal.jsx** - React komponenta za progress
```jsx
<BatchProgressModal
  isOpen={showModal}
  onClose={handleClose}
  batchId={batchId}
  totalRecords={totalRecords}
  progress={progress}
/>
```

#### **2. BatchProgressModal.css** - Stilovi za modal
- Progress bar sa animacijama
- Real-time statistics
- Error handling
- Responsive design

#### **3. BatchImportService.js** - API service
```javascript
const service = new BatchImportService();

// Pokreni import sa progress tracking-om
await service.importWithProgress(data, onProgress, batchSize);

// Prati progress batch-a
await service.trackBatchProgress(batchId, onProgress);
```

#### **4. useBatchImport.js** - React hook
```javascript
const {
  isImporting,
  progress,
  error,
  results,
  startImport,
  reset
} = useBatchImport();
```

#### **5. EukImportExample.jsx** - Primer korišćenja
- File upload
- Sample data generation
- Progress tracking
- Error handling

## 🔧 Implementacija

### **Korak 1: Dodaj fajlove u projekat**

```bash
# Kopiraj fajlove u React projekat
cp BatchProgressModal.jsx src/components/
cp BatchProgressModal.css src/components/
cp BatchImportService.js src/services/
cp useBatchImport.js src/hooks/
cp EukImportExample.jsx src/components/
```

### **Korak 2: Instaliraj dependencies**

```bash
npm install axios  # ako već nije instaliran
```

### **Korak 3: Integriši u postojeću komponentu**

```jsx
import React, { useState } from 'react';
import BatchProgressModal from './components/BatchProgressModal';
import { useBatchImport } from './hooks/useBatchImport';

const YourExistingComponent = () => {
  const [showModal, setShowModal] = useState(false);
  const [importData, setImportData] = useState([]);
  
  const {
    isImporting,
    progress,
    error,
    startImport,
    reset
  } = useBatchImport();

  const handleStartImport = async () => {
    setShowModal(true);
    
    await startImport(importData, {
      batchSize: 100,
      onProgress: (progressData) => {
        console.log('Progress:', progressData);
      },
      onComplete: (result) => {
        console.log('Completed:', result);
      },
      onError: (errorMessage) => {
        console.error('Error:', errorMessage);
      }
    });
  };

  return (
    <div>
      {/* Vaš postojeći kod */}
      
      <button onClick={handleStartImport}>
        Pokreni Import
      </button>

      <BatchProgressModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        totalRecords={importData.length}
        progress={progress}
      />
    </div>
  );
};
```

## 📊 API Endpoints

### **1. Pokreni Batch Import**
```http
POST /api/euk/ugrozena-lica-t1/batch-progress
Content-Type: application/json
Authorization: Bearer <token>

[
  {
    "redniBroj": "RB001",
    "ime": "Marko",
    "prezime": "Marković",
    "jmbg": "1234567890123",
    // ... ostala polja
  }
]
```

**Response:**
```json
{
  "message": "Batch import started",
  "batchId": "batch_1703123456789",
  "totalRecords": 100,
  "status": "PROCESSING"
}
```

### **2. Prati Progress**
```http
GET /api/euk/ugrozena-lica-t1/batch-progress/batch_1703123456789
Authorization: Bearer <token>
```

**Response:**
```json
{
  "batchId": "batch_1703123456789",
  "status": "PROCESSING",
  "totalRecords": 100,
  "processedRecords": 45,
  "successRecords": 40,
  "skippedRecords": 3,
  "errorRecords": 2,
  "percentage": 45,
  "currentRecord": "Marko Marković",
  "errors": [
    "JMBG mora sadržati tačno 13 cifara",
    "Redni broj već postoji"
  ],
  "startTime": 1703123456789,
  "endTime": null,
  "duration": null
}
```

## 🎨 UI Komponente

### **Progress Modal Features:**
- ✅ **Real-time progress bar** sa procentima
- ✅ **Current record** koji se obrađuje
- ✅ **Statistics** - uspešno, preskočeno, greške
- ✅ **Error list** sa detaljima grešaka
- ✅ **Duration tracking** - koliko traje
- ✅ **Responsive design** - radi na svim uređajima
- ✅ **Auto-close** kada završi

### **Progress Bar:**
```css
.progress-bar {
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #1d4ed8);
  transition: width 0.3s ease;
}
```

## 🔄 Batch Processing Logic

### **Kako radi:**
1. **Podeli podatke** na batch-ove (default: 1000 zapisa)
2. **Pošalji batch** na `/batch-progress` endpoint
3. **Dobij batchId** za tracking
4. **Poll progress** svakih 500ms
5. **Prikaži progress** u real-time
6. **Ponovi** za sledeći batch

### **Optimizacije:**
- **Batch size**: 100-1000 zapisa (preporučeno: 100)
- **Poll interval**: 500ms
- **Pauza između batch-ova**: 100ms
- **Error handling**: Preskače nevalidne zapise
- **Duplicate handling**: Preskače duplikate

## 📈 Performanse

### **Pre implementacije:**
- ❌ **30,000 HTTP zahteva** (po jedan za svaki zapis)
- ❌ **Browser se zamrzava**
- ❌ **Backend preopterećen**
- ❌ **Nema progress tracking**

### **Posle implementacije:**
- ✅ **30 HTTP zahteva** (30,000 ÷ 1000)
- ✅ **Browser radi normalno**
- ✅ **Backend optimizovan**
- ✅ **Real-time progress tracking**
- ✅ **Error handling i reporting**

## 🚀 Sledeći koraci

1. **Kopiraj fajlove** u React projekat
2. **Testiraj** sa malim batch-om (10-100 zapisa)
3. **Integriši** u postojeću komponentu
4. **Testiraj** sa velikim batch-om (1000+ zapisa)
5. **Deploy** na production

## 🐛 Troubleshooting

### **Česti problemi:**

1. **Modal se ne zatvara**
   - Proveri da li je `progress.status === 'COMPLETED'`
   - Dodaj `onClose` handler

2. **Progress se ne ažurira**
   - Proveri da li je `batchId` ispravan
   - Proveri network tab u browser-u

3. **Import se zaustavlja**
   - Proveri backend logove
   - Proveri da li su podaci validni

4. **Memory issues**
   - Smanji `batchSize` (100 umesto 1000)
   - Dodaj `useMemo` za velike podatke

## 📝 Napomene

- **Backend je već implementiran** - samo treba frontend
- **Sve fajlove** možete kopirati direktno
- **Kompatibilno** sa postojećim kodom
- **Zero breaking changes** - samo dodaje funkcionalnost

**Sistem je spreman za production! 🚀**

