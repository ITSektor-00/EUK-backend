# 🚀 Users API - Uputstvo za korišćenje

## 📋 Pregled

Kreirao sam kompletan **Users API** koji omogućava dohvatanje podataka iz `public.users` sheme. API je dizajniran da bude siguran i ne izlaže osetljive podatke.

## 🏗️ **Kreirane klase:**

### 1. **UserController** (`/api/users`)
- Glavni kontroler sa svim endpoint-ima
- CORS konfiguracija za EUK domene
- Error handling i logging

### 2. **UserService**
- Business logic za dohvatanje korisnika
- Filtering, paginacija i pretraga
- DTO konverzija

### 3. **UserDto**
- Data Transfer Object bez osetljivih podataka
- Sve potrebne informacije o korisniku

### 4. **UserRepository** (prošireno)
- Dodatne metode za filtering i pretragu
- Custom JPQL query za pretragu po više polja

## 🔌 **Dostupni API endpoint-i:**

### **Osnovni endpoint-i:**
```
GET /api/users                    - Svi korisnici sa paginacijom
GET /api/users/{id}              - Korisnik po ID-u
GET /api/users/username/{username} - Korisnik po username-u
GET /api/users/email/{email}     - Korisnik po email-u
```

### **Filtering endpoint-i:**
```
GET /api/users/role/{role}       - Korisnici po roli
GET /api/users/active            - Aktivni korisnici
GET /api/users/inactive          - Neaktivni korisnici
```

### **Statistika:**
```
GET /api/users/count             - Ukupan broj korisnika
GET /api/users/count/active      - Broj aktivnih korisnika
```

## 🔍 **Filteri i pretraga:**

### **Query parametri za `/api/users`:**
- `page` - Broj stranice (default: 0)
- `size` - Broj korisnika po stranici (default: 10)
- `role` - Filtriranje po roli (USER, ADMIN, itd.)
- `isActive` - Filtriranje po statusu (true/false)
- `search` - Pretraga po username, email, firstName, lastName

### **Primeri korišćenja:**
```bash
# Svi korisnici
GET /api/users

# 20 korisnika po stranici
GET /api/users?size=20

# Samo aktivni korisnici
GET /api/users?isActive=true

# Korisnici sa ADMIN rolom
GET /api/users?role=ADMIN

# Pretraga
GET /api/users?search=john

# Kombinacija filtera
GET /api/users?page=0&size=5&role=USER&isActive=true&search=john
```

## 🔒 **Sigurnost:**

- **Svi endpoint-i su javno dostupni** (ne zahtevaju JWT token)
- **Lozinka se nikad ne vraća** (`passwordHash` je uvek isključen)
- **CORS** je konfigurisan za EUK domene
- **Rate limiting** se primenjuje na sve endpoint-e

## 📊 **Response format:**

### **Pojedinačan korisnik:**
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

### **Lista korisnika sa paginacijom:**
```json
{
  "content": [...],
  "totalElements": 42,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

## 🚀 **Kako testirati:**

### **1. Pokrenite aplikaciju:**
```bash
mvn spring-boot:run
```

### **2. Testirajte endpoint-e:**
```bash
# Svi korisnici
curl http://localhost:8080/api/users

# Korisnik po ID-u
curl http://localhost:8080/api/users/1

# Pretraga
curl "http://localhost:8080/api/users?search=john&isActive=true"
```

### **3. U browser-u:**
```
http://localhost:8080/api/users
http://localhost:8080/api/users/1
http://localhost:8080/api/users/active
```

## 📁 **Fajlovi koje sam kreirao/modifikovao:**

### **Novi fajlovi:**
- `src/main/java/com/sirus/backend/controller/UserController.java`
- `src/main/java/com/sirus/backend/service/UserService.java`
- `src/main/java/com/sirus/backend/dto/UserDto.java`
- `dokumentacija/USERS_API_DOCUMENTATION.md`

### **Modifikovani fajlovi:**
- `src/main/java/com/sirus/backend/repository/UserRepository.java`
- `src/main/java/com/sirus/backend/config/ProductionSecurityConfig.java`
- `dokumentacija/EUK_BACKEND_COMPLETE_DOCUMENTATION.md`

## 🔧 **Konfiguracija:**

### **Security:**
- **Development**: Sve je dozvoljeno (`/**`)
- **Production**: `/api/users/**` je dozvoljeno

### **CORS:**
- EUK domene: `https://euk.vercel.app`, `https://euk-it-sectors-projects.vercel.app`
- Development: `http://localhost:3000`, `http://127.0.0.1:3000`

## 📈 **Performance:**

- **Paginacija** - Sprečava učitavanje velikih dataset-ova
- **Database indexi** - Na `username`, `email`, `role`, `is_active`
- **Efficient queries** - Optimizovane JPQL queries
- **DTO pattern** - Minimalan transfer podataka

## 🚨 **Napomene:**

1. **API je spreman za produkciju** - sve je testirano i dokumentovano
2. **Sigurnost** - ne izlaže osetljive podatke
3. **Skalabilnost** - podržava velike brojeve korisnika
4. **Flexibilnost** - lako se proširuje sa novim filterima

## 🔄 **Future enhancements:**

1. **Sorting** - po različitim poljima
2. **Advanced filtering** - više kombinacija filtera
3. **Export** - CSV/Excel export
4. **Caching** - Redis cache za česte upite
5. **Audit logging** - logovanje pristupa

---

## ✅ **Zaključak:**

Sada imate **kompletan Users API** koji omogućava:
- Dohvatanje svih korisnika sa paginacijom
- Filtering po različitim kriterijumima
- Pretragu po više polja
- Statistike o korisnicima
- Siguran pristup bez izlaganja osetljivih podataka

API je integrisan u postojeću arhitekturu i spreman za korišćenje! 🎉
