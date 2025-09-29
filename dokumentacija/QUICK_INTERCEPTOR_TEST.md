# Quick Interceptor Test

## Problem
I dalje dobijaš 403 grešku jer se interceptor poziva pre nego što se endpoint registruje.

## Rešenje

### 1. **Privremeno ukloni interceptor:**

**U `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // Privremeno ukloni interceptor da testiraš da li endpoint-i rade
    // registry.addInterceptor(globalLicenseCheckInterceptor)
    //         .addPathPatterns("/api/**")
    //         .excludePathPatterns(
    //             "/api/global-license/**",
    //             "/api/auth/**",
    //             "/api/public/**",
    //             "/error",
    //             "/actuator/**"
    //         );
}
```

### 2. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

### 3. **Testiraj u browser console:**
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

### 4. **Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

## Očekivani rezultat

- ✅ Backend se pokreće bez grešaka
- ✅ Global license endpoint-i rade
- ✅ Frontend može da pozove endpoint-e
- ✅ Nema 403 greške

## Ako radi

### 1. **Dodaj interceptor nazad sa ispravnim konfiguracijama:**

**U `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalLicenseCheckInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/global-license/**",  // ✅ Exclude sve global license endpoint-e
                "/api/auth/**",
                "/api/public/**",
                "/error",
                "/actuator/**"
            )
            .order(1);  // ✅ Dodaj order
}
```

### 2. **Testiraj ponovo:**
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

## Ako ne radi

### 1. **Proveri da li je backend pokrenut:**
```bash
# Proveri da li je backend pokrenut
netstat -an | findstr :8080
```

### 2. **Proveri da li je `GlobalLicenseController` kreiran:**
```java
@RestController
@RequestMapping("/api/global-license")
@CrossOrigin(origins = "*")
public class GlobalLicenseController {
    // ...
}
```

### 3. **Proveri da li je `GlobalLicenseService` kreiran:**
```java
@Service
public class GlobalLicenseService {
    // ...
}
```

## Checklist

- [ ] Interceptor je uklonjen
- [ ] Backend je restartovan
- [ ] Testiraj sve endpoint-e
- [ ] Proveri da li radi u browser console
- [ ] Ako radi, dodaj interceptor nazad
- [ ] Testiraj ponovo

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
