-- Setup početnih licenci za testiranje
-- Ovo pokreni nakon što si kreirao licenses tabelu

-- 1. Kreiranje test korisnika (ako ne postoje)
-- Prvo proveri da li već postoje korisnici sa ID-jem 1, 2, 3
-- Ako ne postoje, kreiraj ih:

INSERT INTO users (id, username, email, password, created_at) VALUES 
(1, 'testuser1', 'test1@example.com', 'password123', CURRENT_TIMESTAMP),
(2, 'testuser2', 'test2@example.com', 'password123', CURRENT_TIMESTAMP),
(3, 'testuser3', 'test3@example.com', 'password123', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. Kreiranje test licenci
-- Licenca koja je važeća (istekne za 6 meseci)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false);

-- Licenca koja će isteći za 15 dana (treba obaveštenje)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(2, CURRENT_TIMESTAMP - INTERVAL '11 months 15 days', CURRENT_TIMESTAMP + INTERVAL '15 days', true, false);

-- Licenca koja će isteći za 5 dana (hitno obaveštenje)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(3, CURRENT_TIMESTAMP - INTERVAL '11 months 25 days', CURRENT_TIMESTAMP + INTERVAL '5 days', true, false);

-- 3. Proveri da li su licence kreirane
SELECT 
    l.id,
    l.user_id,
    l.start_date,
    l.end_date,
    l.is_active,
    l.notification_sent,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP)) as days_until_expiry
FROM licenses l
ORDER BY l.user_id;

-- 4. Testiranje funkcija
SELECT 'Licence koje treba da isteknu:' as info;
SELECT * FROM get_licenses_expiring_soon();

SELECT 'Status svih licenci:' as info;
SELECT * FROM check_license_validity();
