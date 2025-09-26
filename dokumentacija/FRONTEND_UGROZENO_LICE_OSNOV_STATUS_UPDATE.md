# 🏷️ Frontend Update - Osnov Sticanja Statusa za Ugrožena Lica

## 📋 Pregled Izmena

Kolumna `osnov_sticanja_statusa` već postoji u `euk.ugrozeno_lice_t1` tabeli i backend kodu. Ova dokumentacija objašnjava kako frontend treba da koristi ovo polje.

## 🔧 Backend Status

### ✅ **Već implementirano:**
- **Entity**: `EukUgrozenoLiceT1.java` - polje `osnovSticanjaStatusa`
- **DTO**: `EukUgrozenoLiceT1Dto.java` - polje `osnovSticanjaStatusa`
- **Service**: `EukUgrozenoLiceT1Service.java` - sve CRUD operacije
- **Repository**: `EukUgrozenoLiceT1Repository.java` - pretrage po osnovu statusa
- **Controller**: `EukUgrozenoLiceT1Controller.java` - svi endpoint-i

### 📊 **Dostupne pretrage:**
- Pretraga po `osnovSticanjaStatusa` u filterima
- Statistike po osnovu sticanja statusa
- Agregacije i brojanje po kategorijama

## 🎯 Frontend Izmene Potrebne

### 1. **Ugroženo Lice Interface/Type**
```typescript
interface UgrozenoLiceT1 {
  ugrozenoLiceId: number;
  redniBroj: string;
  ime: string;
  prezime: string;
  jmbg: string;
  // ... ostala polja
  osnovSticanjaStatusa: string; // MP, NSP, DD, UDTNP
  // ... ostala polja
}
```

### 2. **Forma za Kreiranje/Ažuriranje**
- Dodati dropdown/select za `osnovSticanjaStatusa`
- Opcije: MP, NSP, DD, UDTNP
- Validacija: obavezno polje

### 3. **Tabela Ugroženih Lica**
- Dodati kolonu za prikaz `osnovSticanjaStatusa`
- Možda kao badge ili ikona
- Sortiranje po osnovu statusa

### 4. **Filteri i Pretraga**
- Dropdown filter po osnovu sticanja statusa
- Kombinovana pretraga sa drugim filterima
- Brza pretraga po kodu

## 📝 API Request/Response Primeri

### **GET /api/euk/ugrozena-lica-t1 Response**
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "redniBroj": "001",
      "ime": "Marko",
      "prezime": "Petrović",
      "jmbg": "1234567890123",
      "osnovSticanjaStatusa": "MP",
      "edBrojBrojMernogUredjaja": "ED123456",
      "iznosUmanjenjaSaPdv": 15000.00
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### **POST /api/euk/ugrozena-lica-t1/search/filters**
```json
{
  "osnovStatusa": "MP",
  "gradOpstina": "Beograd",
  "datumOd": "2024-01-01",
  "datumDo": "2024-12-31"
}
```

## 🎨 UI/UX Preporuke

### 1. **Tabela Ugroženih Lica**
```
| Redni Broj | Ime | Prezime | Osnov Statusa | ED Broj | Iznos |
|------------|-----|---------|---------------|---------|-------|
| 001        | Marko | Petrović | [MP] | ED123456 | 15,000 |
```

### 2. **Kartice Ugroženih Lica**
```
┌─────────────────────────────────┐
│ [MP] Marko Petrović             │
│ Redni broj: 001                │
│ ED: ED123456 | Iznos: 15,000   │
└─────────────────────────────────┘
```

### 3. **Filteri**
- Dropdown: "Osnov sticanja statusa"
- Opcije: "Svi", "MP", "NSP", "DD", "UDTNP"
- Kombinovano sa drugim filterima

### 4. **Statistike**
- Grafikon po osnovu sticanja statusa
- Tabela sa brojem lica po kategoriji
- Ukupan iznos po kategoriji

## 📊 **Moguće vrednosti za osnovSticanjaStatusa:**

| Kod | Opis |
|-----|------|
| **MP** | Materijalno potpomognuti |
| **NSP** | Nezaposleni sa posebnim potrebama |
| **DD** | Deca sa posebnim potrebama |
| **UDTNP** | Učenici sa posebnim potrebama |

## 🚀 Koraci za Implementaciju

1. **Ažuriraj TypeScript tipove** za ugrožena lica
2. **Dodaj polje u forme** za kreiranje/ažuriranje
3. **Ažuriraj tabele** za prikaz osnova statusa
4. **Dodaj filtere** po osnovu sticanja statusa
5. **Implementiraj statistike** po kategorijama
6. **Testiraj API pozive** sa novim filterima

## 🔍 Testiranje

- Prikaz ugroženih lica sa osnovom statusa
- Filtriranje po osnovu sticanja statusa
- Kreiranje novog ugroženog lica sa osnovom statusa
- Ažuriranje postojećeg ugroženog lica
- Statistike po kategorijama
- Kombinovana pretraga sa drugim filterima

## 📈 **Dodatne funkcionalnosti:**

### **Statistike po osnovu statusa:**
```typescript
// API poziv za statistike
GET /api/euk/ugrozena-lica-t1/statistics

// Response
{
  "totalRecords": 1000,
  "sumIznosUmanjenjaSaPdv": 15000000.00,
  "avgPotrosnjaKwh": 2500.50,
  "avgZagrevanaPovrsinaM2": 85.75
}
```

### **Pretraga po osnovu statusa:**
```typescript
// Filter objekat
const filters = {
  osnovStatusa: "MP",
  gradOpstina: "Beograd",
  datumOd: "2024-01-01",
  datumDo: "2024-12-31"
};

// API poziv
POST /api/euk/ugrozena-lica-t1/search/filters
```
