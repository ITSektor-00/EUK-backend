# Pisarnica API - Svi Endpointi

## Base URL: `http://localhost:8080/api/pisarnica`

## 1. Filtriranje Predmeta

### POST `/filter`
**Opis:** Glavni endpoint za napredno filtriranje predmeta sa paginacijom
**Method:** POST
**Headers:** `Content-Type: application/json`, `Authorization: Bearer <token>`
**Body:** PredmetFilterRequest JSON

### GET `/filter`
**Opis:** Alternativni GET endpoint sa query parametrima
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Query Params:** searchTerm, idOrganOj, idOdsek, godina, page, size, sortBy, sortDirection, itd.

## 2. Pojedinačni Predmet

### GET `/{id}`
**Opis:** Dobija detaljne informacije o predmetu po ID-u
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Path Variable:** id (Long)

## 3. Filter Opcije

### GET `/filter-options`
**Opis:** Dobija sve dostupne opcije za filter dropdown-ove
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Response:** FilterOptionsDto sa listama za dropdown-ove

## 4. Statistike

### GET `/statistics`
**Opis:** Dobija osnovne statistike o predmetima
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Response:** StatisticsDto sa ukupnim brojem zapisa

### GET `/statistics/year/{godina}`
**Opis:** Statistike po godini
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Path Variable:** godina (Integer)

### GET `/statistics/organ/{idOrganOj}`
**Opis:** Statistike po organu
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Path Variable:** idOrganOj (Long)

## 5. Brza Pretraga

### GET `/search`
**Opis:** Brza pretraga sa osnovnim podacima
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Query Params:** q (string, obavezno), page (default: 0), size (default: 10)

## 6. Health Check

### GET `/health`
**Opis:** Proverava status API-ja i baze podataka
**Method:** GET
**Headers:** `Authorization: Bearer <token>`
**Response:** Status, totalRecords, timestamp

## Primjeri Korišćenja

### 1. Osnovna Pretraga
```bash
curl -X GET "http://localhost:8080/api/pisarnica/filter?searchTerm=Marko&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Napredno Filtriranje
```bash
curl -X POST "http://localhost:8080/api/pisarnica/filter" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "searchTerm": "Marko",
    "godina": 2024,
    "idOrganOj": 1,
    "page": 0,
    "size": 20,
    "sortBy": "datumPredmeta",
    "sortDirection": "desc"
  }'
```

### 3. Brza Pretraga
```bash
curl -X GET "http://localhost:8080/api/pisarnica/search?q=Marko&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Filter Opcije
```bash
curl -X GET "http://localhost:8080/api/pisarnica/filter-options" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Statistike
```bash
curl -X GET "http://localhost:8080/api/pisarnica/statistics" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Health Check
```bash
curl -X GET "http://localhost:8080/api/pisarnica/health" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Response Formati

### PaginatedResponse
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 1500,
  "totalPages": 75,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

### PredmetDto
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

### FilterOptionsDto
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

### StatisticsDto
```json
{
  "totalCount": 1250000
}
```

### Health Check Response
```json
{
  "status": "UP",
  "totalRecords": 1250000,
  "timestamp": "2024-01-15T10:30:00"
}
```

## Error Responses

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
- Headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`

## CORS
- Allowed Origins: `http://localhost:3000`, `http://localhost:3001`, `http://127.0.0.1:3000`
- Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- Allowed Headers: `Content-Type`, `Authorization`, `X-Requested-With`
