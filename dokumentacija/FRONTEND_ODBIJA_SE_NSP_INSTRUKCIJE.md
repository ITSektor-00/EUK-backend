# Frontend Instrukcije za OДБИЈА СЕ NSP,UNSP,DD,UDTNP

## 📋 Pregled

Ovaj dokument opisuje potrebne izmene na frontend-u za implementaciju funkcionalnosti "OДБИЈА СЕ NSP,UNSP,DD,UDTNP" sa automatskim čuvanjem podataka u `ugrozeno_lice_t1` tabelu.

## 🔧 Backend Endpoints

### 1. Čuvanje podataka u bazu (NOVI)

**POST** `/api/dokumenti/odbija-se-nsp/sacuvaj-podatke`

**Opis:** Čuva podatke o ugroženom licu direktno u `ugrozeno_lice_t1` tabelu bez generisanja dokumenta.

**Request Body:**
```json
{
  "imeIPrezimePodnosioca": "Петар Јовановић",
  "jmbg": "0101990123456",
  "grad": "Београд",
  "ulica": "Кнеза Милоша",
  "brojStana": "15",
  "opstina": "Савски Венац",
  "pttBroj": "11000",
  "mestoStanovanja": "Београд",
  "osnovPrava": "НСП",
  "brojClanovaDomacinstava": "4"
}
```

**Response:**
```json
{
  "message": "Podaci uspešno sačuvani u bazu"
}
```

### 2. Generisanje dokumenta (AŽURIRAN)

**POST** `/api/dokumenti/odbija-se-nsp/generisi`

**Opis:** Generiše Word dokument i **automatski čuva podatke u bazu**.

**Request Body:** Isti kao i ranije, ali sada automatski čuva podatke u `ugrozeno_lice_t1`.

## 📝 Frontend Implementacija

### 1. Pop-up forma za podatke o podnosiocu

Kreirati pop-up formu sa sledećim poljima:

```typescript
interface PodnosiocPodaci {
  imeIPrezimePodnosioca: string;    // Ime i prezime podnosioca
  jmbg: string;                    // JMBG (13 cifara)
  grad: string;                     // Grad
  ulica: string;                   // Ulica
  brojStana: string;               // Broj stana/kuće
  opstina: string;                 // Opština
  pttBroj: string;                // PTT broj
  mestoStanovanja: string;         // Mesto stanovanja
  osnovPrava: string;              // Osnov prava (NSP/UNSP/DD/UDTNP)
  brojClanovaDomacinstava: string; // Broj članova domaćinstva
}
```

### 2. Validacija polja

```typescript
const validatePodnosiocPodaci = (podaci: PodnosiocPodaci): string[] => {
  const errors: string[] = [];
  
  if (!podaci.imeIPrezimePodnosioca?.trim()) {
    errors.push("Ime i prezime podnosioca je obavezno");
  }
  
  if (!podaci.jmbg || !/^\d{13}$/.test(podaci.jmbg)) {
    errors.push("JMBG mora imati tačno 13 cifara");
  }
  
  if (!podaci.grad?.trim()) {
    errors.push("Grad je obavezan");
  }
  
  if (!podaci.ulica?.trim()) {
    errors.push("Ulica je obavezna");
  }
  
  if (!podaci.brojStana?.trim()) {
    errors.push("Broj stana je obavezan");
  }
  
  if (!podaci.opstina?.trim()) {
    errors.push("Opština je obavezna");
  }
  
  if (!podaci.pttBroj?.trim()) {
    errors.push("PTT broj je obavezan");
  }
  
  if (!podaci.mestoStanovanja?.trim()) {
    errors.push("Mesto stanovanja je obavezno");
  }
  
  if (!podaci.osnovPrava?.trim()) {
    errors.push("Osnov prava je obavezan");
  }
  
  if (!podaci.brojClanovaDomacinstava || !/^\d+$/.test(podaci.brojClanovaDomacinstava)) {
    errors.push("Broj članova domaćinstva mora biti broj");
  }
  
  return errors;
};
```

