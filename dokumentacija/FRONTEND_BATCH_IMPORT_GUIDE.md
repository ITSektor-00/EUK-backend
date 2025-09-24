# 🚀 Frontend Batch Import Implementation Guide

## 📋 Pregled

Ovaj guide objašnjava kako da implementirate batch import sa progress tracking-om za EUK aplikaciju.

## 🎯 Problem koji rešavamo

- **30,000+ redova** u Excel tabeli
- **Frontend puca** - previše HTTP zahteva  
- **Browser se zamrzava** - previše memorije
- **Backend preopterećen** - 30,000+ zahteva
- **Nema progress tracking** - korisnik ne zna šta se dešava

## ✅ Backend Rešenje (Implementirano)

### **Novi API Endpoints:**

#### **1. Pokreni Batch Import sa Progress Tracking-om**
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
    "pttBroj": "11000",
    "gradOpstina": "Beograd",
    "mesto": "Beograd",
    "ulicaIBroj": "Knez Mihailova 1",
    "brojClanovaDomacinstva": 3,
    "osnovSticanjaStatusa": "MP",
    "edBrojBrojMernogUredjaja": "ED001",
    "potrosnjaKwh": 2500.50,
    "zagrevanaPovrsinaM2": 75.5,
    "iznosUmanjenjaSaPdv": 5000.00,
    "brojRacuna": "RAC001",
    "datumIzdavanjaRacuna": "2024-01-15",
    "datumTrajanjaPrava": "2025-01-15"
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

#### **2. Prati Progress Batch-a**
```http
GET /api/euk/ugrozena-lica-t1/batch-progress/{batchId}
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
    "Redni broj već postoji u sistemu"
  ],
  "startTime": 1703123456789,
  "endTime": null,
  "duration": null,
  "finalMessage": "Import završen: 40 uspešno, 3 preskočeno, 2 grešaka od 100 ukupno"
}
```

#### **3. Status Vrednosti**
- `PROCESSING` - Batch se obrađuje
- `COMPLETED` - Batch završen uspešno
- `ERROR` - Batch završen sa greškom

## 🎨 Frontend Implementation

### **1. React Modal Komponenta**

Kreirajte `BatchProgressModal.jsx`:

