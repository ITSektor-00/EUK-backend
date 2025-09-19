# EUK API Documentation

## Overview

The EUK (Evidencija Ugroženih Korisnika) API provides comprehensive management of categories, cases, and vulnerable persons. The API is built with Spring Boot and follows RESTful principles.

## Base URL

```
http://localhost:8080/api/euk
```

## Authentication

All endpoints require JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. EUK Kategorije (Categories)

#### GET /api/euk/kategorije
Get all categories ordered by name.

**Response:**
```json
[
  {
    "kategorijaId": 1,
    "naziv": "Porodično nasilje"
  },
  {
    "kategorijaId": 2,
    "naziv": "Trgovina ljudima"
  }
]
```

#### GET /api/euk/kategorije/{id}
Get a specific category by ID.

**Response:**
```json
{
  "kategorijaId": 1,
  "naziv": "Porodično nasilje"
}
```

#### POST /api/euk/kategorije
Create a new category.

**Request Body:**
```json
{
  "naziv": "Nova kategorija"
}
```

#### PUT /api/euk/kategorije/{id}
Update an existing category.

**Request Body:**
```json
{
  "naziv": "Ažurirana kategorija"
}
```

#### DELETE /api/euk/kategorije/{id}
Delete a category (only if no cases are associated).

### 2. EUK Predmeti (Cases)

#### GET /api/euk/predmeti
Get all cases with pagination and optional filters.

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `status` (optional) - Filter by status: "aktivan", "zatvoren", "na_cekanju", "u_obradi"
- `prioritet` (optional) - Filter by priority: "nizak", "srednji", "visok", "kritičan"
- `kategorijaId` (optional) - Filter by category ID
- `odgovornaOsoba` (optional) - Filter by responsible person (partial match)

**Example:**
```
GET /api/euk/predmeti?page=0&size=5&status=aktivan&prioritet=visok
```

**Response:**
```json
{
  "content": [
    {
      "predmetId": 1,
      "datumKreiranja": "2024-01-15",
      "nazivPredmeta": "Slučaj porodičnog nasilja",
      "status": "aktivan",
      "odgovornaOsoba": "Marko Petrović",
      "prioritet": "visok",
      "rokZaZavrsetak": "2024-02-15",
      "kategorijaId": 1,
      "kategorijaNaziv": "Porodično nasilje",
      "brojUgrozenihLica": 3
    }
  ],
  "totalElements": 25,
  "totalPages": 5,
  "size": 5,
  "number": 0
}
```

#### GET /api/euk/predmeti/{id}
Get a specific case by ID with associated vulnerable persons.

**Response:**
```json
{
  "predmetId": 1,
  "datumKreiranja": "2024-01-15",
  "nazivPredmeta": "Slučaj porodičnog nasilja",
  "status": "aktivan",
  "odgovornaOsoba": "Marko Petrović",
  "prioritet": "visok",
  "rokZaZavrsetak": "2024-02-15",
  "kategorijaId": 1,
  "kategorijaNaziv": "Porodično nasilje",
  "brojUgrozenihLica": 3,
  "ugrozenaLica": [
    {
      "ugrozenoLiceId": 1,
      "ime": "Ana",
      "prezime": "Jovanović",
      "jmbg": "1234567890123",
      "datumRodjenja": "1985-03-15",
      "drzavaRodjenja": "Srbija",
      "mestoRodjenja": "Beograd",
      "opstinaRodjenja": "Novi Beograd"
    }
  ]
}
```

#### POST /api/euk/predmeti
Create a new case.

**Request Body:**
```json
{
  "nazivPredmeta": "Novi slučaj",
  "status": "aktivan",
  "odgovornaOsoba": "Petar Marković",
  "prioritet": "srednji",
  "rokZaZavrsetak": "2024-03-15",
  "kategorijaId": 1
}
```

#### PUT /api/euk/predmeti/{id}
Update an existing case.

