# Single Global License Constraint Fix

## Problem
Greška kaže da postoji check constraint "single_global_license" koji sprečava kreiranje više globalnih licenci:

```
ERROR: new row for relation "global_license" violates check constraint "single_global_license"
```

## Uzrok
Tabela `global_license` ima constraint koji sprečava kreiranje više globalnih licenci istovremeno.

## Rešenje

### Opcija 1: Ukloni constraint (preporučeno)

**Pokreni:**
```sql
-- Ukloni constraint
ALTER TABLE global_license DROP CONSTRAINT single_global_license;
```

**Ili pokreni script:**
```sql
\i postgresQuery/fix_single_global_license_constraint.sql
```

### Opcija 2: Koristi fixed script

**Pokreni:**
```sql
\i postgresQuery/setup_global_license_expired_fixed.sql
```

**Ovaj script:**
- Briše sve postojeće globalne licence
- Kreira novu istekli licencu
- Izbegava constraint problem

### Opcija 3: Manual rešenje

**1. Obriši postojeće licence:**
```sql
DELETE FROM global_license;
```

**2. Kreiraj novu istekli licencu:**
```sql
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
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 day',
    false,
    true,
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 day'
);
```

## Testiranje

### 1. **Proveri da li je kreirana:**
```sql
SELECT 
    id,
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    EXTRACT(DAYS FROM (NOW() - end_date)) as days_since_expiry
FROM global_license;
```

### 2. **Proveri status:**
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

### 3. **Testiraj backend endpoint:**
```bash
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

## Očekivani rezultat

- ✅ Globalna licenca je kreirana kao istekla
- ✅ Status je 'EXPIRED'
- ✅ `is_active` je false
- ✅ `days_since_expiry` je 1
- ✅ Backend vraća `hasValidLicense: false`

## Troubleshooting

### 1. **Ako i dalje ima constraint grešku:**
```sql
-- Proveri da li constraint postoji
SELECT constraint_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'global_license' 
AND constraint_name = 'single_global_license';

-- Ako postoji, ukloni ga
ALTER TABLE global_license DROP CONSTRAINT single_global_license;
```

### 2. **Ako dobiješ grešku "Table global_license does not exist":**
```sql
-- Pokreni prvo
\i postgresQuery/create_global_license_table.sql
```

### 3. **Ako dobiješ grešku "Permission denied":**
- Proveri da li imaš dozvole za kreiranje tabela
- Proveri da li si povezan sa ispravnom bazom

## Checklist

- [ ] Constraint je uklonjen ili script je pokrenut
- [ ] Postojeće licence su obrisane
- [ ] Nova istekli licenca je kreirana
- [ ] Status je 'EXPIRED'
- [ ] Testiraj backend endpoint
- [ ] Testiraj frontend

## Rezultat

Nakon ovih koraka, globalna licenca treba da bude istekla i treba da blokira pristup aplikaciji.
