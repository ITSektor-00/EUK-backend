# 🔐 User Routes Tabela - Objašnjenje

## 📋 Šta je `user_routes` tabela?

`user_routes` tabela je **tabela za kontrolu pristupa rutama** u vašem sistemu. Ona definiše koje rute može da koristi koji korisnik - to je sistem dozvola (permissions) za rute.

## 🏗️ **Struktura `user_routes` tabele:**

```sql
CREATE TABLE user_routes (
    user_id INTEGER NOT NULL,           -- ID korisnika
    route_name VARCHAR(255) NOT NULL,   -- Naziv rute (npr. /admin/korisnici)
    can_access BOOLEAN DEFAULT TRUE,    -- Da li može pristupiti
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Kada je pristup dat
    
    PRIMARY KEY (user_id, route_name),  -- Jedan korisnik = jedna ruta
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (route_name) REFERENCES rute(naziv)
);
```

## 🔌 **Šta radi `user_routes` tabela:**

### **1. Kontrola pristupa rutama:**
- **Koji korisnik** može pristupiti **kojoj ruti**
- **Da li** korisnik može pristupiti određenoj ruti
- **Kada** je korisnik dobio pristup ruti

### **2. Sigurnosne funkcije:**
- **Route-level security** - kontrola pristupa na nivou pojedinačnih ruta
- **User-specific permissions** - različiti korisnici imaju različite dozvole
- **Dynamic access control** - možete dinamički dodavati/oduzimati pristupe

### **3. Fleksibilnost:**
- **Override default permissions** - možete dati pristup admin ruti običnom korisniku
- **Temporary access** - možete dati pristup na određeno vreme
- **Granular control** - kontrola na nivou pojedinačnih ruta

## 🚀 **Kako funkcioniše u praksi:**

### **Automatsko podešavanje:**
```sql
-- Admin korisnici dobijaju pristup SAMO admin rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role = 'ADMIN' AND r.zahteva_admin_role = true;

-- Obicni korisnici dobijaju pristup EUK rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role != 'ADMIN' AND r.zahteva_admin_role = false;
```

### **Ručno dodavanje pristupa:**
```sql
-- Daj običnom korisniku pristup admin ruti
INSERT INTO user_routes (user_id, route_name) 
VALUES (5, '/admin/korisnici');

-- Oduzmi pristup
DELETE FROM user_routes 
WHERE user_id = 5 AND route_name = '/admin/korisnici';
```

## 🔍 **Korisni upiti za `user_routes`:**

### **Koje rute može korisnik:**
```sql
SELECT 
    u.username,
    r.naziv as route,
    r.zahteva_admin_role,
    ur.can_access
FROM user_routes ur
JOIN users u ON ur.user_id = u.id
JOIN rute r ON ur.route_name = r.naziv
WHERE ur.user_id = 1 AND ur.can_access = true;
```

### **Svi pristupi korisnika:**
```sql
SELECT 
    u.username,
    u.role,
    r.naziv as route,
    r.zahteva_admin_role,
    ur.can_access,
    ur.granted_at
FROM users u
JOIN user_routes ur ON u.id = ur.user_id
JOIN rute r ON ur.route_name = r.naziv
ORDER BY u.username, r.naziv;
```

### **Korisnici koji mogu pristupiti određenoj ruti:**
```sql
SELECT 
    u.username,
    u.role,
    ur.granted_at
FROM user_routes ur
JOIN users u ON ur.user_id = u.id
WHERE ur.route_name = '/admin/korisnici' 
    AND ur.can_access = true;
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

## 🛠️ **Funkcije za upravljanje pristupom:**

### **Dodaj pristup:**
```sql
CREATE OR REPLACE FUNCTION grant_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO user_routes (user_id, route_name) 
    VALUES (user_id, route_name)
    ON CONFLICT (user_id, route_name) DO NOTHING;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Korišćenje:
SELECT grant_access(1, '/admin/korisnici');
```

### **Oduzmi pristup:**
```sql
CREATE OR REPLACE FUNCTION revoke_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
BEGIN
    DELETE FROM user_routes WHERE user_id = user_id AND route_name = route_name;
    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Korišćenje:
SELECT revoke_access(1, '/admin/korisnici');
```

### **Proveri pristup:**
```sql
CREATE OR REPLACE FUNCTION can_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
DECLARE
    user_role VARCHAR(20);
    route_requires_admin BOOLEAN;
