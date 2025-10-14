# Frontend Forma - "OДБИЈА СЕ NSP,UNSP,DD,UDTNP"

## 📋 Kompletan Layout Forme

---

## 🎨 Struktura Forme

Forma treba da ima **4 sekcije** + **Modal sa pitanjima** pre izvoza:

1. **Zaglavlje dokumenta**
2. **Podaci o podnosiocu zahteva**
3. **Podaci za obrazloženje**
4. **Opcioni podaci (textarea)**
5. **Modal sa boolean pitanjima** (prikazuje se pre izvoza)

---

## 1️⃣ ZAGLAVLJE DOKUMENTA

### Polja:

| Label (SR)                     | Field Name              | Type   | Required | Validation | Placeholder                    |
|--------------------------------|-------------------------|--------|----------|------------|--------------------------------|
| Број предмета                  | `brojPredmeta`          | text   | ✅       | -          | "123/2025"                     |
| Датум доношења                 | `datumDonosenja`        | date   | ✅       | yyyy-MM-dd | "2025-01-15"                   |
| Број овлашћења                 | `brojOvlascenja`        | text   | ✅       | -          | "456/2025"                     |
| Датум овлашћења                | `datumOvlascenja`       | date   | ✅       | yyyy-MM-dd | "2025-01-10"                   |
| Име и презиме овлашћеног лица  | `imeIPrezimeOvlascenog` | text   | ✅       | -          | "Марија Петровић"              |

### UI Primer:

```tsx
<section className="form-section">
  <h2>Загlavље документа</h2>
  
  <div className="form-row">
    <div className="form-field">
      <label>Број предмета *</label>
      <input 
        type="text" 
        name="brojPredmeta" 
        placeholder="123/2025"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Датум доношења *</label>
      <input 
        type="date" 
        name="datumDonosenja" 
        required 
      />
    </div>
  </div>
  
  <div className="form-row">
    <div className="form-field">
      <label>Број овлашћења *</label>
      <input 
        type="text" 
        name="brojOvlascenja" 
        placeholder="456/2025"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Датум овлашћења *</label>
      <input 
        type="date" 
        name="datumOvlascenja" 
        required 
      />
    </div>
  </div>
  
  <div className="form-field full-width">
    <label>Име и презиме овлашћеног лица *</label>
    <input 
      type="text" 
      name="imeIPrezimeOvlascenog" 
      placeholder="Марија Петровић"
      required 
    />
  </div>
</section>
```

---

## 2️⃣ PODACI O PODNOSIOCU ZAHTEVA

### Polja:

| Label (SR)                  | Field Name              | Type   | Required | Validation       | Placeholder           |
|-----------------------------|-------------------------|--------|----------|------------------|-----------------------|
| Име и презиме подносиоца    | `imeIPrezimePodnosioca` | text   | ✅       | -                | "Петар Јовановић"     |
| ЈМБГ                        | `jmbg`                  | text   | ✅       | 13 cifara        | "0101990123456"       |
| Град                        | `grad`                  | text   | ✅       | -                | "Београд"             |
| Улица                       | `ulica`                 | text   | ✅       | -                | "Кнеза Милоша"        |
| Број стана/куће             | `brojStana`             | text   | ✅       | -                | "15"                  |
| Општина                     | `opstina`               | text   | ✅       | -                | "Савски Венац"        |
| ПТТ број                    | `pttBroj`               | text   | ✅       | -                | "11000"               |
| Место становања             | `mestoStanovanja`       | text   | ✅       | -                | "Београд"             |

### UI Primer:

```tsx
<section className="form-section">
  <h2>Подаци о подносиоцу захтева</h2>
  
  <div className="form-row">
    <div className="form-field">
      <label>Име и презиме подносиоца *</label>
      <input 
        type="text" 
        name="imeIPrezimePodnosioca" 
        placeholder="Петар Јовановић"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>ЈМБГ *</label>
      <input 
        type="text" 
        name="jmbg" 
        placeholder="0101990123456"
        pattern="[0-9]{13}"
        maxLength={13}
        required 
      />
      <small className="help-text">Унесите 13 цифара</small>
    </div>
  </div>
  
  <div className="form-row">
    <div className="form-field">
      <label>Град *</label>
      <input 
        type="text" 
        name="grad" 
        placeholder="Београд"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Улица *</label>
      <input 
        type="text" 
        name="ulica" 
        placeholder="Кнеза Милоша"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Број *</label>
      <input 
        type="text" 
        name="brojStana" 
        placeholder="15"
        required 
      />
    </div>
  </div>
  
  <div className="form-row">
    <div className="form-field">
      <label>Општина *</label>
      <input 
        type="text" 
        name="opstina" 
        placeholder="Савски Венац"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>ПТТ број *</label>
      <input 
        type="text" 
        name="pttBroj" 
        placeholder="11000"
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Место становања *</label>
      <input 
        type="text" 
        name="mestoStanovanja" 
        placeholder="Београд"
        required 
      />
    </div>
  </div>
</section>
```

