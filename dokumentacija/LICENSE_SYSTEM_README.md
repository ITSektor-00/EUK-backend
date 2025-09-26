# Licencni Sistem - Kompletna Implementacija

## Pregled

Licencni sistem omogućava kontrolu pristupa aplikaciji na osnovu važenja licence. Sistem automatski proverava licencu, šalje obaveštenja o isteku i blokira pristup kada licenca istekne.

## Funkcionalnosti

- ✅ **12-mesečno važenje licence** - Licenca važi tačno 12 meseci od aktivacije
- ✅ **Automatska provera** - Sistem proverava licencu pri svakom zahtevu
- ✅ **Obaveštenja** - Email obaveštenja 30 dana pre isteka
- ✅ **Blokiranje pristupa** - Automatsko blokiranje nakon isteka
- ✅ **Cron job-ovi** - Automatsko slanje obaveštenja i deaktivacija
- ✅ **API endpoint-i** - Kompletni REST API za upravljanje licencama
- ✅ **Frontend integracija** - Kompletne instrukcije za frontend

## Arhitektura

### Backend Komponente

1. **Database Layer**
   - `licenses` tabela sa svim potrebnim poljima
   - PostgreSQL funkcije za proveru važenja
   - Indexi za optimizaciju performansi

2. **Entity Layer**
   - `License` entity sa JPA anotacijama
   - Helper metode za proveru isteka
   - Automatsko ažuriranje timestamp-ova

3. **Repository Layer**
   - `LicenseRepository` sa custom query-jima
   - Optimizovane pretrage za performanse
   - Bulk operacije za ažuriranje

4. **Service Layer**
   - `LicenseService` - glavna logika za upravljanje licencama
   - `LicenseNotificationService` - obaveštenja i cron job-ovi
   - `EmailService` - slanje email obaveštenja

5. **Controller Layer**
   - `LicenseController` - REST API endpoint-i
   - CORS konfiguracija
   - Error handling

6. **Security Layer**
   - `LicenseCheckInterceptor` - provera licence pri svakom zahtevu
   - `WebConfig` - konfiguracija interceptor-a
   - Excluded path-ovi za public endpoint-e

### Frontend Komponente

1. **License Service** - Angular/React/Vue servis za API komunikaciju
2. **License Guard** - Route guard za zaštitu ruta
3. **License Warning** - Komponenta za prikaz upozorenja
4. **License Expired** - Stranica za istekle licence

## Instalacija i Konfiguracija

### 1. Database Setup

```sql
-- Pokreni SQL skriptu za kreiranje tabele
\i postgresQuery/create_licenses_table.sql

-- Dodaj test podatke (opciono)
\i postgresQuery/insert_test_licenses.sql
```

### 2. Backend Konfiguracija

```bash
# Kopiraj environment varijable
cp env.example .env

# Ažuriraj email konfiguraciju u .env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Frontend Integracija

Pratite instrukcije u `dokumentacija/FRONTEND_LICENSE_SYSTEM_IMPLEMENTATION.md`

## API Endpoint-i

### Osnovni Endpoint-i

| Method | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/licenses/status?userId={id}` | Provera statusa licence |
| GET | `/api/licenses/check/{userId}` | Provera važenja licence |
| POST | `/api/licenses/create?userId={id}` | Kreiranje nove licence |
| POST | `/api/licenses/extend?userId={id}` | Produženje licence |
| GET | `/api/licenses/user/{userId}` | Sve licence korisnika |

### Admin Endpoint-i

| Method | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/licenses/admin/expiring` | Licence koje treba da isteknu |
| POST | `/api/licenses/admin/deactivate-expired` | Deaktivacija isteklih |
| POST | `/api/licenses/admin/send-notifications` | Slanje obaveštenja |

## Cron Job-ovi

### 1. Provera obaveštenja (9:00 svakog dana)
```java
@Scheduled(cron = "0 0 9 * * ?")
public void checkAndSendExpirationNotifications()
```

### 2. Deaktivacija isteklih (10:00 svakog dana)
```java
@Scheduled(cron = "0 0 10 * * ?")
public void deactivateExpiredLicenses()
```

## Database Schema

```sql
CREATE TABLE licenses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    notification_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Frontend Implementacija

