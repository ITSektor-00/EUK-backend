# Test Expired License

## Problem
Treba da testiram šta se dešava kada je globalna licenca istekla.

## Rešenje

### 1. **Pokreni SQL script:**

**U PostgreSQL:**
```sql
-- Pokreni setup_global_license_expired.sql
\i postgresQuery/setup_global_license_expired.sql
```

### 2. **Ili pokreni direktno:**

**Kopiraj i pokreni:**
```sql
-- Clear any existing global license
DELETE FROM global_license;

-- Insert an expired global license (expired 1 day ago)
INSERT INTO global_license (
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at
) VALUES (
    'GLOBAL-LICENSE-2024-EXPIRED',
    NOW() - INTERVAL '13 months',  -- Started 13 months ago
    NOW() - INTERVAL '1 day',      -- Expired 1 day ago
    false,                         -- Not active
    true,                          -- Notification already sent
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 day'
);
```

### 3. **Proveri da li je kreirana:**

**Pokreni:**
```sql
SELECT 
    id,
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at,
    EXTRACT(DAYS FROM (NOW() - end_date)) as days_since_expiry
FROM global_license;
```

### 4. **Proveri status licence:**

**Pokreni:**
```sql
SELECT 
    CASE 
        WHEN NOW() > end_date THEN 'EXPIRED'
        WHEN NOW() BETWEEN (end_date - INTERVAL '30 days') AND end_date THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAYS FROM (NOW() - end_date)) as days_since_expiry,
    end_date as expiration_date,
    is_active as is_active
FROM global_license;
```

## Očekivani rezultat

- ✅ Globalna licenca je kreirana
- ✅ Status je 'EXPIRED'
- ✅ `is_active` je false
- ✅ `days_since_expiry` je 1
- ✅ `notification_sent` je true

## Testiranje

### 1. **Testiraj backend endpoint:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"
```

**Očekivani odgovor:**
```json
{
  "hasValidLicense": false,
  "endDate": "2024-09-28T12:00:00",
  "daysUntilExpiry": -1,
  "isExpiringSoon": false,
  "message": "Global license has expired"
}
```

### 2. **Testiraj u browser console:**
```javascript
// Testiraj status
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => {
    console.log('Status:', data);
    console.log('Has Valid License:', data.hasValidLicense);
    console.log('Days Until Expiry:', data.daysUntilExpiry);
    console.log('Message:', data.message);
  })
  .catch(error => console.error('Error:', error));
```

### 3. **Testiraj check endpoint:**
```bash
# Testiraj check
curl -X GET "http://localhost:8080/api/global-license/check"
```

**Očekivani odgovor:**
```json
{
  "hasValidLicense": false,
  "message": "Global license is invalid or expired"
}
```

### 4. **Testiraj active endpoint:**
```bash
# Testiraj active
curl -X GET "http://localhost:8080/api/global-license/active"
```

**Očekivani odgovor:**
```json
{
  "success": true,
  "license": null,
  "message": "No active global license found"
}
```

## Frontend Testiranje

### 1. **Testiraj GlobalLicenseContext:**
```javascript
// U browser console
const { isGlobalLicenseValid, globalLicenseInfo, checkGlobalLicense } = useGlobalLicense();

// Proveri status
console.log('Is Valid:', isGlobalLicenseValid);
console.log('License Info:', globalLicenseInfo);

// Manual provera
await checkGlobalLicense();
```

### 2. **Testiraj GlobalLicenseWarning komponentu:**
- Trebalo bi da se prikaže upozorenje
- Trebalo bi da pokazuje da je licenca istekla
- Trebalo bi da pokazuje datum isteka

## Očekivano ponašanje

### 1. **Backend:**
- ✅ Status endpoint vraća `hasValidLicense: false`
- ✅ Check endpoint vraća `hasValidLicense: false`
- ✅ Active endpoint vraća `license: null`
- ✅ Message kaže da je licenca istekla

### 2. **Frontend:**
- ✅ GlobalLicenseWarning se prikazuje
- ✅ Pokaže da je licenca istekla
- ✅ Pokaže datum isteka
- ✅ Pokaže poruku o isteku

### 3. **Interceptor:**
- ✅ Trebalo bi da blokira pristup API-ju
- ✅ Trebalo bi da vraća 403 grešku
- ✅ Trebalo bi da pokazuje poruku o isteku licence

## Troubleshooting

### 1. **Ako dobiješ grešku "Table global_license does not exist":**
```sql
-- Pokreni prvo
\i postgresQuery/create_global_license_table.sql
```

### 2. **Ako dobiješ grešku "Permission denied":**
- Proveri da li imaš dozvole za kreiranje tabela
- Proveri da li si povezan sa ispravnom bazom

### 3. **Ako dobiješ grešku "Column does not exist":**
- Proveri da li je tabela kreirana sa ispravnim kolonama
- Proveri da li je script pokrenut u ispravnom redosledu

## Checklist

- [ ] Tabela global_license postoji
- [ ] Globalna licenca je kreirana kao istekla
- [ ] Status je 'EXPIRED'
- [ ] is_active je false
- [ ] Testiraj backend endpoint-e
- [ ] Testiraj frontend
- [ ] Proveri interceptor ponašanje

## Rezultat

Nakon ovih koraka, globalna licenca treba da bude istekla i treba da blokira pristup aplikaciji.
