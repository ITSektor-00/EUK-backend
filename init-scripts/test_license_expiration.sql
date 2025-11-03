-- Test podaci za licencni sistem - korisnik sa licencom koja ističe za mesec dana
-- Ovo je samo za testiranje, ne koristiti u produkciji

-- Kreiranje test korisnika sa licencom koja ističe za 30 dana
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
    'testuser_license_expires_30_days', 
    'test.license.30days@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: testpass123
    'Test', 
    'User', 
    'korisnik', 
    true, 
    CURRENT_TIMESTAMP - INTERVAL '11 months', -- kreiran pre 11 meseci
    CURRENT_TIMESTAMP - INTERVAL '11 months'
) ON CONFLICT (username) DO NOTHING;

-- Kreiranje licence koja ističe za 30 dana
INSERT INTO licenses (
    user_id, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    (SELECT id FROM users WHERE username = 'testuser_license_expires_30_days'), 
    CURRENT_TIMESTAMP - INTERVAL '11 months', -- počela pre 11 meseci
    CURRENT_TIMESTAMP + INTERVAL '30 days', -- ističe za 30 dana
    true, 
    false, -- obaveštenje nije poslato
    CURRENT_TIMESTAMP - INTERVAL '11 months', 
    CURRENT_TIMESTAMP - INTERVAL '11 months'
) ON CONFLICT DO NOTHING;

-- Kreiranje test korisnika sa licencom koja ističe za 15 dana (hitno)
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
    'testuser_license_expires_15_days', 
    'test.license.15days@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: testpass123
    'Test', 
    'User15', 
    'korisnik', 
    true, 
    CURRENT_TIMESTAMP - INTERVAL '11 months 15 days', 
    CURRENT_TIMESTAMP - INTERVAL '11 months 15 days'
) ON CONFLICT (username) DO NOTHING;

-- Kreiranje licence koja ističe za 15 dana
INSERT INTO licenses (
    user_id, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    (SELECT id FROM users WHERE username = 'testuser_license_expires_15_days'), 
    CURRENT_TIMESTAMP - INTERVAL '11 months 15 days', 
    CURRENT_TIMESTAMP + INTERVAL '15 days', -- ističe za 15 dana
    true, 
    false, 
    CURRENT_TIMESTAMP - INTERVAL '11 months 15 days', 
    CURRENT_TIMESTAMP - INTERVAL '11 months 15 days'
) ON CONFLICT DO NOTHING;

-- Kreiranje test korisnika sa licencom koja ističe za 5 dana (vrlo hitno)
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
    'testuser_license_expires_5_days', 
    'test.license.5days@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: testpass123
    'Test', 
    'User5', 
    'korisnik', 
    true, 
    CURRENT_TIMESTAMP - INTERVAL '11 months 25 days', 
    CURRENT_TIMESTAMP - INTERVAL '11 months 25 days'
) ON CONFLICT (username) DO NOTHING;

-- Kreiranje licence koja ističe za 5 dana
INSERT INTO licenses (
    user_id, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    (SELECT id FROM users WHERE username = 'testuser_license_expires_5_days'), 
    CURRENT_TIMESTAMP - INTERVAL '11 months 25 days', 
    CURRENT_TIMESTAMP + INTERVAL '5 days', -- ističe za 5 dana
    true, 
    false, 
    CURRENT_TIMESTAMP - INTERVAL '11 months 25 days', 
    CURRENT_TIMESTAMP - INTERVAL '11 months 25 days'
) ON CONFLICT DO NOTHING;

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
    'testuser_license_expired', 
    'test.license.expired@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: testpass123
    'Test', 
    'Expired', 
    'korisnik', 
    true, 
    CURRENT_TIMESTAMP - INTERVAL '13 months', 
    CURRENT_TIMESTAMP - INTERVAL '13 months'
) ON CONFLICT (username) DO NOTHING;

-- Kreiranje istekle licence
INSERT INTO licenses (
    user_id, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    (SELECT id FROM users WHERE username = 'testuser_license_expired'), 
    CURRENT_TIMESTAMP - INTERVAL '13 months', 
    CURRENT_TIMESTAMP - INTERVAL '1 month', -- istekla pre mesec dana
    true, -- još uvek aktivna (treba da bude deaktivirana)
    false, 
    CURRENT_TIMESTAMP - INTERVAL '13 months', 
    CURRENT_TIMESTAMP - INTERVAL '13 months'
) ON CONFLICT DO NOTHING;

-- Proverite da li su podaci kreirani
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
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
FROM users u
LEFT JOIN licenses l ON u.id = l.user_id
WHERE u.username LIKE 'testuser_license%'
ORDER BY l.end_date;

-- Testiranje licence status endpoint-a
-- Možete testirati sa:
-- GET /api/licenses/status?userId=<user_id>
-- GET /api/licenses/check/<user_id>

-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() WHERE user_id IN (
    SELECT id FROM users WHERE username LIKE 'testuser_license%'
);

-- Testiranje funkcija za licence koje treba da isteknu
SELECT * FROM get_licenses_expiring_soon();

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
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.username LIKE 'testuser_license%'
ORDER BY l.end_date;
