# 🎯 ROLE-BASED ACCESS CONTROL SA SEKCIJAMA

## 📋 LOGIKA PRISTUPA

### **🎭 ROLE DEFINICIJE:**

1. **`ADMIN`** - Puni pristup svim rutama
2. **`obradjivaci predmeta`** - Pristup samo EUK rutama
3. **`potpisnik`** - Pristup samo EUK rutama

### **📂 SEKCIJE U RUTE TABELI:**

- **`ADMIN`** - Admin rute (admin/korisnici, admin/rute, admin/user-routes)
- **`EUK`** - EUK rute (euk/kategorija, euk/predmeti, euk/ugrozena-lica, itd.)

---

## 🔐 PRISTUP MATRIX

| Role | ADMIN sekcija | EUK sekcija |
|------|---------------|-------------|
| **ADMIN** | ✅ Pristup | ❌ Nema pristup |
| **obradjivaci predmeta** | ❌ Nema pristup | ✅ Pristup |
| **potpisnik** | ❌ Nema pristup | ✅ Pristup |

---

## 🛠️ IMPLEMENTACIJA

### **1. UserRouteService - Dodaj metode za sekcija-based pristup:**

```java
// Proveri da li korisnik ima pristup sekciji
public boolean hasUserAccessToSection(Long userId, String section) {
    String userRole = userRepository.findById(userId)
        .map(User::getRole)
        .orElse(null);
    
    if (userRole == null) return false;
    
    // ADMIN ima pristup samo ADMIN sekcijama
    if ("ADMIN".equals(userRole)) {
        return "ADMIN".equals(section);
    }
    
    // Ostali korisnici imaju pristup samo EUK sekciji
    return "EUK".equals(section);
}

// Dohvati rute koje korisnik može da vidi
public List<RouteDto> getAccessibleRoutesForUser(Long userId) {
    String userRole = userRepository.findById(userId)
        .map(User::getRole)
        .orElse(null);
    
    if (userRole == null) return Collections.emptyList();
    
    if ("ADMIN".equals(userRole)) {
        // ADMIN vidi samo ADMIN rute
        return routeRepository.findBySekcija("ADMIN").stream()
            .map(this::convertRouteToDto)
            .collect(Collectors.toList());
    } else {
        // Ostali korisnici vide samo EUK rute
        return routeRepository.findBySekcija("EUK").stream()
            .map(this::convertRouteToDto)
            .collect(Collectors.toList());
    }
}

// Dohvati user-routes koje korisnik može da vidi
public List<UserRouteDto> getAccessibleUserRoutesForUser(Long userId) {
    String userRole = userRepository.findById(userId)
        .map(User::getRole)
        .orElse(null);
    
    if (userRole == null) return Collections.emptyList();
    
    if ("ADMIN".equals(userRole)) {
        // ADMIN vidi samo ADMIN user-routes
        return userRouteRepository.findByUserIdAndRouteSekcija(userId, "ADMIN").stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    } else {
        // Ostali korisnici vide samo EUK user-routes
        return userRouteRepository.findByUserIdAndRouteSekcija(userId, "EUK").stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
}
```

### **2. AdminController - Dodaj endpoint-e za sekcija kontrolu:**

