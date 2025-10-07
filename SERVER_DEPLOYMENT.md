# Deployment na Server - Instrukcije

## Za server deployment (ne Render)

### 1. Docker Compose za server
Koristite `docker-compose.prod.yml` za produkciju na serveru:

```yaml
version: '3.8'

services:
  sirus-fullstack:
    build:
      context: .
      dockerfile: Dockerfile.multi
    container_name: sirus-fullstack
    ports:
      - "80:80"      # Frontend
      - "8080:8080"  # Backend API
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - EUK_ALLOWED_DOMAINS=https://your-domain.com,http://your-server-ip
      - EUK_RATE_LIMIT_ENABLED=true
      - EUK_RATE_LIMIT_MAX_REQUESTS=150
      - DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db
      - DATABASE_USERNAME=your-username
      - DATABASE_PASSWORD=your-password
      - JWT_SECRET=your-jwt-secret
      - JWT_EXPIRATION=86400000
      - ADMIN_PASSWORD=your-admin-password
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/test/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

### 2. Pokretanje na serveru
```bash
# Build i pokretanje
docker-compose -f docker-compose.prod.yml up --build -d

# Proverite status
docker-compose -f docker-compose.prod.yml ps

# Logovi
docker-compose -f docker-compose.prod.yml logs -f
```

### 3. Nginx konfiguracija za server
Kreirajte `/etc/nginx/sites-available/euk`:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 4. SSL sertifikat (Let's Encrypt)
```bash
# Instalirajte certbot
sudo apt install certbot python3-certbot-nginx

# Dobijte SSL sertifikat
sudo certbot --nginx -d your-domain.com
```

### 5. Firewall konfiguracija
```bash
# Otvorite potrebne portove
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 22
sudo ufw enable
```

### 6. Environment varijable na serveru
Kreirajte `.env` fajl na serveru:
```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=86400000
ADMIN_PASSWORD=your-admin-password
EUK_ALLOWED_DOMAINS=https://your-domain.com
EUK_RATE_LIMIT_ENABLED=true
EUK_RATE_LIMIT_MAX_REQUESTS=150
```

### 7. Backup i monitoring
```bash
# Backup script
#!/bin/bash
docker-compose -f docker-compose.prod.yml exec sirus-fullstack pg_dump -U your-username your-db > backup_$(date +%Y%m%d_%H%M%S).sql

# Monitoring
docker stats sirus-fullstack
```

## URL-ovi na serveru:
- **Frontend**: http://your-domain.com
- **Backend API**: http://your-domain.com/api
- **Health check**: http://your-domain.com/api/test/health
