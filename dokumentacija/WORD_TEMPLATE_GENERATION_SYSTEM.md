# Word Template Generation System - Frontend Implementation Guide

## 🎯 Pregled sistema

Sistem za generisanje Word rešenja na osnovu postojećih template dokumenata sa automatskom zamenu crvenih slova sa podacima iz T1/T2 tabela i ručno unetim podacima.

## 📋 Tok generisanja

1. **Izbor lice** (T1 ili T2 tabela)
2. **Izbor kategorije**
3. **Izbor obrasci_vrste** (negativno, neograniceno, ograniceno, borci, penzioneri, obustave)
4. **Izbor organizaciona_struktura** (sekretar, podsekretar)
5. **Unos ručnih podataka** (zaglavlje, obrazloženje)
6. **Generisanje Word dokumenta**

## 🔧 API Endpoints

### Glavni endpoint za generisanje

**POST** `/api/template/generate`

**Request Body:**
```json
{
    "liceId": 1,
    "liceTip": "t1", // ili "t2"
    "kategorijaId": 1,
    "obrasciVrsteId": 1,
    "organizacionaStrukturaId": 1,
    "predmetId": 1,
    "manualData": {
        "zaglavlje": "Ručno uneto zaglavlje",
        "obrazlozenje": "Ručno uneto obrazloženje",
        "dodatniPodaci": "Dodatni podaci (opciono)"
    }
}
```

**Response:**
```json
{
    "predmetId": 1,
    "templateFilePath": "generated_templates/resenje_1_1234567890.docx",
    "templateStatus": "generated",
    "templateGeneratedAt": "2024-01-15T10:30:00",
    "message": "Word dokument je uspešno generisan",
    "success": true
}
```

### Ostali endpointi

- `GET /api/template/obrasci-vrste` - Svi obrasci vrste
- `GET /api/template/organizaciona-struktura` - Sve organizacione strukture
- `GET /api/kategorije` - Sve kategorije
- `GET /api/predmeti` - Svi predmeti
- `GET /api/euk/t1` - T1 ugrožena lica
- `GET /api/euk/t2` - T2 ugrožena lica

## 🎨 Frontend Implementation

### 1. Komponente koje treba kreirati

#### WordTemplateGenerationForm
```typescript
interface WordTemplateGenerationFormProps {
    onGenerate: (request: TemplateGenerationRequest) => void;
}

const WordTemplateGenerationForm: React.FC<WordTemplateGenerationFormProps> = ({ onGenerate }) => {
    const [formData, setFormData] = useState<TemplateGenerationRequest>({
        liceId: null,
        liceTip: '',
        kategorijaId: null,
        obrasciVrsteId: null,
        organizacionaStrukturaId: null,
        predmetId: null,
        manualData: {
            zaglavlje: '',
            obrazlozenje: '',
            dodatniPodaci: ''
        }
    });

    // Stepper komponenta sa 6 koraka
    const steps = [
        { title: 'Izbor lice', description: 'Izaberite lice iz T1 ili T2 tabele' },
        { title: 'Izbor kategorije', description: 'Izaberite kategoriju' },
        { title: 'Obrasci vrste', description: 'Izaberite tip obraza' },
        { title: 'Organizaciona struktura', description: 'Izaberite organizacionu strukturu' },
        { title: 'Ručni podaci', description: 'Unesite zaglavlje i obrazloženje' },
        { title: 'Generisanje', description: 'Generiši Word dokument' }
    ];

    return (
        <div className="word-template-form">
            <Stepper steps={steps} currentStep={currentStep} />
            
            {currentStep === 0 && <LiceSelectionStep />}
            {currentStep === 1 && <KategorijaSelectionStep />}
            {currentStep === 2 && <ObrasciVrsteSelectionStep />}
            {currentStep === 3 && <OrganizacionaStrukturaSelectionStep />}
            {currentStep === 4 && <ManualDataStep />}
            {currentStep === 5 && <GenerationStep />}
        </div>
    );
};
```

#### ManualDataStep komponenta
```typescript
const ManualDataStep: React.FC = () => {
    const [manualData, setManualData] = useState({
        zaglavlje: '',
        obrazlozenje: '',
        dodatniPodaci: ''
    });

    return (
        <div className="manual-data-step">
            <h3>Ručni podaci</h3>
            
            <div className="form-group">
                <label>Zaglavlje *</label>
                <textarea
                    value={manualData.zaglavlje}
                    onChange={(e) => setManualData({...manualData, zaglavlje: e.target.value})}
                    placeholder="Unesite zaglavlje rešenja..."
                    rows={3}
                    required
                />
            </div>
            
            <div className="form-group">
                <label>Obrazloženje *</label>
                <textarea
                    value={manualData.obrazlozenje}
                    onChange={(e) => setManualData({...manualData, obrazlozenje: e.target.value})}
                    placeholder="Unesite obrazloženje..."
                    rows={5}
                    required
                />
            </div>
            
            <div className="form-group">
                <label>Dodatni podaci (opciono)</label>
                <textarea
                    value={manualData.dodatniPodaci}
                    onChange={(e) => setManualData({...manualData, dodatniPodaci: e.target.value})}
                    placeholder="Dodatni podaci..."
                    rows={3}
                />
            </div>
        </div>
    );
};
```

### 2. TypeScript interfejsi

```typescript
interface TemplateGenerationRequest {
    liceId: number;
    liceTip: 't1' | 't2';
    kategorijaId: number;
    obrasciVrsteId: number;
    organizacionaStrukturaId: number;
    predmetId: number;
    manualData: ManualData;
}

interface ManualData {
    zaglavlje: string;
    obrazlozenje: string;
    dodatniPodaci?: string;
}

interface TemplateGenerationResponse {
    predmetId: number;
    templateFilePath: string;
    templateStatus: string;
    templateGeneratedAt: string;
    message: string;
    success: boolean;
}
```

