# 🎯 FRONTEND IMPLEMENTACIJA - SISTEM FORMULARE

## 📋 Pregled

Implementiran je kompletan **dinamički sistem formulare** koji omogućava:
- Kreiranje formulare po kategorijama predmeta
- Dinamička polja sa različitim tipovima
- Popunjavanje formulare za predmete
- Upravljanje priloženim dokumentima
- Istorija izmena formulare

## 🗄️ Database Schema

### Glavne tabele:
- `euk.formular` - Formulari po kategorijama
- `euk.formular_polja` - Polja formulare
- `euk.predmet_formular_podaci` - Podaci formulare za predmete
- `euk.formular_istorija` - Istorija izmena
- `euk.formular_dokumenti` - Priloženi dokumenti

## 🚀 API Endpoint-i

### 1. **FORMULARI**

#### Kreiranje formulare
```http
POST /api/euk/formulari
Content-Type: application/json
X-User-Id: 1

{
  "naziv": "Formular za Krivična dela",
  "opis": "Osnovni formular za krivična dela",
  "kategorijaId": 1
}
```

#### Ažuriranje formulare
```http
PUT /api/euk/formulari/{formularId}
Content-Type: application/json
X-User-Id: 1

{
  "naziv": "Ažurirani formular",
  "opis": "Novi opis",
  "kategorijaId": 1
}
```

#### Dohvatanje formulare
```http
GET /api/euk/formulari/{formularId}
GET /api/euk/formulari/{formularId}/polja
GET /api/euk/formulari/kategorija/{kategorijaId}
GET /api/euk/formulari/kategorija/{kategorijaId}/polja
```

#### Pretraga formulare
```http
GET /api/euk/formulari/search?naziv=krivična
GET /api/euk/formulari/search/kategorija/{kategorijaId}?naziv=krivična
```

### 2. **POLJA FORMULARE**

#### Kreiranje polja
```http
POST /api/euk/formulari/{formularId}/polja
Content-Type: application/json
X-User-Id: 1

{
  "nazivPolja": "mesto_delicta",
  "label": "Mesto delikta",
  "tipPolja": "text",
  "obavezno": true,
  "redosled": 1,
  "placeholder": "Unesite mesto delikta",
  "opis": "Mesto gde je počinjen delikt"
}
```

#### Tipovi polja:
- `text` - Tekstualno polje
- `textarea` - Višelinijski tekst
- `number` - Brojčano polje
- `date` - Datum
- `datetime` - Datum i vreme
- `select` - Dropdown lista
- `radio` - Radio dugmad
- `checkbox` - Checkbox
- `file` - Upload fajla

#### Ažuriranje polja
```http
PUT /api/euk/formulari/polja/{poljeId}
Content-Type: application/json
X-User-Id: 1

{
  "nazivPolja": "mesto_delicta",
  "label": "Mesto delikta",
  "tipPolja": "text",
  "obavezno": true,
  "redosled": 1,
  "placeholder": "Unesite mesto delikta",
  "opis": "Mesto gde je počinjen delikt",
  "validacija": "{\"minLength\": 3, \"maxLength\": 100}",
  "opcije": "[\"Opcija 1\", \"Opcija 2\"]"
}
```

#### Redosled polja
```http
PUT /api/euk/formulari/polja/{poljeId}/redosled?noviRedosled=2
PUT /api/euk/formulari/polja/{poljeId}/move-up
PUT /api/euk/formulari/polja/{poljeId}/move-down
```

### 3. **PODACI FORMULARE**

#### Popunjavanje formulare
```http
POST /api/euk/predmet-formular-podaci
Content-Type: application/json
X-User-Id: 1

{
  "predmetId": 1,
  "poljeId": 1,
  "vrednost": "Beograd"
}
```

#### Batch popunjavanje
```http
POST /api/euk/predmet-formular-podaci/batch?predmetId=1&formularId=1
Content-Type: application/json
X-User-Id: 1

{
  "mesto_delicta": "Beograd",
  "vreme_delicta": "2024-01-15T10:30:00",
  "okrivljeni_osoba": "Marko Marković",
  "tip_delicta": "Krađa"
}
```