### Angular
```typescript
// License Service
@Injectable()
export class LicenseService {
  checkLicenseStatus(userId: number): Observable<LicenseInfo> {
    return this.http.get(`/api/licenses/status?userId=${userId}`);
  }
}

// License Guard
@Injectable()
export class LicenseGuard implements CanActivate {
  canActivate(): Observable<boolean> {
    // Provera licence
  }
}
```

### React
```javascript
// License Hook
export const useLicense = (userId) => {
  const [licenseInfo, setLicenseInfo] = useState(null);
  // Hook logika
};

// License Guard Component
const LicenseGuard = ({ children, userId }) => {
  // Provera licence
};
```

## Testiranje

### 1. Unit Testovi
```bash
# Pokreni testove
mvn test
```

### 2. API Testiranje
```bash
# Testiranje endpoint-a
curl -X GET "http://localhost:8080/api/licenses/status?userId=1"
```

### 3. Frontend Testiranje
```bash
# Angular
ng test

# React
npm test
```

## Monitoring i Debug

### 1. Logovi
```bash
# Proveri logove za licence
tail -f logs/application.log | grep -i license
```

### 2. Database Monitoring
```sql
-- Proveri status licenci
SELECT * FROM check_license_validity();

-- Proveri licence koje treba da isteknu
SELECT * FROM get_licenses_expiring_soon();
```

### 3. Email Testiranje
```bash
# Testiranje email konfiguracije
curl -X POST "http://localhost:8080/api/licenses/admin/send-notifications"
```

## Troubleshooting

### Česti Problemi

1. **CORS greške**
   - Proveri CORS konfiguraciju u `WebConfig`
   - Dodaj frontend domenu u allowed origins

2. **Email se ne šalje**
   - Proveri SMTP konfiguraciju u `.env`
   - Testiraj email servis direktno

3. **Licenca se ne proverava**
   - Proveri da li je interceptor registrovan
   - Proveri excluded path-ove

4. **Cron job-ovi ne rade**
   - Proveri da li je `@EnableScheduling` aktivno
   - Proveri logove za cron job greške

### Debug Koraci

1. **Proveri database konekciju**
2. **Testiraj API endpoint-e sa Postman-om**
3. **Proveri frontend network tab**
4. **Proveri backend logove**

## Produkcija

### 1. Environment Varijable
```bash
# Produkcija
SPRING_PROFILES_ACTIVE=prod
LICENSE_NOTIFICATION_ENABLED=true
MAIL_HOST=your-smtp-server.com
```

### 2. Database Backup
```bash
# Backup licenses tabele
pg_dump -t licenses your_database > licenses_backup.sql
```

### 3. Monitoring
- Postaviti monitoring za cron job-ove
- Konfigurisati alerting za email greške
- Praćenje performansi licence provere

## Bezbednost

### 1. API Zaštita
- Svi endpoint-i zahtevaju autentifikaciju
- Admin endpoint-i zahtevaju admin privilegije
- Rate limiting za API pozive

### 2. Database Zaštita
- Foreign key constraint-ovi
- Indexi za performanse
- Backup strategija

### 3. Email Zaštita
- SMTP autentifikacija
- Secure connection (TLS)
- Email template validacija

## Performanse

### 1. Database Optimizacija
- Indexi na `user_id`, `end_date`, `is_active`
- Bulk operacije za ažuriranje
- Connection pooling

### 2. Caching
- Cache licence status u Redis-u
- TTL cache za performanse
- Invalidation strategija

### 3. Monitoring
- Metrije za licence provere
- Response time monitoring
- Error rate tracking

## Zaključak

Licencni sistem je potpuno implementiran sa:
- ✅ Backend API-om
- ✅ Database schema-om
- ✅ Cron job-ovima
- ✅ Email obaveštenjima
- ✅ Frontend instrukcijama
- ✅ Test podacima
- ✅ Dokumentacijom

Sistem je spreman za produkciju i može se koristiti za kontrolu pristupa aplikaciji na osnovu važenja licence.
