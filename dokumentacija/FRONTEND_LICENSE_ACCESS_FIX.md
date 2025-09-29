# Frontend License Access Fix

## Problem
Frontend dobija 403 grešku: "Nemate dozvolu za pristup globalnoj licenci."

## Rešenje
Problem je bio u interceptor-u koji je blokirao pristup global license endpoint-ima. Dodao sam izuzetak za `/api/global-license/` endpoint-e.

## Testiranje

### 1. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

### 2. **Testiraj endpoint-e:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"

# Testiraj check
curl -X GET "http://localhost:8080/api/global-license/check"

# Testiraj active
curl -X GET "http://localhost:8080/api/global-license/active"
```

### 3. **Testiraj u browser console:**
```javascript
// Testiraj status
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => console.log('Status:', data))
  .catch(error => console.error('Error:', error));

// Testiraj check
fetch('/api/global-license/check')
  .then(response => response.json())
  .then(data => console.log('Check:', data))
  .catch(error => console.error('Error:', error));
```

## Očekivani odgovor

### **Status endpoint:**
```json
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 45,
  "isExpiringSoon": false,
  "message": "License is valid"
}
```

### **Check endpoint:**
```json
{
  "hasValidLicense": true,
  "message": "Global license is valid"
}
```

## Ako i dalje ne radi

### 1. **Proveri da li je tabela kreirana:**
```sql
\i postgresQuery/create_global_license_table.sql
```

### 2. **Proveri da li je backend pokrenut:**
```bash
# Proveri da li backend radi
curl -X GET "http://localhost:8080/api/global-license/status" -v
```

### 3. **Proveri logove:**
```bash
# Proveri backend logove za greške
tail -f logs/application.log
```

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