```java
// GET /api/admin/accessible-routes/{userId} - Rute dostupne korisniku
@GetMapping("/accessible-routes/{userId}")
public ResponseEntity<?> getAccessibleRoutesForUser(@PathVariable Long userId) {
    try {
        List<RouteDto> routes = userRouteService.getAccessibleRoutesForUser(userId);
        return ResponseEntity.ok(routes);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(AdminErrorResponse.serverError("Failed to fetch accessible routes"));
    }
}

// GET /api/admin/accessible-user-routes/{userId} - User-routes dostupne korisniku
@GetMapping("/accessible-user-routes/{userId}")
public ResponseEntity<?> getAccessibleUserRoutesForUser(@PathVariable Long userId) {
    try {
        List<UserRouteDto> userRoutes = userRouteService.getAccessibleUserRoutesForUser(userId);
        return ResponseEntity.ok(userRoutes);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(AdminErrorResponse.serverError("Failed to fetch accessible user routes"));
    }
}

// POST /api/admin/assign-route - Dodeli rutu korisniku (samo ako ima pristup)
@PostMapping("/assign-route")
public ResponseEntity<?> assignRouteToUser(@RequestBody UserRouteRequest request) {
    try {
        // Proveri da li admin može da dodeli ovu rutu
        if (!userRouteService.hasUserAccessToSection(request.getUserId(), 
            routeRepository.findById(request.getRouteId())
                .map(Route::getSekcija)
                .orElse(""))) {
            return ResponseEntity.badRequest()
                .body(AdminErrorResponse.serverError("User does not have access to this route section"));
        }
        
        UserRouteDto userRouteDto = new UserRouteDto();
        userRouteDto.setUserId(request.getUserId());
        userRouteDto.setRouteId(request.getRouteId());
        
        UserRouteDto createdUserRoute = userRouteService.createUserRoute(userRouteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserRoute);
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(AdminErrorResponse.serverError("Failed to assign route"));
    }
}
```

### **3. Frontend - Implementiraj sekcija-based navigaciju:**

```javascript
// Funkcija za proveru pristupa na osnovu role i sekcije
const hasAccessToSection = (userRole, section) => {
    if (userRole === 'ADMIN') {
        return section === 'ADMIN'; // ADMIN ima pristup samo ADMIN sekcijama
    }
    return section === 'EUK'; // Ostali korisnici imaju pristup samo EUK sekciji
};

// Menu konfiguracija sa sekcijama
const menuItems = [
    {
        path: '/admin/users',
        label: 'Korisnici',
        section: 'ADMIN',
        requiredRole: 'ADMIN'
    },
    {
        path: '/admin/routes',
        label: 'Rute',
        section: 'ADMIN',
        requiredRole: 'ADMIN'
    },
    {
        path: '/admin/user-routes',
        label: 'User Routes',
        section: 'ADMIN',
        requiredRole: 'ADMIN'
    },
    {
        path: '/euk/kategorija',
        label: 'EUK Kategorije',
        section: 'EUK'
    },
    {
        path: '/euk/predmeti',
        label: 'EUK Predmeti',
        section: 'EUK'
    },
    {
        path: '/euk/ugrozena-lica',
        label: 'Ugrožena Lica',
        section: 'EUK'
    }
];

// Filtriraj menu na osnovu role i sekcije
const filteredMenu = menuItems.filter(item => {
    // Proveri da li korisnik ima pristup sekciji
    if (!hasAccessToSection(currentUser.role, item.section)) {
        return false;
    }
    
    // Proveri required role
    if (item.requiredRole) {
        return currentUser.role === item.requiredRole;
    }
    
    return true;
});
```

---

## 🎯 PRIMER KORIŠĆENJA

### **Admin korisnik:**
- ✅ Vidi samo ADMIN rute (admin/korisnici, admin/rute, admin/user-routes)
- ✅ Može da dodeli samo ADMIN rute
- ✅ Ima pristup samo admin endpoint-ima

### **Obrađivač predmeta:**
- ❌ Ne vidi admin rute
- ✅ Vidi samo EUK rute
- ✅ Može da dobije pristup EUK rutama

### **Potpisnik:**
- ❌ Ne vidi admin rute
- ✅ Vidi samo EUK rute
- ✅ Može da dobije pristup EUK rutama

---

## 📝 SQL PRIMERI

```sql
-- Dohvati sve EUK rute
SELECT * FROM rute WHERE sekcija = 'EUK';

-- Dohvati sve ADMIN rute
SELECT * FROM rute WHERE sekcija = 'ADMIN';

-- Dohvati user-routes za EUK sekciju
SELECT ur.*, r.sekcija 
FROM user_routes ur 
JOIN rute r ON ur.route_id = r.id 
WHERE r.sekcija = 'EUK';
```

**Ova logika omogućava fleksibilnu kontrolu pristupa na osnovu role i sekcije!** 🚀
