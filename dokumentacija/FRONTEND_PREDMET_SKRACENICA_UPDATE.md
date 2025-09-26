# 🏷️ Frontend Update - Dodavanje Kategorija Skraćenice u Predmete

## 📋 Pregled Izmena

Dodana je nova kolona `kategorija_skracenica` u `euk.predmet` tabelu koja čuva skraćenicu kategorije direktno u predmetu.

## 🔧 Backend Izmene

### 1. **Database Schema**
- Dodana kolona `kategorija_skracenica VARCHAR(10)` u `euk.predmet` tabelu
- Query fajl: `postgresQuery/update_predmet_with_kategorija_skracenica.sql`
- Automatsko popunjavanje na osnovu postojećih veza sa kategorijama

### 2. **Entity Ažuriranje**
- `EukPredmet.java` - dodano polje `kategorijaSkracenica`
- `EukPredmetDto.java` - dodano polje `kategorijaSkracenica`

### 3. **Service Ažuriranje**
- `EukPredmetService.java` - ažurirane `create()`, `update()` i `convertToDto()` metode
- Automatsko postavljanje skraćenice na osnovu kategorije

## 🎯 Frontend Izmene Potrebne

### 1. **Predmet Interface/Type**
```typescript
interface Predmet {
  predmetId: number;
  nazivPredmeta: string;
  status: string;
  odgovornaOsoba: string;
  prioritet: string;
  rokZaZavrsetak?: string;
  kategorijaId: number;
  kategorijaNaziv: string;
  kategorijaSkracenica: string; // NOVO POLJE
  brojUgrozenihLica: number;
}
```

### 2. **Tabela Predmeta**
- Dodati kolonu za prikaz `kategorijaSkracenica`
- Možda kao dodatna kolona ili u istom redu sa kategorijaNaziv
- Format: "GRP - Građansko pravo" ili slično

### 3. **Detalji Predmeta**
- Prikazati skraćenicu kategorije u detaljima predmeta
- Možda kao badge ili dodatni tekst

### 4. **Filteri i Pretraga**
- Dodati mogućnost filtriranja po skraćenici kategorije
- Brža pretraga po kratkim kodovima

## 📝 API Request/Response Primeri

### **GET /api/euk/predmeti Response**
```json
{
  "content": [
    {
      "predmetId": 1,
      "nazivPredmeta": "Spor o nasleđu",
      "status": "АКТИВАН",
      "odgovornaOsoba": "Marko Petrović",
      "prioritet": "ВИСОК",
      "rokZaZavrsetak": "2024-12-31",
      "kategorijaId": 1,
      "kategorijaNaziv": "Građansko pravo",
      "kategorijaSkracenica": "GRP",
      "brojUgrozenihLica": 3
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

## 🎨 UI/UX Preporuke

### 1. **Tabela Predmeta**
```
| Naziv Predmeta | Kategorija | Status | Prioritet | Odgovorna Osoba |
|----------------|------------|--------|-----------|-----------------|
| Spor o nasleđu | GRP - Građansko pravo | АКТИВАН | ВИСОК | Marko Petrović |
```

### 2. **Kartice Predmeta**
```
┌─────────────────────────────────┐
│ [GRP] Spor o nasleđu            │
│ Građansko pravo                 │
│ Status: АКТИВАН | Prioritet: ВИСОК │
└─────────────────────────────────┘
```

### 3. **Filteri**
- Dropdown sa kategorijama: "GRP - Građansko pravo"
- Brza pretraga po skraćenici: "GRP"

## ⚠️ Važne Napomene

1. **Automatsko popunjavanje**: Backend automatski postavlja skraćenicu na osnovu kategorije
2. **Sinhronizacija**: Skraćenica se ažurira kada se promeni kategorija predmeta
3. **Performanse**: Direktno čuvanje skraćenice u predmetu poboljšava performanse
4. **Backup**: Napravi backup pre pokretanja database migration-a

## 🚀 Koraci za Implementaciju

1. **Pokreni database migration**: `postgresQuery/update_predmet_with_kategorija_skracenica.sql`
2. **Ažuriraj TypeScript tipove** za predmete
3. **Dodaj kolonu u tabele** za prikaz skraćenice
4. **Ažuriraj kartice/detalje** predmeta
5. **Dodaj filtere** po skraćenici kategorije
6. **Testiraj API pozive** sa novim poljem

## 🔍 Testiranje

- Prikaz predmeta sa skraćenicom kategorije
- Filtriranje po skraćenici kategorije
- Kreiranje novog predmeta (automatsko postavljanje skraćenice)
- Ažuriranje kategorije predmeta (automatsko ažuriranje skraćenice)
