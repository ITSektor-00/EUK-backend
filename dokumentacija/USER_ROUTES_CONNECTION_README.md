# 🔗 Povezivanje Users i Rute Tabela

## 📋 Pregled

Kreirao sam sistem za povezivanje `users` i `rute` tabela kroz **povezujuću tabelu** `user_routes`. Ovo omogućava fleksibilnu kontrolu pristupa rutama za svakog korisnika.

## 🏗️ **Arhitektura sistema:**

```
users (korisnici)
    ↓
user_routes (povezivanje)
    ↓
rute (rute sistema)
```

## 📊 **Struktura tabele `user_routes`:**

| Kolona | Tip | Opis |
|--------|-----|------|
| `user_id` | INTEGER | ID korisnika (FK na users.id) |
| `route_name` | VARCHAR(255) | Naziv rute (FK na rute.naziv) |
| `can_access` | BOOLEAN | Da li korisnik može pristupiti ruti |
| `granted_at` | TIMESTAMP | Kada je pristup dat |

## 🔌 **Kako funkcioniše:**

### **1. Automatsko podešavanje pristupa:**
- **Admin korisnici** (`role = 'ADMIN'`) automatski dobijaju pristup **SAMO admin rutama** (`/admin/**`)
- **Obični korisnici** automatski dobijaju pristup **EUK rutama** (koje ne zahtevaju admin rolu)

### **2. Logika pristupa:**
```sql
-- Korisnik može pristupiti ruti ako:
1. Ima ADMIN rolu i ruta je admin ruta (/admin/**)
2. Ruta ne zahteva admin rolu (EUK rute)
3. Ima eksplicitno odobren pristup u user_routes tabeli
```

## 🚀 **Kako implementirati:**

### **Korak 1: Izvršite SQL fajl**
```bash
# U PostgreSQL-u
psql -d your_database -f user_routes_simple.sql
```

### **Korak 2: Ili kopirajte direktno:**
```sql
-- Kreiraj povezujuću tabelu
CREATE TABLE IF NOT EXISTS user_routes (
    user_id INTEGER NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    can_access BOOLEAN DEFAULT TRUE,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_name) REFERENCES rute(naziv) ON DELETE CASCADE,
    PRIMARY KEY (user_id, route_name)
);

-- Automatski podesi pristupe
-- Admin korisnici dobijaju pristup SAMO admin rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role = 'ADMIN' AND r.zahteva_admin_role = true
ON CONFLICT (user_id, route_name) DO NOTHING;

-- Obicni korisnici dobijaju pristup EUK rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role != 'ADMIN' AND r.zahteva_admin_role = false
ON CONFLICT (user_id, route_name) DO NOTHING;
```

## 🔍 **Korisni upiti:**

### **Svi pristupi korisnika:**
```sql
SELECT 
    u.username,
    u.role,
    r.naziv as route,
    r.zahteva_admin_role,
    ur.can_access
FROM users u
JOIN user_routes ur ON u.id = ur.user_id
JOIN rute r ON ur.route_name = r.naziv
ORDER BY u.username, r.naziv;
```

### **Koje rute može korisnik:**
```sql
SELECT r.naziv, r.zahteva_admin_role
FROM user_routes ur
JOIN rute r ON ur.route_name = r.naziv
WHERE ur.user_id = 1 AND ur.can_access = true;
```

### **Admin rute koje korisnik može pristupiti:**
```sql
SELECT r.naziv
FROM user_routes ur
JOIN rute r ON ur.route_name = r.naziv
WHERE ur.user_id = 1 
    AND ur.can_access = true 
    AND r.zahteva_admin_role = true;
```

### **EUK rute koje korisnik može pristupiti:**
```sql
SELECT r.naziv
FROM user_routes ur
JOIN rute r ON ur.route_name = r.naziv
WHERE ur.user_id = 1 
    AND ur.can_access = true 
    AND r.zahteva_admin_role = false;
```

## 🛠️ **Funkcije za upravljanje pristupom:**

### **Dodaj pristup:**
```sql
-- Funkcija je već kreirana
SELECT grant_access(1, '/admin/korisnici');
```

### **Oduzmi pristup:**
```sql
-- Funkcija je već kreirana
SELECT revoke_access(1, '/admin/korisnici');
```

### **Proveri pristup:**
```sql
-- Funkcija je već kreirana
SELECT can_access(1, '/admin/korisnici');
```

## 📱 **Primer korišćenja u Spring Boot aplikaciji:**

### **1. Kreiraj Entity:**
```java
@Entity
@Table(name = "user_routes")
public class UserRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "route_name")
    private String routeName;
    
    @Column(name = "can_access")
    private Boolean canAccess;
    
    // getters, setters...
}
```

### **2. Kreiraj Repository:**
```java
@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
    List<UserRoute> findByUserIdAndCanAccessTrue(Long userId);
    boolean existsByUserIdAndRouteNameAndCanAccessTrue(Long userId, String routeName);
}
```

### **3. Kreiraj Service:**
```java
@Service
public class RouteAccessService {
    
    @Autowired
    private UserRouteRepository userRouteRepository;
    
    public boolean canUserAccessRoute(Long userId, String routeName) {
        // Proveri eksplicitni pristup
        if (userRouteRepository.existsByUserIdAndRouteNameAndCanAccessTrue(userId, routeName)) {
            return true;
        }
        
        // Proveri rolu korisnika i zahteve rute
        // Ovo implementirajte prema vašoj logici
        return false;
    }
}
```

## 🎯 **Prednosti ovog pristupa:**

1. **Fleksibilnost** - možete dati/oduzeti pristup pojedinačnim rutama
2. **Sigurnost** - kontrola pristupa na nivou korisnika i rute
3. **Skalabilnost** - lako dodavanje novih ruta i korisnika
4. **Praćenje** - možete videti ko je dobio pristup i kada
5. **Automatsko podešavanje** - admin korisnici automatski dobijaju pristup admin rutama

## 🔒 **Sigurnosne preporuke:**

1. **Uvek proveravajte pristup** pre nego što dozvolite pristup ruti
2. **Koristite Spring Security** za dodatnu kontrolu
3. **Logujte sve pristupe** za audit
4. **Redovno proveravajte** pristupe korisnika

## 🎉 **Zaključak:**

Sada imate **kompletan sistem za kontrolu pristupa** koji povezuje korisnike i rute! 

- ✅ **Admin korisnici** automatski dobijaju pristup **SAMO admin rutama** (`/admin/**`)
- ✅ **Obični korisnici** dobijaju pristup EUK rutama
- ✅ **Fleksibilna kontrola** - možete ručno upravljati pristupima
- ✅ **Jednostavna implementacija** u Spring Boot aplikaciji

Sistem je spreman za korišćenje! 🚀