**Request Body:**
```json
{
  "nazivPredmeta": "Ažurirani slučaj",
  "status": "u_obradi",
  "odgovornaOsoba": "Petar Marković",
  "prioritet": "visok",
  "rokZaZavrsetak": "2024-03-15",
  "kategorijaId": 1
}
```

#### DELETE /api/euk/predmeti/{id}
Delete a case (only if no vulnerable persons are associated).

#### GET /api/euk/predmeti/{id}/ugrozena-lica
Get all vulnerable persons for a specific case.

**Response:**
```json
[
  {
    "ugrozenoLiceId": 1,
    "ime": "Ana",
    "prezime": "Jovanović",
    "jmbg": "1234567890123",
    "datumRodjenja": "1985-03-15",
    "drzavaRodjenja": "Srbija",
    "mestoRodjenja": "Beograd",
    "opstinaRodjenja": "Novi Beograd",
    "predmetId": 1,
    "predmetNaziv": "Slučaj porodičnog nasilja",
    "predmetStatus": "aktivan"
  }
]
```

### 3. EUK Ugrožena Lica (Vulnerable Persons)

#### GET /api/euk/ugrozena-lica
Get all vulnerable persons with pagination.

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

**Response:**
```json
{
  "content": [
    {
      "ugrozenoLiceId": 1,
      "ime": "Ana",
      "prezime": "Jovanović",
      "jmbg": "1234567890123",
      "datumRodjenja": "1985-03-15",
      "drzavaRodjenja": "Srbija",
      "mestoRodjenja": "Beograd",
      "opstinaRodjenja": "Novi Beograd",
      "predmetId": 1,
      "predmetNaziv": "Slučaj porodičnog nasilja",
      "predmetStatus": "aktivan"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

#### GET /api/euk/ugrozena-lica/{id}
Get a specific vulnerable person by ID.

**Response:**
```json
{
  "ugrozenoLiceId": 1,
  "ime": "Ana",
  "prezime": "Jovanović",
  "jmbg": "1234567890123",
  "datumRodjenja": "1985-03-15",
  "drzavaRodjenja": "Srbija",
  "mestoRodjenja": "Beograd",
  "opstinaRodjenja": "Novi Beograd",
  "predmetId": 1,
  "predmetNaziv": "Slučaj porodičnog nasilja",
  "predmetStatus": "aktivan"
}
```

#### POST /api/euk/ugrozena-lica
Create a new vulnerable person.

**Request Body:**
```json
{
  "ime": "Milan",
  "prezime": "Petrović",
  "jmbg": "9876543210987",
  "datumRodjenja": "1990-07-20",
  "drzavaRodjenja": "Srbija",
  "mestoRodjenja": "Novi Sad",
  "opstinaRodjenja": "Novi Sad",
  "predmetId": 1
}
```

#### PUT /api/euk/ugrozena-lica/{id}
Update an existing vulnerable person.

**Request Body:**
```json
{
  "ime": "Milan",
  "prezime": "Petrović",
  "jmbg": "9876543210987",
  "datumRodjenja": "1990-07-20",
  "drzavaRodjenja": "Srbija",
  "mestoRodjenja": "Novi Sad",
  "opstinaRodjenja": "Novi Sad",
  "predmetId": 1
}
```

#### DELETE /api/euk/ugrozena-lica/{id}
Delete a vulnerable person.

#### GET /api/euk/ugrozena-lica/search/{jmbg}
Search for a vulnerable person by JMBG.

**Response:**
```json
{
  "ugrozenoLiceId": 1,
  "ime": "Ana",
  "prezime": "Jovanović",
  "jmbg": "1234567890123",
  "datumRodjenja": "1985-03-15",
  "drzavaRodjenja": "Srbija",
  "mestoRodjenja": "Beograd",
  "opstinaRodjenja": "Novi Beograd",
  "predmetId": 1,
  "predmetNaziv": "Slučaj porodičnog nasilja",
  "predmetStatus": "aktivan"
}
```

## Data Validation

### Case Status Values
- `aktivan` - Active
- `zatvoren` - Closed
- `na_cekanju` - Pending
- `u_obradi` - In Progress

### Priority Values
- `nizak` - Low
- `srednji` - Medium
- `visok` - High
- `kritičan` - Critical

### JMBG Validation
- Must contain exactly 13 digits
- Must be unique across all vulnerable persons
- Only numeric characters allowed

### Date Validation
- Birth date cannot be in the future
- Case creation date is automatically set to current date
- Due date can be null

## Error Responses

All endpoints return consistent error responses:

```json
{
  "errorCode": "EUK_ERROR",
  "message": "Detailed error message",
  "path": "/api/euk/predmeti/123"
}
```

### Common Error Codes
- `EUK_ERROR` - General EUK-related errors
- `INVALID_INPUT` - Validation errors
- `USER_NOT_FOUND` - Resource not found
- `INTERNAL_ERROR` - Server errors

## Database Schema

The API uses the EUK schema with the following tables:

### EUK.kategorija
- `kategorija_id` (SERIAL PRIMARY KEY)
- `naziv` (VARCHAR(255) NOT NULL)

### EUK.predmet
- `predmet_id` (SERIAL PRIMARY KEY)
- `datum_kreiranja` (DATE NOT NULL DEFAULT CURRENT_DATE)
- `naziv_predmeta` (VARCHAR(255) NOT NULL)
- `status` (VARCHAR(50) NOT NULL)
- `odgovorna_osoba` (VARCHAR(255) NOT NULL)
- `prioritet` (VARCHAR(20) NOT NULL)
- `rok_za_zavrsetak` (DATE)
- `kategorija_id` (INTEGER NOT NULL FK)

### EUK.ugrozeno_lice
- `ugrozeno_lice_id` (SERIAL PRIMARY KEY)
- `ime` (VARCHAR(100) NOT NULL)
- `prezime` (VARCHAR(100) NOT NULL)
- `jmbg` (CHAR(13) NOT NULL UNIQUE)
- `datum_rodjenja` (DATE NOT NULL)
- `drzava_rodjenja` (VARCHAR(100) NOT NULL)
- `mesto_rodjenja` (VARCHAR(100) NOT NULL)
- `opstina_rodjenja` (VARCHAR(100) NOT NULL)
- `predmet_id` (INTEGER NOT NULL FK)

## Usage Examples

### Creating a Complete Case Workflow

1. **Create a category:**
```bash
curl -X POST http://localhost:8080/api/euk/kategorije \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"naziv": "Porodično nasilje"}'
```

2. **Create a case:**
```bash
curl -X POST http://localhost:8080/api/euk/predmeti \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nazivPredmeta": "Slučaj porodičnog nasilja",
    "status": "aktivan",
    "odgovornaOsoba": "Marko Petrović",
    "prioritet": "visok",
    "kategorijaId": 1
  }'
```

3. **Add vulnerable persons:**
```bash
curl -X POST http://localhost:8080/api/euk/ugrozena-lica \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ime": "Ana",
    "prezime": "Jovanović",
    "jmbg": "1234567890123",
    "datumRodjenja": "1985-03-15",
    "drzavaRodjenja": "Srbija",
    "mestoRodjenja": "Beograd",
    "opstinaRodjenja": "Novi Beograd",
    "predmetId": 1
  }'
```

4. **Search for vulnerable person by JMBG:**
```bash
curl -X GET http://localhost:8080/api/euk/ugrozena-lica/search/1234567890123 \
  -H "Authorization: Bearer <token>"
```

## Security Considerations

- All endpoints require JWT authentication
- Input validation is performed on all endpoints
- SQL injection protection through JPA/Hibernate
- CORS is configured for specific origins
- Rate limiting is applied to prevent abuse

## Performance Considerations

- Pagination is implemented for large datasets
- Database indexes are recommended on frequently queried fields
- Lazy loading is used for entity relationships
- Query optimization through custom repository methods
