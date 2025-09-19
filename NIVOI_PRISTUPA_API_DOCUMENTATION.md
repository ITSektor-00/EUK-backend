# 🎯 NIVOI PRISTUPA - BACKEND API DOKUMENTACIJA

## 📋 Pregled

Implementiran je kompletan backend sistem za upravljanje nivoima pristupa korisnika. Sistem omogućava:
- Upravljanje rutama sa nivoima pristupa
- Dodeljivanje dozvola korisnicima za specifične rute
- Ažuriranje nivoa pristupa korisnika
- Dinamičko upravljanje pristupom na osnovu nivoa

## 🗄️ Database Schema

### Tabela `rute`:
```sql
CREATE TABLE rute (
    id SERIAL PRIMARY KEY,
    ruta VARCHAR(255) NOT NULL UNIQUE,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT,
    sekcija VARCHAR(100),
    nivo_min INTEGER NOT NULL DEFAULT 1,
    nivo_max INTEGER NOT NULL DEFAULT 5,
    aktivna BOOLEAN DEFAULT true,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabela `user_routes`:
```sql
CREATE TABLE user_routes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    route_id INTEGER NOT NULL,
    nivo_dozvole INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES rute(id) ON DELETE CASCADE,
    UNIQUE(user_id, route_id)
);
```

### Ažurirana tabela `users`:
```sql
ALTER TABLE users ADD COLUMN nivo_pristupa INTEGER DEFAULT 1;
```

## 🎯 Nivoi Pristupa

```javascript
const NIVOI_PRISTUPA = {
  1: "Основни корисник",      // Pristup osnovnim funkcionalnostima
  2: "Потписник",            // Može potpisivati dokumente  
  3: "Обрађивач предмета",   // Može obrađivati predmete
  4: "Супервизор",           // Može pregledati izveštaje
  5: "Администратор"         // Pristup svim funkcionalnostima
};
```

## 🚀 API Endpoints

### 1. **GET /api/admin/routes**
**Opis:** Vrati sve rute sa nivoima pristupa
**Response:**
```json
[
  {
    "id": 1,
    "ruta": "euk/kategorije",
    "naziv": "Kategorije",
    "opis": "Upravljanje kategorijama predmeta",
    "sekcija": "EUK",
    "nivoMin": 1,
    "nivoMax": 5,
    "aktivna": true,
    "datumKreiranja": "2024-01-15T10:30:00"
  }
]
```

### 2. **GET /api/admin/user-routes**
**Opis:** Vrati sve user routes sa nivoima
**Response:**
```json
[
  {
    "id": 1,
    "userId": 6,
    "routeId": 1,
    "route": "euk/kategorije",
    "nivoDozvole": 3,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "user": {
      "id": 6,
      "username": "luka.rakic",
      "firstName": "Luka",
      "lastName": "Rakic",
      "role": "admin",
      "isActive": true,
      "nivoPristupa": 5
    },
    "routeDto": {
      "id": 1,
      "ruta": "euk/kategorije",
      "naziv": "Kategorije",
      "nivoMin": 1,
      "nivoMax": 5
    }
  }
]
```

### 3. **GET /api/admin/user-routes/{userId}**
**Opis:** Vrati rute za specifičnog korisnika
**Response:** Isti kao gore, ali filtrirano po userId

### 4. **POST /api/admin/user-routes**
**Opis:** Dodaj novu rutu za korisnika
**Request Body:**
```json
{
  "userId": 6,
  "routeId": 1,
  "nivoDozvole": 3
}
```

### 5. **PUT /api/admin/user-routes/{userId}/{routeId}**
**Opis:** Ažuriraj nivo dozvole
**Request Body:**
```json
{
  "nivoDozvole": 4
}
```

### 6. **DELETE /api/admin/user-routes/{userId}/{routeId}**
**Opis:** Ukloni rutu za korisnika
**Response:** 200 OK

### 7. **PUT /api/admin/users/{userId}/level**
**Opis:** Ažuriraj nivo pristupa korisnika
**Request Body:**
```json
{
  "nivoPristupa": 4
}
```

## 🔧 Dodatni Endpoints

### Route Management:
- `GET /api/admin/routes/{id}` - Get route by ID
- `GET /api/admin/routes/path/{ruta}` - Get route by path
- `GET /api/admin/routes/section/{sekcija}` - Get routes by section
- `GET /api/admin/routes/active` - Get active routes
- `GET /api/admin/routes/accessible/{level}` - Get routes accessible by level
- `POST /api/admin/routes` - Create new route
- `PUT /api/admin/routes/{id}` - Update route
- `DELETE /api/admin/routes/{id}` - Delete route

### User Route Management:
- `GET /api/admin/user-routes/{userId}/{routeId}` - Get specific user route
- `GET /api/admin/user-routes/{userId}/check/{routeId}` - Check if user has access
- `GET /api/admin/user-routes/{userId}/min-level/{minLevel}` - Get routes by minimum level
- `GET /api/admin/user-routes/{userId}/level/{nivoDozvole}` - Get routes by access level
- `GET /api/admin/user-routes/{userId}/count` - Count user routes

## 🛠️ Implementacija

### Kreirane klase:
1. **Entities:**
   - `Route.java` - Route entity sa nivoima pristupa
   - `UserRoute.java` - User-Route linking entity
   - `User.java` - Ažuriran sa `nivoPristupa` poljem

2. **DTOs:**
   - `RouteDto.java` - Route data transfer object
   - `UserRouteDto.java` - User-Route data transfer object
   - `UserLevelUpdateDto.java` - User level update DTO

3. **Repositories:**
   - `RouteRepository.java` - Route data access
   - `UserRouteRepository.java` - User-Route data access

4. **Services:**
   - `RouteService.java` - Route business logic
   - `UserRouteService.java` - User-Route business logic
   - `UserService.java` - Ažuriran sa level update metodom

5. **Controllers:**
   - `RouteController.java` - Route REST endpoints
   - `UserRouteController.java` - User-Route REST endpoints

## 📝 Setup Instrukcije

1. **Pokreni database skript:**
   ```bash
   # U PostgreSQL terminalu
   \i database_setup_nivoi_pristupa.sql
   ```

2. **Restartuj backend aplikaciju**

3. **Testiraj endpoint-e:**
   ```bash
   # Test routes
   curl -X GET "http://localhost:8080/api/admin/routes"
   
   # Test user routes
   curl -X GET "http://localhost:8080/api/admin/user-routes"
   ```

## 🔒 Security

- Svi `/api/admin/**` endpoint-i su javno dostupni (permitAll)
- U produkciji treba dodati proper autentifikaciju i autorizaciju
- Validacija nivoa pristupa (1-5)
- Validacija da korisnik i ruta postoje

## 🎯 Frontend Integration

Frontend može koristiti ove endpoint-e za:
- Dinamičko prikazivanje menija na osnovu nivoa pristupa
- Upravljanje dozvolama korisnika
- Ažuriranje nivoa pristupa
- Proveru da li korisnik ima pristup određenoj ruti

## 📊 Test Data

Skript automatski kreira:
- Početne rute za EUK i Admin sekcije
- Nivoi pristupa za postojeće korisnike
- Početne dozvole na osnovu nivoa pristupa