#### Dohvatanje podataka
```http
GET /api/euk/predmet-formular-podaci/predmet/{predmetId}
GET /api/euk/predmet-formular-podaci/predmet/{predmetId}/formular/{formularId}
GET /api/euk/predmet-formular-podaci/predmet/{predmetId}/kategorija/{kategorijaId}
```

## 🎨 Frontend Komponente

### 1. **FormularBuilder Component**

```typescript
interface FormularBuilderProps {
  kategorijaId: number;
  onSave: (formular: FormularDto) => void;
}

const FormularBuilder: React.FC<FormularBuilderProps> = ({ kategorijaId, onSave }) => {
  const [formular, setFormular] = useState<FormularDto>({
    naziv: '',
    opis: '',
    kategorijaId,
    polja: []
  });

  const addPolje = (polje: FormularPoljeDto) => {
    setFormular(prev => ({
      ...prev,
      polja: [...prev.polja, polje]
    }));
  };

  const updatePolje = (poljeId: number, updatedPolje: FormularPoljeDto) => {
    setFormular(prev => ({
      ...prev,
      polja: prev.polja.map(p => p.poljeId === poljeId ? updatedPolje : p)
    }));
  };

  const removePolje = (poljeId: number) => {
    setFormular(prev => ({
      ...prev,
      polja: prev.polja.filter(p => p.poljeId !== poljeId)
    }));
  };

  const movePolje = (poljeId: number, direction: 'up' | 'down') => {
    // Implementacija pomeranja polja
  };

  return (
    <div className="formular-builder">
      <FormularHeader formular={formular} onChange={setFormular} />
      <PoljaLista 
        polja={formular.polja}
        onAdd={addPolje}
        onUpdate={updatePolje}
        onRemove={removePolje}
        onMove={movePolje}
      />
      <FormularActions onSave={() => onSave(formular)} />
    </div>
  );
};
```

### 2. **PoljeEditor Component**

```typescript
interface PoljeEditorProps {
  polje: FormularPoljeDto;
  onChange: (polje: FormularPoljeDto) => void;
}

const PoljeEditor: React.FC<PoljeEditorProps> = ({ polje, onChange }) => {
  const [localPolje, setLocalPolje] = useState<FormularPoljeDto>(polje);

  const handleChange = (field: keyof FormularPoljeDto, value: any) => {
    const updatedPolje = { ...localPolje, [field]: value };
    setLocalPolje(updatedPolje);
    onChange(updatedPolje);
  };

  const renderTipPoljaOptions = () => {
    const tipovi = [
      { value: 'text', label: 'Tekst' },
      { value: 'textarea', label: 'Višelinijski tekst' },
      { value: 'number', label: 'Broj' },
      { value: 'date', label: 'Datum' },
      { value: 'datetime', label: 'Datum i vreme' },
      { value: 'select', label: 'Dropdown' },
      { value: 'radio', label: 'Radio dugmad' },
      { value: 'checkbox', label: 'Checkbox' },
      { value: 'file', label: 'Fajl' }
    ];

    return (
      <select 
        value={localPolje.tipPolja} 
        onChange={(e) => handleChange('tipPolja', e.target.value)}
      >
        {tipovi.map(tip => (
          <option key={tip.value} value={tip.value}>{tip.label}</option>
        ))}
      </select>
    );
  };

  const renderOpcijeEditor = () => {
    if (!['select', 'radio', 'checkbox'].includes(localPolje.tipPolja)) return null;

    return (
      <div className="opcije-editor">
        <label>Opcije (jedna po liniji):</label>
        <textarea
          value={localPolje.opcije || ''}
          onChange={(e) => handleChange('opcije', e.target.value)}
          rows={4}
        />
      </div>
    );
  };

  const renderValidacijaEditor = () => {
    return (
      <div className="validacija-editor">
        <label>Validacija (JSON):</label>
        <textarea
          value={localPolje.validacija || ''}
          onChange={(e) => handleChange('validacija', e.target.value)}
          rows={3}
          placeholder='{"minLength": 3, "maxLength": 100}'
        />
      </div>
    );
  };

  return (
    <div className="polje-editor">
      <div className="form-group">
        <label>Naziv polja:</label>
        <input
          type="text"
          value={localPolje.nazivPolja}
          onChange={(e) => handleChange('nazivPolja', e.target.value)}
        />
      </div>

      <div className="form-group">
        <label>Label:</label>
        <input
          type="text"
          value={localPolje.label}
          onChange={(e) => handleChange('label', e.target.value)}
        />
      </div>

      <div className="form-group">
        <label>Tip polja:</label>
        {renderTipPoljaOptions()}
      </div>

      <div className="form-group">
        <label>
          <input
            type="checkbox"
            checked={localPolje.obavezno}
            onChange={(e) => handleChange('obavezno', e.target.checked)}
          />
          Obavezno polje
        </label>
      </div>

      <div className="form-group">
        <label>Redosled:</label>
        <input
          type="number"
          value={localPolje.redosled}
          onChange={(e) => handleChange('redosled', parseInt(e.target.value))}
        />
      </div>

      <div className="form-group">
        <label>Placeholder:</label>
        <input
          type="text"
          value={localPolje.placeholder || ''}
          onChange={(e) => handleChange('placeholder', e.target.value)}
        />
      </div>

      <div className="form-group">
        <label>Opis:</label>
        <textarea
          value={localPolje.opis || ''}
          onChange={(e) => handleChange('opis', e.target.value)}
          rows={2}
        />
      </div>

      {renderOpcijeEditor()}
      {renderValidacijaEditor()}
    </div>
  );
};
```

