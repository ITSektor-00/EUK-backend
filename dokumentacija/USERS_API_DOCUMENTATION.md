# 🚀 Users API Documentation

## 📋 Pregled

**Users API** pruža kompletnu funkcionalnost za dohvatanje podataka o korisnicima iz `public.users` sheme. API je dizajniran da bude siguran i ne izlaže osetljive podatke kao što su lozinke.

## 🔗 Base URL

```
http://localhost:8080/api/users
```

## 🔐 Sigurnost

**Svi endpoint-i su javno dostupni** (ne zahtevaju autentifikaciju) u oba profila:
- **Development**: Sve je dozvoljeno
- **Production**: `/api/users/**` je dozvoljeno

## 📊 API Endpoint-i

### 1. **GET /api/users** - Dohvatanje svih korisnika sa paginacijom

**Opis:** Dohvata sve korisnike sa paginacijom i opcionim filterima

**Query Parametri:**
- `page` (opciono, default: 0) - Broj stranice (0-based)
- `size` (opciono, default: 10) - Broj korisnika po stranici
- `role` (opciono) - Filtriranje po roli (USER, ADMIN, itd.)
- `isActive` (opciono) - Filtriranje po statusu aktivnosti (true/false)
- `search` (opciono) - Pretraga po username, email, firstName ili lastName

**Primeri:**
```bash
# Svi korisnici, prva stranica
GET /api/users

# 20 korisnika po stranici, druga stranica
GET /api/users?page=1&size=20

# Samo aktivni korisnici
GET /api/users?isActive=true

# Korisnici sa ADMIN rolom
GET /api/users?role=ADMIN

# Pretraga korisnika
GET /api/users?search=john

# Kombinacija filtera
GET /api/users?page=0&size=5&role=USER&isActive=true&search=john
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER",
      "isActive": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

---

### 2. **GET /api/users/{id}** - Dohvatanje korisnika po ID-u

**Opis:** Dohvata korisnika po njegovom ID-u

**Path Parametri:**
- `id` (obavezno) - ID korisnika (Long)

**Primer:**
```bash
GET /api/users/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Response (404 Not Found):**
```json
// Ako korisnik ne postoji
```

---

### 3. **GET /api/users/username/{username}** - Dohvatanje korisnika po username-u

**Opis:** Dohvata korisnika po njegovom username-u

**Path Parametri:**
- `username` (obavezno) - Username korisnika

**Primer:**
```bash
GET /api/users/username/john_doe
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 4. **GET /api/users/email/{email}** - Dohvatanje korisnika po email-u

**Opis:** Dohvata korisnika po njegovom email-u

**Path Parametri:**
- `email` (obavezno) - Email adresa korisnika

**Primer:**
```bash
GET /api/users/email/john@example.com
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 5. **GET /api/users/role/{role}** - Dohvatanje korisnika po roli

**Opis:** Dohvata sve korisnike sa određenom rolom

**Path Parametri:**
- `role` (obavezno) - Rola korisnika (USER, ADMIN, itd.)

**Primer:**
```bash
GET /api/users/role/ADMIN
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "admin_user",
    "email": "admin@example.com",
    "firstName": "Admin",
    "lastName": "User",
    "role": "ADMIN",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 6. **GET /api/users/active** - Dohvatanje aktivnih korisnika

**Opis:** Dohvata sve aktivne korisnike

**Primer:**
```bash
GET /api/users/active
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 7. **GET /api/users/inactive** - Dohvatanje neaktivnih korisnika

**Opis:** Dohvata sve neaktivne korisnike