### 3. API pozivi

```typescript
// Čuvanje podataka u bazu
const sacuvajPodatke = async (podaci: PodnosiocPodaci) => {
  try {
    const response = await fetch('/api/dokumenti/odbija-se-nsp/sacuvaj-podatke', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(podaci)
    });
    
    if (response.ok) {
      const result = await response.text();
      console.log('Podaci uspešno sačuvani:', result);
      return true;
    } else {
      throw new Error('Greška pri čuvanju podataka');
    }
  } catch (error) {
    console.error('Greška:', error);
    return false;
  }
};

// Generisanje dokumenta (automatski čuva podatke)
const generisiDokument = async (requestData: OdbijaSeNSPRequestDTO) => {
  try {
    const response = await fetch('/api/dokumenti/odbija-se-nsp/generisi', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestData)
    });
    
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ODBIJA_SE_NSP_${requestData.brojPredmeta}.docx`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      return true;
    } else {
      throw new Error('Greška pri generisanju dokumenta');
    }
  } catch (error) {
    console.error('Greška:', error);
    return false;
  }
};
```

### 4. UI Komponenta

```tsx
interface OdbijaSeNSPFormProps {
  onSave: (podaci: PodnosiocPodaci) => void;
  onGenerate: (requestData: OdbijaSeNSPRequestDTO) => void;
}

