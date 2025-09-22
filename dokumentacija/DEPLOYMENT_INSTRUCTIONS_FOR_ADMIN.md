# 🚀 EUK Backend - Instrukcije za System Administratora

## 📋 Pregled
EUK Backend je Spring Boot aplikacija koja se pokreće u Docker kontejneru. Aplikacija koristi PostgreSQL bazu podataka i pruža REST API sa WebSocket podrškom.

---

## 🔧 Preduslovi na Serveru

### 1. Docker Instalacija
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl enable docker
sudo systemctl start docker

# CentOS/RHEL
sudo yum install docker docker-compose
sudo systemctl enable docker
sudo systemctl start docker

# Dodaj korisnika u docker grupu
sudo usermod -aG docker $USER
```

### 2. Mrežni Zahtevi
- **Port 8080** - Glavna aplikacija
- **Port 5432** - PostgreSQL (ako se koristi lokalna baza)
- **Outbound HTTPS** - Za pristup Supabase bazi podataka

### 3. Firewall Konfiguracija
```bash
# UFW (Ubuntu)
sudo ufw allow 8080/tcp
sudo ufw allow 5432/tcp  # samo ako koristiš lokalnu bazu

# Firewalld (CentOS/RHEL)
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=5432/tcp
sudo firewall-cmd --reload
```

---

## 📁 Struktura Fajlova

```
/euk-backend/
├── Dockerfile                 # Docker konfiguracija
├── docker-compose.yml         # Docker Compose konfiguracija
├── .env                       # Environment varijable (KREIRATI)
├── deploy.sh                  # Linux deployment skripta
├── scripts/
│   ├── deploy-windows.bat     # Windows deployment skripta
│   └── deploy-backend.bat     # Backend deployment skripta
└── src/                       # Source kod aplikacije
```

---

## ⚙️ Konfiguracija Environment Varijabli

Kreirati `.env` fajl u root direktorijumu sa sledećim sadržajem:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://your-database-host:5432/euk_database
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_secure_password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION=86400000

# Admin Configuration
ADMIN_PASSWORD=your-admin-password

# Server Configuration
PORT=8080
SPRING_PROFILES_ACTIVE=prod

# EUK Domain Configuration
EUK_ALLOWED_DOMAINS=https://your-frontend-domain.com,https://your-other-domain.com
EUK_RATE_LIMIT_ENABLED=true
EUK_RATE_LIMIT_MAX_REQUESTS=150

# Security Headers
SECURITY_HEADERS_ENABLED=true
```

### 🔐 Sigurnosne Napomene:
- **JWT_SECRET** - Koristiti jak, jedinstven ključ (min. 32 karaktera)
- **ADMIN_PASSWORD** - Koristiti jaku lozinku
- **DATABASE_PASSWORD** - Sigurna lozinka za bazu
- **EUK_ALLOWED_DOMAINS** - Dodati sve domene koje će pristupati API-ju

---

## 🐳 Docker Deployment

### Opcija 1: Jednostavan Docker Run
```bash
# Build Docker image
docker build -t sirus-backend:latest .

# Pokreni aplikaciju
docker run -d \
    --name sirus-backend \
    -p 8080:8080 \
    --env-file .env \
    --restart unless-stopped \
    sirus-backend:latest
```

### Opcija 2: Docker Compose (Preporučeno)
```bash
# Pokreni sa Docker Compose
docker-compose up -d

# Za production
docker-compose -f docker-compose.yml up -d
```

### Opcija 3: Deployment Skripta
```bash
# Linux
chmod +x deploy.sh
./deploy.sh

# Windows
scripts\deploy-windows.bat
```

---

## 🔍 Monitoring i Upravljanje

### Pregled Statusa
```bash
# Pregled pokrenutih kontejnera
docker ps

# Pregled logova
docker logs sirus-backend

# Pregled logova u realnom vremenu
docker logs sirus-backend -f
```

### Upravljanje Aplikacijom
```bash
# Zaustavljanje
docker stop sirus-backend

# Pokretanje
docker start sirus-backend

# Restart
docker restart sirus-backend

# Brisanje
docker rm sirus-backend
```

