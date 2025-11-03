-- Postavljanje da korisnik sa ID 1 ima isteklu licencu
-- Ovo je za testiranje scenarija sa isteklom licencom

-- Proverite da li korisnik sa ID 1 postoji
SELECT 
    id,
    username,
    email,
    created_at,
    updated_at
FROM users 
WHERE id = 1;

-- Proverite trenutnu licencu korisnika sa ID 1
SELECT 
    l.id as license_id,
    l.user_id,
    l.start_date,
    l.end_date,
    l.is_active,
    l.notification_sent,
    l.created_at,
    l.updated_at,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
FROM licenses l
WHERE l.user_id = 1;

-- Ažuriraj licencu korisnika sa ID 1 da ističe pre 30 dana
UPDATE licenses 
SET 
    end_date = CURRENT_TIMESTAMP - INTERVAL '30 days',
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = 1;

-- Proverite da li je licenca ažurirana
SELECT 
    l.id as license_id,
    l.user_id,
    l.start_date,
    l.end_date,
    l.is_active,
    l.notification_sent,
    l.created_at,
    l.updated_at,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry,
    CASE 
        WHEN l.end_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN l.end_date BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status
FROM licenses l
WHERE l.user_id = 1;

-- Testiranje licence status endpoint-a
-- GET /api/licenses/status?userId=1
-- Očekivani odgovor:
-- {
--   "hasValidLicense": false,
--   "endDate": "2024-11-01T00:00:00",
--   "daysUntilExpiry": -30,
--   "isExpiringSoon": false
-- }

-- Testiranje licence check endpoint-a
-- GET /api/licenses/check/1
-- Očekivani odgovor:
-- {
--   "hasValidLicense": false,
--   "message": "License is invalid or expired"
-- }

-- Testiranje login-a sa isteklom licencom
-- POST /api/auth/signin
-- Očekivani odgovor: 401 Unauthorized

-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id = 1;

-- Testiranje deaktivacije istekle licence
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE user_id = 1 AND end_date < CURRENT_TIMESTAMP;

-- Proverite da li je licenca deaktivirana
SELECT 
    u.username,
    l.end_date,
    l.is_active,
    CASE 
        WHEN l.end_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN l.end_date BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.id = 1;

-- Testiranje admin endpoint-a za istekle licence
-- GET /api/licenses/admin/expiring
-- Trebalo bi da prikaže korisnika sa ID 1

-- Testiranje deaktivacije isteklih licenci
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

-- Proverite rezultate
SELECT 
    u.id,
    u.username,
    l.end_date,
    l.is_active,
    CASE 
        WHEN l.end_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN l.end_date BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.id = 1;
