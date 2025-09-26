-- Dodaj licence za postojeće korisnike iz users tabele
-- Pokreni ovo u svojoj PostgreSQL bazi

-- 1. Prvo proveri koji korisnici postoje
SELECT 'Postojeći korisnici:' as info;
SELECT id, username, email, first_name, last_name 
FROM users 
ORDER BY id 
LIMIT 10;

-- 2. Dodaj licence za korisnika sa ID 1 (ako postoji)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) 
VALUES (
    1, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP + INTERVAL '6 months', 
    true, 
    false
) ON CONFLICT (user_id) DO UPDATE SET
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    is_active = EXCLUDED.is_active,
    notification_sent = EXCLUDED.notification_sent;

-- 3. Dodaj licence za korisnika sa ID 2 (ako postoji)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) 
VALUES (
    2, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP + INTERVAL '6 months', 
    true, 
    false
) ON CONFLICT (user_id) DO UPDATE SET
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    is_active = EXCLUDED.is_active,
    notification_sent = EXCLUDED.notification_sent;

-- 4. Proveri da li su licence dodane
SELECT 'Dodane licence:' as info;
SELECT 
    l.id as license_id,
    l.user_id,
    u.username,
    u.email,
    l.start_date,
    l.end_date,
    l.is_active,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP)) as days_until_expiry
FROM licenses l
JOIN users u ON l.user_id = u.id
WHERE l.user_id IN (1, 2)
ORDER BY l.user_id;

-- 5. Ako hoćeš da dodaš licence za sve postojeće korisnike:
-- INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent)
-- SELECT 
--     u.id,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP + INTERVAL '6 months',
--     true,
--     false
-- FROM users u
-- WHERE u.id NOT IN (SELECT user_id FROM licenses)
-- ON CONFLICT (user_id) DO NOTHING;
