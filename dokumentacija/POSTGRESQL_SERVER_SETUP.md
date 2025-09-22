# 🐘 PostgreSQL Server Setup - EUK Backend

## 📋 Pregled
Ovaj vodič objašnjava kako da postavite PostgreSQL bazu podataka direktno na server firme umesto korišćenja Supabase.

---

## 🔧 Instalacija PostgreSQL na Server

### Ubuntu/Debian
```bash
# Update sistema
sudo apt update

# Instalacija PostgreSQL
sudo apt install postgresql postgresql-contrib

# Pokretanje servisa
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Proveri status
sudo systemctl status postgresql
```

### CentOS/RHEL
```bash
# Instalacija PostgreSQL
sudo yum install postgresql-server postgresql-contrib

# Inicijalizacija baze
sudo postgresql-setup initdb

# Pokretanje servisa
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### Windows Server
```powershell
# Download PostgreSQL installer sa https://www.postgresql.org/download/windows/
# Instaliraj sa default opcijama
# Port: 5432
# Superuser password: postgres (ili custom)
```

---

## ⚙️ Konfiguracija PostgreSQL

### 1. Kreiranje Baze i Korisnika
```sql
-- Povezivanje kao postgres superuser
sudo -u postgres psql

-- Kreiranje baze podataka
CREATE DATABASE euk_database;

-- Kreiranje korisnika
CREATE USER euk_user WITH PASSWORD 'your_secure_password_here';

-- Dodela privilegija
GRANT ALL PRIVILEGES ON DATABASE euk_database TO euk_user;

-- Povezivanje na bazu i dodela schema privilegija
\c euk_database
GRANT ALL ON SCHEMA public TO euk_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO euk_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO euk_user;

-- Izađi iz psql
\q
```

### 2. Konfiguracija pg_hba.conf
```bash
# Lokacija fajla
sudo nano /etc/postgresql/15/main/pg_hba.conf

# Dodaj liniju za lokalne konekcije
local   euk_database    euk_user    md5
host    euk_database    euk_user    127.0.0.1/32    md5
host    euk_database    euk_user    ::1/128         md5

# Za remote konekcije (opciono)
host    euk_database    euk_user    0.0.0.0/0       md5
```

### 3. Konfiguracija postgresql.conf
```bash
# Lokacija fajla
sudo nano /etc/postgresql/15/main/postgresql.conf

# Omogući remote konekcije
listen_addresses = '*'
port = 5432

# Performance tuning
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
```

### 4. Restart PostgreSQL
```bash
sudo systemctl restart postgresql
```

---

## 🐳 Docker Compose sa PostgreSQL

Kreiraj `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: euk-postgres
    environment:
      POSTGRES_DB: euk_database
      POSTGRES_USER: euk_user
      POSTGRES_PASSWORD: your_secure_password_here
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./src/main/resources/prod/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - ./src/main/resources/prod/routes_schema.sql:/docker-entrypoint-initdb.d/02-routes.sql
    ports:
      - "5432:5432"
    restart: unless-stopped
    networks:
      - euk-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U euk_user -d euk_database"]
      interval: 30s
      timeout: 10s
      retries: 3

  sirus-backend:
    image: sirus-backend:latest
    container_name: sirus-backend
    ports:
      - "8080:8080"
    env_file:
      - .env
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://postgres:5432/euk_database
      - DATABASE_USERNAME=euk_user
      - DATABASE_PASSWORD=your_secure_password_here
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - euk-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/test/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
    driver: local

networks:
  euk-network:
    driver: bridge
```

---

## 🔧 Ažuriranje .env Fajla

Zameni Supabase konfiguraciju sa lokalnom:

```bash
# Database Configuration - LOCAL POSTGRESQL
DATABASE_URL=jdbc:postgresql://localhost:5432/euk_database
DATABASE_USERNAME=euk_user
DATABASE_PASSWORD=your_secure_password_here

# Application Configuration
SPRING_PROFILES_ACTIVE=prod

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION=86400000

# Admin Configuration
ADMIN_PASSWORD=your-admin-password

# Server Configuration
PORT=8080

# EUK Domain Configuration
EUK_ALLOWED_DOMAINS=https://your-frontend-domain.com,https://your-other-domain.com
EUK_RATE_LIMIT_ENABLED=true
EUK_RATE_LIMIT_MAX_REQUESTS=150

# Security Headers
SECURITY_HEADERS_ENABLED=true
```

---

## 🚀 Deployment Opcije

### Opcija 1: Docker Compose (Preporučeno)
```bash
# Pokreni ceo stack
docker-compose -f docker-compose.prod.yml up -d

