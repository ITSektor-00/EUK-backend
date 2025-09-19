# 🎯 FRONTEND IMPLEMENTACIJA - ROLE-BASED ACCESS CONTROL

## 📋 PREGLED PROMENA

Sistem je migriran sa **nivo-based** na **role-based** pristup. Evo šta treba da implementiraš:

---

## 🔐 NOVA PRISTUP MATRIX

| Role | ADMIN sekcija | EUK sekcija |
|------|---------------|-------------|
| **ADMIN** | ✅ Pristup | ❌ Nema pristup |
| **obradjivaci predmeta** | ❌ Nema pristup | ✅ Pristup |
| **potpisnik** | ❌ Nema pristup | ✅ Pristup |

---

## 🛠️ 1. API ENDPOINT PROMENE

### **UKLONJENI ENDPOINT-I:**
```javascript
// ❌ OVI ENDPOINT-I VIŠE NE POSTOJE:
GET /api/user-routes/{userId}/min-level/{minLevel}
GET /api/user-routes/{userId}/level/{nivoDozvole}  
GET /api/user-routes/level-range/{minLevel}/{maxLevel}
```

### **NOVI ENDPOINT-I:**
```javascript
// ✅ NOVI ENDPOINT-I ZA ROLE-BASED PRISTUP:

// 1. Dohvati rute koje korisnik može da vidi
GET /api/admin/accessible-routes/{userId}
// Response: [RouteDto] - rute dostupne korisniku

// 2. Dohvati user-routes koje korisnik može da vidi  
GET /api/admin/accessible-user-routes/{userId}
// Response: [UserRouteDto] - user-routes dostupne korisniku

// 3. Dodeli rutu korisniku (admin može da dodeli bilo koju rutu bilo kom korisniku)
POST /api/admin/assign-route
{
    "userId": 5,
    "routeId": 1
}
// Response: UserRouteDto - kreirana veza

// 4. Proveri da li korisnik ima pristup sekciji
GET /api/admin/check-section-access/{userId}/{section}
// Response: boolean - da li ima pristup sekciji
```

---

## 🎨 2. FRONTEND KOMPONENTE

### **A) Sidebar Navigation - Role-Based**

```javascript
// components/Sidebar.jsx
import React from 'react';

const Sidebar = ({ userRole }) => {
    // Funkcija za proveru pristupa na osnovu role i sekcije
    const hasAccessToSection = (section) => {
        if (userRole === 'ADMIN') {
            return section === 'ADMIN'; // ADMIN ima pristup samo ADMIN sekcijama
        }
        return section === 'EUK'; // Ostali korisnici imaju pristup samo EUK sekciji
    };

    const menuItems = [
        // ADMIN sekcija - samo za ADMIN role
        {
            section: 'ADMIN',
            title: 'Administracija',
            items: [
                { path: '/admin/users', label: 'Korisnici', icon: '👥' },
                { path: '/admin/routes', label: 'Rute', icon: '🛣️' },
                { path: '/admin/user-routes', label: 'User Routes', icon: '🔗' }
            ]
        },
        // EUK sekcija - za sve role osim ADMIN
        {
            section: 'EUK', 
            title: 'EUK Modul',
            items: [
                { path: '/euk/kategorija', label: 'Kategorije', icon: '📁' },
                { path: '/euk/predmeti', label: 'Predmeti', icon: '📄' },
                { path: '/euk/ugrozena-lica', label: 'Ugrožena lica', icon: '👤' }
            ]
        }
    ];

    return (
        <div className="sidebar">
            {menuItems.map(section => (
                hasAccessToSection(section.section) && (
                    <div key={section.section} className="menu-section">
                        <h3>{section.title}</h3>
                        <ul>
                            {section.items.map(item => (
                                <li key={item.path}>
                                    <a href={item.path}>
                                        <span>{item.icon}</span>
                                        {item.label}
                                    </a>
                                </li>
                            ))}
                        </ul>
                    </div>
                )
            ))}
        </div>
    );
};

export default Sidebar;
```

### **B) Admin Panel - User Route Management**

