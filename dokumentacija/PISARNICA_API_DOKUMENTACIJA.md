# API Dokumentacija za Pisarnicu - EUK Backend

## Pregled
API za upravljanje predmetima u pisarnici sa naprednim filtriranjem, paginacijom i optimizacijom za velike baze podataka (1M+ zapisa).

## Base URL
```
http://localhost:8080/api/pisarnica
```

## Autentifikacija
Svi endpointi zahtevaju JWT token u Authorization header-u:
```
Authorization: Bearer <your_jwt_token>
```

## Endpointi

### 1. Filtriranje Predmeta (POST)
**POST** `/api/pisarnica/filter`

Glavni endpoint za napredno filtriranje predmeta sa paginacijom.

#### Request Body:
```json
{
  "searchTerm": "string (opciono)",
  "idOrganOj": "number (opciono)",
  "idOdsek": "number (opciono)",
  "idKlasifikacija": "number (opciono)",
  "idVrstaPredmeta": "number (opciono)",
  "idOpisKlasifikacije": "number (opciono)",
  "idOpstina": "number (opciono)",
  "tipPodnosioca": "string (opciono)",
  "nazivPodnosioca": "string (opciono)",
  "maticniBroj": "string (opciono)",
  "pib": "string (opciono)",
  "datumPredmetaOd": "YYYY-MM-DD (opciono)",
  "datumPredmetaDo": "YYYY-MM-DD (opciono)",
  "godina": "number (opciono)",
  "vrednostTakseOd": "number (opciono)",
  "vrednostTakseDo": "number (opciono)",
  "napomena": "string (opciono)",
  "straniBroj": "string (opciono)",
  "jedinstveniKod": "string (opciono)",
  "sortBy": "string (default: 'idPredmet')",
  "sortDirection": "string (asc/desc, default: 'asc')",
  "page": "number (default: 0)",
  "size": "number (default: 20, max: 100)"
}
```