**Primer:**
```bash
GET /api/users/inactive
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "username": "inactive_user",
    "email": "inactive@example.com",
    "firstName": "Inactive",
    "lastName": "User",
    "role": "USER",
    "isActive": false,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 8. **GET /api/users/count** - Broj ukupnih korisnika

**Opis:** Vraća ukupan broj korisnika u sistemu

**Primer:**
```bash
GET /api/users/count
```

**Response (200 OK):**
```json
42
```

---

### 9. **GET /api/users/count/active** - Broj aktivnih korisnika

**Opis:** Vraća broj aktivnih korisnika u sistemu

**Primer:**
```bash
GET /api/users/count/active
```

**Response (200 OK):**
```json
38
```

---

## 🔍 **Filteri i Pretraga**

### **Pretraga po više polja**
Endpoint `/api/users` sa `search` parametrom pretražuje:
- Username
- Email
- First Name
- Last Name

Pretraga je **case-insensitive** i koristi **LIKE** operator.

### **Filtriranje po roli**
Dostupne role (možete dodati više u `User` entity):
- `USER` - Običan korisnik
- `ADMIN` - Administrator
- `MODERATOR` - Moderator

### **Filtriranje po statusu**
- `true` - Aktivni korisnici
- `false` - Neaktivni korisnici

---

## 📊 **Paginacija**

Svi endpoint-i koji vraćaju listu korisnika podržavaju paginaciju:

- **page**: Broj stranice (0-based)
- **size**: Broj elemenata po stranici
- **totalElements**: Ukupan broj korisnika
- **totalPages**: Ukupan broj stranica
- **numberOfElements**: Broj elemenata na trenutnoj stranici

---

## 🚨 **Error Handling**

### **400 Bad Request**
- Neispravni parametri
- Neispravni format podataka

### **404 Not Found**
- Korisnik sa navedenim ID/username/email ne postoji

### **500 Internal Server Error**
- Greška na serveru
- Database greška

---

## 🔒 **Sigurnosne napomene**

1. **Lozinka se nikad ne vraća** - `passwordHash` polje je uvek isključeno iz response-a
2. **Javni pristup** - Svi endpoint-i su javno dostupni
3. **Rate limiting** - Podliježe postojećim rate limiting pravilima
4. **CORS** - Podržava sve konfigurisane domene

---

## 📱 **Primeri korišćenja**

### **JavaScript/Fetch**
```javascript
// Dohvati sve korisnike
fetch('/api/users')
  .then(response => response.json())
  .then(data => console.log(data));

// Dohvati korisnika po ID-u
fetch('/api/users/1')
  .then(response => response.json())
  .then(user => console.log(user));

// Pretraži korisnike
fetch('/api/users?search=john&role=USER')
  .then(response => response.json())
  .then(data => console.log(data));
```

### **cURL**
```bash
# Svi korisnici
curl -X GET "http://localhost:8080/api/users"

# Korisnik po ID-u
curl -X GET "http://localhost:8080/api/users/1"

# Pretraga
curl -X GET "http://localhost:8080/api/users?search=john&isActive=true"
```

---

## 🏗️ **Arhitektura**

### **Controller Layer**
- `UserController` - HTTP endpoint-i
- CORS konfiguracija
- Error handling

### **Service Layer**
- `UserService` - Business logic
- DTO konverzija
- Filtering i paginacija

### **Repository Layer**
- `UserRepository` - Database operacije
- Custom query metode
- Spring Data JPA

### **Entity Layer**
- `User` - JPA entity
- `UserDto` - Data Transfer Object

---

## 📈 **Performance**

- **Paginacija** - Sprečava učitavanje velikih dataset-ova
- **Indexi** - Database indexi na `username`, `email`, `role`, `is_active`
- **Lazy loading** - Koristi se gde je potrebno
- **Caching** - Može se dodati Spring Cache ako je potrebno

---

## 🔄 **Future Enhancements**

1. **Sorting** - Dodavanje sortiranja po različitim poljima
2. **Advanced filtering** - Više filtera i kombinacija
3. **Bulk operations** - Operacije nad više korisnika
4. **Export** - Export korisnika u CSV/Excel
5. **Audit logging** - Logovanje pristupa korisničkim podacima
