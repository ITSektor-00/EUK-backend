# Poruka za Backend Tim - Frontend Proxy Promene

## Sažetak

Frontend je migriran da koristi **Next.js API route-ove kao proxy** za sve backend zahteve. Svi API pozivi se sada prosleđuju kroz Next.js server, a ne direktno iz browser-a.

## Ključne izmene

### 1. Arhitektura zahteva
```
Browser → Next.js API Route → Backend Server (port 8080)
```

### 2. Docker okruženje
U production Docker okruženju, Next.js pokušava da pristupi backend-u na:
```
http://host.docker.internal:8080
```

### 3. Zahtevi dolaze od Next.js servera
- ❌ **Ne dolaze direktno od browser-a**
- ✅ **Dolaze od Next.js servera**

**Posledice:**
- `Origin` header = Next.js server URL
- `User-Agent` = Next.js server User-Agent  
- IP adresa = Next.js server IP (ili Docker host IP)

## Potrebne izmene na backend-u

### ✅ 1. Port 8080 (već postoji)
Backend je već dostupan na portu 8080 - **OK**

### ⚠️ 2. CORS konfiguracija
Backend CORS mora dozvoliti zahteve od:
- Next.js server URL-e (dodati u `allowedOrigins`)
- Browser origin-e (za development - već postoji)

**Trenutno u `ProductionSecurityConfig.java`:**
```java
configuration.setAllowedOrigins(List.of(
    "http://localhost",
    "http://localhost:3000",
    "http://localhost:3001",
    // ⚠️ POTREBNO: Dodati Next.js server URL-e ako su poznati
));
```

### ⚠️ 3. Docker networking
Proveriti da li Next.js kontejner može da pristupi backend-u preko `host.docker.internal:8080`.

**Test:**
```bash
# Iz Next.js kontejnera
curl http://host.docker.internal:8080/actuator/health
```

**Alternativa:** Ako su frontend i backend u istom Docker Compose, koristiti service name:
```javascript
backendUrl: 'http://backend:8080'  // Umesto host.docker.internal
```

## Helper funkcija (frontend implementacija)

```javascript
getBackendUrl() {
  // U Docker okruženju (production + server-side)
  if (NODE_ENV === 'production' && server-side) {
    return 'http://host.docker.internal:8080'
  }
  
  // U development okruženju
  return API_BASE_URL || 'http://localhost:8080'
}
```

## Akcije za backend tim

1. ✅ **Proveriti** da backend prima zahteve sa Next.js server origin-a
2. ✅ **Dodati** Next.js server URL-e u CORS `allowedOrigins` (ako su poznati)
3. ✅ **Testirati** da `host.docker.internal:8080` radi iz Next.js kontejnera
4. ✅ **Logovati** `Origin` header za debugging (opciono)

## Pitanja?

- Da li je potrebno dodati dodatne header-e za server-to-server komunikaciju?
- Da li treba posebna autentifikacija za zahteve od Next.js servera?
- Koji su tačni Next.js server URL-e u production Docker okruženju?

## Dodatna dokumentacija

Detaljnija dokumentacija: `dokumentacija/FRONTEND_PROXY_CHANGES.md`

