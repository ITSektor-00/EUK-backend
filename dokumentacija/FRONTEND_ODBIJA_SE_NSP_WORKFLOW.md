# FRONTEND WORKFLOW - Generisanje "ODBIJA SE NSP" Dokumenta

## 📋 PREGLED SISTEMA

Sistem za generisanje Word dokumenta "ODBIJA SE NSP,UNSP,DD,UDTNP" sa čuvanjem u bazu.

---

## 🔄 KOMPLETNI WORKFLOW

### **KORAK 1: Kreiranje/Ažuriranje Predmeta**

Frontend **PRVO** mora da kreira ili ažurira predmet u bazi sa svim podacima.

**Endpoint:**
```
POST /api/predmeti
```
ili
```
PUT /api/predmeti/{id}
```

**Odgovor:**
```json
{
  "predmetId": 123,
  "nazivPredmeta": "...",
  ...
}
```

👉 **ČUVAJ `predmetId` - trebača ti za korak 2!**

---

### **KORAK 2: Generisanje Dokumenta**

Kada imaš `predmetId` iz koraka 1, pozovi endpoint za generisanje dokumenta.

#### **📍 URL:**
```
POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi
```

#### **📤 Request Headers:**
```json
{
  "Content-Type": "application/json"
}
```

#### **📤 Request Body:**
```json
{
  // ========== OBAVEZNO: ID predmeta iz koraka 1 ==========
  "predmetId": 123,
  
  // ========== OBAVEZNA POLJA ==========
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
  "osnovPrava": "NSP",
  "brojClanovaDomacinstava": "4",
  
  // ========== OPCIONA POLJA ==========
  "prilozenaInfo": "1. Potvrda\n2. Izvod",
  "sluzbeniDokazi": "Podaci iz poreske",
  "dodatniTekst": null,
  
  // ========== BOOLEAN OPCIJE ==========
  "vrsilacDuznosti": true,
  "sopstveneRuke": true,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

#### **📥 Response:**
- **Status**: 200 OK
- **Content-Type**: `application/octet-stream`
- **Content-Disposition**: `attachment; filename="ODBIJA_SE_NSP_123-2025.docx"`
- **Body**: Binary `.docx` fajl

**Šta backend radi:**
✅ Generiše Word dokument  
✅ **Čuva dokument u bazu** u tabelu `euk.predmet` (kolona `odbija_se_nsp_dokument`)  
✅ Vraća dokument korisniku za preuzimanje

---

## 📥 KORAK 3 (Opciono): Ponovno Preuzimanje Dokumenta iz Baze

Ako korisnik želi ponovo da preuzme dokument koji je već bio generisan:

#### **📍 URL:**
```
GET http://localhost:8080/api/dokumenti/odbija-se-nsp/preuzmi/{predmetId}
```

**Primer:**
```
GET http://localhost:8080/api/dokumenti/odbija-se-nsp/preuzmi/123
```

#### **📥 Response:**
- **Status**: 200 OK ili 404 Not Found (ako dokument nije generisan)
- **Content-Type**: `application/octet-stream`
- **Body**: Binary `.docx` fajl

---

## 💻 FRONTEND KOD - JavaScript/Axios

### **Primer 1: Kreiranje Predmeta + Generisanje Dokumenta**

```javascript
const kreirajPredmetIGenerisiDokument = async (formData) => {
  try {
    // KORAK 1: Kreiraj predmet
    const predmetResponse = await axios.post('/api/predmeti', {
      nazivPredmeta: formData.nazivPredmeta,
      odgovornaOsoba: formData.odgovornaOsoba,
      status: 'активан',
      prioritet: 'средњи',
      // ... ostali podaci ...
    });
    
    const predmetId = predmetResponse.data.predmetId;
    console.log('Predmet kreiran, ID:', predmetId);
    
    // KORAK 2: Generiši dokument
    const dokumentData = {
      predmetId: predmetId,  // ← VAŽNO!
      brojPredmeta: formData.brojPredmeta,
      datumDonosenja: formData.datumDonosenja,
      brojOvlascenja: formData.brojOvlascenja,
      datumOvlascenja: formData.datumOvlascenja,
      imeIPrezimeOvlascenog: formData.imeIPrezimeOvlascenog,
      imeIPrezimePodnosioca: formData.imeIPrezimePodnosioca,
      jmbg: formData.jmbg,
      grad: formData.grad,
      ulica: formData.ulica,
      brojStana: formData.brojStana,
      opstina: formData.opstina,
      pttBroj: formData.pttBroj,
      mestoStanovanja: formData.mestoStanovanja,
      datumPodnosenja: formData.datumPodnosenja,
      osnovPrava: formData.osnovPrava,
      brojClanovaDomacinstava: formData.brojClanovaDomacinstava,
      prilozenaInfo: formData.prilozenaInfo || null,
      sluzbeniDokazi: formData.sluzbeniDokazi || null,
      dodatniTekst: formData.dodatniTekst || null,
      vrsilacDuznosti: formData.vrsilacDuznosti ?? true,
      sopstveneRuke: formData.sopstveneRuke ?? true,
      dodatakZaPomocOdnosiSe: formData.dodatakZaPomocOdnosiSe ?? false,
      pribavljaDokumentacijuSluzbeno: formData.pribavljaDokumentacijuSluzbeno ?? true
    };
    
    const dokumentResponse = await axios.post(
      '/api/dokumenti/odbija-se-nsp/generisi',
      dokumentData,
      {
        responseType: 'blob'  // VAŽNO: mora biti blob
      }
    );
    
    // KORAK 3: Preuzmi fajl
    const url = window.URL.createObjectURL(new Blob([dokumentResponse.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `ODBIJA_SE_NSP_${formData.brojPredmeta.replace('/', '-')}.docx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    
    alert('Dokument uspešno generisan i sačuvan!');
    
  } catch (error) {
    console.error('Greška:', error);
    alert('Greška pri generisanju dokumenta');
  }
};
```

### **Primer 2: Ponovno Preuzimanje Dokumenta**

```javascript
const preuzimiSacuvaniDokument = async (predmetId) => {
  try {
    const response = await axios.get(
      `/api/dokumenti/odbija-se-nsp/preuzmi/${predmetId}`,
      {
        responseType: 'blob'
      }
    );
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `ODBIJA_SE_NSP_${predmetId}.docx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    
  } catch (error) {
    if (error.response?.status === 404) {
      alert('Dokument još nije generisan za ovaj predmet');
    } else {
      console.error('Greška:', error);
      alert('Greška pri preuzimanju dokumenta');
    }
  }
};
```

---

## 🎯 OSNOV PRAVA - Moguće Vrednosti

| Vrednost | Pun Tekst |
|----------|-----------|
| `"NSP"` | Новчану социјалну помоћ |
| `"UNSP"` | Увећану новчану социјалну помоћ |
| `"DD"` | Дечији додатак |
| `"UDTNP"` | Увећани додатак за помоћ и негу другог лица |

---

## ✅ VALIDACIJA

Frontend mora da validira:
- **`predmetId`**: Mora biti prosleđen (Integer)
- **Datumi**: Format `yyyy-MM-dd` (npr. `2025-01-15`)
- **JMBG**: Tačno 13 cifara
- **Sva obavezna polja**: Ne smeju biti prazna

---

## 🗄️ ŠTA SE ČUVA U BAZI

Kada se dokument generiše, u tabeli `euk.predmet` se čuvaju:

| Kolona | Tip | Opis |
|--------|-----|------|
| `odbija_se_nsp_dokument` | BYTEA | Binary Word dokument (.docx) |
| `odbija_se_nsp_dokument_naziv` | VARCHAR(255) | Naziv fajla (npr. "ODBIJA_SE_NSP_123-2025.docx") |
| `odbija_se_nsp_dokument_datum` | TIMESTAMP | Datum i vreme generisanja |

---

## 🚀 SQL MIGRACIJA

Pre nego što frontend počne da koristi ove endpointe, pokreni SQL migraciju:

```sql
-- Pokreni ovaj fajl u PostgreSQL:
postgresQuery/add_odbija_se_nsp_dokument_column.sql
```

---

## 📌 VAŽNE NAPOMENE

1. ✅ **Prvo kreiraj predmet**, pa onda generiši dokument
2. ✅ **`predmetId` je obavezan** ako želiš da se dokument čuva u bazu
3. ✅ Dokument se **i čuva u bazu I vraća korisniku**
4. ✅ Korisnik može **ponovo preuzeti** dokument iz baze korišćenjem GET endpointa
5. ✅ Sva polja podržavaju **ćirilicu**
6. ✅ Datumi moraju biti u formatu `yyyy-MM-dd`

---

## 🐛 TROUBLESHOOTING

### Problem: "404 Not Found" pri preuzimanju
- Dokument još nije generisan za taj predmet
- Prvo pozovi POST endpoint za generisanje

### Problem: "predmetId ne sme biti null"
- Proveri da li šalješ `predmetId` u JSON request body-u

### Problem: "Dokument nije sačuvan u bazu"
- Proveri da li je SQL migracija pokrenuta
- Proveri backend logove

