# Frontend Proxy Arhitektura - Promene

## Pregled promena

Frontend je ažuriran da koristi **Next.js API route-ove kao proxy** za sve backend zahteve. Ovo znači da zahtevi više nisu direktno iz browser-a, već se svi API pozivi prosleđuju kroz Next.js server.

## Tehničke detalje

### Arhitektura

```
Browser → Next.js API Route → Backend Server
```

Umesto:
```
Browser → Backend Server (direktno)
```

### Docker okruženje

U production Docker okruženju, Next.js server pokušava da pristupi backend-u preko:

```
http://host.docker.internal:8080
```

### Helper funkcija u frontend-u

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

Svi Next.js API route-ovi koriste ovu funkciju za prosleđivanje zahteva backend-u.

## Zahtevi za Backend tim

### 1. Dostupnost na portu 8080

Backend **mora biti dostupan na portu 8080** (ovo je već konfigurisano u Docker setup-u).

### 2. Zahtevi dolaze od Next.js servera

**Bitna izmena:** Backend više ne prima direktne zahteve od browser-a. Svi zahtevi dolaze od Next.js servera.

**Posledice:**
- `Origin` header će biti Next.js server URL, ne browser URL
- `User-Agent` će biti Next.js server User-Agent
- IP adresa će biti Next.js server IP (ili Docker host IP)

### 3. CORS konfiguracija

Backend CORS konfiguracija mora dozvoliti zahteve od:
- Next.js servera (u Docker okruženju: `http://host.docker.internal` ili Next.js container URL)
- Browser-a (za development: `http://localhost:3000`, `http://localhost:3001`)

**Trenutna CORS konfiguracija** u `ProductionSecurityConfig.java` i `DevelopmentSecurityConfig.java` treba da uključuje:
- Next.js server URL-e (ako su poznati)
- Browser origin-e (za development)

### 4. Docker networking

Ako se backend i frontend pokreću u Docker Compose okruženju:

**Opcija 1: Koristiti Docker service names**
```yaml
# frontend docker-compose
backend:
  build: .
  environment:
    BACKEND_URL: http://backend:8080  # Docker service name
```

**Opcija 2: Koristiti host.docker.internal** (trenutno rešenje)
```javascript
// frontend
backendUrl: 'http://host.docker.internal:8080'
```

**Preporuka:** Ako su oba servisa u istom Docker Compose, koristiti service names umesto `host.docker.internal`.

## Testiranje

### 1. Proveriti da backend prima zahteve

```bash
# Pregled backend logova
docker-compose logs backend -f

# Očekivani log:
# Request from: [Next.js server IP or host.docker.internal]
# Origin: [Next.js server URL]
```

### 2. Proveriti CORS

Ako se pojave CORS greške:
- Proveriti da li Next.js server URL je u `allowedOrigins` listi
- Proveriti da li `allowCredentials` je podešeno pravilno
- Proveriti da li `Access-Control-Allow-Origin` header se šalje

### 3. Proveriti networking

Ako Next.js ne može da pristupi backend-u:
```bash
# Iz Next.js kontejnera
curl http://host.docker.internal:8080/actuator/health

# Ili ako su u istom Docker Compose
curl http://backend:8080/actuator/health
```

## Preporuke

1. **Docker Compose setup:** Ako frontend i backend rade zajedno, koristiti Docker service names umesto `host.docker.internal`.

2. **CORS konfiguracija:** Dodati Next.js server URL-e u CORS `allowedOrigins` listu.

3. **Logging:** Dodati logovanje `Origin` i `User-Agent` header-a za debugging.

4. **Health check:** Next.js može da koristi `/actuator/health` endpoint za proveru dostupnosti backend-a.

## Trenutna konfiguracija

### Backend Docker ports
```yaml
# docker-compose.yml
backend:
  ports:
    - "${BACKEND_PORT}:8080"  # Podrazumevano: 8080:8080
```

### Backend CORS (ProductionSecurityConfig.java)
```java
configuration.setAllowedOrigins(List.of(
    "https://euk.vercel.app",
    "https://euk-it-sectors-projects.vercel.app",
    "http://localhost",
    "http://localhost:3000",
    "http://localhost:3001",
    "http://127.0.0.1:3000",
    "http://127.0.0.1"
));
```

**Napomena:** Ako Next.js server ima drugačiji URL u production Docker okruženju, dodati ga u ovu listu.

## Pitanja za backend tim

1. Da li je backend spreman da prima zahteve od Next.js servera?
2. Da li CORS konfiguracija dozvoljava Next.js server origin-e?
3. Da li je `host.docker.internal:8080` dostupan iz Next.js kontejnera?
4. Da li treba dodati dodatne header-e ili autentifikaciju za server-to-server komunikaciju?

