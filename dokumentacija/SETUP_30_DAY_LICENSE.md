# Setup 30-Day Global License

## Problem
Treba da postavim globalnu licencu da ističe za 30 dana za testiranje.

## Rešenje

### 1. **Pokreni SQL script:**

**U PostgreSQL:**
```sql
-- Pokreni setup_global_license_expires_30_days.sql
\i postgresQuery/setup_global_license_expires_30_days.sql
```

### 2. **Ili pokreni direktno:**

**Kopiraj i pokreni:**
```sql
-- Clear any existing global license
DELETE FROM global_license;

-- Insert a new global license that expires in 30 days
INSERT INTO global_license (
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at
) VALUES (
    'GLOBAL-LICENSE-2024-30DAYS',
    NOW(),
    NOW() + INTERVAL '30 days',
    true,
    false,
    NOW(),
    NOW()
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
    EXTRACT(DAYS FROM (end_date - NOW())) as days_until_expiry
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
    EXTRACT(DAYS FROM (end_date - NOW())) as days_until_expiry,
    end_date as expiration_date
FROM global_license;
```

## Očekivani rezultat

- ✅ Globalna licenca je kreirana
- ✅ Ističe za 30 dana
- ✅ Status je 'VALID'
- ✅ `days_until_expiry` je približno 30

## Testiranje

### 1. **Testiraj backend endpoint:**
```bash
# Testiraj status
curl -X GET "http://localhost:8080/api/global-license/status"
```

### 2. **Testiraj u browser console:**
```javascript
// Testiraj status
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => console.log('Status:', data))
  .catch(error => console.error('Error:', error));
```

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
- [ ] Globalna licenca je kreirana
- [ ] Ističe za 30 dana
- [ ] Status je 'VALID'
- [ ] Testiraj backend endpoint
- [ ] Testiraj frontend

## Rezultat

Nakon ovih koraka, globalna licenca treba da ističe za 30 dana i treba da radi ispravno.