---

## 3️⃣ PODACI ZA OBRAZLOŽENJE

### Polja:

| Label (SR)                  | Field Name                  | Type     | Required | Validation | Placeholder    |
|-----------------------------|-----------------------------|----------|----------|------------|----------------|
| Датум подношења             | `datumPodnosenja`           | date     | ✅       | yyyy-MM-dd | "2025-01-05"   |
| Основ права                 | `osnovPrava`                | select   | ✅       | -          | "НСП"          |
| Број чланова домаћинства    | `brojClanovaDomacinstava`   | number   | ✅       | > 0        | "4"            |

### `osnovPrava` - Dropdown opcije:

```tsx
const osnovPravaOptions = [
  { value: 'НСП', label: 'НСП - Новчана социјална помоћ' },
  { value: 'УНСП', label: 'УНСП - Увећана новчана социјална помоћ' },
  { value: 'ДД', label: 'ДД - Дечији додатак' },
  { value: 'УДТНП', label: 'УДТНП - Увећан дечији додатак за децу без родитељског старања' }
];
```

### UI Primer:

```tsx
<section className="form-section">
  <h2>Подаци за образложење</h2>
  
  <div className="form-row">
    <div className="form-field">
      <label>Датум подношења *</label>
      <input 
        type="date" 
        name="datumPodnosenja" 
        required 
      />
    </div>
    
    <div className="form-field">
      <label>Основ права *</label>
      <select name="osnovPrava" required>
        <option value="">-- Изаберите --</option>
        <option value="НСП">НСП - Новчана социјална помоћ</option>
        <option value="УНСП">УНСП - Увећана новчана социјална помоћ</option>
        <option value="ДД">ДД - Дечији додатак</option>
        <option value="УДТНП">УДТНП - Увећан дечији додатак</option>
      </select>
    </div>
    
    <div className="form-field">
      <label>Број чланова домаћинства *</label>
      <input 
        type="number" 
        name="brojClanovaDomacinstava" 
        placeholder="4"
        min="1"
        required 
      />
    </div>
  </div>
</section>
```

---

## 4️⃣ OPCIONI PODACI (Textarea)

### Polja:

| Label (SR)                      | Field Name          | Type     | Required | Rows | Placeholder                           |
|---------------------------------|---------------------|----------|----------|------|---------------------------------------|
| Приложена документација         | `prilozenaInfo`     | textarea | ❌       | 5    | "1. Потврда о приходима\n2. Извод..." |
| Службено прибављени докази      | `sluzbeniDokazi`    | textarea | ❌       | 5    | "Подаци о приходима из Пореске..."    |
| Додатни текст                   | `dodatniTekst`      | textarea | ❌       | 5    | "Опционо..."                          |

### UI Primer:

```tsx
<section className="form-section">
  <h2>Опциони подаци</h2>
  
  <div className="form-field full-width">
    <label>Приложена документација</label>
    <textarea 
      name="prilozenaInfo" 
      rows={5}
      placeholder="Унесите листу приложене документације (опционо)"
    />
    <small className="help-text">Свака ставка у новом реду</small>
  </div>
  
  <div className="form-field full-width">
    <label>Службено прибављени докази</label>
    <textarea 
      name="sluzbeniDokazi" 
      rows={5}
      placeholder="Унесите службено прибављене доказе (опционо)"
    />
  </div>
  
  <div className="form-field full-width">
    <label>Додатни текст</label>
    <textarea 
      name="dodatniTekst" 
      rows={5}
      placeholder="Унесите додатни текст ако је потребно (опционо)"
    />
  </div>
</section>
```

---

