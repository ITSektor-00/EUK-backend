-- Test primer za isteklu licencu
-- Kreiranje korisnika sa licencom koja je istekla pre 30 dana

-- Kreiranje test korisnika sa isteklom licencom
INSERT INTO users (
    username, 
    email, 
    password_hash, 
    first_name, 
    last_name, 
    role, 
    is_active, 
    created_at, 
    updated_at
) VALUES (
    'testuser_expired_license', 
    'test.expired@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: testpass123
    'Test', 
    'Expired', 
    'korisnik', 
    true, 
    CURRENT_TIMESTAMP - INTERVAL '13 months', -- kreiran pre 13 meseci
    CURRENT_TIMESTAMP - INTERVAL '13 months'
) ON CONFLICT (username) DO NOTHING;

-- Kreiranje istekle licence (istekla pre 30 dana)
INSERT INTO licenses (
    user_id, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    (SELECT id FROM users WHERE username = 'testuser_expired_license'), 
    CURRENT_TIMESTAMP - INTERVAL '13 months', -- počela pre 13 meseci
    CURRENT_TIMESTAMP - INTERVAL '30 days', -- istekla pre 30 dana
    true, -- još uvek aktivna (treba da bude deaktivirana)
    false, 
    CURRENT_TIMESTAMP - INTERVAL '13 months', 
    CURRENT_TIMESTAMP - INTERVAL '13 months'
) ON CONFLICT DO NOTHING;

-- Proverite da li je korisnik kreiran
SELECT 
    u.id,
    u.username,
    u.email,
    u.created_at,
    u.updated_at,
    l.id as license_id,
    l.start_date,
    l.end_date,
    l.is_active,
    l.notification_sent,
    l.created_at as license_created_at,
    l.updated_at as license_updated_at,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM users u
LEFT JOIN licenses l ON u.id = l.user_id
WHERE u.username = 'testuser_expired_license';

-- Testiranje licence status endpoint-a
-- GET /api/licenses/status?userId=<user_id>
-- Očekivani odgovor:
-- {
--   "hasValidLicense": false,
--   "endDate": "2024-11-01T00:00:00",
--   "daysUntilExpiry": -30,
--   "isExpiringSoon": false
-- }

-- Testiranje licence check endpoint-a
-- GET /api/licenses/check/<user_id>
-- Očekivani odgovor:
-- {
--   "hasValidLicense": false,
--   "message": "License is invalid or expired"
-- }

-- Testiranje login-a sa isteklom licencom
-- POST /api/auth/signin
-- {
--   "usernameOrEmail": "testuser_expired_license",
--   "password": "testpass123"
-- }
-- Očekivani odgovor: 401 Unauthorized

-- Testiranje deaktivacije istekle licence
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

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
WHERE u.username = 'testuser_expired_license';

-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id = (SELECT id FROM users WHERE username = 'testuser_expired_license');

-- Testiranje funkcija za istekle licence
SELECT * FROM get_licenses_expiring_soon() 
WHERE user_id = (SELECT id FROM users WHERE username = 'testuser_expired_license');

-- Testiranje deaktivacije isteklih licenci
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

-- Proverite rezultate
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
WHERE u.username = 'testuser_expired_license';