```javascript
// components/AdminPanel/UserRouteManager.jsx
import React, { useState, useEffect } from 'react';

const UserRouteManager = () => {
    const [users, setUsers] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [userRoutes, setUserRoutes] = useState([]);

    // Dohvati sve korisnike
    const fetchUsers = async () => {
        const response = await fetch('/api/admin/users');
        const data = await response.json();
        setUsers(data.content || data);
    };

    // Dohvati sve rute
    const fetchRoutes = async () => {
        const response = await fetch('/api/admin/routes');
        const data = await response.json();
        setRoutes(data);
    };

    // Dohvati rute koje korisnik može da vidi
    const fetchUserAccessibleRoutes = async (userId) => {
        const response = await fetch(`/api/admin/accessible-routes/${userId}`);
        const data = await response.json();
        setUserRoutes(data);
    };

    // Dodeli rutu korisniku
    const assignRouteToUser = async (userId, routeId) => {
        try {
            const response = await fetch('/api/admin/assign-route', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId, routeId })
            });

            if (response.ok) {
                // Osveži listu ruta
                fetchUserAccessibleRoutes(userId);
                alert('Ruta je uspešno dodeljena korisniku!');
            } else {
                const error = await response.json();
                alert(`Greška: ${error.message}`);
            }
        } catch (error) {
            alert('Greška pri dodeljivanju rute!');
        }
    };

    // Oduzmi rutu od korisnika
    const removeRouteFromUser = async (userId, routeId) => {
        try {
            const response = await fetch(`/api/admin/user-routes/${userId}/${routeId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                fetchUserAccessibleRoutes(userId);
                alert('Ruta je uspešno uklonjena!');
            }
        } catch (error) {
            alert('Greška pri uklanjanju rute!');
        }
    };

    useEffect(() => {
        fetchUsers();
        fetchRoutes();
    }, []);

    return (
        <div className="user-route-manager">
            <h2>User Route Management</h2>
            
            {/* Lista korisnika */}
            <div className="users-section">
                <h3>Korisnici</h3>
                <div className="users-grid">
                    {users.map(user => (
                        <div 
                            key={user.id} 
                            className={`user-card ${selectedUser?.id === user.id ? 'selected' : ''}`}
                            onClick={() => {
                                setSelectedUser(user);
                                fetchUserAccessibleRoutes(user.id);
                            }}
                        >
                            <h4>{user.firstName} {user.lastName}</h4>
                            <p>Role: <strong>{user.role}</strong></p>
                            <p>Username: {user.username}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* Rute za selektovanog korisnika */}
            {selectedUser && (
                <div className="user-routes-section">
                    <h3>Rute za: {selectedUser.firstName} {selectedUser.lastName}</h3>
                    
                    {/* Trenutne rute */}
                    <div className="current-routes">
                        <h4>Trenutne rute:</h4>
                        {userRoutes.length > 0 ? (
                            <ul>
                                {userRoutes.map(route => (
                                    <li key={route.id} className="route-item">
                                        <span>{route.naziv}</span>
                                        <span className="section-badge">{route.sekcija}</span>
                                        <button 
                                            onClick={() => removeRouteFromUser(selectedUser.id, route.id)}
                                            className="remove-btn"
                                        >
                                            Ukloni
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Korisnik nema dodeljene rute</p>
                        )}
                    </div>

                    {/* Dostupne rute za dodeljivanje */}
                    <div className="available-routes">
                        <h4>Dostupne rute za dodeljivanje:</h4>
                        <div className="routes-grid">
                            {routes
                                .filter(route => !userRoutes.some(ur => ur.id === route.id))
                                .map(route => (
                                    <div key={route.id} className="route-card">
                                        <h5>{route.naziv}</h5>
                                        <p>{route.opis}</p>
                                        <span className={`section-badge ${route.sekcija.toLowerCase()}`}>
                                            {route.sekcija}
                                        </span>
                                        <button 
                                            onClick={() => assignRouteToUser(selectedUser.id, route.id)}
                                            className="assign-btn"
                                        >
                                            Dodeli rutu
                                        </button>
                                    </div>
                                ))
                            }
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserRouteManager;
```

### **C) Route Access Checker**

```javascript
// hooks/useRouteAccess.js
import { useState, useEffect } from 'react';

export const useRouteAccess = (userId, routeId) => {
    const [hasAccess, setHasAccess] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAccess = async () => {
            try {
                const response = await fetch(`/api/user-routes/${userId}/check/${routeId}`);
                const access = await response.json();
                setHasAccess(access);
            } catch (error) {
                console.error('Error checking route access:', error);
                setHasAccess(false);
            } finally {
                setLoading(false);
            }
        };

        if (userId && routeId) {
            checkAccess();
        }
    }, [userId, routeId]);

    return { hasAccess, loading };
};

// Komponenta za proveru pristupa
const RouteGuard = ({ userId, routeId, children, fallback }) => {
    const { hasAccess, loading } = useRouteAccess(userId, routeId);

    if (loading) {
        return <div>Proverava se pristup...</div>;
    }

    if (!hasAccess) {
        return fallback || <div>Nemate pristup ovoj ruti!</div>;
    }

    return children;
};

export default RouteGuard;
```

---

## 🎨 3. CSS STILOVI

```css
/* styles/admin-panel.css */
.user-route-manager {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

.users-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
    margin-bottom: 30px;
}

.user-card {
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    padding: 15px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.user-card:hover {
    border-color: #007bff;
    transform: translateY(-2px);
}

.user-card.selected {
    border-color: #007bff;
    background-color: #f8f9fa;
}

.routes-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 15px;
}

.route-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    background: white;
}

.route-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border: 1px solid #eee;
    border-radius: 4px;
    margin-bottom: 5px;
}

.section-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
}

.section-badge.admin {
    background-color: #dc3545;
    color: white;
}

.section-badge.euk {
    background-color: #28a745;
    color: white;
}

.assign-btn, .remove-btn {
    padding: 5px 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}

.assign-btn {
    background-color: #007bff;
    color: white;
}

.remove-btn {
    background-color: #dc3545;
    color: white;
}

/* Sidebar styles */
.sidebar {
    width: 250px;
    background-color: #f8f9fa;
    padding: 20px;
    height: 100vh;
    overflow-y: auto;
}

.menu-section {
    margin-bottom: 30px;
}

.menu-section h3 {
    color: #495057;
    margin-bottom: 15px;
    font-size: 16px;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.menu-section ul {
    list-style: none;
    padding: 0;
}

.menu-section li {
    margin-bottom: 8px;
}

.menu-section a {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    text-decoration: none;
    color: #495057;
    border-radius: 6px;
    transition: all 0.3s ease;
}

.menu-section a:hover {
    background-color: #e9ecef;
    color: #007bff;
}

.menu-section a span {
    margin-right: 10px;
    font-size: 16px;
}
```

---

## 🔧 4. API SERVICE FUNKCIJE

```javascript
// services/routeService.js
class RouteService {
    // Dohvati rute dostupne korisniku
    static async getAccessibleRoutes(userId) {
        const response = await fetch(`/api/admin/accessible-routes/${userId}`);
        return response.json();
    }

    // Dohvati user-routes dostupne korisniku
    static async getAccessibleUserRoutes(userId) {
        const response = await fetch(`/api/admin/accessible-user-routes/${userId}`);
        return response.json();
    }

    // Dodeli rutu korisniku
    static async assignRoute(userId, routeId) {
        const response = await fetch('/api/admin/assign-route', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, routeId })
        });
        return response.json();
    }

    // Ukloni rutu od korisnika
    static async removeRoute(userId, routeId) {
        const response = await fetch(`/api/admin/user-routes/${userId}/${routeId}`, {
            method: 'DELETE'
        });
        return response.ok;
    }

    // Proveri pristup sekciji
    static async checkSectionAccess(userId, section) {
        const response = await fetch(`/api/admin/check-section-access/${userId}/${section}`);
        return response.json();
    }

    // Proveri pristup ruti
    static async checkRouteAccess(userId, routeId) {
        const response = await fetch(`/api/user-routes/${userId}/check/${routeId}`);
        return response.json();
    }
}

export default RouteService;
```

---

## 🚀 5. IMPLEMENTACIJA KORAK PO KORAK

### **Korak 1: Ažuriraj Sidebar**
- Zameni postojeći sidebar sa novim role-based logikom
- Koristi `hasAccessToSection()` funkciju

### **Korak 2: Implementiraj Admin Panel**
- Dodaj `UserRouteManager` komponentu
- Omogući admin-u da dodeli bilo koju rutu bilo kom korisniku

### **Korak 3: Dodaj Route Guards**
- Koristi `RouteGuard` komponentu za zaštitu ruta
- Implementiraj `useRouteAccess` hook

### **Korak 4: Ažuriraj API pozive**
- Zameni stare endpoint-e sa novim
- Koristi `RouteService` klasu

### **Korak 5: Testiranje**
- Testiraj sa različitim rolama
- Proveri da li admin može da dodeli rute
- Proveri da li role-based logika radi

---

## ⚠️ VAŽNE NAPOMENE

1. **Role case sensitivity**: Koristi `'ADMIN'` (velika slova) u kodu
2. **Error handling**: Uvek proveri response.ok pre parsiranja
3. **Loading states**: Prikaži loading indikatore tokom API poziva
4. **User feedback**: Prikaži poruke o uspehu/grešci
5. **Responsive design**: Prilagodi za mobile uređaje

---

## 🎯 REZULTAT

Nakon implementacije imaćeš:
- ✅ Role-based sidebar navigaciju
- ✅ Admin panel za upravljanje rutama
- ✅ Fleksibilnost za dodeljivanje ruta
- ✅ Route guards za sigurnost
- ✅ Moderni UI/UX

**Sistem je spreman za produkciju!** 🚀
