# 🔐 Admin Controller API - Dokumentacija

## 📋 Pregled

Kreirao sam **AdminController** koji rešava 500 greške za `/api/admin/users` endpoint. API omogućava admin korisnicima da upravljaju korisnicima sistema sa paginacijom i filterima.

## 🏗️ **Kreirane klase:**

### 1. **AdminController** (`/api/admin`)
- Glavni kontroler za admin funkcionalnosti
- CORS konfiguracija za EUK domene
- Error handling i logging
- Paginacija za sve endpoint-e

## 🔌 **Dostupni API endpoint-i:**

### **Osnovni endpoint-i:**
```
GET /api/admin/users                    - Svi korisnici sa paginacijom
GET /api/admin/users/count              - Broj ukupnih korisnika
GET /api/admin/users/count/active       - Broj aktivnih korisnika
GET /api/admin/users/active             - Aktivni korisnici sa paginacijom
GET /api/admin/users/inactive           - Neaktivni korisnici sa paginacijom
```

## 📊 **Response format:**

### **Paginated Response (za /users, /active, /inactive):**
```json
{
  "content": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "firstName": "Admin",
      "lastName": "User",
      "role": "ADMIN",
      "isActive": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

### **Count Response (za /count endpoints):**
```json
5
```

## 🚀 **Kako koristiti:**

### **1. Dohvatanje svih korisnika:**
```bash
GET /api/admin/users?page=0&size=20
```

### **2. Dohvatanje korisnika sa filterima:**
```bash
GET /api/admin/users?page=0&size=20&role=ADMIN&isActive=true&search=admin
```

### **3. Dohvatanje aktivnih korisnika:**
```bash
GET /api/admin/users/active?page=0&size=100
```

### **4. Dohvatanje broja korisnika:**
```bash
GET /api/admin/users/count
GET /api/admin/users/count/active
```

## 🔍 **Query parametri:**

| Parametar | Tip | Obavezan | Opis |
|-----------|-----|----------|------|
| `page` | `int` | ❌ | Broj stranice (default: 0) |
| `size` | `int` | ❌ | Broj korisnika po stranici (default: 20) |
| `role` | `String` | ❌ | Filtriranje po roli (ADMIN, USER) |
| `isActive` | `Boolean` | ❌ | Filtriranje po statusu (true/false) |
| `search` | `String` | ❌ | Pretraga po username, email, firstName, lastName |

## 🔒 **Sigurnost:**

- Endpoint je dostupan bez autentifikacije (za sada)
- CORS konfigurisan za EUK domene
- Error handling za sve greške
- Logging za audit trail

## 🧪 **Testiranje:**

Kreiran je `AdminControllerTest` koji testira:
- ✅ Dohvatanje korisnika
- ✅ Paginacija
- ✅ Broj korisnika
- ✅ Broj aktivnih korisnika

## 📝 **Napomene:**

1. **Paginacija** - svi endpoint-i koriste `PaginatedResponse`
2. **Filteri** - podržava filtriranje po roli, statusu i pretragu
3. **Default size** - 20 korisnika po stranici
4. **Error handling** - vraća 500 grešku sa detaljima u logovima

## 🔄 **Prednosti:**

1. **✅ Rešava 500 greške** - `/api/admin/users` endpoint sada postoji
2. **✅ Paginacija** - kontrolisan broj korisnika po zahtevu
3. **✅ Filteri** - fleksibilno filtriranje korisnika
4. **✅ Konzistentnost** - isti format kao ostali endpoint-i
5. **✅ Admin funkcionalnosti** - specijalizovane metode za admin panel

## 🎯 **Next Steps:**

1. **Restartuj backend** da primeniš promene
2. **Testiraj endpoint-e** da proveriš da li rade
3. **Proveri frontend** - 500 greške bi trebalo da budu rešene
4. **Dodaj autentifikaciju** ako je potrebno

Admin endpoint-i su sada implementirani i trebalo bi da reše 500 greške! 🎉