## 5️⃣ MODAL SA BOOLEAN PITANJIMA (PRE IZVOZA)

### Polja:

| Pitanje (SR)                                                          | Field Name                          | Type    | Default |
|-----------------------------------------------------------------------|-------------------------------------|---------|---------|
| Да ли у тексту иде "в.д." (вршилац дужности)?                        | `vrsilacDuznosti`                   | boolean | `true`  |
| Да ли у тексту иде "с.р." (сопствене руке)?                          | `sopstveneRuke`                     | boolean | `false` |
| Да ли се део о "Додатак за помоћ и негу другог лица..." односи на конкретан предмет? | `dodatakZaPomocOdnosiSe`            | boolean | `false` |
| Да ли се документација прибавља по службеној дужности?              | `pribavljaDokumentacijuSluzbeno`    | boolean | `true`  |

### UI Primer (Modal Component):

```tsx
const DodatnaPitanjaModal = ({ isOpen, onClose, onConfirm }) => {
  const [vrsilacDuznosti, setVrsilacDuznosti] = useState(true);
  const [sopstveneRuke, setSopstveneRuke] = useState(false);
  const [dodatakZaPomocOdnosiSe, setDodatakZaPomocOdnosiSe] = useState(false);
  const [pribavljaDokumentacijuSluzbeno, setPribavljaDokumentacijuSluzbeno] = useState(true);

  const handleConfirm = () => {
    onConfirm({
      vrsilacDuznosti,
      sopstveneRuke,
      dodatakZaPomocOdnosiSe,
      pribavljaDokumentacijuSluzbeno
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div className="modal-header">
        <h2>Додатна питања</h2>
      </div>
      
      <div className="modal-body">
        <p className="modal-description">
          Одговорите на следећа питања пре генерисања документа:
        </p>
        
        <div className="checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={vrsilacDuznosti}
              onChange={(e) => setVrsilacDuznosti(e.target.checked)}
            />
            <span>Да ли у тексту иде "в.д." (вршилац дужности)?</span>
          </label>
          <small className="help-text">
            Ако је означено, текст "в.д." ће остати у документу
          </small>
        </div>
        
        <div className="checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={sopstveneRuke}
              onChange={(e) => setSopstveneRuke(e.target.checked)}
            />
            <span>Да ли у тексту иде "с.р." (сопствене руке)?</span>
          </label>
          <small className="help-text">
            Ако је означено, текст "с.р." ће остати у документу
          </small>
        </div>
        
        <div className="checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={dodatakZaPomocOdnosiSe}
              onChange={(e) => setDodatakZaPomocOdnosiSe(e.target.checked)}
            />
            <span>Да ли се део о "Додатак за помоћ и негу другог лица..." односи на конкретан предмет?</span>
          </label>
          <small className="help-text">
            Ако је означено, тај параграф ће остати у документу
          </small>
        </div>
        
        <div className="checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={pribavljaDokumentacijuSluzbeno}
              onChange={(e) => setPribavljaDokumentacijuSluzbeno(e.target.checked)}
            />
            <span>Да ли се документација прибавља по службеној дужности?</span>
          </label>
          <small className="help-text">
            Ако је означено, параграф "Службеним путем, овај орган је..." ће остати у документу
          </small>
        </div>
      </div>
      
      <div className="modal-footer">
        <button onClick={onClose} className="btn-secondary">
          Откажи
        </button>
        <button onClick={handleConfirm} className="btn-primary">
          Генериши документ
        </button>
      </div>
    </Modal>
  );
};
```

---

## 🔄 FLOW - Kako radi?

### 1. Korisnik popunjava formu (sekcije 1-4)

```tsx
const [formData, setFormData] = useState({
  // Zaglavlje
  brojPredmeta: '',
  datumDonosenja: '',
  brojOvlascenja: '',
  datumOvlascenja: '',
  imeIPrezimeOvlascenog: '',
  
  // Podnosilac
  imeIPrezimePodnosioca: '',
  jmbg: '',
  grad: '',
  ulica: '',
  brojStana: '',
  opstina: '',
  pttBroj: '',
  mestoStanovanja: '',
  
  // Obrazloženje
  datumPodnosenja: '',
  osnovPrava: '',
  brojClanovaDomacinstava: '',
  
  // Opciono
  prilozenaInfo: '',
  sluzbeniDokazi: '',
  dodatniTekst: ''
});
```