```jsx
import React, { useState, useEffect } from 'react';

const BatchProgressModal = ({ isOpen, onClose, batchId, totalRecords }) => {
  const [progress, setProgress] = useState({
    status: 'PROCESSING',
    processedRecords: 0,
    successRecords: 0,
    skippedRecords: 0,
    errorRecords: 0,
    percentage: 0,
    currentRecord: '',
    errors: [],
    finalMessage: ''
  });

  const [isPolling, setIsPolling] = useState(false);

  useEffect(() => {
    if (isOpen && batchId) {
      startPolling();
    }
    return () => {
      setIsPolling(false);
    };
  }, [isOpen, batchId]);

  const startPolling = () => {
    setIsPolling(true);
    pollProgress();
  };

  const pollProgress = async () => {
    if (!isPolling) return;

    try {
      const response = await fetch(`/api/euk/ugrozena-lica-t1/batch-progress/${batchId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setProgress(data);

        // Ako je završeno, zaustavi polling
        if (data.status === 'COMPLETED' || data.status === 'ERROR') {
          setIsPolling(false);
          return;
        }
      }
    } catch (error) {
      console.error('Error polling progress:', error);
    }

    // Poll svakih 500ms
    if (isPolling) {
      setTimeout(pollProgress, 500);
    }
  };

  const handleClose = () => {
    setIsPolling(false);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Import EUK Lica T1</h2>
          <button 
            className="close-button" 
            onClick={handleClose}
            disabled={progress.status === 'PROCESSING'}
          >
            ×
          </button>
        </div>

        <div className="modal-body">
          {/* Status */}
          <div className="status-section">
            <div className="status-indicator">
              <div className={`status-dot ${progress.status.toLowerCase()}`} />
              <span className="status-text">
                {progress.status === 'PROCESSING' ? 'Obrađuje se...' : 
                 progress.status === 'COMPLETED' ? 'Završeno' : 'Greška'}
              </span>
            </div>
          </div>

          {/* Progress Bar */}
          <div className="progress-section">
            <div className="progress-info">
              <span>{progress.processedRecords} / {totalRecords}</span>
              <span>{progress.percentage}%</span>
            </div>
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${progress.percentage}%` }}
              />
            </div>
          </div>

          {/* Current Record */}
          {progress.currentRecord && (
            <div className="current-record">
              <span className="label">Trenutno obrađuje:</span>
              <span className="value">{progress.currentRecord}</span>
            </div>
          )}

          {/* Statistics */}
          <div className="statistics">
            <div className="stat-item success">
              <span className="stat-label">Uspešno:</span>
              <span className="stat-value">{progress.successRecords}</span>
            </div>
            <div className="stat-item skipped">
              <span className="stat-label">Preskočeno:</span>
              <span className="stat-value">{progress.skippedRecords}</span>
            </div>
            <div className="stat-item error">
              <span className="stat-label">Greške:</span>
              <span className="stat-value">{progress.errorRecords}</span>
            </div>
          </div>

          {/* Errors */}
          {progress.errors && progress.errors.length > 0 && (
            <div className="errors-section">
              <h4>Greške:</h4>
              <div className="errors-list">
                {progress.errors.slice(0, 5).map((error, index) => (
                  <div key={index} className="error-item">
                    {error}
                  </div>
                ))}
                {progress.errors.length > 5 && (
                  <div className="more-errors">
                    ... i još {progress.errors.length - 5} grešaka
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Final Message */}
          {progress.finalMessage && (
            <div className="final-message">
              <p>{progress.finalMessage}</p>
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button 
            className="close-btn" 
            onClick={handleClose}
            disabled={progress.status === 'PROCESSING'}
          >
            {progress.status === 'PROCESSING' ? 'Zatvori kada završi' : 'Zatvori'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default BatchProgressModal;
```

### **2. CSS Stilovi**

Kreirajte `BatchProgressModal.css`:

```css
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  padding: 4px;
  border-radius: 4px;
}

.close-button:hover:not(:disabled) {
  background: #f3f4f6;
  color: #374151;
}

.close-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
}

/* Status Section */
.status-section {
  margin-bottom: 24px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-dot.processing {
  background-color: #3b82f6;
}

.status-dot.completed {
  background-color: #10b981;
}

.status-dot.error {
  background-color: #ef4444;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-weight: 600;
  font-size: 1.1rem;
  color: #374151;
}

/* Progress Section */
.progress-section {
  margin-bottom: 24px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.875rem;
  color: #6b7280;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #1d4ed8);
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* Current Record */
.current-record {
  background: #f3f4f6;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  border-left: 4px solid #3b82f6;
}

.current-record .label {
  display: block;
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 4px;
}

.current-record .value {
  font-weight: 500;
  color: #374151;
}

/* Statistics */
.statistics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  border-radius: 8px;
  background: #f9fafb;
}

.stat-item.success {
  border-left: 4px solid #10b981;
}

.stat-item.skipped {
  border-left: 4px solid #f59e0b;
}

.stat-item.error {
  border-left: 4px solid #ef4444;
}

.stat-label {
  display: block;
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

/* Errors Section */
.errors-section {
  margin-bottom: 24px;
}

.errors-section h4 {
  margin: 0 0 12px 0;
  font-size: 1rem;
  color: #ef4444;
}

.errors-list {
  max-height: 200px;
  overflow-y: auto;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 12px;
}

.error-item {
  padding: 8px 0;
  border-bottom: 1px solid #fecaca;
  font-size: 0.875rem;
  color: #dc2626;
}

.error-item:last-child {
  border-bottom: none;
}

.more-errors {
  padding: 8px 0;
  font-style: italic;
  color: #9ca3af;
  text-align: center;
}

/* Final Message */
.final-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.final-message p {
  margin: 0;
  color: #166534;
  font-weight: 500;
}

/* Footer */
.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  justify-content: flex-end;
}

.close-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover:not(:disabled) {
  background: #2563eb;
}

.close-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 640px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .statistics {
    grid-template-columns: 1fr;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }
}
```

### **3. Korišćenje u Komponenti**

```jsx
import React, { useState } from 'react';
import BatchProgressModal from './BatchProgressModal';

const YourComponent = () => {
  const [showModal, setShowModal] = useState(false);
  const [batchId, setBatchId] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);

  const handleBatchImport = async (data) => {
    try {
      // Pokreni batch import
      const response = await fetch('/api/euk/ugrozena-lica-t1/batch-progress', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        const result = await response.json();
        setBatchId(result.batchId);
        setTotalRecords(result.totalRecords);
        setShowModal(true);
      }
    } catch (error) {
      console.error('Batch import failed:', error);
    }
  };

  return (
    <div>
      {/* Vaš postojeći kod */}
      
      <button onClick={() => handleBatchImport(yourData)}>
        Pokreni Batch Import
      </button>

      <BatchProgressModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        batchId={batchId}
        totalRecords={totalRecords}
      />
    </div>
  );
};
```

## 🔄 Batch Processing Logic

### **Kako radi:**
1. **Podeli podatke** na batch-ove (preporučeno: 100-1000 zapisa)
2. **Pošalji batch** na `/batch-progress` endpoint
3. **Dobij batchId** za tracking
4. **Poll progress** svakih 500ms
5. **Prikaži progress** u real-time
6. **Ponovi** za sledeći batch

### **Optimizacije:**
- **Batch size**: 100-1000 zapisa (preporučeno: 100)
- **Poll interval**: 500ms
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

1. **Kopiraj kod** u React projekat
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

- **Backend je implementiran** - samo treba frontend
- **Sve fajlove** možete kopirati direktno
- **Kompatibilno** sa postojećim kodom
- **Zero breaking changes** - samo dodaje funkcionalnost

**Sistem je spreman za production! 🚀**
