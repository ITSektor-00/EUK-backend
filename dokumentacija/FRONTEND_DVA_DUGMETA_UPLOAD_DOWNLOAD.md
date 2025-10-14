# FRONTEND - Upload i Download Dokumenta

## 📋 SCENARIO: Stranica Postojećeg Predmeta

Kada korisnik uđe na stranicu postojećeg predmeta, treba prikazati **DVA DUGMETA**:

```
┌──────────────────────────────────────┐
│  Predmet #123                        │
│  Naziv: NSP Zahtev...                │
│  Status: Активан                     │
│  ...                                  │
├──────────────────────────────────────┤
│                                       │
│  📄 ODBIJA SE NSP Dokument:          │
│                                       │
│  [📤 Upload Dokument]  [📥 Preuzmi]  │
│                                       │
└──────────────────────────────────────┘
```

---

## 🔍 **KORAK 0: Proveri da li dokument postoji**

Kada se stranica učita, prvo proveri da li dokument već postoji.

### **URL:**
```
GET /api/dokumenti/odbija-se-nsp/info/{predmetId}
```

### **Response:**
```json
{
  "postoji": true,
  "naziv": "ODBIJA_SE_NSP_123-2025.docx",
  "datum": "2025-10-09T15:20:00"
}
```

Ako je `postoji: false`, prikaži samo **Upload** dugme.  
Ako je `postoji: true`, prikaži **oba dugmeta**.

---

## 📤 **DUGME 1: Upload Dokumenta**

Omogući korisniku da uploaduje Word dokument (.doc ili .docx).

### **URL:**
```
POST /api/dokumenti/odbija-se-nsp/upload/{predmetId}
```

### **Request:**
- **Content-Type**: `multipart/form-data`
- **Body**: Fajl sa key-em `file`

### **Response:**
- **200 OK**: `"Dokument uspešno sačuvan"`
- **400 Bad Request**: `"Fajl je prazan"` ili `"Dozvoljeni su samo Word dokumenti"`
- **404 Not Found**: Predmet ne postoji

---

## 📥 **DUGME 2: Preuzmi Dokument**

Preuzmi sačuvani dokument iz baze.

### **URL:**
```
GET /api/dokumenti/odbija-se-nsp/preuzmi/{predmetId}
```

### **Response:**
- **200 OK**: Binary Word dokument (.docx)
- **404 Not Found**: Dokument ne postoji ili nije uploadovan

---

## 💻 FRONTEND KOD - JavaScript/React

### **Primer sa Axios:**

```javascript
import axios from 'axios';
import { useState, useEffect } from 'react';

const PredmetDokument = ({ predmetId }) => {
  const [dokumentInfo, setDokumentInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  // KORAK 0: Proveri da li dokument postoji
  useEffect(() => {
    const proveriDokument = async () => {
      try {
        const response = await axios.get(
          `/api/dokumenti/odbija-se-nsp/info/${predmetId}`
        );
        setDokumentInfo(response.data);
      } catch (error) {
        console.error('Greška pri proveri dokumenta:', error);
      } finally {
        setLoading(false);
      }
    };

    proveriDokument();
  }, [predmetId]);

  // DUGME 1: Upload dokumenta
  const handleUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post(
        `/api/dokumenti/odbija-se-nsp/upload/${predmetId}`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );

      alert('Dokument uspešno uploadovan!');
      
      // Refresh info
      const infoResponse = await axios.get(
        `/api/dokumenti/odbija-se-nsp/info/${predmetId}`
      );
      setDokumentInfo(infoResponse.data);

    } catch (error) {
      console.error('Greška pri upload-u:', error);
      alert('Greška pri upload-u dokumenta');
    }
  };

  // DUGME 2: Download dokumenta
  const handleDownload = async () => {
    try {
      const response = await axios.get(
        `/api/dokumenti/odbija-se-nsp/preuzmi/${predmetId}`,
        {
          responseType: 'blob'
        }
      );

      // Preuzmi fajl
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', dokumentInfo.naziv || 'dokument.docx');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error('Greška pri download-u:', error);
      if (error.response?.status === 404) {
        alert('Dokument ne postoji');
      } else {
        alert('Greška pri preuzimanju dokumenta');
      }
    }
  };

  if (loading) {
    return <div>Učitavanje...</div>;
  }

  return (
    <div className="dokument-sekcija">
      <h3>📄 ODBIJA SE NSP Dokument</h3>
      
      {dokumentInfo?.postoji && (
        <div className="dokument-info">
          <p>Naziv: {dokumentInfo.naziv}</p>
          <p>Datum: {new Date(dokumentInfo.datum).toLocaleString('sr-RS')}</p>
        </div>
      )}

      <div className="dugmici">
        {/* UPLOAD DUGME */}
        <label className="btn btn-primary">
          📤 Upload Dokument
          <input
            type="file"
            accept=".doc,.docx"
            onChange={handleUpload}
            style={{ display: 'none' }}
          />
        </label>

        {/* DOWNLOAD DUGME - prikaži samo ako dokument postoji */}
        {dokumentInfo?.postoji && (
          <button 
            className="btn btn-success" 
            onClick={handleDownload}
          >
            📥 Preuzmi Dokument
          </button>
        )}
      </div>
    </div>
  );
};

export default PredmetDokument;
```

