# 403 Forbidden Fix

## Problem
Frontend dobija `403 Forbidden` grešku kada pokušava da pozove `/api/global-license/status` endpoint.

## Uzrok
`GlobalLicenseCheckInterceptor` blokira pristup global license endpoint-ima jer proverava globalnu licencu pre nego što dođe do controller-a.

## Rešenje

### 1. **Proveri da li je interceptor pravilno konfigurisan:**

**U `GlobalLicenseCheckInterceptor.java`:**
```java
// Preskoči proveru za global license endpoint-e (da ne bi bilo circular dependency)
if (requestPath.startsWith("/api/global-license/")) {
    return true;
}
```

### 2. **Proveri da li je interceptor registrovan u `WebConfig.java`:**

**U `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalLicenseCheckInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/**",
            "/api/global-license/**",  // ✅ Ovo je ključno!
            "/error",
            "/actuator/**"
        );
}
```

### 3. **Ako i dalje ne radi, dodaj debug log:**

**U `GlobalLicenseCheckInterceptor.java`:**
```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String requestPath = request.getRequestURI();
    String method = request.getMethod();
    
    // Debug log
    System.out.println("=== GLOBAL LICENSE INTERCEPTOR DEBUG ===");
    System.out.println("Request Path: " + requestPath);
    System.out.println("Method: " + method);
    
    // Preskoči proveru za excluded path-ove
    if (isExcludedPath(requestPath)) {
        System.out.println("Path excluded: " + requestPath);
        return true;
    }
    
    // Preskoči proveru za global license endpoint-e
    if (requestPath.startsWith("/api/global-license/")) {
        System.out.println("Global license endpoint excluded: " + requestPath);
        return true;
    }
    
    // ... ostatak koda
}
```

### 4. **Alternativno rešenje - ukloni interceptor za global license endpoint-e:**

**U `WebConfig.java`:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalLicenseCheckInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/**",
            "/api/global-license/**",  // ✅ Ovo je ključno!
            "/error",
            "/actuator/**"
        );
}
```

### 5. **Testiranje:**

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
- Proveri da li je interceptor pravilno konfigurisan
- Proveri da li su endpoint-i excluded
- Dodaj debug log da vidiš šta se dešava

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` pravilno konfigurisan
- Proveri da li je `GlobalLicenseRepository` pravilno konfigurisan

## Checklist

- [ ] Interceptor je pravilno konfigurisan
- [ ] Global license endpoint-i su excluded
- [ ] Debug log je dodat
- [ ] Testiraj sve endpoint-e
- [ ] Proveri da li radi u browser console

## Rezultat

Nakon ovih koraka, frontend treba da može da pozove globalne license endpoint-e bez 403 greške.
