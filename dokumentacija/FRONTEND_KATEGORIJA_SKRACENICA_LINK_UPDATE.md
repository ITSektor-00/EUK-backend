# 🔗 Frontend Update - Povezivanje Kategorija sa Ugroženim Licima

## 📋 Pregled Izmena

Povezana je `osnov_sticanja_statusa` kolona iz `ugrozeno_lice_t1` tabele sa `skracenica` kolonom iz `kategorija` tabele.

## 🔧 Backend Izmene

### 1. **Database Schema**
- Povezivanje `euk.ugrozeno_lice_t1.osnov_sticanja_statusa` sa `euk.kategorija.skracenica`
- Query fajl: `postgresQuery/link_osnov_status_with_kategorija_skracenica.sql`
- Dodani indexi za bolje performanse

### 2. **Entity Ažuriranje**
- `EukUgrozenoLiceT1.java` - ažuriran komentar da je povezano sa kategorija.skracenica
- Ograničena dužina na 10 karaktera (kao kategorija.skracenica)

### 3. **Repository Ažuriranje**
- `EukUgrozenoLiceT1Repository.java` - dodane metode za pretragu po kategoriji
- Statistike po kategorijama

### 4. **Service Ažuriranje**
- `EukUgrozenoLiceT1Service.java` - dodane metode za pretragu i statistike po kategoriji

### 5. **Controller Ažuriranje**
- `EukUgrozenoLiceT1Controller.java` - dodani endpoint-i za pretragu po kategoriji

## 🎯 Frontend Izmene Potrebne

### 1. **Dropdown za Osnov Sticanja Statusa**
```typescript
// Umesto hardkodovanih vrednosti, dohvati kategorije sa skraćenicama
interface Kategorija {
  kategorijaId: number;
  naziv: string;
  skracenica: string;
}

// Dropdown opcije
const osnovSticanjaStatusaOptions = kategorije.map(kat => ({
  value: kat.skracenica,
  label: `${kat.skracenica} - ${kat.naziv}`
}));
```

### 2. **Forma za Kreiranje/Ažuriranje Ugroženog Lica**
- Dropdown za `osnovSticanjaStatusa` se popunjava iz kategorija
- Validacija: vrednost mora postojati u kategorijama
- Prikaz: "GRP - Građansko pravo" umesto samo "GRP"

### 3. **Tabela Ugroženih Lica**
- Kolona "Osnov Statusa" prikazuje skraćenicu + naziv kategorije
- Format: "GRP - Građansko pravo"
- Sortiranje po kategoriji

### 4. **Filteri i Pretraga**
- Dropdown filter po kategoriji (skraćenica + naziv)
- Brza pretraga po skraćenici kategorije
- Kombinovana pretraga sa drugim filterima

## 📝 API Request/Response Primeri

### **GET /api/euk/kategorije**
```json
[
  {
    "kategorijaId": 1,
    "naziv": "Građansko pravo",
    "skracenica": "GRP"
  },
  {
    "kategorijaId": 2,
    "naziv": "Krivično pravo", 
    "skracenica": "KRP"
  }
]
```

### **GET /api/euk/ugrozena-lica-t1/search/kategorija/GRP**
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "ime": "Marko",
      "prezime": "Petrović",
      "osnovSticanjaStatusa": "GRP"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### **GET /api/euk/ugrozena-lica-t1/statistics/kategorija**
```json
{
  "GRP": 150,
  "KRP": 75,
  "ADM": 25
}
```

## 🎨 UI/UX Preporuke

### 1. **Dropdown u Formi**
```typescript
<select name="osnovSticanjaStatusa">
  <option value="">Izaberite kategoriju...</option>
  {kategorije.map(kat => (
    <option key={kat.kategorijaId} value={kat.skracenica}>
      {kat.skracenica} - {kat.naziv}
    </option>
  ))}
</select>
```

### 2. **Tabela Ugroženih Lica**
```
| Ime | Prezime | Osnov Statusa | ED Broj |
|-----|---------|---------------|---------|
| Marko | Petrović | GRP - Građansko pravo | ED123456 |
| Ana | Jovanović | KRP - Krivično pravo | ED789012 |
```

### 3. **Filteri**
```
┌─────────────────────────────────┐
│ Kategorija: [GRP - Građansko pravo ▼] │
│ Grad: [Beograd ▼]              │
│ Datum: [2024-01-01] - [2024-12-31] │
└─────────────────────────────────┘
```

### 4. **Statistike**
```
┌─────────────────────────────────┐
│ Statistike po kategorijama:     │
│ GRP - Građansko pravo: 150      │
│ KRP - Krivično pravo: 75        │
│ ADM - Administrativno: 25       │
└─────────────────────────────────┘
```

## 🚀 Koraci za Implementaciju

1. **Dohvati kategorije** sa skraćenicama iz API-ja
2. **Ažuriraj dropdown-ove** u formama za kreiranje/ažuriranje
3. **Ažuriraj tabele** za prikaz kategorija sa nazivima
4. **Implementiraj filtere** po kategoriji
5. **Dodaj statistike** po kategorijama
6. **Testiraj API pozive** sa novim endpoint-ima

## 🔍 Testiranje

- Kreiranje ugroženog lica sa kategorijom iz dropdown-a
- Ažuriranje kategorije ugroženog lica
- Filtriranje po kategoriji
- Statistike po kategorijama
- Validacija da kategorija postoji

## 📊 **Novi API Endpoint-i:**

### **Pretraga po kategoriji:**
```bash
GET /api/euk/ugrozena-lica-t1/search/kategorija/{kategorijaSkracenica}?page=0&size=10
```

### **Statistike po kategorijama:**
```bash
GET /api/euk/ugrozena-lica-t1/statistics/kategorija
```

### **Kombinovana pretraga:**
```bash
POST /api/euk/ugrozena-lica-t1/search/filters
{
  "osnovStatusa": "GRP",
  "gradOpstina": "Beograd"
}
```

## ⚠️ Važne Napomene

1. **Sinhronizacija**: Kada se promeni kategorija, ažurira se i osnov sticanja statusa
2. **Validacija**: Osnov sticanja statusa mora postojati u kategorijama
3. **Performanse**: Dodani indexi za brže pretrage
4. **Backup**: Napravi backup pre pokretanja database migration-a
