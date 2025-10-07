# 🚀 Frontend Integracija - Docker Setup

## 📁 Struktura Projekta
Vaš frontend kod treba da bude u ovom formatu:
```
euk-backend/
├── frontend/                    # 👈 VAŠ FRONTEND KOD IDE OVDE
│   ├── package.json
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   └── App.js/App.tsx
│   ├── public/
│   │   └── index.html
│   └── dist/                   # Build folder (generiše se automatski)
├── Dockerfile.multi            # Multi-stage Dockerfile
├── nginx.conf                  # Nginx konfiguracija
├── start.sh                    # Start script
└── docker-compose.prod.yml     # Production Docker Compose
```

## ✅ Koraci za Integraciju

### 1. 📂 Kreirajte `frontend/` folder
```bash
# U root direktorijumu euk-backend projekta
mkdir frontend
# Kopirajte vaš frontend kod u frontend/ folder
```

### 2. 📦 Prilagodite package.json
Vaš `frontend/package.json` treba da ima:
```json
{
  "name": "euk-frontend",
  "version": "1.0.0",
  "scripts": {
    "build": "npm run build:prod",
    "build:prod": "react-scripts build",
    "start": "react-scripts start",
    "test": "react-scripts test"
  },
  "dependencies": {
    "react": "^18.0.0",
    "react-dom": "^18.0.0",
    "react-scripts": "5.0.1"
  }
}
```

### 3. 🔗 Prilagodite API URL-ove
U vašem frontend kodu, koristite relativne putanje:
```javascript
// ❌ NE KORISTITE:
// const API_URL = 'http://localhost:8080/api';

// ✅ KORISTITE:
const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

// Primer API poziva:
const response = await fetch(`${API_BASE_URL}/users`);
```

### 4. 🏗️ Build Proces
Docker će automatski:
- ✅ Instalirati npm dependencies
- ✅ Build-ovati frontend (generiše `dist/` folder)
- ✅ Kopirati build u nginx folder
- ✅ Pokrenuti nginx + Spring Boot u istom kontejneru

## 🚀 Deployment na Server

### 1. Pokretanje na serveru:
```bash
# Build i pokretanje
docker-compose -f docker-compose.prod.yml up --build -d

# Proverite status
docker-compose -f docker-compose.prod.yml ps

# Pogledajte logove
docker-compose -f docker-compose.prod.yml logs -f
```

### 2. Environment varijable na serveru:
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

## 🧪 Lokalno Testiranje
```bash
# Build kontejner
docker build -f Dockerfile.multi -t sirus-full .

# Pokreni lokalno
docker run -p 80:80 -p 8080:8080 sirus-full
```

## 🌐 URL-ovi
- **Frontend**: `http://localhost:80` ili `http://localhost`
- **Backend API**: `http://localhost:8080/api`
- **Health Check**: `http://localhost:8080/api/test/health`

## 🔧 Kako Funkcioniše

### Nginx Konfiguracija:
```nginx
# Frontend se servira na portu 80
location / {
    try_files $uri $uri/ /index.html;
}

# API zahtevi se proxy-uju na Spring Boot
location /api/ {
    proxy_pass http://localhost:8080;
}
```

### Prednosti:
- ✅ **Jedan kontejner** = lakši deployment
- ✅ **Nginx proxy** = automatsko rutiranje
- ✅ **Production ready** = optimizovano za server
- ✅ **Lakše održavanje** = sve u jednom mestu

## 📋 Checklist za Frontend Tim

- [ ] Kreirati `frontend/` folder u root direktorijumu
- [ ] Kopirati frontend kod u `frontend/` folder
- [ ] Prilagoditi API URL-ove (koristiti `/api` umesto `http://localhost:8080/api`)
- [ ] Proveriti da `package.json` ima `build` script
- [ ] Testirati lokalno sa `docker build -f Dockerfile.multi -t sirus-full .`
- [ ] Deploy na server sa `docker-compose -f docker-compose.prod.yml up --build -d`

## 🆘 Troubleshooting

### Problem: Frontend se ne učitava
```bash
# Proverite da li je nginx pokrenut
docker exec -it sirus-fullstack nginx -t

# Restartujte kontejner
docker-compose -f docker-compose.prod.yml restart
```

### Problem: API pozivi ne rade
```bash
# Proverite da li Spring Boot radi
curl http://localhost:8080/api/test/health

# Proverite nginx proxy
docker exec -it sirus-fullstack cat /etc/nginx/nginx.conf
```

### Problem: Build ne prolazi
```bash
# Proverite da li frontend kod postoji
ls -la frontend/

# Proverite package.json
cat frontend/package.json
```

## 📞 Kontakt
Za pitanja ili probleme, kontaktirajte backend tim!
