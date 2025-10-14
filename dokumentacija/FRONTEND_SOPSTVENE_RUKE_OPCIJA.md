# Frontend Instrukcije - "с.р." (сопствене руке) Opcija

## 📋 Pregled

Ova dokumentacija opisuje kako koristiti opciju **"с.р." (сопствене руке)** u OДБИЈА СЕ NSP,UNSP,DD,UDTNP dokumentu.

## 🔧 Backend Implementacija

### Logika
- **Ako je `sopstveneRuke: true`** → u dokumentu se **PISАЈЕ** "с.р."
- **Ako je `sopstveneRuke: false`** → iz dokumenta se **UKLANJA** "с.р."

### Placeholder sistem
Backend koristi placeholder `{{с.р.}}` u Word template-u koji se zamenjuje:
- **TRUE**: `{{с.р.}}` → `с.р.`
- **FALSE**: `{{с.р.}}` → (prazan string - obriše se)

## 📝 Frontend Implementacija

### 1. Dodaj checkbox u formu

```typescript
// U formi za generisanje OДБИЈА СЕ NSP dokumenta
interface OdbijaSeNSPFormData {
  // ... ostala polja ...
  
  /**
   * Da li u tekstu ide "с.р." (сопствене руке)?
   * Ako je FALSE, "с.р." će biti uklonjeno iz dokumenta.
   */
  sopstveneRuke: boolean;
}
```

### 2. UI Element

```jsx
// React komponenta
<div className="form-group">
  <label className="checkbox-label">
    <input 
      type="checkbox"
      checked={formData.sopstveneRuke}
      onChange={(e) => setFormData({
        ...formData,
        sopstveneRuke: e.target.checked
      })}
    />
    <span className="checkbox-text">
      Да ли у тексту иде "с.р." (сопствене руке)?
    </span>
  </label>
  <small className="form-text text-muted">
    Ако није означено, "с.р." ће бити уклоњено из документа.
  </small>
</div>
```

### 3. CSS Stilizovanje

```css
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  margin-bottom: 8px;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.checkbox-text {
  font-size: 14px;
  font-weight: 500;
}

.form-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 4px;
}
```

## 🔄 API Request

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
  "brojClanovaDomacinstava": "4",
  "prilozenaInfo": "1. Потврда о приходима\\n2. Извод из матичне књиге рођених",
  "sluzbeniDokazi": "Подаци о приходима из Пореске управе",
  "dodatniTekst": null,
  "vrsilacDuznosti": true,
  "sopstveneRuke": true,  // ← OVAJ PARAMETAR KONTROLIŠE "с.р."
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

### Scenario 1: sopstveneRuke = true
- **Input**: `"sopstveneRuke": true`
- **Očekivani rezultat**: Dokument sadrži "с.р." na svim mestima gde je bio placeholder `{{с.р.}}`

### Scenario 2: sopstveneRuke = false  
- **Input**: `"sopstveneRuke": false`
- **Očekivani rezultat**: Dokument NE sadrži "с.р." - svi placeholder-i `{{с.р.}}` su uklonjeni

## 🧪 Test Fajlovi

Kreirani su test fajlovi:
- `test-sr-option.json` - za testiranje sa `sopstveneRuke: true`
- `test-sr-option-false.json` - za testiranje sa `sopstveneRuke: false`
- `test-sr-option.bat` - batch script za testiranje

## 📋 Validacija

```typescript
const validateSopstveneRuke = (sopstveneRuke: boolean): string[] => {
  const errors: string[] = [];
  
  // sopstveneRuke je boolean, nema potrebe za validaciju
  // Samo proveri da nije undefined/null
  if (sopstveneRuke === undefined || sopstveneRuke === null) {
    errors.push("Opcija 'с.р.' mora biti definisana");
  }
  
  return errors;
};
```

## 🎯 Zaključak

**"с.р." opcija je potpuno implementirana u backend-u!**

Frontend treba samo da:
1. Doda checkbox sa label-om "Да ли у тексту иде 'с.р.' (сопствене руке)?"
2. Pošalje `sopstveneRuke` boolean vrednost u API request-u
3. Backend će automatski obrisati ili zadržati "с.р." u dokumentu na osnovu ove vrednosti

**Nema potrebe za dodatnim backend izmenama!** 🎉
