# Bean Creation Error Fix

## Problem
Backend ne može da pronađe `GlobalLicenseCheckInterceptor` bean:

```
Field globalLicenseCheckInterceptor in com.sirus.backend.config.WebConfig required a bean of type 'com.sirus.backend.interceptor.GlobalLicenseCheckInterceptor' that could not be found.
```

## Uzrok
Problem je verovatno što se `GlobalLicenseService` ne može da kreira, što sprečava kreiranje `GlobalLicenseCheckInterceptor` bean-a.

## Rešenje

### 1. **Proveri da li je `GlobalLicenseService` kreiran:**

**U `GlobalLicenseService.java`:**
```java
@Service
public class GlobalLicenseService {
    // ...
}
```

### 2. **Proveri da li je `GlobalLicenseRepository` kreiran:**

**U `GlobalLicenseRepository.java`:**
```java
@Repository
public interface GlobalLicenseRepository extends JpaRepository<GlobalLicense, Long> {
    // ...
}
```

### 3. **Proveri da li je `GlobalLicense` entity kreiran:**

**U `GlobalLicense.java`:**
```java
@Entity
@Table(name = "global_license")
public class GlobalLicense {
    // ...
}
```

### 4. **Proveri da li je tabela kreirana u bazi:**

**Pokreni SQL:**
```sql
-- Proveri da li postoji tabela
SELECT * FROM information_schema.tables WHERE table_name = 'global_license';

-- Ako ne postoji, pokreni
\i postgresQuery/create_global_license_table.sql
```

### 5. **Ako i dalje ne radi, privremeno ukloni interceptor:**

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

## Testiranje

### 1. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

### 2. **Proveri da li se backend pokreće:**
- Backend treba da se pokrene bez grešaka
- Proveri da li su svi bean-ovi kreirani

### 3. **Testiraj endpoint-e:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"
```

## Troubleshooting

### 1. **Ako i dalje ima grešku:**
- Proveri da li su svi potrebni fajlovi kreirani
- Proveri da li je tabela kreirana u bazi
- Proveri da li su svi bean-ovi pravilno konfigurirani

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` pravilno konfigurisan
- Proveri da li je `GlobalLicenseRepository` pravilno konfigurisan

## Checklist

- [ ] GlobalLicenseService je kreiran
- [ ] GlobalLicenseRepository je kreiran
- [ ] GlobalLicense entity je kreiran
- [ ] Tabela je kreirana u bazi
- [ ] Backend se pokreće bez grešaka
- [ ] Testiraj sve endpoint-e

## Rezultat

Nakon ovih koraka, backend treba da se pokrene bez grešaka i svi bean-ovi treba da budu kreirani.
