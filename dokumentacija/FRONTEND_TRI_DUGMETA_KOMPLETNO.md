# FRONTEND - Tri Dugmeta za ODBIJA SE NSP Dokument

## 🎨 UI DIZAJN - Stranica Predmeta

```
┌────────────────────────────────────────────────────────────┐
│  Predmet #123                                              │
│  Naziv: NSP Zahtev - Петар Јовановић                      │
│  Status: Активан                                           │
│  ...                                                        │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  📄 ODBIJA SE NSP Dokument                                 │
│                                                             │
│  Status: ✅ Dokument generisan 09.10.2025. u 15:32        │
│          (ODBIJA_SE_NSP_123-2025.docx)                     │
│                                                             │
│  [📤 Upload]  [🔄 Generisi Novi]  [📥 Preuzmi]            │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

---

## 🔘 DUGME 1: Upload Dokumenta (ručno)

Omogući korisniku da **ručno uploaduje** postojeći Word dokument.

### **Frontend Akcija:**
```javascript
const handleUpload = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  await axios.post(`/api/dokumenti/odbija-se-nsp/upload/${predmetId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  
  alert('Dokument uploadovan!');
  refreshDokumentInfo(); // Refresh status
};
```

---

## 🔘 DUGME 2: Generisi Novi Dokument (iz template-a)

Otvara **modal/pop-up** sa formom gde korisnik unosi sve podatke, a zatim generiše dokument.

### **Frontend Akcija:**

#### **2.1. Otvori Modal sa Formom:**
```javascript
const handleGenerisiNovi = () => {
  // Otvori modal sa formom
  setShowGenerisiModal(true);
};
```

#### **2.2. Modal/Form (sva polja):**
```jsx
<Modal show={showGenerisiModal} onHide={() => setShowGenerisiModal(false)}>
  <Modal.Header>
    <Modal.Title>🔄 Generisi ODBIJA SE NSP Dokument</Modal.Title>
  </Modal.Header>
  
  <Modal.Body>
    <Form>
      {/* SEKCIJA 1: Zaglavlje */}
      <h5>Zaglavlje</h5>
      <Form.Group>
        <Form.Label>Broj predmeta *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.brojPredmeta}
          onChange={(e) => setFormData({...formData, brojPredmeta: e.target.value})}
          placeholder="123/2025"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Datum donošenja *</Form.Label>
        <Form.Control 
          type="date" 
          value={formData.datumDonosenja}
          onChange={(e) => setFormData({...formData, datumDonosenja: e.target.value})}
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Broj ovlašćenja *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.brojOvlascenja}
          onChange={(e) => setFormData({...formData, brojOvlascenja: e.target.value})}
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Datum ovlašćenja *</Form.Label>
        <Form.Control 
          type="date" 
          value={formData.datumOvlascenja}
          onChange={(e) => setFormData({...formData, datumOvlascenja: e.target.value})}
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Ime i prezime ovlašćenog lica *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.imeIPrezimeOvlascenog}
          onChange={(e) => setFormData({...formData, imeIPrezimeOvlascenog: e.target.value})}
          placeholder="Марија Петровић"
        />
      </Form.Group>
      
      {/* SEKCIJA 2: Podnosilac zahteva */}
      <h5 className="mt-4">Podnosilac zahteva</h5>
      
      <Form.Group>
        <Form.Label>Ime i prezime podnosioca *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.imeIPrezimePodnosioca}
          onChange={(e) => setFormData({...formData, imeIPrezimePodnosioca: e.target.value})}
          placeholder="Петар Јовановић"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>JMBG *</Form.Label>
        <Form.Control 
          type="text" 
          maxLength={13}
          value={formData.jmbg}
          onChange={(e) => setFormData({...formData, jmbg: e.target.value})}
          placeholder="0101990123456"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Град *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.grad}
          onChange={(e) => setFormData({...formData, grad: e.target.value})}
          placeholder="Београд"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Улица *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.ulica}
          onChange={(e) => setFormData({...formData, ulica: e.target.value})}
          placeholder="Кнеза Милоша"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Број *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.brojStana}
          onChange={(e) => setFormData({...formData, brojStana: e.target.value})}
          placeholder="15"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Општина *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.opstina}
          onChange={(e) => setFormData({...formData, opstina: e.target.value})}
          placeholder="Савски Венац"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>ПТТ број *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.pttBroj}
          onChange={(e) => setFormData({...formData, pttBroj: e.target.value})}
          placeholder="11000"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Место становања *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.mestoStanovanja}
          onChange={(e) => setFormData({...formData, mestoStanovanja: e.target.value})}
          placeholder="Београд"
        />
      </Form.Group>
      
      {/* SEKCIJA 3: Obrazloženje */}
      <h5 className="mt-4">Obrazloženje</h5>
      
      <Form.Group>
        <Form.Label>Datum podnošenja zahteva *</Form.Label>
        <Form.Control 
          type="date" 
          value={formData.datumPodnosenja}
          onChange={(e) => setFormData({...formData, datumPodnosenja: e.target.value})}
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Osnov prava *</Form.Label>
        <Form.Select 
          value={formData.osnovPrava}
          onChange={(e) => setFormData({...formData, osnovPrava: e.target.value})}
        >
          <option value="">Izaberi...</option>
          <option value="NSP">НСП - Новчану социјалну помоћ</option>
          <option value="UNSP">УНСП - Увећану новчану социјалну помоћ</option>
          <option value="DD">ДД - Дечији додатак</option>
          <option value="UDTNP">УДДНЛ - Увећани додатак за помоћ и негу другог лица</option>
        </Form.Select>
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Број чланова домаћинства *</Form.Label>
        <Form.Control 
          type="text" 
          value={formData.brojClanovaDomacinstava}
          onChange={(e) => setFormData({...formData, brojClanovaDomacinstava: e.target.value})}
          placeholder="4"
        />
      </Form.Group>
      
      {/* SEKCIJA 4: Opciona polja */}
      <h5 className="mt-4">Dodatne informacije (opciono)</h5>
      
      <Form.Group>
        <Form.Label>Приложена информација</Form.Label>
        <Form.Control 
          as="textarea" 
          rows={3}
          value={formData.prilozenaInfo}
          onChange={(e) => setFormData({...formData, prilozenaInfo: e.target.value})}
          placeholder="1. Потврда о приходима&#10;2. Извод из матичне књиге"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Службени докази</Form.Label>
        <Form.Control 
          as="textarea" 
          rows={2}
          value={formData.sluzbeniDokazi}
          onChange={(e) => setFormData({...formData, sluzbeniDokazi: e.target.value})}
          placeholder="Подаци из Пореске управе"
        />
      </Form.Group>
      
      <Form.Group>
        <Form.Label>Додатни текст</Form.Label>
        <Form.Control 
          as="textarea" 
          rows={2}
          value={formData.dodatniTekst}
          onChange={(e) => setFormData({...formData, dodatniTekst: e.target.value})}
        />
      </Form.Group>
      
      {/* SEKCIJA 5: Checkbox opcije */}
      <h5 className="mt-4">Опције</h5>
      
      <Form.Check 
        type="checkbox"
        label="в.д. (Вршилац дужности)"
        checked={formData.vrsilacDuznosti}
        onChange={(e) => setFormData({...formData, vrsilacDuznosti: e.target.checked})}
      />
      
      <Form.Check 
        type="checkbox"
        label="с.р. (Сопствене руке)"
        checked={formData.sopstveneRuke}
        onChange={(e) => setFormData({...formData, sopstveneRuke: e.target.checked})}
      />
      
      <Form.Check 
        type="checkbox"
        label="Додатак за помоћ и негу другог лица се односи на предмет"
        checked={formData.dodatakZaPomocOdnosiSe}
        onChange={(e) => setFormData({...formData, dodatakZaPomocOdnosiSe: e.target.checked})}
      />
      
      <Form.Check 
        type="checkbox"
        label="Прибавља документацију службеним путем"
        checked={formData.pribavljaDokumentacijuSluzbeno}
        onChange={(e) => setFormData({...formData, pribavljaDokumentacijuSluzbeno: e.target.checked})}
      />
    </Form>
  </Modal.Body>
  
  <Modal.Footer>
    <Button variant="secondary" onClick={() => setShowGenerisiModal(false)}>
      Otkaži
    </Button>
    <Button variant="primary" onClick={handleGenerisiDokument}>
      🔄 Generisi Dokument
    </Button>
  </Modal.Footer>
</Modal>
```

---

## 💻 JavaScript Logika:

```javascript
// DUGME 1: Upload
const handleUpload = async (file) => {
  if (!file) return;
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    await axios.post(
      `/api/dokumenti/odbija-se-nsp/upload/${predmetId}`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    
    alert('✅ Dokument uploadovan!');
    refreshDokumentInfo();
  } catch (error) {
    console.error('Greška:', error);
    alert('❌ Greška pri upload-u');
  }
};

// DUGME 2: Generisi Novi Dokument
const handleGenerisiDokument = async () => {
  try {
    // Validacija
    if (!formData.brojPredmeta || !formData.datumDonosenja || !formData.imeIPrezimePodnosioca) {
      alert('Popunite sva obavezna polja!');
      return;
    }
    
    // Validacija JMBG (13 cifara)
    if (!/^\d{13}$/.test(formData.jmbg)) {
      alert('JMBG mora imati tačno 13 cifara!');
      return;
    }
    
    // Validacija datuma (yyyy-MM-dd format)
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(formData.datumDonosenja) || 
        !dateRegex.test(formData.datumOvlascenja) ||
        !dateRegex.test(formData.datumPodnosenja)) {
      alert('Datumi moraju biti u formatu yyyy-MM-dd!');
      return;
    }
    
    // Pozovi backend endpoint
    const response = await axios.post(
      '/api/dokumenti/odbija-se-nsp/generisi',
      {
        predmetId: predmetId,  // VAŽNO: ID predmeta
        brojPredmeta: formData.brojPredmeta,
        datumDonosenja: formData.datumDonosenja,
        brojOvlascenja: formData.brojOvlascenja,
        datumOvlascenja: formData.datumOvlascenja,
        imeIPrezimeOvlascenog: formData.imeIPrezimeOvlascenog,
        imeIPrezimePodnosioca: formData.imeIPrezimePodnosioca,
        jmbg: formData.jmbg,
        grad: formData.grad,
        ulica: formData.ulica,
        brojStana: formData.brojStana,
        opstina: formData.opstina,
        pttBroj: formData.pttBroj,
        mestoStanovanja: formData.mestoStanovanja,
        datumPodnosenja: formData.datumPodnosenja,
        osnovPrava: formData.osnovPrava,
        brojClanovaDomacinstava: formData.brojClanovaDomacinstava,
        prilozenaInfo: formData.prilozenaInfo || null,
        sluzbeniDokazi: formData.sluzbeniDokazi || null,
        dodatniTekst: formData.dodatniTekst || null,
        vrsilacDuznosti: formData.vrsilacDuznosti ?? true,
        sopstveneRuke: formData.sopstveneRuke ?? true,
        dodatakZaPomocOdnosiSe: formData.dodatakZaPomocOdnosiSe ?? false,
        pribavljaDokumentacijuSluzbeno: formData.pribavljaDokumentacijuSluzbeno ?? true
      },
      { responseType: 'blob' }
    );
    
    // Preuzmi generisani dokument
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `ODBIJA_SE_NSP_${formData.brojPredmeta.replace('/', '-')}.docx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    
    alert('✅ Dokument generisan i sačuvan u bazu!');
    setShowGenerisiModal(false);
    refreshDokumentInfo();
    
  } catch (error) {
    console.error('Greška:', error);
    alert('❌ Greška pri generisanju dokumenta');
  }
};