### Health Check
```bash
# Testiranje aplikacije
curl http://localhost:8080/api/test/health

# Očekivani odgovor:
# {"service":"SIRUS Backend","version":"1.0.0","status":"OK","timestamp":"..."}
```

---

## 🌐 Nginx Reverse Proxy (Opciono)

### Nginx Konfiguracija
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

### SSL sa Let's Encrypt
```bash
# Instalacija Certbot
sudo apt install certbot python3-certbot-nginx

# Generisanje SSL sertifikata
sudo certbot --nginx -d your-domain.com
```

---

## 📊 Baza Podataka

### PostgreSQL Setup (ako se koristi lokalna baza)
```sql
-- Kreiranje baze
CREATE DATABASE euk_database;

-- Kreiranje korisnika
CREATE USER euk_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE euk_database TO euk_user;

-- Pokretanje schema.sql
\i /path/to/schema.sql
```

### Supabase (Preporučeno)
- Koristiti postojeću Supabase instancu
- Konfigurisati DATABASE_URL u .env fajlu
- Dodati server IP u Supabase allowed hosts

---

## 🔄 Backup i Maintenance

### Backup Baze Podataka
```bash
# PostgreSQL backup
pg_dump -h your-db-host -U your-username euk_database > backup_$(date +%Y%m%d).sql

# Restore
psql -h your-db-host -U your-username euk_database < backup_20240101.sql
```

### Log Rotation
```bash
# Docker log rotation
sudo nano /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

### Systemd Service (Opciono)
```ini
[Unit]
Description=EUK Backend Docker Container
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
ExecStart=/usr/bin/docker start sirus-backend
ExecStop=/usr/bin/docker stop sirus-backend
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

---

## 🚨 Troubleshooting

### Česte Greške

#### 1. Port Already in Use
```bash
# Proveri koji proces koristi port 8080
sudo netstat -tulpn | grep :8080
sudo lsof -i :8080

# Zaustavi postojeći proces ili promeni port u .env
```

#### 2. Database Connection Failed
```bash
# Proveri konekciju sa bazom
docker exec -it sirus-backend ping your-db-host
docker exec -it sirus-backend nslookup your-db-host

# Proveri credentials u .env fajlu
```

#### 3. Container Won't Start
```bash
# Pregled logova
docker logs sirus-backend

# Proveri Docker daemon
sudo systemctl status docker

# Proveri disk space
df -h
```

#### 4. Memory Issues
```bash
# Proveri RAM usage
free -h
docker stats sirus-backend

# Dodaj swap ako je potrebno
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

---

## 📈 Performance Tuning

### Docker Resource Limits
```bash
# Pokreni sa resource limitima
docker run -d \
    --name sirus-backend \
    --memory="2g" \
    --cpus="2" \
    -p 8080:8080 \
    --env-file .env \
    sirus-backend:latest
```

### JVM Tuning
Dodati u .env fajl:
```bash
JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
```

---

## 📞 Kontakt i Podrška

### Log Fajlovi
- **Application Logs:** `docker logs sirus-backend`
- **System Logs:** `/var/log/syslog`
- **Docker Logs:** `/var/lib/docker/containers/`

### Monitoring Endpoints
- **Health Check:** `http://your-server:8080/api/test/health`
- **Actuator Info:** `http://your-server:8080/actuator/info`
- **Actuator Health:** `http://your-server:8080/actuator/health`

### Backup Kontakt
- **Developer:** [Developer Email]
- **System Admin:** [Admin Email]
- **Emergency:** [Emergency Contact]

---

## ✅ Deployment Checklist

- [ ] Docker instaliran i konfigurisan
- [ ] Firewall konfigurisan (port 8080)
- [ ] .env fajl kreiran sa ispravnim vrednostima
- [ ] Baza podataka dostupna i konfigurisana
- [ ] Docker image build-ovan
- [ ] Container pokrenut i radi
- [ ] Health check endpoint odgovara
- [ ] Logovi se čitaju bez grešaka
- [ ] Nginx konfigurisan (opciono)
- [ ] SSL sertifikat instaliran (opciono)
- [ ] Backup strategija implementirana
- [ ] Monitoring konfigurisan

---

**Napomena:** Ova aplikacija koristi embedded Tomcat server unutar Spring Boot-a, tako da ne zahteva spoljašnji Tomcat server.
