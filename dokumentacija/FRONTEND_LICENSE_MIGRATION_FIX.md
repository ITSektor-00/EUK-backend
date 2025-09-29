# Frontend License Migration Fix

## Problem

Frontend pokušava da pozove endpoint-e koji vraćaju 403 grešku:
- `Nemate dozvolu za pristup globalnoj licenci.`
- `Nemate dozvolu za pristup licencnim podacima.`

## Rešenje

### 1. **Proveri da li backend radi:**

```bash
# Testiraj da li backend odgovara
curl -X GET "http://localhost:8080/api/global-license/status"
curl -X GET "http://localhost:8080/api/global-license/check"
```

### 2. **Ako backend ne radi, pokreni ga:**

```bash
# Pokreni backend
mvn spring-boot:run
```

### 3. **Ako backend radi, proveri endpoint-e:**

```bash
# Proveri da li postoje endpoint-i
curl -X GET "http://localhost:8080/api/global-license/status" -v
curl -X GET "http://localhost:8080/api/global-license/check" -v
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

### 2. **Ažuriraj licenseService.ts:**

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

### 3. **Dodaj CORS podršku:**

```typescript
// Dodaj CORS headers u fetch zahteve
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

## Backend Fix

### 1. **Proveri da li je GlobalLicenseController registrovan:**

```java
// Proveri da li postoji u src/main/java/com/sirus/backend/controller/GlobalLicenseController.java
@RestController
@RequestMapping("/api/global-license")
@CrossOrigin(origins = "*")
public class GlobalLicenseController {
    // ...
}
```

### 2. **Proveri da li je WebConfig ispravan:**

```java
// Proveri src/main/java/com/sirus/backend/config/WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private GlobalLicenseCheckInterceptor globalLicenseCheckInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalLicenseCheckInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/global-license/status",
                    "/api/global-license/check", 
                    "/api/global-license/active",
                    "/api/global-license/create",
                    "/api/auth/**",
                    "/api/public/**",
                    "/error"
                );
    }
}
```

### 3. **Proveri da li je global_license tabela kreirana:**

```sql
-- Pokreni u PostgreSQL bazi
\i postgresQuery/create_global_license_table.sql
```

## Testiranje

### 1. **Testiraj backend endpoint-e:**

```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"

# Testiraj check
curl -X GET "http://localhost:8080/api/global-license/check"

# Testiraj active
curl -X GET "http://localhost:8080/api/global-license/active"
```

### 2. **Testiraj frontend:**

```javascript
// U browser console
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

## Troubleshooting

### 1. **Ako dobiješ 403 grešku:**
- Proveri da li je interceptor ispravno konfigurisan
- Proveri da li su endpoint-i excluded u WebConfig

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je GlobalLicenseController kreiran
- Proveri da li je backend pokrenut

### 3. **Ako dobiješ CORS grešku:**
- Proveri da li je @CrossOrigin(origins = "*") dodano
- Proveri da li su CORS headers ispravni

## Checklist

- [ ] Backend je pokrenut
- [ ] GlobalLicenseController je kreiran
- [ ] WebConfig je ispravan
- [ ] global_license tabela je kreirana
- [ ] Frontend koristi nove endpoint-e
- [ ] CORS je konfigurisan
- [ ] Testiraj sve endpoint-e