const OdbijaSeNSPForm: React.FC<OdbijaSeNSPFormProps> = ({ onSave, onGenerate }) => {
  const [podnosiocPodaci, setPodnosiocPodaci] = useState<PodnosiocPodaci>({
    imeIPrezimePodnosioca: '',
    jmbg: '',
    grad: '',
    ulica: '',
    brojStana: '',
    opstina: '',
    pttBroj: '',
    mestoStanovanja: '',
    osnovPrava: '',
    brojClanovaDomacinstava: ''
  });

  const [errors, setErrors] = useState<string[]>([]);

  const handleSave = () => {
    const validationErrors = validatePodnosiocPodaci(podnosiocPodaci);
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      return;
    }
    
    setErrors([]);
    onSave(podnosiocPodaci);
  };

  return (
    <div className="odbija-se-nsp-form">
      <h3>Podaci o podnosiocu zahteva</h3>
      
      {errors.length > 0 && (
        <div className="error-messages">
          {errors.map((error, index) => (
            <div key={index} className="error">{error}</div>
          ))}
        </div>
      )}
      
      <div className="form-group">
        <label>Ime i prezime podnosioca:</label>
        <input
          type="text"
          value={podnosiocPodaci.imeIPrezimePodnosioca}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, imeIPrezimePodnosioca: e.target.value})}
          placeholder="Unesite ime i prezime"
        />
      </div>
      
      <div className="form-group">
        <label>JMBG:</label>
        <input
          type="text"
          value={podnosiocPodaci.jmbg}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, jmbg: e.target.value})}
          placeholder="13 cifara"
          maxLength={13}
        />
      </div>
      
      <div className="form-group">
        <label>Grad:</label>
        <input
          type="text"
          value={podnosiocPodaci.grad}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, grad: e.target.value})}
          placeholder="Unesite grad"
        />
      </div>
      
      <div className="form-group">
        <label>Ulica:</label>
        <input
          type="text"
          value={podnosiocPodaci.ulica}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, ulica: e.target.value})}
          placeholder="Unesite ulicu"
        />
      </div>
      
      <div className="form-group">
        <label>Broj stana/kuće:</label>
        <input
          type="text"
          value={podnosiocPodaci.brojStana}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, brojStana: e.target.value})}
          placeholder="Unesite broj stana/kuće"
        />
      </div>
      
      <div className="form-group">
        <label>Opština:</label>
        <input
          type="text"
          value={podnosiocPodaci.opstina}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, opstina: e.target.value})}
          placeholder="Unesite opštinu"
        />
      </div>
      
      <div className="form-group">
        <label>PTT broj:</label>
        <input
          type="text"
          value={podnosiocPodaci.pttBroj}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, pttBroj: e.target.value})}
          placeholder="Unesite PTT broj"
        />
      </div>
      
      <div className="form-group">
        <label>Mesto stanovanja:</label>
        <input
          type="text"
          value={podnosiocPodaci.mestoStanovanja}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, mestoStanovanja: e.target.value})}
          placeholder="Unesite mesto stanovanja"
        />
      </div>
      
      <div className="form-group">
        <label>Osnov prava:</label>
        <select
          value={podnosiocPodaci.osnovPrava}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, osnovPrava: e.target.value})}
        >
          <option value="">Izaberite osnov prava</option>
          <option value="NSP">NSP - Novčana socijalna pomoć</option>
          <option value="UNSP">UNSP - Uvećana novčana socijalna pomoć</option>
          <option value="DD">DD - Dečiji dodatak</option>
          <option value="UDTNP">UDTNP - Uvećani dodatak za pomoć i negu drugog lica</option>
        </select>
      </div>
      
      <div className="form-group">
        <label>Broj članova domaćinstva:</label>
        <input
          type="number"
          value={podnosiocPodaci.brojClanovaDomacinstava}
          onChange={(e) => setPodnosiocPodaci({...podnosiocPodaci, brojClanovaDomacinstava: e.target.value})}
          placeholder="Unesite broj članova"
          min="1"
        />
      </div>
      
      <div className="form-actions">
        <button onClick={handleSave} className="btn-save">
          Sačuvaj podatke u bazu
        </button>
        <button onClick={() => onGenerate({...podnosiocPodaci, /* ostala polja */})} className="btn-generate">
          Generiši dokument
        </button>
      </div>
    </div>
  );
};
```

## 🗄️ Mapiranje podataka

Frontend podaci se mapiraju u bazu na sledeći način:

| Frontend polje | Baza kolona | Opis |
|----------------|-------------|------|
| `imeIPrezimePodnosioca` | `ime` + `prezime` | Automatski se razdvaja |
| `jmbg` | `jmbg` | Direktno mapiranje |
| `pttBroj` | `ptt_broj` | Direktno mapiranje |
| `opstina` | `grad_opstina` | Direktno mapiranje |
| `mestoStanovanja` | `mesto` | Direktno mapiranje |
| `ulica` + `brojStana` | `ulica_i_broj` | Kombinuje se |
| `brojClanovaDomacinstava` | `broj_clanova_domacinstva` | Direktno mapiranje |
| `osnovPrava` | `osnov_sticanja_statusa` | Direktno mapiranje |
| - | `ed_broj_broj_mernog_uredjaja` | Prazno po default-u |
| - | `redni_broj` | Automatski generisan |

## ✅ Testiranje

1. **Test čuvanja podataka:**
   - Popuniti sva obavezna polja
   - Kliknuti "Sačuvaj podatke u bazu"
   - Proveriti da li se podaci čuvaju u `ugrozeno_lice_t1` tabeli

2. **Test generisanja dokumenta:**
   - Popuniti sva obavezna polja
   - Kliknuti "Generiši dokument"
   - Proveriti da li se dokument generiše i da li se podaci automatski čuvaju u bazu

3. **Test validacije:**
   - Pokušati sačuvati sa praznim poljima
   - Pokušati sačuvati sa neispravnim JMBG-om
   - Proveriti da li se prikazuju odgovarajuće greške

## 🔄 Workflow

1. Korisnik otvara pop-up formu za "OДБИЈА СЕ NSP,UNSP,DD,UDTNP"
2. Popunjava podatke o podnosiocu zahteva
3. Može da:
   - **Samo sačuva podatke** u bazu (novi endpoint)
   - **Generiše dokument** (automatski čuva podatke + generiše Word)
4. Podaci se automatski mapiraju u `ugrozeno_lice_t1` tabelu
5. Dokument se preuzima kao .docx fajl

## 📞 Kontakt

Za dodatna pitanja ili probleme, kontaktirajte backend tim.
