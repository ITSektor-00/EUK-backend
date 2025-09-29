# Rate Limiting Fix

## Problem
Backend baca grešku:
```
java.util.regex.PatternSyntaxException: Dangling meta character '*' near index 11
/api/auth/**
```

## Rešenje
Popravljen je `RateLimitingFilter` da pravilno konvertuje glob pattern-e u regex.

## Šta je popravljeno

### **Staro (neispravno):**
```java
if (requestURI.matches(pattern.trim())) {
    // Ovo ne radi sa /api/auth/**
}
```

### **Novo (ispravno):**
```java
String regexPattern = trimmedPattern
    .replace("**", ".*")
    .replace("*", "[^/]*")
    .replace(".", "\\.");

if (requestURI.matches(regexPattern)) {
    // Ovo radi sa /api/auth/**
}
```

## Testiranje

### 1. **Restartuj backend:**
```bash
# Zaustavi backend (Ctrl+C)
# Pokreni ponovo
mvn spring-boot:run
```

### 2. **Testiraj endpoint-e:**
```bash
# Testiraj global license endpoint-e
curl -X GET "http://localhost:8080/api/global-license/status"
curl -X GET "http://localhost:8080/api/global-license/check"
curl -X GET "http://localhost:8080/api/global-license/active"
```

### 3. **Testiraj auth endpoint-e:**
```bash
# Testiraj auth endpoint-e
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d '{"username":"test","password":"test"}'
```

## Očekivani rezultat

- ✅ Backend se pokreće bez grešaka
- ✅ Global license endpoint-i rade
- ✅ Auth endpoint-i rade
- ✅ Rate limiting radi ispravno

## Troubleshooting

### Ako i dalje ima greške:

1. **Proveri da li je fajl sačuvan**
2. **Proveri da li je backend restartovan**
3. **Proveri logove za nove greške**

### Ako rate limiting ne radi:

1. **Proveri da li je `euk.rate-limit.enabled=true` u application.properties**
2. **Proveri da li su exclude patterns ispravni**
3. **Testiraj sa različitim endpoint-ima**

## Rezultat

Nakon ovih koraka, backend treba da radi bez regex grešaka i frontend treba da može da pozove globalne license endpoint-e.