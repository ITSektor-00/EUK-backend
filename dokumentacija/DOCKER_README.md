# EUK Backend Docker Setup

Ovaj fajl sadrži instrukcije za pokretanje EUK backend aplikacije koristeći Docker.

## Struktura

```
euk-backend/
├── Dockerfile                 # Docker image za backend aplikaciju
├── docker-compose.yml        # Docker Compose konfiguracija
├── .env                      # Environment varijable
├── init-scripts/             # SQL skripte za inicijalizaciju baze
│   ├── 01-create-database.sql
│   ├── 02-add-admin-user.sql
│   ├── 03-add-global-license.sql
│   └── 04-add-categories.sql
└── .dockerignore            # Fajlovi koji se ne uključuju u Docker image
```

## Pokretanje

### 1. Pokretanje samo backend-a sa bazom

```bash
# Pokretanje servisa
docker-compose up -d

# Pregled logova
docker-compose logs -f

# Zaustavljanje servisa
docker-compose down
```

### 2. Rebuild aplikacije

```bash
# Rebuild i pokretanje
docker-compose up --build -d

# Ili samo rebuild
docker-compose build --no-cache
```

### 3. Brisanje svih podataka

```bash
# Zaustavi i obriši sve
docker-compose down -v

# Pokretanje sa čistom bazom
docker-compose up -d
```

## Konfiguracija

### Environment varijable (.env)

```env
# Database Configuration
POSTGRES_DB=euk_database
POSTGRES_USER=euk_user
POSTGRES_PASSWORD=euk_password_2024
POSTGRES_PORT=5432

# Backend Configuration
BACKEND_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Security Configuration
ADMIN_PASSWORD=admin123
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000

# License Configuration
LICENSE_CHECK_INTERVAL=86400000
```

### Admin korisnik

- **Username:** admin
- **Password:** admin123
- **Email:** admin@euk.rs
- **Role:** admin

### Globalna licenca

Automatski se kreira globalna licenca sa:
- **Start date:** trenutni datum i vreme
- **End date:** 1 godina od kreiranja
- **Status:** aktivna

## Servisi

### Backend (Port 8080)
- Spring Boot aplikacija
- Health check: `http://localhost:8080/actuator/health`
- API dokumentacija: `http://localhost:8080/swagger-ui.html` (ako je omogućena)

### PostgreSQL (Port 5432)
- Baza podataka
- Host: localhost
- Port: 5432
- Database: euk_database
- Username: euk_user
- Password: euk_password_2024

## Inicijalizacija baze

Baza se automatski inicijalizuje sa:

1. **01-create-database.sql** - Kreiranje svih tabela, funkcija i indeksa
2. **02-add-admin-user.sql** - Dodavanje admin korisnika
3. **03-add-global-license.sql** - Kreiranje globalne licence
4. **04-add-categories.sql** - Dodavanje osnovnih kategorija

## Troubleshooting

### Problem sa konekcijom na bazu
```bash
# Proveri da li je PostgreSQL pokrenut
docker-compose ps

# Pregled logova PostgreSQL-a
docker-compose logs postgres
```

### Problem sa backend aplikacijom
```bash
# Pregled logova backend-a
docker-compose logs backend

# Restart backend servisa
docker-compose restart backend
```

### Brisanje i ponovno kreiranje
```bash
# Zaustavi sve servise
docker-compose down

# Obriši sve volume-ove (uključujući bazu)
docker-compose down -v

# Obriši sve image-ove
docker-compose down --rmi all

# Ponovno pokretanje
docker-compose up --build -d
```

## Produkcija

Za produkciju, promenite sledeće u `.env` fajlu:

```env
SPRING_PROFILES_ACTIVE=prod
ADMIN_PASSWORD=<jaka_lozinka>
JWT_SECRET=<jaki_jwt_secret>
POSTGRES_PASSWORD=<jaka_lozinka_za_bazu>
```

## Integracija sa ostalim servisima

Ovaj backend je dizajniran da radi sa:
- `euk-frontend` - React frontend aplikacija
- `euk-pisarnica-api` - Pisarnica API servis
- `nginx` - Reverse proxy

Za pokretanje celog sistema, koristite root `docker-compose.override.yml` fajl.
