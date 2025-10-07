# Instrukcije za Frontend Tim - Docker Integracija

## Struktura foldera
Vaš frontend kod treba da bude u ovom formatu:
```
euk-backend/
├── frontend/                    # VAŠ FRONTEND KOD IDE OVDE
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
└── docker-compose.yml          # Docker Compose
```

## Šta treba da uradite:

### 1. Kreirajte `frontend/` folder u root direktorijumu
```bash
mkdir frontend
# Kopirajte vaš frontend kod u frontend/ folder
```

### 2. Prilagodite package.json build script
Vaš `frontend/package.json` treba da ima:
```json
{
  "scripts": {
    "build": "npm run build:prod",
    "build:prod": "react-scripts build"
  }
}
```

### 3. Prilagodite API URL-ove
U vašem frontend kodu, koristite relativne putanje:
```javascript
// Umesto: http://localhost:8080/api
// Koristite: /api
const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';
```

### 4. Build proces
Docker će automatski:
- Instalirati npm dependencies
- Build-ovati frontend
- Kopirati build u nginx folder
- Pokrenuti nginx + Spring Boot

## Deployment na Server

### 1. Koristite docker-compose.prod.yml:
```bash
# Build i pokretanje na serveru
docker-compose -f docker-compose.prod.yml up --build -d
```

### 2. Environment varijable na serveru:
Kreirajte `.env` fajl na serveru sa svim potrebnim varijablama:
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
JWT_SECRET=your-jwt-secret
EUK_ALLOWED_DOMAINS=https://your-domain.com
```

## Pokretanje lokalno:
```bash
# Build
docker build -f Dockerfile.multi -t sirus-full .

# Run
docker run -p 80:80 -p 8080:8080 sirus-full
```

## URL-ovi:
- Frontend: http://localhost:80
- Backend API: http://localhost:8080/api
- Frontend će automatski proxy-ovati /api zahteve na backend

## Važne napomene:
1. **Nginx će servirati frontend** na portu 80
2. **Spring Boot će raditi** na portu 8080
3. **Nginx će proxy-ovati** /api zahteve na Spring Boot
4. **Jedan kontejner** = lakši deployment
5. **Sve environment varijable** se postavljaju na Render-u
