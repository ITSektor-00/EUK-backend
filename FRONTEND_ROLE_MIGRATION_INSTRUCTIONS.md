# 🔄 MIGRACIJA SA NIVOA PRISTUPA NA ROLE-BASED SISTEM

## 📋 PREGLED PROMENA

Sistem se prebacuje sa **nivoa pristupa** (1-5) na **role-based sistem** sa 3 glavne role:

### 🎭 NOVE ROLE:
1. **`ADMIN`** - Puni pristup svim funkcionalnostima
2. **`obradjivaci predmeta`** - Obrađivači predmeta
3. **`potpisnik`** - Potpisnici

---

## 🗑️ SQL MIGRACIJA (POKRENI U BAZI)

```sql
-- 1. Brisanje kolone nivo_pristupa iz users tabele
ALTER TABLE users DROP COLUMN IF EXISTS nivo_pristupa;

-- 2. Brisanje kolone nivo_dozvole iz user_routes tabele  
ALTER TABLE user_routes DROP COLUMN IF EXISTS nivo_dozvole;

-- 3. Brisanje kolona nivo_min i nivo_max iz rute tabele
ALTER TABLE rute DROP COLUMN IF EXISTS nivo_min;
ALTER TABLE rute DROP COLUMN IF EXISTS nivo_max;
```

---

## 🔧 BACKEND PROMENE

### ✅ Šta je uklonjeno:
- `nivoPristupa` iz User entiteta
- `nivoDozvole` iz UserRoute entiteta  
- `nivoMin` i `nivoMax` iz Route entiteta
- PUT endpoint za ažuriranje nivoa pristupa

### ✅ Šta je dodano:
- Role-based validacija
- Jednostavna vezna tabela `user_routes` između `users` i `rute`

---

## 🎯 FRONTEND INSTRUKCIJE

### 1. **ADMIN PANEL - UPRAVLJANJE KORISNICIMA**

**Promeni u korisničkom interfejsu:**

```javascript
// STARO - nivoi pristupa (1-5)
const accessLevels = [1, 2, 3, 4, 5];

// NOVO - role
const userRoles = [
    'ADMIN',
    'obradjivaci predmeta', 
    'potpisnik'
];
```

**Forma za kreiranje/ažuriranje korisnika:**
```jsx
// Zameni dropdown za nivo pristupa sa role dropdown
<select name="role" value={user.role} onChange={handleRoleChange}>
    <option value="ADMIN">Admin</option>
    <option value="obradjivaci predmeta">Obrađivači predmeta</option>
    <option value="potpisnik">Potpisnik</option>
</select>
```

### 2. **ADMIN PANEL - UPRAVLJANJE RUTAMA**

**Ukloni sve vezano za nivo pristupa:**
- ❌ Ukloni `nivoMin` i `nivoMax` polja
- ❌ Ukloni validaciju nivoa (1-5)
- ❌ Ukloni PUT endpoint za ažuriranje nivoa pristupa

**Jednostavna forma za rute:**
```jsx
// Nova forma za rute
<div className="route-form">
    <input name="ruta" placeholder="Ruta (npr. /api/euk/predmeti)" />
    <input name="naziv" placeholder="Naziv rute" />
    <input name="opis" placeholder="Opis" />
    <input name="sekcija" placeholder="Sekcija" />
</div>
```

### 3. **API ENDPOINT PROMENE**

**POST `/api/admin/user-routes` - Nova struktura:**
```javascript
// STARO
{
    "userId": 1,
    "routeId": 2,
    "nivoDozvola": 3
}

// NOVO - uklonjen nivoDozvola
{
    "userId": 1,
    "routeId": 2
}
```

**Response struktura:**
```javascript
// STARO
{
    "id": 1,
    "userId": 1,
    "routeId": 2,
    "nivoDozvole": 3,
    "createdAt": "2025-01-15T11:00:00"
}

// NOVO - uklonjen nivoDozvole
{
    "id": 1,
    "userId": 1,
    "routeId": 2,
    "createdAt": "2025-01-15T11:00:00"
}
```

### 4. **SIDEBAR NAVIGACIJA**

**Implementiraj role-based navigaciju:**
```javascript
// Funkcija za proveru pristupa - proverava da li korisnik ima pristup ruti
const hasAccessToRoute = async (userRole, routePath) => {
    // Pozovi API da proveri da li korisnik ima pristup ovoj ruti
    const response = await fetch(`/api/user-routes/check/${userId}/${routeId}`);
    return response.ok;
};

// Primer korišćenja u sidebar-u
const menuItems = [
    {
        path: '/admin/users',
        label: 'Korisnici',
        requiredRole: 'ADMIN'
    },
    {
        path: '/admin/routes', 
        label: 'Rute',
        requiredRole: 'ADMIN'
    },
    {
        path: '/euk/predmeti',
        label: 'EUK Predmeti',
        allowedRoles: ['ADMIN', 'obradjivaci predmeta']
    },
    {
        path: '/euk/potpisivanje',
        label: 'Potpisivanje',
        allowedRoles: ['ADMIN', 'potpisnik']
    }
];

// Filtriraj menu na osnovu role
const filteredMenu = menuItems.filter(item => {
    if (item.requiredRole) {
        return currentUser.role === item.requiredRole;
    }
    if (item.allowedRoles) {
        return item.allowedRoles.includes(currentUser.role);
    }
    return true;
});
```

### 5. **VALIDACIJA I GREŠKE**

**Ažuriraj error handling:**
```javascript
// Ukloni sve greške vezane za nivo pristupa
// Dodaj validaciju za role
const validateRole = (role) => {
    const validRoles = ['ADMIN', 'obradjivaci predmeta', 'potpisnik'];
    return validRoles.includes(role);
};
```

---

## 🚀 KORACI ZA IMPLEMENTACIJU

### 1. **Baza podataka**
```bash
# Pokreni SQL migraciju
psql -d your_database -f remove_nivo_pristupa_columns.sql
```

### 2. **Frontend promene**
1. ✅ Zameni sve `nivoPristupa` sa `role`
2. ✅ Ukloni `nivoDozvola` iz user-routes forma
3. ✅ Ukloni `nivoMin`/`nivoMax` iz routes forma
4. ✅ Implementiraj role-based sidebar
5. ✅ Ažuriraj validaciju

### 3. **Testiranje**
1. ✅ Testiraj kreiranje korisnika sa novim rolama
2. ✅ Testiraj dodeljivanje ruta korisnicima
3. ✅ Testiraj role-based navigaciju
4. ✅ Testiraj admin panel funkcionalnost

---

## 📞 PODRŠKA

Ako imaš pitanja ili problema tokom migracije, kontaktiraj backend tim.

**Backend je spreman za role-based sistem!** 🎉
