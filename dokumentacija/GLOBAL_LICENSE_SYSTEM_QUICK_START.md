# Global License System - Brzo Pokretanje

## Pregled

Globalni licencni sistem kontroliše pristup celom softveru na osnovu jedne globalne licence. Kada licenca istekne, ceo sistem prestaje da radi.

## 1. Database Setup (5 minuta)

```bash
# Pokreni SQL skriptu za kreiranje global_license tabele
psql -h your-host -U your-user -d your-database -f postgresQuery/create_global_license_table.sql

# Obriši individualni licencni sistem (opciono)
psql -h your-host -U your-user -d your-database -f postgresQuery/remove_individual_license_system.sql
```

## 2. Backend Konfiguracija (2 minuta)

```bash
# Kopiraj environment fajl
cp env.example .env

# Ažuriraj email konfiguraciju u .env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 3. Pokretanje Backend-a

```bash
# Pokreni aplikaciju
mvn spring-boot:run

# Ili sa Docker-om
docker-compose up
```

## 4. Testiranje API-ja

```bash
# Testiranje statusa globalne licence
curl -X GET "http://localhost:8080/api/global-license/status"

# Testiranje provere globalne licence
curl -X GET "http://localhost:8080/api/global-license/check"

# Testiranje aktivne globalne licence
curl -X GET "http://localhost:8080/api/global-license/active"
```

## 5. Frontend Integracija

Pratite instrukcije u `dokumentacija/FRONTEND_GLOBAL_LICENSE_SYSTEM.md`

## 6. Proverite da li radi

1. **Database**: Proverite da li je `global_license` tabela kreirana
2. **API**: Testirajte endpoint-e sa Postman-om
3. **Cron job-ovi**: Proverite logove za cron job izvršavanje
4. **Email**: Testirajte slanje obaveštenja

## API Endpoint-i

| Method | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/global-license/status` | Status globalne licence |
| GET | `/api/global-license/check` | Provera važenja |
| GET | `/api/global-license/active` | Aktivna licenca |
| POST | `/api/global-license/create` | Kreiranje licence (Admin) |
| POST | `/api/global-license/extend` | Produženje licence (Admin) |

## Cron Job-ovi

### 1. Provera obaveštenja (9:00 svakog dana)
```java
@Scheduled(cron = "0 0 9 * * ?")
public void checkAndSendGlobalLicenseExpirationNotification()
```

### 2. Deaktivacija isteklih (10:00 svakog dana)
```java
@Scheduled(cron = "0 0 10 * * ?")
public void deactivateExpiredGlobalLicense()
```

## Troubleshooting

### Česti problemi:

1. **CORS greške**: Dodaj frontend URL u CORS konfiguraciju
2. **Email ne radi**: Proveri SMTP konfiguraciju
3. **Database greške**: Proveri konekciju i SQL skripte
4. **Global license check ne radi**: Proveri da li je interceptor pravilno konfigurisan

### Debug koraci:

1. Proveri network tab u browser-u za API pozive
2. Proveri console log-ove za greške
3. Testiraj API endpoint-e direktno sa Postman-om
4. Proveri da li su cron job-ovi aktivni

## Razlika od Individualnog Sistema

| **Individualni Sistem** | **Globalni Sistem** |
|-------------------------|-------------------|
| ❌ Licenca po korisniku | ✅ **Jedna licenca za ceo softver** |
| ❌ `licenses` tabela | ✅ **`global_license` tabela** |
| ❌ `/api/licenses/*` | ✅ **`/api/global-license/*`** |
| ❌ Provera po korisniku | ✅ **Provera za ceo sistem** |
| ❌ Obaveštenja korisnicima | ✅ **Obaveštenja administratorima** |

## Sledeći koraci

1. Implementiraj frontend komponente
2. Konfiguriraj email servis
3. Testiraj sve funkcionalnosti
4. Deploy u produkciju
