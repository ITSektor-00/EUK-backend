# Quick JSON Debug

## Problem
Server vraća status 200 ali neispravan JSON response.

## Brza dijagnoza

### 1. **Proveri da li je backend pokrenut:**
```bash
# Testiraj da li backend radi
curl -X GET "http://localhost:8080/api/global-license/status"
```

### 2. **Ako backend ne radi:**
```bash
# Pokreni backend
mvn spring-boot:run
```

### 3. **Ako backend radi, proveri response:**
```bash
# Testiraj sa curl i vidi šta vraća
curl -X GET "http://localhost:8080/api/global-license/status" -v
```

### 4. **Testiraj u browser console:**
```javascript
// Testiraj i vidi šta vraća
fetch('/api/global-license/status')
  .then(response => {
    console.log('Status:', response.status);
    console.log('Headers:', response.headers);
    return response.text(); // Prvo kao text
  })
  .then(text => {
    console.log('Response text:', text);
    console.log('Is JSON?', text.startsWith('{') || text.startsWith('['));
  })
  .catch(error => console.error('Error:', error));
```

## Mogući uzroci

### 1. **Backend nije pokrenut**
- Pokreni: `mvn spring-boot:run`

### 2. **Tabela ne postoji**
```sql
-- Proveri da li tabela postoji
SELECT * FROM information_schema.tables WHERE table_name = 'global_license';

-- Ako ne postoji, kreiraj je
\i postgresQuery/create_global_license_table.sql
```

### 3. **Tabela je prazna**
```sql
-- Proveri da li ima podataka
SELECT * FROM global_license;

-- Ako je prazna, dodaj podatke
\i postgresQuery/setup_global_license_expired_fixed.sql
```

### 4. **Controller nije kreiran**
- Proveri da li postoji `GlobalLicenseController.java`
- Proveri da li ima `@RestController` anotaciju

### 5. **Service nije kreiran**
- Proveri da li postoji `GlobalLicenseService.java`
- Proveri da li ima `@Service` anotaciju

## Rešenje

### 1. **Pokreni backend:**
```bash
mvn spring-boot:run
```

### 2. **Kreiraj tabelu:**
```sql
\i postgresQuery/create_global_license_table.sql
```

### 3. **Dodaj podatke:**
```sql
\i postgresQuery/setup_global_license_expired_fixed.sql
```

### 4. **Testiraj:**
```bash
curl -X GET "http://localhost:8080/api/global-license/status"
```

## Očekivani rezultat

```json
{
  "hasValidLicense": false,
  "endDate": "2024-09-28T12:00:00",
  "daysUntilExpiry": -1,
  "isExpiringSoon": false,
  "message": "Global license has expired"
}
```

## Rezultat

Nakon ovih koraka, backend treba da vraća ispravan JSON response.