---

## 🎨 VANILLA JAVASCRIPT (bez React):

```html
<div id="dokumentSekcija">
  <h3>📄 ODBIJA SE NSP Dokument</h3>
  
  <div id="dokumentInfo" style="display: none;">
    <p>Naziv: <span id="nazivDokumenta"></span></p>
    <p>Datum: <span id="datumDokumenta"></span></p>
  </div>
  
  <div>
    <label class="btn btn-primary">
      📤 Upload Dokument
      <input type="file" id="fileInput" accept=".doc,.docx" style="display: none;">
    </label>
    
    <button id="downloadBtn" class="btn btn-success" style="display: none;">
      📥 Preuzmi Dokument
    </button>
  </div>
</div>

<script>
const predmetId = 123; // Dobij iz URL-a ili konteksta

// KORAK 0: Proveri da li dokument postoji
async function proveriDokument() {
  try {
    const response = await fetch(`/api/dokumenti/odbija-se-nsp/info/${predmetId}`);
    const data = await response.json();
    
    if (data.postoji) {
      document.getElementById('dokumentInfo').style.display = 'block';
      document.getElementById('nazivDokumenta').textContent = data.naziv;
      document.getElementById('datumDokumenta').textContent = new Date(data.datum).toLocaleString('sr-RS');
      document.getElementById('downloadBtn').style.display = 'inline-block';
    }
  } catch (error) {
    console.error('Greška:', error);
  }
}

// DUGME 1: Upload
document.getElementById('fileInput').addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (!file) return;
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await fetch(`/api/dokumenti/odbija-se-nsp/upload/${predmetId}`, {
      method: 'POST',
      body: formData
    });
    
    if (response.ok) {
      alert('Dokument uspešno uploadovan!');
      proveriDokument(); // Refresh
    } else {
      alert('Greška pri upload-u');
    }
  } catch (error) {
    console.error('Greška:', error);
  }
});

// DUGME 2: Download
document.getElementById('downloadBtn').addEventListener('click', async () => {
  try {
    const response = await fetch(`/api/dokumenti/odbija-se-nsp/preuzmi/${predmetId}`);
    
    if (!response.ok) {
      alert('Dokument ne postoji');
      return;
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = document.getElementById('nazivDokumenta').textContent || 'dokument.docx';
    link.click();
    window.URL.revokeObjectURL(url);
    
  } catch (error) {
    console.error('Greška:', error);
  }
});

// Pozovi na load
proveriDokument();
</script>
```

---

## ✅ VALIDACIJA

Backend validira:
- ✅ Fajl ne sme biti prazan
- ✅ Dozvoljeni tipovi: `.doc` i `.docx`
- ✅ Predmet mora postojati u bazi

---

## 🎯 KOMPLETAN ENDPOINT PREGLED

| Endpoint | Method | Opis |
|----------|--------|------|
| `/api/dokumenti/odbija-se-nsp/info/{id}` | GET | Proveri da li dokument postoji |
| `/api/dokumenti/odbija-se-nsp/upload/{id}` | POST | Upload dokumenta |
| `/api/dokumenti/odbija-se-nsp/preuzmi/{id}` | GET | Download dokumenta |
| `/api/dokumenti/odbija-se-nsp/generisi` | POST | Generiši novi dokument |

---

## 🚀 DEPLOYMENT CHECKLIST

- [x] SQL migracija pokrenuta (`add_odbija_se_nsp_dokument_column.sql`)
- [x] Backend kompajliran
- [x] Endpoint-i dostupni
- [ ] Frontend implementira upload dugme
- [ ] Frontend implementira download dugme
- [ ] Frontend implementira proveru postojanja dokumenta

---

## 📌 NAPOMENE

1. ✅ Dokument se čuva kao **BYTEA** u PostgreSQL (do ~1GB)
2. ✅ Upload prihvata **samo Word dokumente** (.doc, .docx)
3. ✅ Korisnik može **prepisati** postojeći dokument novim upload-om
4. ✅ **Info endpoint** omogućava frontend-u da prikaže status (postoji/ne postoji)

