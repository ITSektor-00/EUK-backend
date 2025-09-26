-- Popravi licenses tabelu - dodaj unique constraint na user_id
-- Pokreni ovo u svojoj PostgreSQL bazi

-- 1. Proveri trenutnu strukturu licenses tabele
SELECT 'Trenutna struktura licenses tabele:' as info;
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'licenses'
ORDER BY ordinal_position;

-- 2. Proveri da li postoji unique constraint na user_id
SELECT 'Postojeći constraint-ovi:' as info;
SELECT constraint_name, constraint_type, column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'licenses';

-- 3. Dodaj unique constraint na user_id (svaki korisnik može imati samo jednu licencu)
ALTER TABLE licenses ADD CONSTRAINT unique_user_license UNIQUE (user_id);

-- 4. Sada dodaj licence za postojeće korisnike
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

-- 5. Dodaj licencu za korisnika sa ID 2
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

-- 6. Proveri da li su licence dodane
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
