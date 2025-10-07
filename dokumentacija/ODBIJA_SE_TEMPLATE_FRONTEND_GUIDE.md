# Frontend Implementacija za OДБИЈА СЕ NSP,UNSP,DD,UDTNP Template

## Pregled

Ovaj dokument opisuje potrebne izmene na frontend-u za implementaciju generisanja Word template-a za rešenje "OДБИЈА СЕ NSP,UNSP,DD,UDTNP".

## Nova polja u ManualDataDto

Backend je proširen sa sledećim poljima u `ManualDataDto`:

### Ručno uneti podaci (obavezni)
- `predmet` - (предмета)_____________________
- `datumDonosenjaResenja` - (доношења решења)_____________године
- `brojResenja` - (број)___________________________
- `datumOvlastenja` - (датум овлашћења)________________
- `datumPodnosenja` - _______________(датум подношења)

### Podaci iz baze (automatski se popunjavaju)
- `imePrezimeLica` - (име и презиме)___________ - iz T1/T2 tabele
- `ulicaIBroj` - Ул .__________бр._______ - iz T1/T2 tabele
- `imePrezimePravnogLica` - (име и презиме)__________________ - iz T1/T2 tabele
- `jmbgPravnogLica` - ЈМБГ ___________ - iz T1/T2 tabele
- `adresaPravnogLica` - ________________(град, улица и број, општина, ПТТ број) - iz T1/T2 tabele
- `imePrezimePodnosioca` - (име и презиме)__________________ - iz T1/T2 tabele
- `jmbgPodnosioca` - ЈМБГ ___________ - iz T1/T2 tabele
- `adresaPodnosioca` - из __________,Ул. ____________ бр. ______ - iz T1/T2 tabele

### Opciona polja
- `dodatniTekst` - Оставити простор да се, уколико има потребе, убаци још неки део текста

### Logička polja (checkbox-ovi)
- `pribavljaDokumentaciju` - da li se pribavlja dokumentacija po službenoj dužnosti
- `vdPotpis` - da li je в.д. ili ne
- `srPotpis` - da li ima potpis с.р.

## Frontend Implementacija

### 1. Ažuriranje TypeScript interfejsa

```typescript
interface ManualData {
    // Postojeća polja
    zaglavlje: string;
    obrazlozenje: string;
    dodatniPodaci?: string;
    
    // Nova polja za OДБИЈА СЕ template
    predmet?: string;
    datumDonosenjaResenja?: string;
    brojResenja?: string;
    datumOvlastenja?: string;
    datumPodnosenja?: string;
    imePrezimeLica?: string;
    ulicaIBroj?: string;
    imePrezimePravnogLica?: string;
    jmbgPravnogLica?: string;
    adresaPravnogLica?: string;
    imePrezimePodnosioca?: string;
    jmbgPodnosioca?: string;
    adresaPodnosioca?: string;
    dodatniTekst?: string;
    pribavljaDokumentaciju?: boolean;
    vdPotpis?: boolean;
    srPotpis?: boolean;
}
```

### 2. Komponenta za ručne podatke

