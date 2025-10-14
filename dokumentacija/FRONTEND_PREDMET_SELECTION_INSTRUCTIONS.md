# 🎯 Frontend Instrukcije - Izbor Predmeta za Štampanje Koverte

## 📋 Pregled Funkcionalnosti

Implementirana je funkcionalnost za izbor predmeta pre štampanja zadnje strane koverte. Korisnik može da bira između dva načina:

1. **Izbor iz tabele predmeta** - dropdown sa filterom
2. **Ručni unos** - tekst polje za slobodan unos

## 🔧 Backend Endpoint-i

### 1. **Dohvatanje Naziva Predmeta**
```
GET /api/euk/predmeti/nazivi
```
**Response:**
```json
[
  "Građansko pravo - Spor o imovini",
  "Krivično pravo - Ubistvo",
  "Porodično pravo - Razvod braka",
  "Upravno pravo - Žalba na odluku"
]
```

### 2. **Filter Predmeta po Nazivu**
```
GET /api/euk/predmeti?nazivPredmeta=građansko&page=0&size=10
```
**Response:**
```json
{
  "content": [
    {
      "predmetId": 1,
      "nazivPredmeta": "Građansko pravo - Spor o imovini",
      "status": "AKTIVAN",
      "odgovornaOsoba": "Marko Petrović",
      "prioritet": "VISOK",
      "kategorijaNaziv": "Građansko pravo",
      "kategorijaSkracenica": "GRP"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 3. **Štampanje Zadnje Strane Koverte - Single**
```
POST /api/generate-single-envelope-back-side-pdf
```
**Request Body:**
```json
{
  "template": "T1",
  "ugrozenaLica": [
    {
      "ime": "Marko",
      "prezime": "Petrović",
      "ulicaIBroj": "Knez Mihailova 15"
    }
  ],
  "nazivPredmeta": "Građansko pravo - Spor o imovini"
}
```
**Response:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="koverte-zadnja-strana-t1.pdf"

[PDF bytes - jedan fajl]
```

### 4. **Štampanje Zadnje Strane Koverte - Legacy (Deprecated)**
```
POST /api/generate-envelope-back-side-pdf
```
**Napomena:** Ovaj endpoint je zastareo, koristiti `/api/generate-single-envelope-back-side-pdf`

### 5. **Batch Štampanje - Jedan PDF sa Više Stranica**
```
POST /api/generate-batch-envelope-back-side-pdf
```
**Request Body:**
```json
{
  "requests": [
    {
      "template": "T1",
      "ugrozenaLica": [
        {
          "ime": "Marko",
          "prezime": "Petrović",
          "ulicaIBroj": "Knez Mihailova 15"
        }
      ],
      "nazivPredmeta": "Građansko pravo - Spor o imovini"
    },
    {
      "template": "T2",
      "ugrozenaLica": [
        {
          "ime": "Ana",
          "prezime": "Marković",
          "ulicaIBroj": "Terazije 5"
        }
      ],
      "nazivPredmeta": "Krivično pravo - Ubistvo"
    }
  ]
}
```
**Response:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="koverte-zadnja-strana-batch.pdf"

[PDF bytes - jedan fajl sa više stranica]
```

**Napomena:** Svako lice je na posebnoj strani u istom PDF-u. Ako imaš 3 zahteva sa po 2 lica, PDF će imati 6 stranica.

## 🎨 Frontend Implementacija

### 1. **Popup Modal Komponenta**

Kreirati modal komponentu `PredmetSelectionModal.jsx`:

```jsx
import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Alert } from 'react-bootstrap';