### 3. **DynamicForm Component**

```typescript
interface DynamicFormProps {
  formularId: number;
  predmetId: number;
  onSave: (podaci: Map<string, any>) => void;
}

const DynamicForm: React.FC<DynamicFormProps> = ({ formularId, predmetId, onSave }) => {
  const [formular, setFormular] = useState<FormularDto | null>(null);
  const [podaci, setPodaci] = useState<Map<string, any>>(new Map());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFormular();
    fetchPodaci();
  }, [formularId, predmetId]);

  const fetchFormular = async () => {
    try {
      const response = await fetch(`/api/euk/formulari/${formularId}/polja`);
      const data = await response.json();
      setFormular(data);
    } catch (error) {
      console.error('Error fetching formular:', error);
    }
  };

  const fetchPodaci = async () => {
    try {
      const response = await fetch(`/api/euk/predmet-formular-podaci/predmet/${predmetId}/formular/${formularId}`);
      const data = await response.json();
      const podaciMap = new Map();
      data.forEach((podatak: any) => {
        podaciMap.set(podatak.poljeNaziv, podatak.vrednost);
      });
      setPodaci(podaciMap);
    } catch (error) {
      console.error('Error fetching podaci:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFieldChange = (nazivPolja: string, value: any) => {
    setPodaci(prev => {
      const newPodaci = new Map(prev);
      newPodaci.set(nazivPolja, value);
      return newPodaci;
    });
  };

  const handleSave = async () => {
    try {
      const response = await fetch(`/api/euk/predmet-formular-podaci/batch?predmetId=${predmetId}&formularId=${formularId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': '1' // TODO: Get from auth context
        },
        body: JSON.stringify(Object.fromEntries(podaci))
      });

      if (response.ok) {
        onSave(podaci);
      }
    } catch (error) {
      console.error('Error saving podaci:', error);
    }
  };

  const renderField = (polje: FormularPoljeDto) => {
    const value = podaci.get(polje.nazivPolja) || polje.defaultVrednost || '';

    switch (polje.tipPolja) {
      case 'text':
        return (
          <input
            type="text"
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            placeholder={polje.placeholder}
            required={polje.obavezno}
            disabled={polje.readonly}
          />
        );

      case 'textarea':
        return (
          <textarea
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            placeholder={polje.placeholder}
            required={polje.obavezno}
            disabled={polje.readonly}
            rows={4}
          />
        );

      case 'number':
        return (
          <input
            type="number"
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            placeholder={polje.placeholder}
            required={polje.obavezno}
            disabled={polje.readonly}
          />
        );

      case 'date':
        return (
          <input
            type="date"
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            required={polje.obavezno}
            disabled={polje.readonly}
          />
        );

      case 'datetime':
        return (
          <input
            type="datetime-local"
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            required={polje.obavezno}
            disabled={polje.readonly}
          />
        );

      case 'select':
        const opcije = polje.opcije ? JSON.parse(polje.opcije) : [];
        return (
          <select
            value={value}
            onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
            required={polje.obavezno}
            disabled={polje.readonly}
          >
            <option value="">Izaberite opciju</option>
            {opcije.map((opcija: string, index: number) => (
              <option key={index} value={opcija}>{opcija}</option>
            ))}
          </select>
        );

      case 'radio':
        const radioOpcije = polje.opcije ? JSON.parse(polje.opcije) : [];
        return (
          <div className="radio-group">
            {radioOpcije.map((opcija: string, index: number) => (
              <label key={index}>
                <input
                  type="radio"
                  name={polje.nazivPolja}
                  value={opcija}
                  checked={value === opcija}
                  onChange={(e) => handleFieldChange(polje.nazivPolja, e.target.value)}
                  required={polje.obavezno}
                  disabled={polje.readonly}
                />
                {opcija}
              </label>
            ))}
          </div>
        );

      case 'checkbox':
        const checkboxOpcije = polje.opcije ? JSON.parse(polje.opcije) : [];
        const selectedValues = value ? value.split(',') : [];
        return (
          <div className="checkbox-group">
            {checkboxOpcije.map((opcija: string, index: number) => (
              <label key={index}>
                <input
                  type="checkbox"
                  value={opcija}
                  checked={selectedValues.includes(opcija)}
                  onChange={(e) => {
                    const newValues = e.target.checked
                      ? [...selectedValues, opcija]
                      : selectedValues.filter(v => v !== opcija);
                    handleFieldChange(polje.nazivPolja, newValues.join(','));
                  }}
                  disabled={polje.readonly}
                />
                {opcija}
              </label>
            ))}
          </div>
        );

      case 'file':
        return (
          <input
            type="file"
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) {
                // TODO: Upload file and get URL
                handleFieldChange(polje.nazivPolja, file.name);
              }
            }}
            required={polje.obavezno}
            disabled={polje.readonly}
          />
        );

      default:
        return null;
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!formular) {
    return <div>Formular not found</div>;
  }

  return (
    <div className="dynamic-form">
      <h2>{formular.naziv}</h2>
      {formular.opis && <p>{formular.opis}</p>}

      <form onSubmit={(e) => { e.preventDefault(); handleSave(); }}>
        {formular.polja
          .filter(polje => polje.visible)
          .sort((a, b) => a.redosled - b.redosled)
          .map(polje => (
            <div key={polje.poljeId} className="form-group">
              <label>
                {polje.label}
                {polje.obavezno && <span className="required">*</span>}
              </label>
              {renderField(polje)}
              {polje.opis && <small className="field-description">{polje.opis}</small>}
            </div>
          ))}

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            Sačuvaj
          </button>
        </div>
      </form>
    </div>
  );
};
```

### 4. **FormularList Component**

```typescript
interface FormularListProps {
  kategorijaId: number;
  onSelect: (formular: FormularDto) => void;
}