```tsx
import React, { useState, useEffect } from 'react';

interface ManualDataFormProps {
    manualData: ManualData;
    setManualData: (data: ManualData) => void;
    liceData?: any; // Podaci iz T1/T2 tabele
}

const ManualDataForm: React.FC<ManualDataFormProps> = ({ 
    manualData, 
    setManualData, 
    liceData 
}) => {
    
    // Automatsko popunjavanje podataka iz baze
    useEffect(() => {
        if (liceData) {
            setManualData({
                ...manualData,
                imePrezimeLica: `${liceData.ime || ''} ${liceData.prezime || ''}`.trim(),
                ulicaIBroj: liceData.ulicaIBroj || '',
                imePrezimePravnogLica: `${liceData.ime || ''} ${liceData.prezime || ''}`.trim(),
                jmbgPravnogLica: liceData.jmbg || '',
                adresaPravnogLica: buildFullAddress(liceData),
                imePrezimePodnosioca: `${liceData.ime || ''} ${liceData.prezime || ''}`.trim(),
                jmbgPodnosioca: liceData.jmbg || '',
                adresaPodnosioca: buildFullAddress(liceData)
            });
        }
    }, [liceData]);

    const buildFullAddress = (data: any) => {
        const parts = [];
        if (data.gradOpstina) parts.push(data.gradOpstina);
        if (data.ulicaIBroj) parts.push(data.ulicaIBroj);
        if (data.mesto) parts.push(data.mesto);
        if (data.pttBroj) parts.push(data.pttBroj);
        return parts.join(', ');
    };

    return (
        <div className="manual-data-form">
            <h3>Podaci za rešenje OДБИЈА СЕ NSP,UNSP,DD,UDTNP</h3>
            
            {/* Osnovni podaci */}
            <div className="form-section">
                <h4>Osnovni podaci</h4>
                
                <div className="form-group">
                    <label>Predmet *</label>
                    <input
                        type="text"
                        value={manualData.predmet || ''}
                        onChange={(e) => setManualData({...manualData, predmet: e.target.value})}
                        placeholder="Unesite predmet..."
                        required
                    />
                </div>
                
                <div className="form-group">
                    <label>Datum donošenja rešenja *</label>
                    <input
                        type="text"
                        value={manualData.datumDonosenjaResenja || ''}
                        onChange={(e) => setManualData({...manualData, datumDonosenjaResenja: e.target.value})}
                        placeholder="Unesite datum donošenja rešenja..."
                        required
                    />
                </div>
                
                <div className="form-group">
                    <label>Broj rešenja *</label>
                    <input
                        type="text"
                        value={manualData.brojResenja || ''}
                        onChange={(e) => setManualData({...manualData, brojResenja: e.target.value})}
                        placeholder="Unesite broj rešenja..."
                        required
                    />
                </div>
                
                <div className="form-group">
                    <label>Datum ovlašćenja *</label>
                    <input
                        type="text"
                        value={manualData.datumOvlastenja || ''}
                        onChange={(e) => setManualData({...manualData, datumOvlastenja: e.target.value})}
                        placeholder="Unesite datum ovlašćenja..."
                        required
                    />
                </div>
                
                <div className="form-group">
                    <label>Datum podnošenja *</label>
                    <input
                        type="text"
                        value={manualData.datumPodnosenja || ''}
                        onChange={(e) => setManualData({...manualData, datumPodnosenja: e.target.value})}
                        placeholder="Unesite datum podnošenja..."
                        required
                    />
                </div>
            </div>
            
            {/* Podaci iz baze (read-only) */}
            <div className="form-section">
                <h4>Podaci iz baze (automatski popunjeni)</h4>
                
                <div className="form-group">
                    <label>Ime i prezime lica</label>
                    <input
                        type="text"
                        value={manualData.imePrezimeLica || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>Ulica i broj</label>
                    <input
                        type="text"
                        value={manualData.ulicaIBroj || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>Ime i prezime pravnog lica</label>
                    <input
                        type="text"
                        value={manualData.imePrezimePravnogLica || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>JMBG pravnog lica</label>
                    <input
                        type="text"
                        value={manualData.jmbgPravnogLica || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>Adresa pravnog lica</label>
                    <input
                        type="text"
                        value={manualData.adresaPravnogLica || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>Ime i prezime podnosioca</label>
                    <input
                        type="text"
                        value={manualData.imePrezimePodnosioca || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>JMBG podnosioca</label>
                    <input
                        type="text"
                        value={manualData.jmbgPodnosioca || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
                
                <div className="form-group">
                    <label>Adresa podnosioca</label>
                    <input
                        type="text"
                        value={manualData.adresaPodnosioca || ''}
                        readOnly
                        className="readonly"
                    />
                </div>
            </div>
            
            {/* Opciona polja */}
            <div className="form-section">
                <h4>Opciona polja</h4>
                
                <div className="form-group">
                    <label>Dodatni tekst</label>
                    <textarea
                        value={manualData.dodatniTekst || ''}
                        onChange={(e) => setManualData({...manualData, dodatniTekst: e.target.value})}
                        placeholder="Dodatni tekst ako je potreban..."
                        rows={3}
                    />
                </div>
            </div>
            
            {/* Logička polja - OBAVEZNO PITATI PRE GENERISANJA */}
            <div className="form-section">
                <h4>Potpisi u rešenju (obavezno odrediti pre generisanja)</h4>
                <p className="info-text">
                    <strong>VAŽNO:</strong> Pre generisanja dokumenta, morate odrediti koji potpisi će biti uključeni u rešenje.
                </p>
                
                <div className="form-group checkbox-group">
                    <label>
                        <input
                            type="checkbox"
                            checked={manualData.pribavljaDokumentaciju || false}
                            onChange={(e) => setManualData({...manualData, pribavljaDokumentaciju: e.target.checked})}
                        />
                        Pribavlja dokumentaciju po službenoj dužnosti
                        <span className="help-text">(Ako je označeno, uključuje se tekst o pribavljanju dokumenata)</span>
                    </label>
                </div>
                
                <div className="form-group checkbox-group">
                    <label>
                        <input
                            type="checkbox"
                            checked={manualData.vdPotpis || false}
                            onChange={(e) => setManualData({...manualData, vdPotpis: e.target.checked})}
                        />
                        в.д. potpis
                        <span className="help-text">(Ako je označeno, "в.д." će biti uključeno u rešenje)</span>
                    </label>
                </div>
                
                <div className="form-group checkbox-group">
                    <label>
                        <input
                            type="checkbox"
                            checked={manualData.srPotpis || false}
                            onChange={(e) => setManualData({...manualData, srPotpis: e.target.checked})}
                        />
                        с.р. potpis
                        <span className="help-text">(Ako je označeno, "с.р." će biti uključeno u rešenje)</span>
                    </label>
                </div>
            </div>
        </div>
    );
};

export default ManualDataForm;
```

