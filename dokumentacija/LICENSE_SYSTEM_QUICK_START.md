# Licencni Sistem - Brzo Pokretanje

## 1. Database Setup (5 minuta)

```bash
# Pokreni SQL skriptu
psql -h your-host -U your-user -d your-database -f postgresQuery/create_licenses_table.sql

# Dodaj test podatke (opciono)
psql -h your-host -U your-user -d your-database -f postgresQuery/insert_test_licenses.sql
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
# Testiranje statusa licence
curl -X GET "http://localhost:8080/api/licenses/status?userId=1"

# Testiranje kreiranja licence
curl -X POST "http://localhost:8080/api/licenses/create?userId=1"

# Testiranje provere licence
curl -X GET "http://localhost:8080/api/licenses/check/1"
```

## 5. Frontend Integracija

Pratite instrukcije u `dokumentacija/FRONTEND_LICENSE_SYSTEM_IMPLEMENTATION.md`

## 6. Proverite da li radi

1. **Database**: Proverite da li je `licenses` tabela kreirana
2. **API**: Testirajte endpoint-e sa Postman-om
3. **Cron job-ovi**: Proverite logove za cron job izvršavanje
4. **Email**: Testirajte slanje obaveštenja

## Troubleshooting

- **CORS greške**: Dodaj frontend URL u CORS konfiguraciju
- **Email ne radi**: Proveri SMTP konfiguraciju
- **Database greške**: Proveri konekciju i SQL skripte

## Sledeći koraci

1. Implementiraj frontend komponente
2. Konfiguriraj email servis
3. Testiraj sve funkcionalnosti
4. Deploy u produkciju