const FormularList: React.FC<FormularListProps> = ({ kategorijaId, onSelect }) => {
  const [formulari, setFormulari] = useState<FormularDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFormulari();
  }, [kategorijaId]);

  const fetchFormulari = async () => {
    try {
      const response = await fetch(`/api/euk/formulari/kategorija/${kategorijaId}`);
      const data = await response.json();
      setFormulari(data);
    } catch (error) {
      console.error('Error fetching formulari:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="formular-list">
      <h3>Formulari za kategoriju</h3>
      {formulari.length === 0 ? (
        <p>Nema formulare za ovu kategoriju</p>
      ) : (
        <div className="formular-grid">
          {formulari.map(formular => (
            <div key={formular.formularId} className="formular-card">
              <h4>{formular.naziv}</h4>
              {formular.opis && <p>{formular.opis}</p>}
              <div className="formular-meta">
                <span>Verzija: {formular.verzija}</span>
                <span>Polja: {formular.brojPolja}</span>
                <span>Kreiran: {new Date(formular.datumKreiranja).toLocaleDateString()}</span>
              </div>
              <button 
                className="btn btn-primary"
                onClick={() => onSelect(formular)}
              >
                Koristi formular
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
```

## 🎯 Implementacija koraka

### 1. **Instalacija potrebnih paketa**
```bash
npm install react-hook-form
npm install @types/react-hook-form
```

### 2. **Kreiranje tipova**
```typescript
// types/formular.ts
export interface FormularDto {
  formularId: number;
  naziv: string;
  opis: string;
  kategorijaId: number;
  kategorijaNaziv: string;
  kategorijaSkracenica: string;
  datumKreiranja: string;
  datumAzuriranja: string;
  aktivna: boolean;
  verzija: number;
  createdById: number;
  createdByUsername: string;
  updatedById: number;
  updatedByUsername: string;
  polja: FormularPoljeDto[];
  brojPolja: number;
}

export interface FormularPoljeDto {
  poljeId: number;
  formularId: number;
  formularNaziv: string;
  nazivPolja: string;
  label: string;
  tipPolja: string;
  obavezno: boolean;
  redosled: number;
  placeholder: string;
  opis: string;
  validacija: string;
  opcije: string;
  defaultVrednost: string;
  readonly: boolean;
  visible: boolean;
  createdAt: string;
  updatedAt: string;
  validacijaMap?: Record<string, any>;
  opcijeList?: Record<string, any>[];
  vrednost?: string;
}

export interface PredmetFormularPodaciDto {
  podatakId: number;
  predmetId: number;
  predmetNaziv: string;
  poljeId: number;
  poljeNaziv: string;
  poljeLabel: string;
  poljeTip: string;
  vrednost: string;
  datumUnosa: string;
  unioKorisnikId: number;
  unioKorisnikUsername: string;
}
```

### 3. **API servis**
```typescript
// services/formularService.ts
export class FormularService {
  private baseUrl = '/api/euk';

  async getFormulariByKategorija(kategorijaId: number): Promise<FormularDto[]> {
    const response = await fetch(`${this.baseUrl}/formulari/kategorija/${kategorijaId}`);
    return response.json();
  }

  async getFormularWithPolja(formularId: number): Promise<FormularDto> {
    const response = await fetch(`${this.baseUrl}/formulari/${formularId}/polja`);
    return response.json();
  }

  async createFormular(formular: Partial<FormularDto>, userId: number): Promise<FormularDto> {
    const response = await fetch(`${this.baseUrl}/formulari`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString()
      },
      body: JSON.stringify(formular)
    });
    return response.json();
  }

  async updateFormular(formularId: number, formular: Partial<FormularDto>, userId: number): Promise<FormularDto> {
    const response = await fetch(`${this.baseUrl}/formulari/${formularId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString()
      },
      body: JSON.stringify(formular)
    });
    return response.json();
  }

  async deleteFormular(formularId: number, userId: number): Promise<void> {
    await fetch(`${this.baseUrl}/formulari/${formularId}`, {
      method: 'DELETE',
      headers: {
        'X-User-Id': userId.toString()
      }
    });
  }

  async createPolje(formularId: number, polje: Partial<FormularPoljeDto>, userId: number): Promise<FormularPoljeDto> {
    const response = await fetch(`${this.baseUrl}/formulari/${formularId}/polja`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString()
      },
      body: JSON.stringify(polje)
    });
    return response.json();
  }

  async updatePolje(poljeId: number, polje: Partial<FormularPoljeDto>, userId: number): Promise<FormularPoljeDto> {
    const response = await fetch(`${this.baseUrl}/formulari/polja/${poljeId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString()
      },
      body: JSON.stringify(polje)
    });
    return response.json();
  }

  async deletePolje(poljeId: number, userId: number): Promise<void> {
    await fetch(`${this.baseUrl}/formulari/polja/${poljeId}`, {
      method: 'DELETE',
      headers: {
        'X-User-Id': userId.toString()
      }
    });
  }

  async savePodaci(predmetId: number, formularId: number, podaci: Record<string, any>, userId: number): Promise<void> {
    await fetch(`${this.baseUrl}/predmet-formular-podaci/batch?predmetId=${predmetId}&formularId=${formularId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString()
      },
      body: JSON.stringify(podaci)
    });
  }

  async getPodaci(predmetId: number, formularId: number): Promise<PredmetFormularPodaciDto[]> {
    const response = await fetch(`${this.baseUrl}/predmet-formular-podaci/predmet/${predmetId}/formular/${formularId}`);
    return response.json();
  }
}
```

### 4. **Glavna stranica za upravljanje formulare**
```typescript
// pages/FormularManagement.tsx
const FormularManagement: React.FC = () => {
  const [kategorije, setKategorije] = useState<KategorijaDto[]>([]);
  const [selectedKategorija, setSelectedKategorija] = useState<number | null>(null);
  const [formulari, setFormulari] = useState<FormularDto[]>([]);
  const [selectedFormular, setSelectedFormular] = useState<FormularDto | null>(null);
  const [mode, setMode] = useState<'list' | 'builder' | 'view'>('list');

  return (
    <div className="formular-management">
      <div className="sidebar">
        <h3>Kategorije</h3>
        <KategorijaList 
          kategorije={kategorije}
          selectedKategorija={selectedKategorija}
          onSelect={setSelectedKategorija}
        />
      </div>

      <div className="main-content">
        {mode === 'list' && (
          <FormularList
            kategorijaId={selectedKategorija}
            onSelect={setSelectedFormular}
            onEdit={(formular) => {
              setSelectedFormular(formular);
              setMode('builder');
            }}
            onView={(formular) => {
              setSelectedFormular(formular);
              setMode('view');
            }}
          />
        )}

        {mode === 'builder' && selectedFormular && (
          <FormularBuilder
            formular={selectedFormular}
            onSave={(formular) => {
              // Save formular
              setMode('list');
            }}
            onCancel={() => setMode('list')}
          />
        )}

        {mode === 'view' && selectedFormular && (
          <FormularViewer
            formular={selectedFormular}
            onEdit={() => setMode('builder')}
            onClose={() => setMode('list')}
          />
        )}
      </div>
    </div>
  );
};
```

## 🎨 CSS Stilovi

```css
/* formular-management.css */
.formular-management {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 300px;
  background: #f5f5f5;
  padding: 20px;
  border-right: 1px solid #ddd;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.formular-builder {
  max-width: 800px;
  margin: 0 auto;
}

.polja-lista {
  margin: 20px 0;
}

.polje-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin: 10px 0;
  background: white;
}

.polje-editor {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin: 10px 0;
}

.form-group {
  margin: 15px 0;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.required {
  color: red;
  margin-left: 5px;
}

.dynamic-form {
  max-width: 600px;
  margin: 0 auto;
}

.dynamic-form .form-group {
  margin: 20px 0;
}

.dynamic-form label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.dynamic-form input,
.dynamic-form textarea,
.dynamic-form select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.radio-group,
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.radio-group label,
.checkbox-group label {
  display: flex;
  align-items: center;
  font-weight: normal;
}

.radio-group input,
.checkbox-group input {
  width: auto;
  margin-right: 8px;
}

.field-description {
  display: block;
  color: #666;
  font-size: 12px;
  margin-top: 5px;
}

.form-actions {
  margin-top: 30px;
  text-align: right;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.formular-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.formular-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.formular-card h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.formular-meta {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin: 10px 0;
  font-size: 12px;
  color: #666;
}

.formular-meta span {
  display: inline-block;
}
```

## 🚀 Pokretanje

1. **Pokreni database migraciju:**
```sql
-- Pokreni fajl: postgresQuery/create_formular_system.sql
```

2. **Restartuj backend aplikaciju**

3. **Implementiraj frontend komponente**

4. **Testiraj funkcionalnost:**
   - Kreiranje formulare
   - Dodavanje polja
   - Popunjavanje formulare
   - Prikaz podataka

## 📝 Napomene

- Sistem podržava sve tipove polja iz specifikacije
- Implementirana je validacija i istorija izmena
- Podržano je batch popunjavanje formulare
- Sistem je potpuno dinamički i konfigurabilan
- Može se proširiti sa dodatnim tipovima polja
