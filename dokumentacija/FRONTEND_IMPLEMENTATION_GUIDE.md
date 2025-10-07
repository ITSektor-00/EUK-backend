# 🚀 Frontend Implementation Guide - Word Template Generation

## 📋 Pregled sistema

Implementirajte sistem za generisanje Word rešenja sa hijerarhijskim izborom parametara i automatskom zamenu crvenih slova sa podacima iz baze.

## 🎯 Glavni tok

1. **Izbor lice** (T1 ili T2 tabela)
2. **Izbor kategorije** 
3. **Izbor obrasci_vrste** (negativno, neograniceno, ograniceno, borci, penzioneri, obustave)
4. **Izbor organizaciona_struktura** (sekretar, podsekretar)
5. **Unos ručnih podataka** (zaglavlje, obrazloženje)
6. **Generisanje Word dokumenta**

## 🔧 API Endpoints

### Glavni endpoint za generisanje

**POST** `/api/template/generate`

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

```typescript
// Dobijanje podataka za formu
GET /api/template/obrasci-vrste          // Svi obrasci vrste
GET /api/template/organizaciona-struktura // Sve organizacione strukture
GET /api/kategorije                      // Sve kategorije
GET /api/predmeti                        // Svi predmeti
GET /api/euk/t1                          // T1 ugrožena lica
GET /api/euk/t2                          // T2 ugrožena lica
```

## 🎨 Frontend komponente

### 1. Glavna komponenta

```typescript
import React, { useState } from 'react';

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

const WordTemplateGenerationForm: React.FC = () => {
    const [currentStep, setCurrentStep] = useState(0);
    const [formData, setFormData] = useState<TemplateGenerationRequest>({
        liceId: 0,
        liceTip: '',
        kategorijaId: 0,
        obrasciVrsteId: 0,
        organizacionaStrukturaId: 0,
        predmetId: 0,
        manualData: {
            zaglavlje: '',
            obrazlozenje: '',
            dodatniPodaci: ''
        }
    });

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

### 2. Stepper komponenta

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

### 3. Ručni podaci komponenta

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

### 4. API Service

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
    
    // Dobijanje obrasci vrste
    async getObrasciVrste(): Promise<ObrasciVrste[]> {
        const response = await fetch('/api/template/obrasci-vrste');
        return response.json();
    }
    
    // Dobijanje organizaciona struktura
    async getOrganizacionaStruktura(): Promise<OrganizacionaStruktura[]> {
        const response = await fetch('/api/template/organizaciona-struktura');
        return response.json();
    }
    
    // Dobijanje kategorija
    async getKategorije(): Promise<Kategorija[]> {
        const response = await fetch('/api/kategorije');
        return response.json();
    }
    
    // Dobijanje predmeti
    async getPredmeti(): Promise<Predmet[]> {
        const response = await fetch('/api/predmeti');
        return response.json();
    }
    
    // Dobijanje T1 lice
    async getT1Lice(): Promise<Lice[]> {
        const response = await fetch('/api/euk/t1');
        return response.json();
    }
    
    // Dobijanje T2 lice
    async getT2Lice(): Promise<Lice[]> {
        const response = await fetch('/api/euk/t2');
        return response.json();
    }
}
```

## 🎨 CSS stilovi

```css
.stepper {
    display: flex;
    justify-content: space-between;
    margin-bottom: 2rem;
}

.step {
    display: flex;
    align-items: center;
    flex: 1;
    opacity: 0.5;
    transition: opacity 0.3s;
}

.step.active {
    opacity: 1;
}

.step-number {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #e0e0e0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 1rem;
    font-weight: bold;
}

.step.active .step-number {
    background: #007bff;
    color: white;
}

.manual-data-step {
    max-width: 600px;
    margin: 0 auto;
}

.form-group {
    margin-bottom: 1.5rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: bold;
}

.form-group textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-family: inherit;
    resize: vertical;
}

.form-group textarea:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}
```

## 🔄 Validacija

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

## 🚀 Korišćenje

```typescript
const handleGenerate = async () => {
    try {
        setLoading(true);
        const response = await wordTemplateService.generateWordTemplate(formData);
        
        if (response.success) {
            // Download generisanog dokumenta
            await wordTemplateService.downloadTemplate(response.templateFilePath);
            alert('Word dokument je uspešno generisan!');
        } else {
            alert('Greška pri generisanju: ' + response.message);
        }
    } catch (error) {
        alert('Greška: ' + error.message);
    } finally {
        setLoading(false);
    }
};
```

## 📋 Checklist za implementaciju

- [ ] Kreirati Stepper komponentu
- [ ] Implementirati sve 6 koraka forme
- [ ] Dodati validaciju na svakom koraku
- [ ] Implementirati API service
- [ ] Dodati loading states
- [ ] Implementirati download funkcionalnost
- [ ] Dodati error handling
- [ ] Testirati sa backend-om

## 🎯 Ključne napomene

1. **Crvena slova** u template-u se automatski zamenjuju sa podacima
2. **Linije pored crvenih slova** se brišu
3. **Ručni podaci** se unose u zaglavlje i obrazloženje
4. **Generisani fajl** se automatski download-uje
5. **Svi endpointi** su već konfigurisani u backend-u

**Sistem je spreman za implementaciju!** 🚀