const PredmetSelectionModal = ({ show, onHide, onConfirm, ugrozenaLica, template }) => {
  const [selectionType, setSelectionType] = useState('database'); // 'database' ili 'manual'
  const [selectedPredmet, setSelectedPredmet] = useState('');
  const [manualPredmet, setManualPredmet] = useState('');
  const [predmetiOptions, setPredmetiOptions] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredPredmeti, setFilteredPredmeti] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Učitaj nazive predmeta prilikom otvaranja modala
  useEffect(() => {
    if (show) {
      fetchPredmetiOptions();
    }
  }, [show]);

  // Filter predmeta na osnovu pretrage
  useEffect(() => {
    if (searchTerm) {
      const filtered = predmetiOptions.filter(predmet => 
        predmet.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredPredmeti(filtered);
    } else {
      setFilteredPredmeti(predmetiOptions);
    }
  }, [searchTerm, predmetiOptions]);

  const fetchPredmetiOptions = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/euk/predmeti/nazivi');
      if (response.ok) {
        const data = await response.json();
        setPredmetiOptions(data);
        setFilteredPredmeti(data);
      } else {
        setError('Greška pri učitavanju predmeta');
      }
    } catch (err) {
      setError('Greška pri učitavanju predmeta: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirm = () => {
    if (selectionType === 'database' && !selectedPredmet) {
      setError('Molimo izaberite predmet iz liste');
      return;
    }
    if (selectionType === 'manual' && !manualPredmet.trim()) {
      setError('Molimo unesite naziv predmeta');
      return;
    }

    const nazivPredmeta = selectionType === 'database' ? selectedPredmet : manualPredmet;
    onConfirm(nazivPredmeta);
  };

  const handleClose = () => {
    setSelectedPredmet('');
    setManualPredmet('');
    setSearchTerm('');
    setError('');
    onHide();
  };

  return (
    <Modal show={show} onHide={handleClose} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>🎯 Izbor Predmeta za Štampanje</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {error && <Alert variant="danger">{error}</Alert>}
        
        <Form>
          <Row className="mb-3">
            <Col>
              <Form.Label><strong>Način izbora predmeta:</strong></Form.Label>
            </Col>
          </Row>
          
          <Row className="mb-3">
            <Col>
              <Form.Check
                type="radio"
                id="database-option"
                name="selectionType"
                label="📋 Izaberite iz tabele predmeta"
                checked={selectionType === 'database'}
                onChange={() => setSelectionType('database')}
              />
            </Col>
          </Row>
          
          <Row className="mb-3">
            <Col>
              <Form.Check
                type="radio"
                id="manual-option"
                name="selectionType"
                label="✏️ Unesite ručno"
                checked={selectionType === 'manual'}
                onChange={() => setSelectionType('manual')}
              />
            </Col>
          </Row>

          {selectionType === 'database' && (
            <>
              <Row className="mb-3">
                <Col>
                  <Form.Label>Pretraži predmete:</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Unesite naziv predmeta za pretragu..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </Col>
              </Row>
              
              <Row className="mb-3">
                <Col>
                  <Form.Label>Izaberite predmet:</Form.Label>
                  <Form.Select
                    value={selectedPredmet}
                    onChange={(e) => setSelectedPredmet(e.target.value)}
                    disabled={loading}
                  >
                    <option value="">-- Izaberite predmet --</option>
                    {filteredPredmeti.map((predmet, index) => (
                      <option key={index} value={predmet}>
                        {predmet}
                      </option>
                    ))}
                  </Form.Select>
                  {loading && <small className="text-muted">Učitavanje predmeta...</small>}
                </Col>
              </Row>
            </>
          )}

          {selectionType === 'manual' && (
            <Row className="mb-3">
              <Col>
                <Form.Label>Unesite naziv predmeta:</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Unesite naziv predmeta..."
                  value={manualPredmet}
                  onChange={(e) => setManualPredmet(e.target.value)}
                />
              </Col>
            </Row>
          )}
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          Otkaži
        </Button>
        <Button variant="primary" onClick={handleConfirm}>
          Potvrdi i Štampaj
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default PredmetSelectionModal;
```

### 2. **Glavna Komponenta za Štampanje**

Ažurirati postojeću komponentu za štampanje:

```jsx
import React, { useState } from 'react';
import PredmetSelectionModal from './PredmetSelectionModal';

const StampanjeKoverte = () => {
  const [showPredmetModal, setShowPredmetModal] = useState(false);
  const [ugrozenaLica, setUgrozenaLica] = useState([]);
  const [template, setTemplate] = useState('T1');

  const handleStampanjeZadnjeStrane = () => {
    // Proveri da li ima ugroženih lica
    if (ugrozenaLica.length === 0) {
      alert('Molimo dodajte ugrožena lica pre štampanja');
      return;
    }
    
    // Otvori modal za izbor predmeta
    setShowPredmetModal(true);
  };

  const handlePredmetConfirm = async (nazivPredmeta) => {
    try {
      // Pozovi backend za štampanje zadnje strane
      const response = await fetch('/api/generate-envelope-back-side-pdf', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          template: template,
          ugrozenaLica: ugrozenaLica,
          nazivPredmeta: nazivPredmeta
        })
      });

      if (response.ok) {
        // Preuzmi PDF
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `koverte-zadnja-strana-${template.toLowerCase()}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        setShowPredmetModal(false);
      } else {
        alert('Greška pri štampanju zadnje strane koverte');
      }
    } catch (error) {
      console.error('Greška:', error);
      alert('Greška pri štampanju zadnje strane koverte');
    }
  };

  return (
    <div>
      {/* Postojeći kod za štampanje koverte */}
      
      <Button 
        variant="success" 
        onClick={handleStampanjeZadnjeStrane}
        className="me-2"
      >
        🖨️ Štampaj Zadnju Stranu Koverte
      </Button>

      <PredmetSelectionModal
        show={showPredmetModal}
        onHide={() => setShowPredmetModal(false)}
        onConfirm={handlePredmetConfirm}
        ugrozenaLica={ugrozenaLica}
        template={template}
      />
    </div>
  );
};

export default StampanjeKoverte;
```

### 3. **Batch Processing Komponenta**

Kreirati komponentu `BatchStampanjeKoverte.jsx` za batch processing:

```jsx
import React, { useState } from 'react';
import { Button, Card, Row, Col, Alert, ProgressBar } from 'react-bootstrap';
import PredmetSelectionModal from './PredmetSelectionModal';

const BatchStampanjeKoverte = () => {
  const [batchRequests, setBatchRequests] = useState([]);
  const [showPredmetModal, setShowPredmetModal] = useState(false);
  const [currentRequestIndex, setCurrentRequestIndex] = useState(0);
  const [processing, setProcessing] = useState(false);
  const [results, setResults] = useState(null);

  const handleDodajZahtev = () => {
    setCurrentRequestIndex(batchRequests.length);
    setShowPredmetModal(true);
  };

  const handlePredmetConfirm = (nazivPredmeta) => {
    const newRequest = {
      template: 'T1', // Default template
      ugrozenaLica: [], // Treba da se popuni
      nazivPredmeta: nazivPredmeta
    };
    
    setBatchRequests([...batchRequests, newRequest]);
    setShowPredmetModal(false);
  };

  const handleBatchStampanje = async () => {
    if (batchRequests.length === 0) {
      alert('Molimo dodajte bar jedan zahtev');
      return;
    }

    try {
      setProcessing(true);
      
      const response = await fetch('/api/generate-batch-envelope-back-side-pdf', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          requests: batchRequests
        })
      });

      if (response.ok) {
        // Preuzmi jedan PDF sa više stranica
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'koverte-zadnja-strana-batch.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        setResults({
          totalRequests: batchRequests.length,
          successfulRequests: batchRequests.length,
          failedRequests: 0,
          message: `Uspešno generisan PDF sa ${batchRequests.length} zahtevima`
        });
      } else {
        alert('Greška pri batch štampanju');
      }
    } catch (error) {
      console.error('Greška:', error);
      alert('Greška pri batch štampanju');
    } finally {
      setProcessing(false);
    }
  };

  const handleObrisiZahtev = (index) => {
    const newRequests = batchRequests.filter((_, i) => i !== index);
    setBatchRequests(newRequests);
  };

  return (
    <div className="container mt-4">
      <h2>🖨️ Batch Štampanje Koverte</h2>
      
      <Card className="mb-4">
        <Card.Header>
          <h5>Zahtevi za Štampanje</h5>
        </Card.Header>
        <Card.Body>
          {batchRequests.length === 0 ? (
            <Alert variant="info">
              Nema zahteva. Kliknite "Dodaj Zahtev" da počnete.
            </Alert>
          ) : (
            <div>
              {batchRequests.map((request, index) => (
                <Card key={index} className="mb-2">
                  <Card.Body>
                    <Row>
                      <Col md={8}>
                        <strong>Predmet:</strong> {request.nazivPredmeta}<br/>
                        <strong>Template:</strong> {request.template}<br/>
                        <strong>Ugrožena lica:</strong> {request.ugrozenaLica.length}
                      </Col>
                      <Col md={4} className="text-end">
                        <Button 
                          variant="danger" 
                          size="sm"
                          onClick={() => handleObrisiZahtev(index)}
                        >
                          Obriši
                        </Button>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>
              ))}
            </div>
          )}
          
          <div className="mt-3">
            <Button 
              variant="primary" 
              onClick={handleDodajZahtev}
              className="me-2"
            >
              ➕ Dodaj Zahtev
            </Button>
            
            {batchRequests.length > 0 && (
              <Button 
                variant="success" 
                onClick={handleBatchStampanje}
                disabled={processing}
              >
                {processing ? '🔄 Obrađujem...' : '🖨️ Štampaj Sve'}
              </Button>
            )}
          </div>
        </Card.Body>
      </Card>

      {processing && (
        <Card className="mb-4">
          <Card.Body>
            <h6>Obrađujem zahteve...</h6>
            <ProgressBar animated now={100} />
          </Card.Body>
        </Card>
      )}

      {results && (
        <Card>
          <Card.Header>
            <h5>Rezultati Batch Procesiranja</h5>
          </Card.Header>
          <Card.Body>
            <Alert variant={results.failedRequests === 0 ? 'success' : 'warning'}>
              <strong>{results.message}</strong>
            </Alert>
            
            <div className="mt-3">
              <h6>Rezultat:</h6>
              <div className="mb-2">
                <strong>PDF generisan:</strong> 
                <span className="text-success"> ✅ koverte-zadnja-strana-batch.pdf</span>
              </div>
              <div className="mb-2">
                <strong>Broj zahteva:</strong> {results.totalRequests}
              </div>
              <div className="mb-2">
                <strong>Status:</strong> 
                <span className="text-success"> ✅ Uspešno</span>
              </div>
            </div>
          </Card.Body>
        </Card>
      )}

      <PredmetSelectionModal
        show={showPredmetModal}
        onHide={() => setShowPredmetModal(false)}
        onConfirm={handlePredmetConfirm}
        ugrozenaLica={[]} // Treba da se popuni
        template="T1"
      />
    </div>
  );
};

export default BatchStampanjeKoverte;
```

## 🎯 Ključne Funkcionalnosti

### 1. **Dva Načina Izbora**
- **Iz tabele predmeta**: Dropdown sa pretragom
- **Ručni unos**: Slobodan tekst

### 2. **Pretraga Predmeta**
- Real-time pretraga kroz nazive predmeta
- Case-insensitive pretraga
- Automatsko filtriranje rezultata

### 3. **Validacija**
- Obavezan izbor/unes predmeta
- Provera da li postoje ugrožena lica
- Error handling za sve API pozive

### 4. **User Experience**
- Intuitivni radio button izbor
- Loading indikatori
- Error poruke
- Responsive dizajn

### 5. **Batch Processing**
- **Jedan PDF**: Generiše se jedan PDF sa više stranica
- **Svako lice na posebnoj strani**: Svako ugroženo lice ima svoju stranu
- **Sekvencijalno procesiranje**: Zahtevi se obrađuju redom
- **Error handling**: Validacija svih zahteva pre generisanja
- **Automatic download**: Automatsko preuzimanje jednog PDF-a
- **Simple result**: Jednostavan pregled rezultata

## 📝 API Pozivi

### 1. **Učitavanje Naziva Predmeta**
```javascript
const response = await fetch('/api/euk/predmeti/nazivi');
const nazivi = await response.json();
```

### 2. **Pretraga Predmeta**
```javascript
const response = await fetch(`/api/euk/predmeti?nazivPredmeta=${searchTerm}&page=0&size=10`);
const data = await response.json();
```

### 3. **Štampanje PDF-a - Single**
```javascript
const response = await fetch('/api/generate-single-envelope-back-side-pdf', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    template: 'T1',
    ugrozenaLica: ugrozenaLica,
    nazivPredmeta: selectedNazivPredmeta
  })
});

if (response.ok) {
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'koverte-zadnja-strana-t1.pdf';
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}
```

### 4. **Batch Štampanje - Jedan PDF sa Više Stranica**
```javascript
const batchRequests = [
  {
    template: 'T1',
    ugrozenaLica: ugrozenaLica1,
    nazivPredmeta: 'Građansko pravo - Spor o imovini'
  },
  {
    template: 'T2',
    ugrozenaLica: ugrozenaLica2,
    nazivPredmeta: 'Krivično pravo - Ubistvo'
  }
];

const response = await fetch('/api/generate-batch-envelope-back-side-pdf', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    requests: batchRequests
  })
});

if (response.ok) {
  // Preuzmi jedan PDF sa više stranica
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'koverte-zadnja-strana-batch.pdf';
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
} else {
  alert('Greška pri batch štampanju');
}
```

## 🔄 Workflow

### **Single Request Workflow**
1. **Korisnik klikne "Štampaj Zadnju Stranu"**
2. **Otvara se modal sa opcijama**
3. **Korisnik bira način izbora predmeta**
4. **Ako bira iz tabele**: pretražuje i bira predmet
5. **Ako bira ručno**: unosi naziv predmeta
6. **Klikne "Potvrdi i Štampaj"**
7. **Backend poziva `/api/generate-single-envelope-back-side-pdf`**
8. **PDF se preuzima automatski** (jedan fajl)

### **Batch Request Workflow**
1. **Korisnik klikne "Dodaj Zahtev"**
2. **Otvara se modal za izbor predmeta** (isti kao single request)
3. **Korisnik unosi predmet i dodaje zahtev u listu**
4. **Ponavlja korake 1-3 za svaki dodatni zahtev**
5. **Korisnik klikne "Štampaj Sve"**
6. **Backend poziva `/api/generate-batch-envelope-back-side-pdf`**
7. **PDF se preuzima** automatski kao jedan fajl sa više stranica
8. **Svako lice je na posebnoj strani** u istom PDF-u

## ✅ Testiranje

### 1. **Test Scenariji**
- [ ] Modal se otvara kada se klikne dugme
- [ ] Radio button prebacivanje radi
- [ ] Pretraga predmeta radi
- [ ] Validacija radi (prazan izbor)
- [ ] API pozivi rade
- [ ] PDF se generiše i preuzima
- [ ] Error handling radi

### 2. **Edge Cases**
- [ ] Prazna lista predmeta
- [ ] Mrežne greške
- [ ] Neispravni API odgovori
- [ ] Veliki broj predmeta
- [ ] Specijalni karakteri u nazivima

## 🚀 Deployment

1. **Backend**: Već implementirano
2. **Frontend**: Dodati komponentu u postojeći kod
3. **Testiranje**: Proveriti sve funkcionalnosti
4. **Dokumentacija**: Ažurirati korisničku dokumentaciju
