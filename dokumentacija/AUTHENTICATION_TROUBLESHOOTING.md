# 🔐 Autentifikacija - Rešavanje Problema

## 🚨 Česti Problemi sa Autentifikacijom

### 1. **404 Not Found Greška**
**Problem**: Frontend pokušava da pristupi endpoint-u koji ne postoji ili nije dostupan.

**Uzroci**:
- Backend aplikacija nije pokrenuta
- Pogrešan port (frontend koristi 3000, backend radi na 8080)
- Pogrešan URL endpoint-a

**Rešenje**:
```bash
# 1. Proverite da li je backend pokrenut
docker ps | grep sirus-backend

# 2. Pokrenite backend ako nije pokrenut
cd C:\Users\Luka Rakic\Desktop\EUK\euk-backend
bashSkripte\start-backend.bat

# 3. Proverite da li backend radi
curl http://localhost:8080/api/auth/test
```

### 2. **CORS Greške**
**Problem**: Browser blokira zahteve zbog CORS policy-ja.

**Simptomi**:
- `Access to fetch at 'http://localhost:8080/api/auth/signin' from origin 'http://localhost:3000' has been blocked by CORS policy`
- `No 'Access-Control-Allow-Origin' header is present on the requested resource`

**Rešenje**:
- ✅ **Development**: Kreiran `DevelopmentSecurityConfig` sa `allowedOriginPatterns("*")`
- ✅ **Production**: Konfigurisan CORS sa dozvoljenim domenima
- ✅ **PDF Endpoint-i**: Dodati u security config kao permitAll

### 3. **JWT Token Problemi**
**Problem**: Token nije validan ili je istekao.

**Simptomi**:
- `401 Unauthorized`
- `Invalid token`
- `Token expired`

**Rešenje**:
```javascript
// Frontend - proverite token storage
const token = localStorage.getItem('token');
if (!token) {
    // Redirect na login
    window.location.href = '/login';
}

// Proverite da li je token istekao
const tokenData = JSON.parse(atob(token.split('.')[1]));
const currentTime = Date.now() / 1000;
if (tokenData.exp < currentTime) {
    // Token je istekao, redirect na login
    localStorage.removeItem('token');
    window.location.href = '/login';
}
```

### 4. **Security Configuration Problemi**
**Problem**: Endpoint-i nisu dozvoljeni u security config-u.

**Rešenje**:
- ✅ **Development**: `DevelopmentSecurityConfig` dozvoljava sve potrebne endpoint-e
- ✅ **Production**: `ProductionSecurityConfig` konfigurisan za produkciju
- ✅ **PDF Endpoint-i**: Dodati kao `permitAll()`

## 🔧 Konfiguracija za Različite Environment-e

### Development (localhost)
```properties
# application.properties
spring.profiles.active=dev
server.port=8080
```

**Security Config**: `DevelopmentSecurityConfig`
- CORS: Dozvoli sve origin-e
- PDF endpoint-i: permitAll
- Auth endpoint-i: permitAll

### Production (Render)
```properties
# Environment variables
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

**Security Config**: `ProductionSecurityConfig`
- CORS: Dozvoljeni domeni iz `EUK_ALLOWED_DOMAINS`
- PDF endpoint-i: permitAll
- Auth endpoint-i: permitAll

## 📋 Endpoint-i za Autentifikaciju

### 1. **POST** `/api/auth/signup`
**Registracija novog korisnika**

```json
{
  "username": "korisnik123",
  "email": "korisnik@example.com",
  "password": "lozinka123",
  "firstName": "Marko",
  "lastName": "Petrović"
}
```

**Response**:
```json
{
  "message": "Uspešno ste se registrovali. Čeka se odobrenje administratora.",
  "user": {
    "id": 1,
    "username": "korisnik123",
    "email": "korisnik@example.com",
    "firstName": "Marko",
    "lastName": "Petrović",
    "role": "USER",
    "isActive": false
  }
}
```

### 2. **POST** `/api/auth/signin`
**Prijava korisnika**

```json
{
  "usernameOrEmail": "korisnik123",
  "password": "lozinka123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "korisnik123",
    "email": "korisnik@example.com",
    "firstName": "Marko",
    "lastName": "Petrović",
    "role": "USER",
    "isActive": true
  }
}
```

### 3. **GET** `/api/auth/me`
**Dohvatanje trenutnog korisnika**

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**:
```json
{
  "id": 1,
  "username": "korisnik123",
  "email": "korisnik@example.com",
  "firstName": "Marko",
  "lastName": "Petrović",
  "role": "USER",
  "isActive": true
}
```

## 🧪 Testiranje Autentifikacije

### 1. **Test Backend Dostupnosti**
```bash
curl http://localhost:8080/api/auth/test
# Očekivani odgovor: "Auth API is working!"
```

### 2. **Test CORS-a**
```bash
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:8080/api/auth/signin
```

### 3. **Test Registracije**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "email": "test@example.com",
       "password": "testpass123",
       "firstName": "Test",
       "lastName": "User"
     }'
```

### 4. **Test Prijave**
```bash
curl -X POST http://localhost:8080/api/auth/signin \
     -H "Content-Type: application/json" \
     -d '{
       "usernameOrEmail": "testuser",
       "password": "testpass123"
     }'
```

## 🔍 Debugging Autentifikacije

### 1. **Proverite Backend Logove**
```bash
# Docker logovi
docker logs sirus-backend

# Ili ako koristite Maven
mvn spring-boot:run
```

### 2. **Proverite Network Tab u Browser-u**
- Otvorite Developer Tools (F12)
- Idite na Network tab
- Pokušajte da se prijavite
- Proverite da li se šalju zahtevi
- Proverite response status i headers

### 3. **Proverite CORS Headers**
```bash
curl -I -H "Origin: http://localhost:3000" http://localhost:8080/api/auth/signin
```

Očekivani headers:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, X-Requested-With, Accept
```

## 🚀 Pokretanje Backend-a

### Lokalno (Development)
```bash
# Opcija 1: Docker
cd C:\Users\Luka Rakic\Desktop\EUK\euk-backend
bashSkripte\start-backend.bat

# Opcija 2: Maven
mvn spring-boot:run

# Opcija 3: JAR fajl
java -jar target/sirus-backend-0.0.1-SNAPSHOT.jar
```

### Production (Render)
- Automatski se deploy-uje kada se push-uje u git
- Environment varijable se postavljaju u Render dashboard-u

## 📞 Kontakt za Podršku

Ako i dalje imate probleme sa autentifikacijom:

1. **Proverite logove** - `docker logs sirus-backend`
2. **Testirajte endpoint-e** - koristite curl komande iznad
3. **Proverite CORS** - proverite da li su origin-i dozvoljeni
4. **Proverite token** - da li je validan i nije istekao

## 🔧 Dodatne Konfiguracije

### Environment Variables
```bash
# JWT konfiguracija
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# CORS konfiguracija
EUK_ALLOWED_DOMAINS=http://localhost:3000,https://euk.vercel.app

# Rate limiting
EUK_RATE_LIMIT_ENABLED=true
EUK_RATE_LIMIT_MAX_REQUESTS=150
```
