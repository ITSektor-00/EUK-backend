# Quick Frontend License Debug

## Problem
Frontend pokušava da pozove global license endpoint-e ali ima grešku u `globalLicenseService.ts`.

## Brza dijagnoza

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

### 3. **Ako backend radi, proveri frontend servis:**

**Proveri `globalLicenseService.ts`:**
```typescript
// Trebalo bi da bude:
const url = '/api/global-license/status';

// NE:
const url = '/api/licenses/status?userId=1';
```

### 4. **Proveri headers:**
```typescript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${token}`
};
```

### 5. **Testiraj u browser console:**
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

## Frontend Fix

### 1. **Zameni stare endpoint-e sa novim:**

**STARO (obrisati):**
```typescript
const url = '/api/licenses/status?userId=1';
```

**NOVO (koristiti):**
```typescript
const url = '/api/global-license/status';
```

### 2. **Dodaj error handling:**
```typescript
try {
  const response = await fetch('/api/global-license/status', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  const data = await response.json();
  return data;
} catch (error) {
  console.error('Error fetching global license status:', error);
  throw error;
}
```

### 3. **Dodaj CORS podršku:**
```typescript
const response = await fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  }
});
```

## Testiranje

### 1. **Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

### 2. **Testiraj u browser console:**
```javascript
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez grešaka.
