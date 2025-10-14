# Frontend Instrukcije - Kategorija Osnova Prava

## 📋 Pregled

Ova dokumentacija opisuje kako koristiti novo polje **`kategorijaOsnovaPrava`** u OДБИЈА СЕ NSP,UNSP,DD,UDTNP dokumentu.

## 🔧 Backend Izmene

### Novo polje u DTO
Dodato je novo polje `kategorijaOsnovaPrava` u `OdbijaSeNSPRequestDTO`:

```java
/**
 * Kategorija osnova prava za dugi placeholder.
 * Frontend šalje specifičnu kategoriju umesto placeholder-a sa svim opcijama.
 * Primer: "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ"
 */
private String kategorijaOsnovaPrava;
```

### Logika
Backend sada koristi `kategorijaOsnovaPrava` polje za zamenjivanje dugog placeholder-a:
```
{{на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица, односно није корисник права на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица}}
```

## 📝 Frontend Implementacija

### 1. Dodaj novo polje u formu

```typescript
interface OdbijaSeNSPFormData {
  // ... postojeća polja ...
  
  /**
   * Kategorija osnova prava za dugi placeholder.
   * Frontend određuje tačnu kategoriju na osnovu osnovPrava polja.
   */
  kategorijaOsnovaPrava: string;
}
```

### 2. Logika za određivanje kategorije

```typescript
const getKategorijaOsnovaPrava = (osnovPrava: string): string => {
  switch (osnovPrava.toUpperCase()) {
    case "NSP":
    case "НСП":
      return "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ";
      
    case "UNSP":
    case "УНСП":
      return "на увећану новчану социјалну помоћ, односно није корисник права на увећану новчану социјалну помоћ";
      
    case "DD":
    case "ДД":
      return "на дечији додатaк, односно није корисник права на дечији додатaк";
      
    case "UDTNP":
    case "UDДНЛ":
    case "УДДНЛ":
    case "УДТНП":
      return "на увећани додатак за помоћ и негу другог лица, односно није корисник права на увећани додатак за помоћ и негу другог лица";
      
    default:
      // Fallback - vraća osnov prava
      return `на ${osnovPrava}`;
  }
};
```

### 3. Korišćenje u formi

```jsx
// React komponenta
const [formData, setFormData] = useState<OdbijaSeNSPFormData>({
  // ... ostala polja ...
  osnovPrava: "NSP",
  kategorijaOsnovaPrava: "", // Ovo će biti automatski popunjeno
});

// Automatski ažuriraj kategoriju kada se promeni osnov prava
useEffect(() => {
  if (formData.osnovPrava) {
    const kategorija = getKategorijaOsnovaPrava(formData.osnovPrava);
    setFormData(prev => ({
      ...prev,
      kategorijaOsnovaPrava: kategorija
    }));
  }
}, [formData.osnovPrava]);

// Ili direktno u onChange handler-u
const handleOsnovPravaChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
  const osnovPrava = e.target.value;
  const kategorija = getKategorijaOsnovaPrava(osnovPrava);
  
  setFormData(prev => ({
    ...prev,
    osnovPrava: osnovPrava,
    kategorijaOsnovaPrava: kategorija
  }));
};
```

### 4. Select opcije

```jsx
<select 
  value={formData.osnovPrava}
  onChange={handleOsnovPravaChange}
>
  <option value="NSP">НСП - Новчана социјална помоћ</option>
  <option value="UNSP">УНСП - Увећана новчана социјална помоћ</option>
  <option value="DD">ДД - Дечији додатaк</option>
  <option value="UDTNP">УДТНП - Увећани додатак за помоћ и негу другог лица</option>
</select>
```

## 📤 API Request

### Request Body

```json
{
  "brojPredmeta": "123/2025",
  "datumDonosenja": "2025-01-15",
  "brojOvlascenja": "456/2025",
  "datumOvlascenja": "2025-01-10",
  "imeIPrezimeOvlascenog": "Марија Петровић",
  "imeIPrezimePodnosioca": "Петар Јовановић",
  "jmbg": "0101990123456",
  "grad": "Београд",
  "ulica": "Кнеза Милоша",
  "brojStana": "15",
  "opstina": "Савски Венац",
  "pttBroj": "11000",
  "mestoStanovanja": "Београд",
  "datumPodnosenja": "2025-01-05",
  "osnovPrava": "НСП",
  "kategorijaOsnovaPrava": "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ",
  "brojClanovaDomacinstava": "4",
  "prilozenaInfo": "1. Потврда о приходима\\n2. Извод из матичне књиге рођених",
  "sluzbeniDokazi": "Подаци о приходима из Пореске управе",
  "dodatniTekst": null,
  "vrsilacDuznosti": true,
  "sopstveneRuke": true,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

### Endpoint

```
POST /api/dokumenti/odbija-se-nsp/generisi
Content-Type: application/json
```

## ✅ Test Scenariji

### Scenario 1: NSP
- **Input**: `"osnovPrava": "NSP"`
- **Očekivani kategorijaOsnovaPrava**: `"на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ"`

### Scenario 2: UNSP
- **Input**: `"osnovPrava": "UNSP"`
- **Očekivani kategorijaOsnovaPrava**: `"на увећану новчану социјалну помоћ, односно није корисник права на увећану новчану социјалну помоћ"`

### Scenario 3: DD
- **Input**: `"osnovPrava": "DD"`
- **Očekivani kategorijaOsnovaPrava**: `"на дечији додатaк, односно није корисник права на дечији додатaк"`

### Scenario 4: UDTNP
- **Input**: `"osnovPrava": "UDTNP"`
- **Očekivani kategorijaOsnovaPrava**: `"на увећани додатак за помоћ и негу другог лица, односно није корисник права на увећани додатак за помоћ и негу другог лица"`

## 🔄 Backward Compatibility

- **Ako frontend NE pošalje `kategorijaOsnovaPrava`** → backend koristi staru logiku (fallback)
- **Ako frontend pošalje `kategorijaOsnovaPrava`** → backend koristi novu vrednost

## 📋 Validacija

```typescript
const validateKategorijaOsnovaPrava = (kategorija: string): string[] => {
  const errors: string[] = [];
  
  if (!kategorija || kategorija.trim().length === 0) {
    errors.push("Kategorija osnova prava je obavezna");
  }
  
  // Proveri da li sadrži ključne reči
  if (!kategorija.includes("односно није корисник права на")) {
    errors.push("Kategorija mora sadržavati 'односно није корисник права на'");
  }
  
  return errors;
};
```

## 🎯 Zaključak

**Frontend treba da:**

1. **Doda novo polje** `kategorijaOsnovaPrava` u formu
2. **Implementira logiku** za automatsko određivanje kategorije na osnovu `osnovPrava`
3. **Pošalje oba polja** u API request-u:
   - `osnovPrava` - skraćenica (NSP, UNSP, DD, UDTNP)
   - `kategorijaOsnovaPrava` - puni tekst za dugi placeholder

**Backend će automatski koristiti `kategorijaOsnovaPrava` za zamenjivanje dugog placeholder-a!** 🚀
