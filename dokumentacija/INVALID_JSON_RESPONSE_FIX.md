# Invalid JSON Response Fix

## Problem
Server vraća status 200 ali neispravan JSON response:

```
Server returned invalid JSON response. Status: 200
```

## Uzrok
Backend endpoint radi (status 200) ali ne vraća ispravan JSON format.

## Rešenje

### 1. **Proveri da li je backend pokrenut:**
```bash
# Proveri da li backend radi
curl -X GET "http://localhost:8080/api/global-license/status"
```

### 2. **Proveri da li endpoint vraća JSON:**
```bash
# Testiraj sa curl
curl -X GET "http://localhost:8080/api/global-license/status" -H "Accept: application/json"
```

### 3. **Proveri da li je tabela kreirana:**
```sql
-- Proveri da li tabela postoji
SELECT * FROM information_schema.tables WHERE table_name = 'global_license';

-- Proveri da li ima podataka
SELECT * FROM global_license;
```

### 4. **Ako tabela ne postoji, kreiraj je:**
```sql
\i postgresQuery/create_global_license_table.sql
```

### 5. **Ako tabela postoji ali nema podataka, dodaj podatke:**
```sql
\i postgresQuery/setup_global_license_expired_fixed.sql
```

## Testiranje

### 1. **Testiraj backend direktno:**
```bash
# Testiraj status endpoint
curl -X GET "http://localhost:8080/api/global-license/status" -v

# Testiraj check endpoint
curl -X GET "http://localhost:8080/api/global-license/check" -v

# Testiraj active endpoint
curl -X GET "http://localhost:8080/api/global-license/active" -v
```

### 2. **Proveri backend logove:**
Trebalo bi da vidiš:
```
=== GLOBAL LICENSE INTERCEPTOR DEBUG ===
Request Path: /api/global-license/status
Method: GET
Global license endpoint excluded: /api/global-license/status
```

### 3. **Testiraj u browser console:**
```javascript
// Testiraj status
fetch('/api/global-license/status')
  .then(response => {
    console.log('Response status:', response.status);
    console.log('Response headers:', response.headers);
    return response.text(); // Prvo kao text da vidiš šta vraća
  })
  .then(text => {
    console.log('Response text:', text);
    try {
      const json = JSON.parse(text);
      console.log('Parsed JSON:', json);
    } catch (e) {
      console.error('JSON parse error:', e);
    }
  })
  .catch(error => console.error('Error:', error));
```

## Troubleshooting

### 1. **Ako backend ne radi:**
```bash
# Pokreni backend
mvn spring-boot:run
```

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan
- Proveri da li je controller kreiran

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` kreiran
- Proveri da li je tabela kreirana
- Proveri backend logove za greške

### 4. **Ako dobiješ HTML response umesto JSON:**
- Proveri da li je controller pravilno konfigurisan
- Proveri da li je `@RestController` anotacija dodana
- Proveri da li je `@RequestMapping` ispravan

## Očekivani rezultat

### 1. **Backend endpoint treba da vraća:**
```json
{
  "hasValidLicense": false,
  "endDate": "2024-09-28T12:00:00",
  "daysUntilExpiry": -1,
  "isExpiringSoon": false,
  "message": "Global license has expired"
}
```

### 2. **Frontend treba da može da parsira JSON:**
```javascript
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => {
    console.log('License Status:', data);
    console.log('Has Valid License:', data.hasValidLicense);
    console.log('Message:', data.message);
  })
  .catch(error => console.error('Error:', error));
```

## Checklist

- [ ] Backend je pokrenut
- [ ] Tabela global_license postoji
- [ ] Tabela ima podatke
- [ ] Controller je kreiran
- [ ] Endpoint vraća JSON
- [ ] Frontend može da parsira JSON

## Rezultat

Nakon ovih koraka, backend treba da vraća ispravan JSON response i frontend treba da može da ga parsira.