// DUGME 3: Preuzmi Postojeći Dokument
const handleDownload = async () => {
  try {
    const response = await axios.get(
      `/api/dokumenti/odbija-se-nsp/preuzmi/${predmetId}`,
      { responseType: 'blob' }
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
    console.error('Greška:', error);
    if (error.response?.status === 404) {
      alert('❌ Dokument ne postoji');
    } else {
      alert('❌ Greška pri preuzimanju');
    }
  }
};

// Refresh info o dokumentu
const refreshDokumentInfo = async () => {
  try {
    const response = await axios.get(`/api/dokumenti/odbija-se-nsp/info/${predmetId}`);
    setDokumentInfo(response.data);
  } catch (error) {
    console.error('Greška:', error);
  }
};
```

---

## 🎯 KOMPLETNA KOMPONENTA (React):

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Modal, Button, Form } from 'react-bootstrap';

const OdbijaSeNSPDokument = ({ predmetId }) => {
  const [dokumentInfo, setDokumentInfo] = useState(null);
  const [showGenerisiModal, setShowGenerisiModal] = useState(false);
  const [formData, setFormData] = useState({
    brojPredmeta: '',
    datumDonosenja: '',
    brojOvlascenja: '',
    datumOvlascenja: '',
    imeIPrezimeOvlascenog: '',
    imeIPrezimePodnosioca: '',
    jmbg: '',
    grad: '',
    ulica: '',
    brojStana: '',
    opstina: '',
    pttBroj: '',
    mestoStanovanja: '',
    datumPodnosenja: '',
    osnovPrava: 'NSP',
    brojClanovaDomacinstava: '',
    prilozenaInfo: '',
    sluzbeniDokazi: '',
    dodatniTekst: '',
    vrsilacDuznosti: true,
    sopstveneRuke: true,
    dodatakZaPomocOdnosiSe: false,
    pribavljaDokumentacijuSluzbeno: true
  });

  // Učitaj info o dokumentu pri mount-u
  useEffect(() => {
    refreshDokumentInfo();
  }, [predmetId]);

  const refreshDokumentInfo = async () => {
    try {
      const response = await axios.get(`/api/dokumenti/odbija-se-nsp/info/${predmetId}`);
      setDokumentInfo(response.data);
    } catch (error) {
      console.error('Greška:', error);
    }
  };

  const handleUpload = async (file) => {
    if (!file) return;
    
    const formDataUpload = new FormData();
    formDataUpload.append('file', file);
    
    try {
      await axios.post(
        `/api/dokumenti/odbija-se-nsp/upload/${predmetId}`,
        formDataUpload,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
      
      alert('✅ Dokument uploadovan!');
      refreshDokumentInfo();
    } catch (error) {
      console.error('Greška:', error);
      alert('❌ Greška pri upload-u');
    }
  };

  const handleGenerisi = async () => {
    try {
      const response = await axios.post(
        '/api/dokumenti/odbija-se-nsp/generisi',
        { predmetId, ...formData },
        { responseType: 'blob' }
      );
      
      // Download
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `ODBIJA_SE_NSP_${formData.brojPredmeta.replace('/', '-')}.docx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      alert('✅ Dokument generisan!');
      setShowGenerisiModal(false);
      refreshDokumentInfo();
      
    } catch (error) {
      console.error('Greška:', error);
      alert('❌ Greška pri generisanju');
    }
  };

  const handleDownload = async () => {
    try {
      const response = await axios.get(
        `/api/dokumenti/odbija-se-nsp/preuzmi/${predmetId}`,
        { responseType: 'blob' }
      );
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', dokumentInfo.naziv || 'dokument.docx');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
    } catch (error) {
      console.error('Greška:', error);
      alert('❌ Dokument ne postoji');
    }
  };

  return (
    <div className="dokument-sekcija">
      <h4>📄 ODBIJA SE NSP Dokument</h4>
      
      {/* Status dokumenta */}
      {dokumentInfo?.postoji && (
        <div className="alert alert-success">
          ✅ Dokument: <strong>{dokumentInfo.naziv}</strong>
          <br />
          Generisan: {new Date(dokumentInfo.datum).toLocaleString('sr-RS')}
        </div>
      )}
      
      {!dokumentInfo?.postoji && (
        <div className="alert alert-warning">
          ⚠️ Dokument još nije generisan
        </div>
      )}
      
      {/* Tri dugmeta */}
      <div className="btn-group" role="group">
        {/* DUGME 1: Upload */}
        <label className="btn btn-outline-primary">
          📤 Upload
          <input
            type="file"
            accept=".doc,.docx"
            onChange={(e) => handleUpload(e.target.files[0])}
            style={{ display: 'none' }}
          />
        </label>
        
        {/* DUGME 2: Generisi */}
        <button 
          className="btn btn-outline-success" 
          onClick={() => setShowGenerisiModal(true)}
        >
          🔄 Generisi Novi
        </button>
        
        {/* DUGME 3: Preuzmi (samo ako postoji) */}
        {dokumentInfo?.postoji && (
          <button 
            className="btn btn-outline-info" 
            onClick={handleDownload}
          >
            📥 Preuzmi
          </button>
        )}
      </div>
      
      {/* Modal za generisanje */}
      <Modal show={showGenerisiModal} onHide={() => setShowGenerisiModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>🔄 Generisi ODBIJA SE NSP Dokument</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {/* SVA POLJA IZ PRIMERA IZNAD */}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowGenerisiModal(false)}>
            Otkaži
          </Button>
          <Button variant="primary" onClick={handleGenerisi}>
            🔄 Generisi i Preuzmi
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default OdbijaSeNSPDokument;
```

---

## 🎯 REZIME - 3 DUGMETA:

| Dugme | Akcija | Endpoint |
|-------|--------|----------|
| **📤 Upload** | Ručni upload Word dokumenta | `POST /api/dokumenti/odbija-se-nsp/upload/{id}` |
| **🔄 Generisi** | Otvori modal → popuni podatke → generiši iz template-a | `POST /api/dokumenti/odbija-se-nsp/generisi` |
| **📥 Preuzmi** | Download postojećeg dokumenta | `GET /api/dokumenti/odbija-se-nsp/preuzmi/{id}` |

---

## ✅ Backend je SPREMAN za sva 3 dugmeta!

Sve endpointe sam već implementirao:
- ✅ Upload endpoint
- ✅ Generisi endpoint (sa `predmetId` → čuva u bazu)
- ✅ Download endpoint
- ✅ Info endpoint (provera postojanja)

**Kompletan primer React komponente** je u:
```
dokumentacija/FRONTEND_TRI_DUGMETA_KOMPLETNO.md
```

Da li treba još nešto? 🚀