### 3. CSS stilovi

```css
.manual-data-form {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

.form-section {
    margin-bottom: 30px;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #f9f9f9;
}

.form-section h4 {
    margin-top: 0;
    color: #333;
    border-bottom: 2px solid #007bff;
    padding-bottom: 10px;
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #555;
}

.form-group input,
.form-group textarea {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
}

.form-group input.readonly {
    background-color: #f5f5f5;
    color: #666;
    cursor: not-allowed;
}

.checkbox-group label {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
    width: auto;
    margin-right: 10px;
}

.form-group textarea {
    resize: vertical;
    min-height: 80px;
}

.info-text {
    background-color: #e7f3ff;
    border: 1px solid #b3d9ff;
    border-radius: 4px;
    padding: 10px;
    margin-bottom: 15px;
    color: #0066cc;
}

.help-text {
    display: block;
    font-size: 12px;
    color: #666;
    font-style: italic;
    margin-top: 5px;
}
```

### 4. Validacija

```typescript
const validateManualData = (manualData: ManualData): string[] => {
    const errors: string[] = [];
    
    // Obavezna polja
    if (!manualData.predmet?.trim()) {
        errors.push('Predmet je obavezan');
    }
    
    if (!manualData.datumDonosenjaResenja?.trim()) {
        errors.push('Datum donošenja rešenja je obavezan');
    }
    
    if (!manualData.brojResenja?.trim()) {
        errors.push('Broj rešenja je obavezan');
    }
    
    if (!manualData.datumOvlastenja?.trim()) {
        errors.push('Datum ovlašćenja je obavezan');
    }
    
    if (!manualData.datumPodnosenja?.trim()) {
        errors.push('Datum podnošenja je obavezan');
    }
    
    // OBAVEZNO: Potpisi moraju biti eksplicitno odredjeni
    if (manualData.vdPotpis === undefined) {
        errors.push('Morate odrediti da li će biti в.д. potpis u rešenju');
    }
    
    if (manualData.srPotpis === undefined) {
        errors.push('Morate odrediti da li će biti с.р. potpis u rešenju');
    }
    
    if (manualData.pribavljaDokumentaciju === undefined) {
        errors.push('Morate odrediti da li se pribavlja dokumentacija po službenoj dužnosti');
    }
    
    return errors;
};
```

### 5. API poziv

```typescript
const generateTemplate = async (request: TemplateGenerationRequest) => {
    try {
        const response = await fetch('/api/template/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(request)
        });
        
        if (!response.ok) {
            throw new Error('Greška pri generisanju template-a');
        }
        
        const result = await response.json();
        return result;
    } catch (error) {
        console.error('Greška:', error);
        throw error;
    }
};
```

## Implementacija koraka

1. **Ažuriranje interfejsa** - Dodajte nova polja u `ManualData` interfejs
2. **Kreiranje komponente** - Implementirajte `ManualDataForm` komponentu
3. **Dodavanje stilova** - Dodajte CSS stilove za novu komponentu
4. **Implementacija validacije** - Dodajte validaciju za obavezna polja
5. **Integracija sa postojećim kodom** - Integrišite novu komponentu u postojeći workflow

## Napomene

- Podaci iz baze se automatski popunjavaju na osnovu izabranog lica (T1 ili T2)
- Read-only polja se ne mogu menjati od strane korisnika
- **OBAVEZNO:** Pre generisanja dokumenta, korisnik mora eksplicitno odrediti:
  - Da li će biti **в.д.** potpis u rešenju
  - Da li će biti **с.р.** potpis u rešenju  
  - Da li se pribavlja dokumentacija po službenoj dužnosti
- Checkbox-ovi za logička polja kontrolišu da li će se određeni tekstovi prikazati u finalnom dokumentu
- Validacija se vrši pre slanja zahteva na backend
- Sve obavezne polja moraju biti popunjena pre generisanja template-a

## Logika potpisa

- **в.д. potpis = true**: "в.д." će biti uključeno u rešenje
- **в.д. potpis = false**: "в.д." će biti obrisano iz rešenja
- **с.р. potpis = true**: "с.р." će biti uključeno u rešenje  
- **с.р. potpis = false**: "с.р." će biti obrisano iz rešenja
- **Pribavlja dokumentaciju = true**: Uključuje se tekst o pribavljanju dokumenata
- **Pribavlja dokumentaciju = false**: Tekst o pribavljanju dokumenata se briše
