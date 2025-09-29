# Quick Bean Fix

## Problem
Backend ne može da pronađe `GlobalLicenseCheckInterceptor` bean jer se `GlobalLicenseService` ne može da kreira.

## Rešenje

### 1. **Kreiraj tabelu u bazi:**

**Pokreni SQL:**
```sql
-- Pokreni create_global_license_table.sql
\i postgresQuery/create_global_license_table.sql
```

### 2. **Ako ne možeš da pokreneš SQL, privremeno ukloni interceptor:**

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

### 3. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

## Testiranje

### 1. **Proveri da li se backend pokreće:**
- Backend treba da se pokrene bez grešaka
- Proveri da li su svi bean-ovi kreirani

### 2. **Testiraj endpoint-e:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"
```

## Ako radi

### 1. **Dodaj interceptor nazad:**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalLicenseCheckInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/global-license/**",
                "/api/auth/**",
                "/api/public/**",
                "/error",
                "/actuator/**"
            );
}
```

### 2. **Testiraj ponovo:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"
```

## Rezultat

Nakon ovih koraka, backend treba da se pokrene bez grešaka i svi bean-ovi treba da budu kreirani.
