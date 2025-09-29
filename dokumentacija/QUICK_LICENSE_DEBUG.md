# Quick License Debug Guide

## Problem
Frontend dobija 403 grešku: "Nemate dozvolu za pristup globalnoj licenci."

## Brza provera

### 1. **Proveri da li backend radi:**
```bash
curl -X GET "http://localhost:8080/api/global-license/status"
```

**Očekivani odgovor:**
```json
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 45,
  "isExpiringSoon": false,
  "message": "License is valid"
}
```

### 2. **Ako backend ne radi:**
```bash
# Pokreni backend
mvn spring-boot:run
```

### 3. **Ako backend radi, proveri endpoint-e:**
```bash
# Proveri sve endpoint-e
curl -X GET "http://localhost:8080/api/global-license/status" -v
curl -X GET "http://localhost:8080/api/global-license/check" -v
curl -X GET "http://localhost:8080/api/global-license/active" -v
```

### 4. **Ako endpoint-i ne postoje:**
```bash
# Proveri da li je GlobalLicenseController kreiran
ls src/main/java/com/sirus/backend/controller/GlobalLicenseController.java
```

### 5. **Ako controller ne postoji:**
```bash
# Kreiraj controller
# (Controller je već kreiran u src/main/java/com/sirus/backend/controller/GlobalLicenseController.java)
```

### 6. **Proveri da li je tabela kreirana:**
```sql
-- Pokreni u PostgreSQL bazi
\i postgresQuery/create_global_license_table.sql
```

### 7. **Proveri da li je WebConfig ispravan:**
```bash
# Proveri da li postoji
ls src/main/java/com/sirus/backend/config/WebConfig.java
```

## Frontend Fix

### 1. **Zameni stare endpoint-e sa novim:**

**STARO (obrisati):**
```typescript
fetch('/api/licenses/status?userId=1')
```

**NOVO (koristiti):**
```typescript
fetch('/api/global-license/status')
```

### 2. **Dodaj CORS headers:**
```typescript
fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
})
```

## Testiranje

### 1. **Testiraj u browser console:**
```javascript
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### 2. **Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
