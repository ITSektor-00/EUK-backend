-- Dodaj test korisnike sa ID 1 i 2
-- Pokreni ovo u svojoj PostgreSQL bazi

-- 1. Dodaj korisnika sa ID 1
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at) 
VALUES (
    1, 
    'testuser1', 
    'test1@example.com', 
    'password123', 
    'Test', 
    'User1', 
    'USER', 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    email = EXCLUDED.email,
    password_hash = EXCLUDED.password_hash,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    updated_at = CURRENT_TIMESTAMP;

-- 2. Dodaj korisnika sa ID 2
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at) 
VALUES (
    2, 
    'testuser2', 
    'test2@example.com', 
    'password123', 
    'Test', 
    'User2', 
    'USER', 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    email = EXCLUDED.email,
    password_hash = EXCLUDED.password_hash,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    updated_at = CURRENT_TIMESTAMP;

-- 3. Proveri da li su korisnici dodati
SELECT id, username, email, first_name, last_name, role, is_active 
FROM users 
WHERE id IN (1, 2)
ORDER BY id;

-- 4. Dodaj licence za ove korisnike
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) 
VALUES 
    (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false),
    (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false)
ON CONFLICT (user_id) DO UPDATE SET
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    is_active = EXCLUDED.is_active,
    notification_sent = EXCLUDED.notification_sent;

-- 5. Proveri da li su licence dodane
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
