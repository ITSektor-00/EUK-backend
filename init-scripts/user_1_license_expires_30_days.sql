-- Postavljanje da korisnik sa ID 1 ima licencu koja ističe za 30 dana
-- Ovo je za testiranje scenarija sa licencom koja ističe uskoro

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

-- Ažuriraj licencu korisnika sa ID 1 da ističe za 30 dana
UPDATE licenses 
SET 
    end_date = CURRENT_TIMESTAMP + INTERVAL '30 days',
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
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry,
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
--   "hasValidLicense": true,
--   "endDate": "2024-12-31T23:59:59",
--   "daysUntilExpiry": 30,
--   "isExpiringSoon": true
-- }

-- Testiranje licence check endpoint-a
-- GET /api/licenses/check/1
-- Očekivani odgovor:
-- {
--   "hasValidLicense": true,
--   "message": "License is valid"
-- }

-- Testiranje login-a sa licencom koja ističe za 30 dana
-- POST /api/auth/signin
-- Očekivani odgovor: 200 OK (login treba da radi)

-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id = 1;

-- Testiranje funkcija za licence koje treba da isteknu
SELECT * FROM get_licenses_expiring_soon() 
WHERE user_id = 1;

-- Proverite da li se korisnik prikazuje u admin panelu za licence koje ističu
-- GET /api/licenses/admin/expiring
-- Trebalo bi da prikaže korisnika sa ID 1

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
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.id = 1;
