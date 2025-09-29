# Interceptor Order Fix

## Problem
I dalje dobijaš 403 grešku jer se interceptor poziva pre nego što se endpoint registruje.

## Rešenje

### 1. **Ukloni interceptor za global license endpoint-e:**

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
            );
}
```

### 2. **Ako i dalje ne radi, dodaj interceptor order:**

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

### 3. **Alternativno rešenje - ukloni interceptor potpuno:**

**U `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // Privremeno ukloni interceptor da testiraš
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

### 4. **Testiranje:**

**Testiraj u browser console:**
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

**Testiraj sa Postman-om:**
- GET `http://localhost:8080/api/global-license/status`
- GET `http://localhost:8080/api/global-license/check`
- GET `http://localhost:8080/api/global-license/active`

## Troubleshooting

### 1. **Ako i dalje ima 403 grešku:**
- Ukloni interceptor potpuno
- Testiraj da li endpoint-i rade
- Dodaj interceptor nazad sa ispravnim konfiguracijama

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` pravilno konfigurisan
- Proveri da li je `GlobalLicenseRepository` pravilno konfigurisan

## Checklist

- [ ] Interceptor je pravilno konfigurisan
- [ ] Global license endpoint-i su excluded
- [ ] Testiraj sve endpoint-e
- [ ] Proveri da li radi u browser console

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