### 3. API Service funkcije

```typescript
export class WordTemplateService {
    
    // Generisanje Word dokumenta
    async generateWordTemplate(request: TemplateGenerationRequest): Promise<TemplateGenerationResponse> {
        const response = await fetch('/api/template/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(request)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    }
    
    // Download generisanog dokumenta
    async downloadTemplate(filePath: string): Promise<void> {
        const response = await fetch(`/api/template/download?filePath=${encodeURIComponent(filePath)}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filePath.split('/').pop() || 'resenje.docx';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }
}
```

### 4. Stepper komponenta

```typescript
interface StepperProps {
    steps: Step[];
    currentStep: number;
}

interface Step {
    title: string;
    description: string;
}

const Stepper: React.FC<StepperProps> = ({ steps, currentStep }) => {
    return (
        <div className="stepper">
            {steps.map((step, index) => (
                <div key={index} className={`step ${index <= currentStep ? 'active' : ''}`}>
                    <div className="step-number">{index + 1}</div>
                    <div className="step-content">
                        <h4>{step.title}</h4>
                        <p>{step.description}</p>
                    </div>
                </div>
            ))}
        </div>
    );
};
```

## 📁 Template fajlovi

### Struktura template direktorijuma
```
src/main/resources/obrasci/
├── obrasci NEGATIVNO sa potpisom podsekretara/
│   └── OДБИЈА СЕ NSP,UNSP,DD,UDTNP.doc
├── obrasci NEOGRANICENO sa potpisom sekretara/
│   └── template_neograniceno.doc
└── obrasci OGRANICENO sa potpisom sekretara/
    └── template_ograniceno.doc
```

### Generisani fajlovi
```
generated_templates/
├── resenje_1_1234567890.docx
├── resenje_2_1234567891.docx
└── ...
```

## 🔄 Zamena podataka u template-u

### Automatski podaci iz T1/T2
- **T1_IME** - Ime iz T1 tabele
- **T1_PREZIME** - Prezime iz T1 tabele
- **T1_JMBG** - JMBG iz T1 tabele
- **T1_ADRESA** - Kompletna adresa iz T1 tabele
- **T1_ED_BROJ** - ED broj iz T1 tabele
- **T2_IME** - Ime iz T2 tabele
- **T2_PREZIME** - Prezime iz T2 tabele
- **T2_JMBG** - JMBG iz T2 tabele
- **T2_ADRESA** - Kompletna adresa iz T2 tabele
- **T2_ED_BROJ** - ED broj iz T2 tabele

### Ručno uneti podaci
- **ZAGLAVLJE** - Zaglavlje rešenja
- **OBRAZLOZENJE** - Obrazloženje rešenja
- **DODATNI_PODACI** - Dodatni podaci

### Sistemski podaci
- **DATUM_GENERISANJA** - Datum generisanja (dd.MM.yyyy)
- **DATUM_GENERISANJA_FULL** - Datum generisanja (dd. MMMM yyyy.)

## 🎯 Logika zamene

1. **Crvena slova** u template-u se zamenjuju sa odgovarajućim podacima
2. **Linije pored crvenih slova** se brišu
3. **Podaci se upisuju** na mesto crvenih slova
4. **Format se čuva** - samo se zamenjuje sadržaj

## 📋 Validacija

### Frontend validacija
```typescript
const validateForm = (data: TemplateGenerationRequest): string[] => {
    const errors: string[] = [];
    
    if (!data.liceId) errors.push('Lice je obavezno');
    if (!data.liceTip) errors.push('Tip lice je obavezan');
    if (!data.kategorijaId) errors.push('Kategorija je obavezna');
    if (!data.obrasciVrsteId) errors.push('Obrasci vrste je obavezno');
    if (!data.organizacionaStrukturaId) errors.push('Organizaciona struktura je obavezna');
    if (!data.predmetId) errors.push('Predmet je obavezan');
    
    if (!data.manualData?.zaglavlje) errors.push('Zaglavlje je obavezno');
    if (!data.manualData?.obrazlozenje) errors.push('Obrazloženje je obavezno');
    
    return errors;
};
```

## 🚀 Testiranje

### Test generisanja
```bash
curl -X POST http://localhost:8080/api/template/generate \
  -H "Content-Type: application/json" \
  -d '{
    "liceId": 1,
    "liceTip": "t1",
    "kategorijaId": 1,
    "obrasciVrsteId": 1,
    "organizacionaStrukturaId": 1,
    "predmetId": 1,
    "manualData": {
        "zaglavlje": "Test zaglavlje",
        "obrazlozenje": "Test obrazloženje"
    }
  }'
```

## 📝 Napomene za implementaciju

1. **Template fajlovi** se čuvaju u `src/main/resources/obrasci/`
2. **Generisani fajlovi** se čuvaju u `generated_templates/`
3. **Crvena slova** se prepoznaju po pattern-u i zamenjuju sa podacima
4. **Linije pored crvenih slova** se automatski brišu
5. **Format dokumenta** se čuva tokom zamene
6. **Download** generisanog dokumenta je moguć preko API-ja

## 🔧 Troubleshooting

### Česte greške
1. **Template fajl ne postoji** - Proveriti putanju template fajla
2. **Crvena slova se ne zamenjuju** - Proveriti pattern matching
3. **Generisani fajl je prazan** - Proveriti podatke iz T1/T2 tabela
4. **Download ne radi** - Proveriti CORS konfiguraciju

### Logovi
```bash
# Proveriti logove aplikacije
tail -f logs/application.log | grep "WordTemplate"
```