### 2. Klikne "Извоз у Word"

```tsx
const handleExport = () => {
  // Validacija forme
  if (!validateForm()) {
    showError('Молимо попуните сва обавезна поља');
    return;
  }
  
  // Prikaži modal sa pitanjima
  setShowModal(true);
};
```

### 3. Modal se otvara → korisnik odgovara na pitanja → klikne "Генериши"

```tsx
const handleConfirmExport = async (booleanAnswers) => {
  setShowModal(false);
  
  // Spoji podatke iz forme + boolean odgovore
  const requestData = {
    ...formData,
    ...booleanAnswers
  };
  
  try {
    // Pozovi backend
    const response = await fetch('/api/dokumenti/odbija-se-nsp/generisi', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestData)
    });
    
    // Preuzmi dokument
    const blob = await response.blob();
    const fileName = `ODBIJA_SE_NSP_${formData.brojPredmeta.replace('/', '-')}.doc`;
    saveAs(blob, fileName);
    
    showSuccess('Документ успешно генерисан!');
  } catch (error) {
    showError('Грешка при генерисању документа');
  }
};
```

---

## 📤 Request Primer (JSON)

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
  "prilozenaInfo": "1. Потврда о приходима\n2. Извод из матичне књиге рођених",
  "sluzbeniDokazi": "Подаци о приходима из Пореске управе",
  "dodatniTekst": null,
  "vrsilacDuznosti": true,
  "sopstveneRuke": false,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

---

## 🎨 CSS Primer (Stilizacija)

```css
.form-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.form-section h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 20px;
  border-bottom: 2px solid #007bff;
  padding-bottom: 8px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
}

.form-field.full-width {
  grid-column: 1 / -1;
}

.form-field label {
  margin-bottom: 6px;
  font-weight: 600;
  color: #555;
}

.form-field input,
.form-field select,
.form-field textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
}

.help-text {
  margin-top: 4px;
  font-size: 12px;
  color: #666;
}

.checkbox-group {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  margin-top: 4px;
  width: 18px;
  height: 18px;
}

.checkbox-label span {
  flex: 1;
  font-size: 15px;
  color: #333;
}
```

---

## ✅ Validacije na Frontend-u

```tsx
const validateForm = () => {
  const errors = [];
  
  // JMBG mora biti 13 cifara
  if (!/^\d{13}$/.test(formData.jmbg)) {
    errors.push('ЈМБГ мора имати тачно 13 цифара');
  }
  
  // Datum mora biti u yyyy-MM-dd formatu
  if (!formData.datumDonosenja.match(/^\d{4}-\d{2}-\d{2}$/)) {
    errors.push('Неисправан формат датума доношења');
  }
  
  // Broj clanova > 0
  if (parseInt(formData.brojClanovaDomacinstava) <= 0) {
    errors.push('Број чланова домаћинства мора бити већи од 0');
  }
  
  // Obavezna polja
  const requiredFields = [
    'brojPredmeta', 'datumDonosenja', 'brojOvlascenja',
    'datumOvlascenja', 'imeIPrezimeOvlascenog',
    'imeIPrezimePodnosioca', 'jmbg', 'grad', 'ulica',
    'brojStana', 'opstina', 'pttBroj', 'mestoStanovanja',
    'datumPodnosenja', 'osnovPrava', 'brojClanovaDomacinstava'
  ];
  
  requiredFields.forEach(field => {
    if (!formData[field] || formData[field].trim() === '') {
      errors.push(`Поље "${field}" је обавезно`);
    }
  });
  
  if (errors.length > 0) {
    alert(errors.join('\n'));
    return false;
  }
  
  return true;
};
```

---

## 📝 Rezime

### **Ukupno polja: 22 polja + 4 boolean pitanja**

- ✅ **17 obaveznih polja** (text, date, select, number)
- ⚪ **3 opciona polja** (textarea)
- ☑️ **4 boolean pitanja** (modal pre izvoza)

### **Layout:**
- 4 sekcije u formi
- 1 modal sa pitanjima

### **Backend Endpoint:**
- **POST** `/api/dokumenti/odbija-se-nsp/generisi`
- **Content-Type:** `application/json`
- **Response:** Binary `.doc` fajl

---

**Javi mi ako treba nešto dodatno da pojasnim ili promenim!** 🚀

