# Frontend Global License Endpoints

## Pregled

Globalni licencni sistem koristi nove endpoint-e umesto starih individualnih license endpoint-a.

## 🔄 Promena Endpoint-a

| **Stari Endpoint** | **Novi Endpoint** | **Opis** |
|-------------------|------------------|----------|
| ❌ `/api/licenses/status?userId={id}` | ✅ `/api/global-license/status` | Status globalne licence |
| ❌ `/api/licenses/check/{userId}` | ✅ `/api/global-license/check` | Provera važenja |
| ❌ `/api/licenses/user/{userId}` | ✅ `/api/global-license/active` | Aktivna licenca |

## 📋 Novi API Endpoint-i

### 1. **Status Globalne Licence**
```javascript
GET /api/global-license/status
```

**Response:**
```json
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 45,
  "isExpiringSoon": false,
  "message": "License is valid"
}
```

### 2. **Provera Globalne Licence**
```javascript
GET /api/global-license/check
```

**Response:**
```json
{
  "hasValidLicense": true,
  "message": "Global license is valid"
}
```

### 3. **Aktivna Globalna Licenca**
```javascript
GET /api/global-license/active
```

**Response:**
```json
{
  "success": true,
  "license": {
    "id": 1,
    "licenseKey": "GLOBAL-LICENSE-2024",
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "isActive": true,
    "notificationSent": false
  }
}
```

## 🔧 Frontend Migracija

### **Angular Service Update:**

```typescript
// STARO (obrisati)
// this.http.get(`/api/licenses/status?userId=${userId}`)

// NOVO (koristiti)
this.http.get('/api/global-license/status')
```

### **React Hook Update:**

```javascript
// STARO (obrisati)
// const response = await axios.get(`/api/licenses/status?userId=${userId}`)

// NOVO (koristiti)
const response = await axios.get('/api/global-license/status')
```

### **Vue.js Update:**

```javascript
// STARO (obrisati)
// this.$http.get(`/api/licenses/status?userId=${userId}`)

// NOVO (koristiti)
this.$http.get('/api/global-license/status')
```

## 🚀 Testiranje

### **1. Testiraj nove endpoint-e:**
```bash
# Status globalne licence
curl -X GET "http://localhost:8080/api/global-license/status"

# Provera globalne licence
curl -X GET "http://localhost:8080/api/global-license/check"

# Aktivna globalna licenca
curl -X GET "http://localhost:8080/api/global-license/active"
```

### **2. Proveri da li stari endpoint-i ne rade:**
```bash
# Ovi endpoint-i NEĆE raditi
curl -X GET "http://localhost:8080/api/licenses/status?userId=1"
curl -X GET "http://localhost:8080/api/licenses/check/1"
```

## ⚠️ Važne Napomene

### **1. Nema više userId parametra:**
- **Staro**: `/api/licenses/status?userId=1`
- **Novo**: `/api/global-license/status` (bez parametara)

### **2. Jedna licenca za ceo softver:**
- **Staro**: Licenca po korisniku
- **Novo**: Jedna globalna licenca za ceo sistem

### **3. Obaveštenja administratorima:**
- **Staro**: Obaveštenja korisnicima
- **Novo**: Obaveštenja administratorima

## 📝 Checklist za Migraciju

- [ ] Zameni sve `/api/licenses/*` sa `/api/global-license/*`
- [ ] Ukloni `userId` parametre iz API poziva
- [ ] Ažuriraj error handling za nove response formate
- [ ] Testiraj sve nove endpoint-e
- [ ] Ažuriraj dokumentaciju
- [ ] Obavesti tim o promenama

## 🎯 Rezultat

Nakon migracije, frontend će koristiti globalni licencni sistem umesto individualnog sistema.
