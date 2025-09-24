# 🎯 Pojednostavljeni Sistem Rola - Implementacija

## 📋 Pregled promena

Sistem je pojednostavljen sa kompleksnog role-based sistema na **samo dve role**:
- **`admin`** - Pristup admin dashboard-u
- **`korisnik`** - Pristup samo EUK funkcionalnostima

---

## 🗑️ Uklonjeno iz sistema

### **Entiteti:**
- ❌ `Route.java` - Tabela ruta
- ❌ `UserRoute.java` - Povezivanje korisnika i ruta

### **Repository-je:**
- ❌ `RouteRepository.java`
- ❌ `UserRouteRepository.java`

### **Servisi:**
- ❌ `UserRouteService.java`

### **Tabele u bazi:**
- ❌ `rute` tabela
- ❌ `user_routes` tabela

---

## ✅ Dodano u sistem

### **User entitet:**
- ✅ Enum `UserRole` sa `ADMIN` i `KORISNIK`
- ✅ Helper metode: `isAdmin()`, `isKorisnik()`, `setRoleAsAdmin()`, `setRoleAsKorisnik()`
- ✅ Default role: `"korisnik"`

### **AdminController:**
- ✅ Pojednostavljeni endpoint-i
- ✅ Validacija role (samo `admin` ili `korisnik`)
- ✅ Dashboard statistike

### **UserPermissionsController:**
- ✅ Pojednostavljena logika dozvola
- ✅ Admin ima pristup admin dashboard-u
- ✅ Korisnici imaju pristup samo EUK funkcionalnostima

---

## 🗄️ SQL Migracija

Pokreni `simplify_roles_system.sql` u bazi:

```sql
-- 1. Ažuriraj postojeće korisnike
UPDATE users 
SET role = CASE 
    WHEN role = 'admin' THEN 'admin'
    WHEN role = 'ADMIN' THEN 'admin'
    ELSE 'korisnik'
END;

-- 2. Obriši tabele
DELETE FROM user_routes;
DELETE FROM rute;
DROP TABLE IF EXISTS user_routes;
DROP TABLE IF EXISTS rute;

-- 3. Dodaj constraint
ALTER TABLE users 
ADD CONSTRAINT chk_user_role 
CHECK (role IN ('admin', 'korisnik'));

-- 4. Postavi default
ALTER TABLE users 
ALTER COLUMN role SET DEFAULT 'korisnik';
```

---

## 🔐 Logika pristupa

### **Admin korisnici (`role = 'admin'`):**
- ✅ Pristup admin dashboard-u (`/api/admin/**`)
- ✅ Upravljanje korisnicima
- ✅ Pristup svim EUK funkcionalnostima
- ✅ Može brisati podatke

### **Obični korisnici (`role = 'korisnik'`):**
- ❌ Nema pristup admin dashboard-u
- ❌ Ne može upravljati korisnicima
- ✅ Pristup EUK funkcionalnostima
- ❌ Ne može brisati podatke

---

## 🚀 API Endpoint-i

### **Admin Dashboard:**
```
GET /api/admin/dashboard - Statistike
GET /api/admin/users - Lista korisnika
PUT /api/admin/users/{id}/role - Promena role
PUT /api/admin/users/{id}/status - Aktiviranje/deaktiviranje
```

### **User Permissions:**
```
GET /api/user-permissions/{userId} - Dozvole korisnika
```

---

## 📱 Frontend implementacija

### **1. Role Selection:**
```javascript
const userRoles = [
    { value: 'admin', label: 'Admin' },
    { value: 'korisnik', label: 'Korisnik' }
];
```

### **2. Access Control:**
```javascript
// Proveri da li je admin
const isAdmin = user.role === 'admin';

// Prikaži admin dashboard
if (isAdmin) {
    // Prikaži admin linkove
}

// Prikaži EUK funkcionalnosti
// (svi korisnici imaju pristup)
```

### **3. Navigation:**
```javascript
const navigation = [
    { name: 'EUK', path: '/euk', access: 'all' },
    { name: 'Admin', path: '/admin', access: 'admin' }
];
```

---

## 🔧 Testiranje

### **1. Kreiranje admin korisnika:**
```sql
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active)
VALUES ('admin', 'admin@euk.rs', '$2a$10$...', 'Admin', 'Korisnik', 'admin', true);
```

### **2. Test API-ja:**
```bash
# Admin dashboard
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/admin/dashboard

# User permissions
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/user-permissions/1
```

---

## 📊 Rezultat

### **Pre:**
- 🔴 Kompleksan sistem sa 3+ role
- 🔴 Tabele `rute` i `user_routes`
- 🔴 Složena logika pristupa
- 🔴 Teško održavanje

### **Posle:**
- ✅ Jednostavan sistem sa 2 role
- ✅ Samo `users` tabela
- ✅ Jasna logika pristupa
- ✅ Lako održavanje

---

## 🎯 Sledeći koraci

1. **Pokreni SQL migraciju** u bazi
2. **Deploy-uj backend** sa novim kodom
3. **Ažuriraj frontend** da koristi nove role
4. **Testiraj** admin dashboard funkcionalnost

**Sistem je sada značajno pojednostavljen i lakši za održavanje!** 🚀
