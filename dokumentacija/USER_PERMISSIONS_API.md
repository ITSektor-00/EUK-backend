# 🔐 User Permissions API - Dokumentacija

## 📋 Pregled

Kreirao sam **User Permissions API** koji rešava 404 greške za `/api/user-permissions/{id}` endpoint. API omogućava dohvatanje dozvola za korisnike na osnovu njihove role.

## 🏗️ **Kreirane klase:**

### 1. **UserPermissionsController** (`/api/user-permissions`)
- Glavni kontroler za dozvole korisnika
- CORS konfiguracija za EUK domene
- Error handling i logging
- Role-based permission generation

## 🔌 **Dostupni API endpoint-i:**

### **Osnovni endpoint-i:**
```
GET /api/user-permissions/{id}    - Dozvole za specifičnog korisnika
GET /api/user-permissions/me      - Dozvole za trenutnog korisnika
```

## 🔐 **Sistem dozvola:**

### **Role-based permissions:**

#### **ADMIN korisnici:**
- ✅ Admin panel pristup
- ✅ User management
- ✅ EUK funkcionalnosti
- ✅ Reports i analytics
- ✅ Settings
- ✅ Delete operacije

#### **USER korisnici:**
- ❌ Admin panel pristup
- ❌ User management
- ❌ Reports i analytics
- ❌ Settings
- ✅ EUK funkcionalnosti
- ❌ Delete operacije

### **EUK-specific permissions:**
```
kategorije: true      - Pregled kategorija
predmeti: true        - Pregled predmeta
ugrozena-lica: true   - Pregled ugroženih lica
create: true          - Kreiranje novih zapisa
read: true            - Čitanje postojećih zapisa
update: true          - Ažuriranje zapisa
delete: ADMIN only    - Brisanje zapisa (samo admin)
```

## 📊 **Response format:**

### **Uspešan response:**
```json
{
  "userId": 1,
  "username": "admin",
  "role": "ADMIN",
  "isActive": true,
  "routes": {
    "admin": true,
    "users": true,
    "euk": true,
    "reports": true,
    "settings": true,
    "analytics": true
  },
  "euk": {
    "kategorije": true,
    "predmeti": true,
    "ugrozena-lica": true,
    "create": true,
    "read": true,
    "update": true,
    "delete": true
  },
  "canDelete": true,
  "canManageUsers": true,
  "canViewAnalytics": true
}
```

### **Error response:**
```json
{
  "error": "Internal server error",
  "message": "Failed to fetch user permissions"
}
```

## 🚀 **Kako koristiti:**

### **1. Dohvatanje dozvola za korisnika:**
```bash
GET /api/user-permissions/6
Authorization: Bearer <jwt-token>
```

### **2. Dohvatanje dozvola za trenutnog korisnika:**
```bash
GET /api/user-permissions/me
Authorization: Bearer <jwt-token>
```

## 🔒 **Sigurnost:**

- Endpoint je dostupan bez autentifikacije (za sada)
- Role-based permission generation
- Validacija korisničkog ID-a
- Error handling za nepostojeće korisnike

## 🧪 **Testiranje:**

Kreiran je `UserPermissionsControllerTest` koji testira:
- ✅ Admin korisnik permissions
- ✅ Regular korisnik permissions
- ✅ User not found scenario
- ✅ Current user permissions

## 📝 **Napomene:**

1. **Trenutno implementacija** je pojednostavljena i ne zahteva JWT validaciju
2. **Permissions se generišu** na osnovu role korisnika
3. **EUK funkcionalnosti** su dostupne svim korisnicima
4. **Admin funkcionalnosti** su ograničene na ADMIN role
5. **Delete operacije** su ograničene na ADMIN role

## 🔄 **Buduća poboljšanja:**

- JWT token validacija za `/me` endpoint
- Database-driven permissions
- Granular permission control
- Permission caching
- Audit logging za permission changes
