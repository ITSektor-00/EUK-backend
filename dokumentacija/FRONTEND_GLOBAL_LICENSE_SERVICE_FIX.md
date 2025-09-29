# Frontend Global License Service Fix

## Problem
Frontend pokušava da pozove global license endpoint-e ali ima grešku u `globalLicenseService.ts` na liniji 124.

## Rešenje

### 1. **Proveri da li backend radi:**
```bash
# Testiraj da li backend odgovara
curl -X GET "http://localhost:8080/api/global-license/status"
```

### 2. **Ako backend ne radi, pokreni ga:**
```bash
# Pokreni backend
mvn spring-boot:run
```

### 3. **Ako backend radi, proveri URL u frontend servisu:**

**Proveri `globalLicenseService.ts`:**
```typescript
// Trebalo bi da bude:
const url = '/api/global-license/status';

// NE:
const url = '/api/licenses/status?userId=1';
```

### 4. **Proveri headers u frontend servisu:**

**Trebalo bi da bude:**
```typescript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${token}`
};
```

### 5. **Proveri da li je CORS konfigurisan:**

**U backend-u treba da bude:**
```java
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/global-license")
public class GlobalLicenseController {
    // ...
}
```

## Frontend Fix

### 1. **Ažuriraj globalLicenseService.ts:**

```typescript
// Zameni stare endpoint-e sa novim
const response = await fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
});
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

### 1. **Testiraj u browser console:**
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

### 2. **Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

## Troubleshooting

### 1. **Ako dobiješ CORS grešku:**
- Proveri da li je `@CrossOrigin(origins = "*")` dodano u controller
- Proveri da li su CORS headers ispravni

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 403 grešku:**
- Proveri da li je interceptor ispravno konfigurisan
- Proveri da li su endpoint-i excluded

## Checklist

- [ ] Backend je pokrenut
- [ ] GlobalLicenseController je kreiran
- [ ] CORS je konfigurisan
- [ ] Frontend koristi nove endpoint-e
- [ ] Headers su ispravni
- [ ] Error handling je dodan
- [ ] Testiraj sve endpoint-e

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez grešaka.