# Pregled statusa
docker-compose -f docker-compose.prod.yml ps

# Pregled logova
docker-compose -f docker-compose.prod.yml logs -f
```

### Opcija 2: Samostalni PostgreSQL + Docker App
```bash
# 1. Pokreni PostgreSQL na host sistemu
sudo systemctl start postgresql

# 2. Pokreni samo aplikaciju u Docker-u
docker run -d \
    --name sirus-backend \
    -p 8080:8080 \
    --env-file .env \
    --restart unless-stopped \
    sirus-backend:latest
```

### Opcija 3: Sve na Host Sistemu
```bash
# 1. Pokreni PostgreSQL
sudo systemctl start postgresql

# 2. Build aplikaciju
./mvnw clean package -DskipTests

# 3. Pokreni JAR
java -jar target/sirus-backend-0.0.1-SNAPSHOT.jar
```

---

## 🔍 Testiranje Konekcije

### 1. Test PostgreSQL Konekcije
```bash
# Lokalna konekcija
psql -h localhost -U euk_user -d euk_database

# Remote konekcija (iz drugog računara)
psql -h your-server-ip -U euk_user -d euk_database
```

### 2. Test Aplikacije
```bash
# Health check
curl http://localhost:8080/api/test/health

# Pregled logova
docker logs sirus-backend
```

---

## 🔒 Sigurnosne Napomene

### 1. Firewall Konfiguracija
```bash
# Dozvoli samo lokalne konekcije
sudo ufw allow from 127.0.0.1 to any port 5432
sudo ufw allow from 10.0.0.0/8 to any port 5432  # lokalna mreža

# Za remote pristup (oprezno!)
sudo ufw allow from trusted-ip to any port 5432
```

### 2. SSL Konekcije
```bash
# Generiši SSL sertifikate
sudo -u postgres openssl req -new -x509 -days 365 -nodes -text -out server.crt -keyout server.key

# Konfiguracija u postgresql.conf
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'
```

### 3. Backup Strategija
```bash
# Dnevni backup
#!/bin/bash
BACKUP_DIR="/backup/postgresql"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U euk_user euk_database > $BACKUP_DIR/euk_backup_$DATE.sql

# Automatizacija sa cron
0 2 * * * /path/to/backup_script.sh
```

---

## 📊 Performance Tuning

### PostgreSQL Konfiguracija
```bash
# /etc/postgresql/15/main/postgresql.conf

# Memory
shared_buffers = 256MB                    # 25% of RAM
effective_cache_size = 1GB                # 75% of RAM
work_mem = 4MB
maintenance_work_mem = 64MB

# Checkpoints
checkpoint_completion_target = 0.9
wal_buffers = 16MB
checkpoint_segments = 32

# Query Planning
default_statistics_target = 100
random_page_cost = 1.1

# Connections
max_connections = 100
```

### Monitoring
```bash
# Instalacija pgAdmin (opciono)
sudo apt install pgadmin4

# Ili koristi psql za monitoring
psql -U euk_user -d euk_database -c "SELECT * FROM pg_stat_activity;"
```

---

## 🚨 Troubleshooting

### Česte Greške

#### 1. Connection Refused
```bash
# Proveri da li PostgreSQL radi
sudo systemctl status postgresql

# Proveri port
sudo netstat -tulpn | grep 5432

# Proveri pg_hba.conf
sudo cat /etc/postgresql/15/main/pg_hba.conf
```

#### 2. Authentication Failed
```bash
# Proveri korisnika
sudo -u postgres psql -c "\du"

# Resetuj lozinku
sudo -u postgres psql -c "ALTER USER euk_user PASSWORD 'new_password';"
```

#### 3. Database Does Not Exist
```bash
# Kreiraj bazu
sudo -u postgres createdb euk_database

# Ili kroz psql
sudo -u postgres psql -c "CREATE DATABASE euk_database;"
```

---

## ✅ Checklist za PostgreSQL Setup

- [ ] PostgreSQL instaliran
- [ ] Baza `euk_database` kreirana
- [ ] Korisnik `euk_user` kreiran sa privilegijama
- [ ] pg_hba.conf konfigurisan
- [ ] postgresql.conf konfigurisan
- [ ] Firewall konfigurisan
- [ ] .env fajl ažuriran
- [ ] Konekcija testirana
- [ ] Backup strategija implementirana
- [ ] Monitoring konfigurisan

---

**Prednosti lokalne PostgreSQL baze:**
- ✅ Potpuna kontrola nad podacima
- ✅ Bolja sigurnost (podaci ostaju u firmi)
- ✅ Niži troškovi
- ✅ Brži pristup
- ✅ Mogućnost custom konfiguracije