#### Response:
```json
{
  "content": [
    {
      "idPredmet": 1,
      "idOrganOj": 1,
      "idOdsek": 1,
      "idKlasifikacija": 1,
      "idVrstaPredmeta": 1,
      "idOpisKlasifikacije": 1,
      "idOpstina": 1,
      "tipPodnosioca": "Fizičko lice",
      "nazivPodnosioca": "Marko Petrović",
      "maticniBroj": "1234567890123",
      "pib": "12345678",
      "adresa": "Knez Mihailova 15",
      "ekstemiIdPodnosioca": "EXT001",
      "napomena": "Važan predmet",
      "dodatnaNapomena": "Dodatne informacije",
      "straniBroj": "STR001",
      "katastarskiPodaci": "Katastar 123",
      "vrednostTakse": 1500.50,
      "datumPredmeta": "2024-01-15",
      "datumUnosa": "2024-01-15T10:30:00",
      "godina": 2024,
      "azurirano": "2024-01-15T10:30:00",
      "idOvlascenoLice": 1,
      "id": 1,
      "idBaza": 1,
      "popisAkata": "Popis 123",
      "jedinstveniKod": "UNI001"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1500,
  "totalPages": 75,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

### 2. Filtriranje Predmeta (GET)
**GET** `/api/pisarnica/filter`

Alternativni GET endpoint sa query parametrima.

#### Query Parameters:
- `searchTerm` (string, opciono)
- `idOrganOj` (number, opciono)
- `idOdsek` (number, opciono)
- `idKlasifikacija` (number, opciono)
- `idVrstaPredmeta` (number, opciono)
- `idOpisKlasifikacije` (number, opciono)
- `idOpstina` (number, opciono)
- `tipPodnosioca` (string, opciono)
- `nazivPodnosioca` (string, opciono)
- `maticniBroj` (string, opciono)
- `pib` (string, opciono)
- `datumPredmetaOd` (YYYY-MM-DD, opciono)
- `datumPredmetaDo` (YYYY-MM-DD, opciono)
- `godina` (number, opciono)
- `vrednostTakseOd` (number, opciono)
- `vrednostTakseDo` (number, opciono)
- `napomena` (string, opciono)
- `straniBroj` (string, opciono)
- `jedinstveniKod` (string, opciono)
- `sortBy` (string, default: 'idPredmet')
- `sortDirection` (string, asc/desc, default: 'asc')
- `page` (number, default: 0)
- `size` (number, default: 20, max: 100)

#### Example:
```
GET /api/pisarnica/filter?searchTerm=Marko&godina=2024&page=0&size=20&sortBy=datumPredmeta&sortDirection=desc
```

### 3. Dobijanje Predmeta po ID-u
**GET** `/api/pisarnica/{id}`

Dobija detaljne informacije o predmetu.

#### Response:
```json
{
  "idPredmet": 1,
  "idOrganOj": 1,
  "idOdsek": 1,
  "idKlasifikacija": 1,
  "idVrstaPredmeta": 1,
  "idOpisKlasifikacije": 1,
  "idOpstina": 1,
  "tipPodnosioca": "Fizičko lice",
  "nazivPodnosioca": "Marko Petrović",
  "maticniBroj": "1234567890123",
  "pib": "12345678",
  "adresa": "Knez Mihailova 15",
  "ekstemiIdPodnosioca": "EXT001",
  "napomena": "Važan predmet",
  "dodatnaNapomena": "Dodatne informacije",
  "straniBroj": "STR001",
  "katastarskiPodaci": "Katastar 123",
  "vrednostTakse": 1500.50,
  "datumPredmeta": "2024-01-15",
  "datumUnosa": "2024-01-15T10:30:00",
  "godina": 2024,
  "azurirano": "2024-01-15T10:30:00",
  "idOvlascenoLice": 1,
  "id": 1,
  "idBaza": 1,
  "popisAkata": "Popis 123",
  "jedinstveniKod": "UNI001"
}
```

### 4. Filter Opcije
**GET** `/api/pisarnica/filter-options`

Dobija sve dostupne opcije za filter dropdown-ove.

#### Response:
```json
{
  "idOrganOjList": [1, 2, 3, 4, 5],
  "idOdsekList": [1, 2, 3, 4, 5],
  "idKlasifikacijaList": [1, 2, 3, 4, 5],
  "idVrstaPredmetaList": [1, 2, 3, 4, 5],
  "idOpisKlasifikacijeList": [1, 2, 3, 4, 5],
  "idOpstinaList": [1, 2, 3, 4, 5],
  "tipPodnosiocaList": ["Fizičko lice", "Pravno lice", "Preduzetnik"],
  "godinaList": [2024, 2023, 2022, 2021, 2020]
}
```

### 5. Statistike
**GET** `/api/pisarnica/statistics`

Dobija osnovne statistike o predmetima.

#### Response:
```json
{
  "totalCount": 1250000
}
```

### 6. Statistike po Godini
**GET** `/api/pisarnica/statistics/year/{godina}`

Dobija broj predmeta za određenu godinu.

#### Response:
```json
{
  "godina": 2024,
  "count": 150000
}
```

### 7. Statistike po Organu
**GET** `/api/pisarnica/statistics/organ/{idOrganOj}`

Dobija broj predmeta za određeni organ.

#### Response:
```json
{
  "idOrganOj": 1,
  "count": 50000
}
```

### 8. Brza Pretraga
**GET** `/api/pisarnica/search`

Brza pretraga sa osnovnim podacima.

#### Query Parameters:
- `q` (string, obavezno) - search term
- `page` (number, default: 0)
- `size` (number, default: 10)

#### Example:
```
GET /api/pisarnica/search?q=Marko&page=0&size=10
```

### 9. Health Check
**GET** `/api/pisarnica/health`

Proverava status API-ja i baze podataka.

#### Response:
```json
{
  "status": "UP",
  "totalRecords": 1250000,
  "timestamp": "2024-01-15T10:30:00"
}
```

## Optimizacije za Velike Baze Podataka

### 1. Paginacija
- Default veličina stranice: 20
- Maksimalna veličina stranice: 100
- Uvek koristite paginaciju za velike rezultate

### 2. Indeksiranje
⚠️ **VAŽNO: Baza ima samo READ privilegije!**

Ne možeš kreirati indekse. Optimizacija se oslanja na postojeće indekse u bazi.

```sql
-- Preporučeni indeksi (za database administratora):
CREATE INDEX idx_predmet_id_organ_oj ON predmet(ID_Organ_OJ);
CREATE INDEX idx_predmet_id_odsek ON predmet(ID_Odsek);
CREATE INDEX idx_predmet_id_klasifikacija ON predmet(ID_Klasifikacija);
CREATE INDEX idx_predmet_godina ON predmet(Godina);
CREATE INDEX idx_predmet_datum_predmeta ON predmet(Datum_Predmeta);
CREATE INDEX idx_predmet_naziv_podnosioca ON predmet(Naziv_Podnosioca);
CREATE INDEX idx_predmet_maticni_broj ON predmet(Maticni_Broj);
CREATE INDEX idx_predmet_pib ON predmet(PIB);
CREATE INDEX idx_predmet_strani_broj ON predmet(Strani_Broj);
CREATE INDEX idx_predmet_jedinstveni_kod ON predmet(Jedinstveni_Kod);
```

### 3. Connection Pool
Konfiguracija za velike baze:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

### 4. JPA Optimizacije
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

## Error Handling

### 400 Bad Request
```json
{
  "error": "Invalid request parameters",
  "message": "Detailed error message",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found
```json
{
  "error": "Resource not found",
  "message": "Predmet with ID 123 not found",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error",
  "message": "Database connection failed",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Rate Limiting

- Maksimalno 100 zahteva po minuti po IP adresi
- Burst kapacitet: 200 zahteva
- Headers u response-u:
  - `X-RateLimit-Limit`: 100
  - `X-RateLimit-Remaining`: 95
  - `X-RateLimit-Reset`: 1642248600

## CORS

Svi endpointi podržavaju CORS sa sledećim konfiguracijama:
- Allowed Origins: `http://localhost:3000`, `http://localhost:3001`, `http://127.0.0.1:3000`
- Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- Allowed Headers: `Content-Type`, `Authorization`, `X-Requested-With`

## Primjeri Korišćenja

### 1. Osnovna pretraga
```javascript
const response = await fetch('/api/pisarnica/filter', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    searchTerm: 'Marko',
    page: 0,
    size: 20
  })
});
```

### 2. Napredno filtriranje
```javascript
const response = await fetch('/api/pisarnica/filter', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    godina: 2024,
    idOrganOj: 1,
    datumPredmetaOd: '2024-01-01',
    datumPredmetaDo: '2024-12-31',
    sortBy: 'datumPredmeta',
    sortDirection: 'desc',
    page: 0,
    size: 50
  })
});
```

### 3. Brza pretraga
```javascript
const response = await fetch('/api/pisarnica/search?q=Marko&page=0&size=10', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
```

## Napomene

1. **Performance**: Za baze sa 1M+ zapisa, uvek koristite paginaciju
2. **Caching**: Razmislite o implementaciji cache-a za često korišćene filtere
3. **Monitoring**: Pratite performance metrike kroz Actuator endpoint-e
4. **Backup**: Redovno pravi backup baze podataka
5. **Logging**: Omogućite detaljno logovanje za debugging

## Kontakt

Za dodatna pitanja ili podršku, kontaktirajte development tim.
