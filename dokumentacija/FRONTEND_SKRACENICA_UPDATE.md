# 🏷️ Frontend Update - Dodavanje Skraćenice za Kategorije

## 📋 Pregled Izmena

Dodana je nova kolona `skracenica` u `euk.kategorija` tabelu i ažuriran je ceo backend kod.

## 🔧 Backend Izmene

### 1. **Database Schema**
- Dodana kolona `skracenica VARCHAR(10) NOT NULL` u `euk.kategorija` tabelu
- Query fajl: `postgresQuery/add_skracenica_to_kategorija.sql`

### 2. **Entity Ažuriranje**
- `EukKategorija.java` - dodano polje `skracenica`
- `EukKategorijaDto.java` - dodano polje `skracenica` sa validacijom

### 3. **Service Ažuriranje**
- `EukKategorijaService.java` - dodana logika za `skracenica` u create/update operacijama
- Dodana validacija za jedinstvenost skraćenice

### 4. **Repository Ažuriranje**
- `EukKategorijaRepository.java` - dodana metoda `existsBySkracenica()`

## 🎯 Frontend Izmene Potrebne

### 1. **Kategorija Interface/Type**
```typescript
interface Kategorija {
  kategorijaId: number;
  naziv: string;
  skracenica: string; // NOVO POLJE
}
```

### 2. **Forma za Kreiranje/Ažuriranje Kategorije**
- Dodati input polje za `skracenica`
- Maksimalno 10 karaktera
- Obavezno polje
- Validacija za jedinstvenost

### 3. **Tabela Kategorija**
- Dodati kolonu za prikaz `skracenica`
- Možda kao dodatna kolona ili u istom redu sa nazivom

### 4. **API Pozivi**
Svi postojeći API pozivi će sada vraćati i `skracenica` polje:
- `GET /api/euk/kategorije` - vraća sve kategorije sa skraćenicom
- `POST /api/euk/kategorije` - zahteva `skracenica` u request body-ju
- `PUT /api/euk/kategorije/{id}` - ažuriranje sa `skracenica`

## 📝 API Request/Response Primeri

### **POST /api/euk/kategorije**
```json
{
  "naziv": "Građansko pravo",
  "skracenica": "GRP"
}
```

### **Response**
```json
{
  "kategorijaId": 1,
  "naziv": "Građansko pravo", 
  "skracenica": "GRP"
}
```

## ⚠️ Važne Napomene

1. **Obavezno polje**: `skracenica` je obavezno polje (NOT NULL)
2. **Jedinstvenost**: Svaka skraćenica mora biti jedinstvena
3. **Maksimalna dužina**: 10 karaktera
4. **Validacija**: Backend proverava jedinstvenost pre čuvanja

## 🚀 Koraci za Implementaciju

1. **Pokreni database migration**: `postgresQuery/add_skracenica_to_kategorija.sql`
2. **Ažuriraj TypeScript tipove** za kategorije
3. **Dodaj polje u forme** za kreiranje/ažuriranje
4. **Ažuriraj tabele** za prikaz skraćenice
5. **Testiraj API pozive** sa novim poljem

## 🔍 Testiranje

- Kreiranje kategorije sa skraćenicom
- Ažuriranje postojeće kategorije
- Validacija jedinstvenosti skraćenice
- Prikaz skraćenice u listi kategorija
