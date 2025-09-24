# 🎯 Frontend Migracija - Pojednostavljeni Sistem Rola

## 📋 Šta se promenilo u backend-u

Backend je **pojednostavljen** sa kompleksnog sistema rola na **samo dve role**:

### **Nove role:**
- **`admin`** - Pristup admin dashboard-u
- **`korisnik`** - Pristup samo EUK funkcionalnostima

### **Uklonjeno:**
- ❌ `obradjivaci predmeta` role
- ❌ `potpisnik` role  
- ❌ Kompleksni sistem ruta i dozvola
- ❌ Tabele `rute` i `user_routes`

---

## 🔧 Frontend promene

### **1. Role Selection (dropdown/select):**

**STARO:**
```javascript
const userRoles = [
    { value: 'admin', label: 'Admin' },
    { value: 'obradjivaci predmeta', label: 'Obrađivači predmeta' },
    { value: 'potpisnik', label: 'Potpisnik' }
];
```

**NOVO:**
```javascript
const userRoles = [
    { value: 'admin', label: 'Admin' },
    { value: 'korisnik', label: 'Korisnik' }
];
```

### **2. Access Control Logic:**

**STARO:**
```javascript
// Kompleksna logika sa rutama
const hasAccess = user.routes.includes('/admin/users');
```

**NOVO:**
```javascript
// Jednostavna logika
const isAdmin = user.role === 'admin';
const isKorisnik = user.role === 'korisnik';

// Admin ima pristup admin dashboard-u
if (isAdmin) {
    // Prikaži admin linkove
}

// Svi korisnici imaju pristup EUK funkcionalnostima
// (ne treba provera)
```

### **3. Navigation Menu:**

**STARO:**
```javascript
const navigation = [
    { name: 'EUK', path: '/euk', roles: ['admin', 'obradjivaci predmeta', 'potpisnik'] },
    { name: 'Admin', path: '/admin', roles: ['admin'] }
];
```

**NOVO:**
```javascript
const navigation = [
    { name: 'EUK', path: '/euk', access: 'all' }, // Svi korisnici
    { name: 'Admin', path: '/admin', access: 'admin' } // Samo admin
];

// Ili još jednostavnije:
const navigation = [
    { name: 'EUK', path: '/euk' }, // Uvek vidljivo
    ...(user.role === 'admin' ? [{ name: 'Admin', path: '/admin' }] : [])
];
```

### **4. User Management Form:**

**STARO:**
```jsx
<select name="role" value={user.role} onChange={handleRoleChange}>
    <option value="admin">Admin</option>
    <option value="obradjivaci predmeta">Obrađivači predmeta</option>
    <option value="potpisnik">Potpisnik</option>
</select>
```

**NOVO:**
```jsx
<select name="role" value={user.role} onChange={handleRoleChange}>
    <option value="admin">Admin</option>
    <option value="korisnik">Korisnik</option>
</select>
```

### **5. Permission Checks:**

**STARO:**
```javascript
// Kompleksne provere
const canManageUsers = user.permissions?.routes?.admin === true;
const canAccessEUK = user.permissions?.routes?.euk === true;
```

**NOVO:**
```javascript
// Jednostavne provere
const canManageUsers = user.role === 'admin';
const canAccessEUK = true; // Svi korisnici imaju pristup
const canDelete = user.role === 'admin';
```

---

## 🚀 API Endpoint-i

### **Admin Dashboard:**
```
GET /api/admin/dashboard - Statistike
GET /api/admin/users - Lista korisnika
PUT /api/admin/users/{id}/role - Promena role (samo admin/korisnik)
PUT /api/admin/users/{id}/status - Aktiviranje/deaktiviranje
```

### **User Permissions:**
```
GET /api/user-permissions/{userId} - Dozvole korisnika
```

**Response format:**
```json
{
  "userId": 1,
  "username": "admin",
  "role": "admin",
  "isActive": true,
  "routes": {
    "admin": true,
    "adminDashboard": true,
    "userManagement": true,
    "euk": true
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
  "canManageUsers": true
}
```

---

## 📱 Komponente za ažuriranje

### **1. UserRoleSelector:**
```jsx
const UserRoleSelector = ({ value, onChange }) => (
  <select value={value} onChange={onChange}>
    <option value="admin">Admin</option>
    <option value="korisnik">Korisnik</option>
  </select>
);
```

### **2. AdminGuard:**
```jsx
const AdminGuard = ({ children }) => {
  const { user } = useAuth();
  
  if (user?.role !== 'admin') {
    return <Navigate to="/euk" replace />;
  }
  
  return children;
};
```

### **3. Navigation:**
```jsx
const Navigation = () => {
  const { user } = useAuth();
  
  return (
    <nav>
      <Link to="/euk">EUK</Link>
      {user?.role === 'admin' && (
        <Link to="/admin">Admin</Link>
      )}
    </nav>
  );
};
```

---

## ⚠️ Važne napomene

1. **Svi postojeći korisnici** će biti migrirani:
   - `obradjivaci predmeta` → `korisnik`
   - `potpisnik` → `korisnik`
   - `admin` → `admin`

2. **EUK funkcionalnosti** su dostupne svim korisnicima

3. **Admin dashboard** je dostupan samo admin korisnicima

4. **Brisanje podataka** može samo admin

---

## 🎯 Sledeći koraci

1. **Ažuriraj role selection** u formama
2. **Pojednostavi access control** logiku
3. **Testiraj** admin dashboard funkcionalnost
4. **Deploy** frontend sa novim promenama

**Sistem je sada značajno jednostavniji i lakši za održavanje!** 🚀
