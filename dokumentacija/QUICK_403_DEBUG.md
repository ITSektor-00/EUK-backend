# Quick 403 Debug

## Problem
Frontend dobija `403 Forbidden` grešku kada pokušava da pozove `/api/global-license/status` endpoint.

## Rešenje

### 1. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

### 2. **Testiraj u browser console:**
```javascript
// Testiraj status
fetch('/api/global-license/status')
  .then(response => {
    console.log('Status:', response.status);
    return response.json();
  })
  .then(data => console.log('Data:', data))
  .catch(error => console.error('Error:', error));
```

### 3. **Proveri backend logove:**
Trebalo bi da vidiš:
```
=== GLOBAL LICENSE INTERCEPTOR DEBUG ===
Request Path: /api/global-license/status
Method: GET
Global license endpoint excluded: /api/global-license/status
```

### 4. **Ako i dalje ima 403 grešku:**

**Proveri da li je interceptor pravilno konfigurisan u `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalLicenseCheckInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/global-license/**",  // ✅ Ovo je ključno!
                "/api/auth/**",
                "/api/public/**",
                "/error",
                "/actuator/**"
            );
}
```

### 5. **Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

## Troubleshooting

### 1. **Ako i dalje ima 403 grešku:**
- Proveri da li je interceptor pravilno konfigurisan
- Proveri da li su endpoint-i excluded
- Proveri backend logove za debug poruke

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` pravilno konfigurisan
- Proveri da li je `GlobalLicenseRepository` pravilno konfigurisan

## Checklist

- [ ] Backend je restartovan
- [ ] Interceptor je pravilno konfigurisan
- [ ] Global license endpoint-i su excluded
- [ ] Debug log je dodat
- [ ] Testiraj sve endpoint-e
- [ ] Proveri da li radi u browser console

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