BEGIN
    -- Proveri rolu korisnika
    SELECT role INTO user_role FROM users WHERE id = user_id;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Proveri da li ruta zahteva admin rolu
    SELECT zahteva_admin_role INTO route_requires_admin FROM rute WHERE naziv = route_name;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Ako ruta zahteva admin rolu, korisnik mora biti admin
    IF route_requires_admin AND user_role != 'ADMIN' THEN
        RETURN FALSE;
    END IF;
    
    -- Proveri eksplicitni pristup
    IF EXISTS (SELECT 1 FROM user_routes WHERE user_id = user_id AND route_name = route_name AND can_access = true) THEN
        RETURN TRUE;
    END IF;
    
    -- Ako nema eksplicitnog pristupa, dozvoli pristup EUK rutama
    RETURN NOT route_requires_admin;
END;
$$ LANGUAGE plpgsql;

-- Korišćenje:
SELECT can_access(1, '/admin/korisnici');
```

## 📱 **Kako se koristi u Spring Boot aplikaciji:**

### **1. Kreiraj Entity:**
```java
@Entity
@Table(name = "user_routes")
public class UserRoute {
    @EmbeddedId
    private UserRouteId id; // Composite key: user_id + route_name
    
    @Column(name = "can_access")
    private Boolean canAccess;
    
    @Column(name = "granted_at")
    private LocalDateTime grantedAt;
    
    // getters, setters...
}

@Embeddable
public class UserRouteId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "route_name")
    private String routeName;
    
    // getters, setters, equals, hashCode...
}
```

### **2. Kreiraj Repository:**
```java
@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, UserRouteId> {
    List<UserRoute> findByUserIdAndCanAccessTrue(Long userId);
    boolean existsByUserIdAndRouteNameAndCanAccessTrue(Long userId, String routeName);
    void deleteByUserIdAndRouteName(Long userId, String routeName);
}
```

### **3. Kreiraj Service:**
```java
@Service
public class RouteAccessService {
    
    @Autowired
    private UserRouteRepository userRouteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public boolean canUserAccessRoute(Long userId, String routeName) {
        // Proveri eksplicitni pristup
        if (userRouteRepository.existsByUserIdAndRouteNameAndCanAccessTrue(userId, routeName)) {
            return true;
        }
        
        // Proveri rolu korisnika i zahteve rute
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        
        // Ako je admin, može pristupiti admin rutama
        if ("ADMIN".equals(user.getRole()) && routeName.startsWith("/admin/")) {
            return true;
        }
        
        // Ako nije admin, može pristupiti EUK rutama
        return routeName.startsWith("/euk/") || routeName.startsWith("/api/");
    }
    
    public void grantAccess(Long userId, String routeName) {
        UserRoute userRoute = new UserRoute();
        UserRouteId id = new UserRouteId();
        id.setUserId(userId);
        id.setRouteName(routeName);
        
        userRoute.setId(id);
        userRoute.setCanAccess(true);
        userRoute.setGrantedAt(LocalDateTime.now());
        
        userRouteRepository.save(userRoute);
    }
    
    public void revokeAccess(Long userId, String routeName) {
        UserRouteId id = new UserRouteId();
        id.setUserId(userId);
        id.setRouteName(routeName);
        
        userRouteRepository.deleteById(id);
    }
}
```

## 🎯 **Prednosti `user_routes` tabele:**

1. **Fleksibilnost** - možete dati/oduzeti pristup pojedinačnim rutama
2. **Sigurnost** - kontrola pristupa na nivou korisnika i rute
3. **Skalabilnost** - lako dodavanje novih ruta i korisnika
4. **Praćenje** - možete videti ko je dobio pristup i kada
5. **Override** - možete dati pristup admin ruti običnom korisniku

## 🔒 **Sigurnosne preporuke:**

1. **Uvek proveravajte pristup** pre nego što dozvolite pristup ruti
2. **Koristite Spring Security** za dodatnu kontrolu
3. **Logujte sve promene pristupa** za audit
4. **Redovno proveravajte** pristupe korisnika
5. **Implementirajte role-based access control** kao dodatnu sigurnost

## 🎉 **Zaključak:**

`user_routes` tabela je **ključna za kontrolu pristupa** vašeg sistema! Omogućava vam da:

- ✅ **Kontrolišete** ko može pristupiti kojoj ruti
- ✅ **Dajete** pristup admin rutama običnim korisnicima (ako je potrebno)
- ✅ **Oduzimate** pristup određenim rutama
- ✅ **Pratite** sve promene pristupa
- ✅ **Imate fleksibilan** sistem dozvola

Ovo je **neophodno** za bilo koji sistem koji zahteva kontrolu pristupa na nivou ruta! 🔐
