# EUK Kombinovana Docker Aplikacija

Ovaj setup omogućava pokretanje i backend-a i frontend-a u jednom Docker kontejneru.

## Struktura

- **Backend**: Spring Boot aplikacija (port 3000)
- **Frontend**: Next.js aplikacija (port 3000)
- **Database**: PostgreSQL 16 (eksterna baza)
- **Nginx**: Reverse proxy (port 80)

## Fajlovi

- `Dockerfile.combined` - Multi-stage Dockerfile koji kombinuje backend i frontend
- `docker-compose.combined.yml` - Docker Compose konfiguracija
- `nginx-combined.conf` - Nginx konfiguracija za reverse proxy
- `start-combined.sh` - Skript za pokretanje servisa
- `scripts/start-combined.bat` - Windows skript za pokretanje
- `scripts/stop-combined.bat` - Windows skript za zaustavljanje

## Pokretanje

### Windows
```bash
# Pokretanje
scripts\start-combined.bat

# Zaustavljanje
scripts\stop-combined.bat
```

### Linux/Mac
```bash
# Gradnja i pokretanje
docker-compose -f docker-compose.combined.yml up --build

# Zaustavljanje
docker-compose -f docker-compose.combined.yml down
```

## Pristup

- **Frontend**: http://localhost
- **Backend API**: http://localhost/api
- **Database**: PostgreSQL 16 (eksterna)
- **Health Check**: http://localhost/api/test/health

## Konfiguracija

### Environment Variables
- `SPRING_PROFILES_ACTIVE=prod`
- `EUK_ALLOWED_DOMAINS` - Dozvoljeni domeni
- `EUK_RATE_LIMIT_ENABLED=true`
- `EUK_RATE_LIMIT_MAX_REQUESTS=150`
- `NODE_ENV=production`
- `DB_HOST=localhost` - Database host (eksterna baza)
- `DB_PORT=5432` - Database port
- `DB_NAME=euk_db` - Database name
- `DB_USERNAME=postgres.wynfrojhkzddzjbrpdcr` - Database username
- `DB_PASSWORD=a*Xxk3B7?HF8&3r` - Database password
- `ADMIN_PASSWORD=moja-admin-lozinka123` - Admin password

### Nginx Routing
- `/api/*` → Spring Boot backend (port 3000)
- `/ws/*` → WebSocket podrška za STOMP
- `/*` → Next.js frontend (port 3000)

## Prednosti

1. **Jednostavnost**: Jedan kontejner za aplikaciju
2. **Performanse**: Manje overhead-a
3. **Deployment**: Lakše za deployment na Render
4. **Networking**: Nema potrebe za Docker networking
5. **Database**: Koristi eksternu PostgreSQL 16 bazu

## Napomene

- Next.js je konfigurisan sa `output: 'standalone'` za optimalne performanse
- Nginx služi kao reverse proxy između frontend-a i backend-a
- WebSocket podrška je konfigurisana za STOMP protokol
- Health check proverava backend dostupnost
