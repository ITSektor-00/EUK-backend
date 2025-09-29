# CORS Headers Fix

## Problem
Frontend šalje CORS headers koji nisu dozvoljeni od strane backend-a. **CORS headers treba da šalje BACKEND, ne frontend!**

## Rešenje

### 1. **Ukloni CORS headers iz frontend servisa:**

**STARO (pogrešno):**
```typescript
const response = await fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
    'Access-Control-Allow-Origin': '*',        // ❌ OVO NE TREBA!
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',  // ❌ OVO NE TREBA!
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'  // ❌ OVO NE TREBA!
  }
});
```

**NOVO (ispravno):**
```typescript
const response = await fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
});
```

### 2. **Backend već ima CORS konfiguraciju:**

**U `GlobalLicenseController.java`:**
```java
@RestController
@RequestMapping("/api/global-license")
@CrossOrigin(origins = "*")  // ✅ Backend šalje CORS headers
public class GlobalLicenseController {
    // ...
}
```

### 3. **Proveri da li je CORS filter konfigurisan:**

**U `WebConfig.java`:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Frontend Fix

### 1. **Ukloni CORS headers iz svih servisa:**

**`globalLicenseService.ts`:**
```typescript
// ✅ ISPRAVNO - samo potrebni headers
const response = await fetch('/api/global-license/status', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
});
```

**`licenseService.ts`:**
```typescript
// ✅ ISPRAVNO - samo potrebni headers
const response = await fetch('/api/global-license/check', {
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

### 1. **Ako i dalje ima CORS grešku:**
- Proveri da li je `@CrossOrigin(origins = "*")` dodano u controller
- Proveri da li je CORS filter konfigurisan u `WebConfig.java`

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 403 grešku:**
- Proveri da li je interceptor ispravno konfigurisan
- Proveri da li su endpoint-i excluded

## Checklist

- [ ] Ukloni CORS headers iz frontend servisa
- [ ] Backend ima `@CrossOrigin(origins = "*")` u controller-u
- [ ] CORS filter je konfigurisan u `WebConfig.java`
- [ ] Testiraj sve endpoint-e
- [ ] Proveri da li radi u browser console

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez CORS grešaka.
