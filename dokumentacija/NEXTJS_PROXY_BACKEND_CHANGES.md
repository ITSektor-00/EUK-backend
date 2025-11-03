# Backend Izmene za Next.js Proxy Arhitekturu

## Pregled

Backend je ažuriran da podržava novu arhitekturu gde frontend koristi Next.js API route-ove kao proxy za backend zahteve.

## Izmene

### 1. CORS Konfiguracija

#### ProductionSecurityConfig.java

- **Promenjeno** sa `setAllowedOrigins()` na `setAllowedOriginPatterns()` za fleksibilniju konfiguraciju
- **Dodato** podrška za `host.docker.internal:*` za Docker networking
- **Dodato** pattern matching (`http://localhost:*`, `http://127.0.0.1:*`) da dozvoli različite portove

**Ključne izmene:**
```java
configuration.setAllowedOriginPatterns(List.of(
    "https://euk.vercel.app",
    "https://euk-it-sectors-projects.vercel.app",
    "http://localhost:*",
    "http://127.0.0.1:*",
    "http://host.docker.internal:*"  // Za Docker networking
));
```

#### DevelopmentSecurityConfig.java

- **Iste izmene** kao za production, ali sa dodatnom fleksibilnošću za development

### 2. Logovanje za Debugging

#### JwtAuthenticationFilter.java

- **Dodato** logovanje Origin, Referer, User-Agent i RemoteAddr header-a za sve API zahteve
- **Logovanje se dešava na DEBUG nivou** - vidljivo kada je log level podešen na DEBUG

**Log format:**
```
DEBUG API Request - URI: /api/export/dynamic, Origin: null, Referer: null, User-Agent: node-fetch/2.6.7, RemoteAddr: 172.18.0.2
```

**Upotreba:**
- Omogućava debugging zahteva koji dolaze od Next.js servera
- Pomaže identifikaciji problema sa CORS konfiguracijom
- Korisno za razumevanje server-to-server komunikacije

### 3. HTTP Metode

**Dodato** `PATCH` metoda u dozvoljene HTTP metode za CORS.

## Zašto su ove izmene potrebne?

### Problem: Server-to-Server Komunikacija

Kada Next.js server šalje zahteve kao proxy:
- Origin header može biti `null` ili drugačiji od browser origin-a
- User-Agent će biti Next.js server User-Agent (npr. `node-fetch/...`)
- IP adresa će biti Next.js server IP, ne browser IP

### Rešenje: Pattern Matching

Korišćenje `setAllowedOriginPatterns()` umesto `setAllowedOrigins()`:
- Dozvoljava fleksibilnije pattern matching
- Podržava wildcard za portove (`:*`)
- Omogućava server-to-server komunikaciju

## Docker Networking

### host.docker.internal

Backend sada dozvoljava zahteve sa `http://host.docker.internal:*` origin-a, što omogućava:
- Next.js serveru u Docker kontejneru da pristupi backend-u preko `host.docker.internal:8080`
- Komunikaciji između različitih Docker servisa

**Napomena:** Ako su frontend i backend u istom Docker Compose, može se koristiti i service name:
```javascript
backendUrl: 'http://backend:8080'  // Umesto host.docker.internal
```

## Testiranje

### 1. Provera logova

```bash
# Pregled backend logova sa DEBUG nivoom
docker-compose logs backend -f | grep "API Request"
```

Očekivani output za zahteve od Next.js servera:
```
DEBUG API Request - URI: /api/export/dynamic, Origin: null, Referer: null, User-Agent: node-fetch/..., RemoteAddr: 172.18.0.2
```

### 2. Test CORS konfiguracije

```bash
# Test iz Next.js kontejnera
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://host.docker.internal:8080/api/export/dynamic
```

### 3. Test endpoint-a

```bash
# Direktan test endpoint-a
curl http://localhost:8080/api/export/dynamic
```

## Sigurnosne Napomene

### Pattern Matching vs Explicit Origins

- ✅ **Pattern matching** omogućava fleksibilnost, ali mora se koristiti pažljivo
- ⚠️ **Wildcard (`*`)** nije korišćen u production - koriste se konkretni pattern-i
- ✅ **Environment varijable** (`EUK_ALLOWED_DOMAINS`) omogućavaju konfiguraciju bez redeploy-a

### Preporuke

1. **U production:** Koristiti eksplicitne origin-e kroz `EUK_ALLOWED_DOMAINS` environment varijablu
2. **U development:** Pattern matching je OK za lakše testiranje
3. **Monitoring:** Pratiti logove da vidite koje origin-e stižu

## Environment Varijable

### EUK_ALLOWED_DOMAINS

```bash
# Production
EUK_ALLOWED_DOMAINS=https://euk.vercel.app,https://euk-it-sectors-projects.vercel.app

# Development
EUK_ALLOWED_DOMAINS=http://localhost:3000,http://localhost:3001
```

**Napomena:** Ako je `EUK_ALLOWED_DOMAINS` podešena, koristi se ta lista umesto default vrednosti.

## Rezime

✅ **CORS konfiguracija** ažurirana za server-to-server komunikaciju  
✅ **Pattern matching** omogućen za fleksibilniju konfiguraciju  
✅ **Logovanje** dodato za debugging  
✅ **Docker networking** podrška dodata (`host.docker.internal`)  
✅ **PATCH metoda** dodata u dozvoljene metode  

## Pitanja?

- Da li su svi potrebni origin-i dodati u konfiguraciju?
- Da li je Docker networking pravilno podešen?
- Da li se logovanje pojavljuje u logovima?

