-- Test podaci za licencni sistem
-- Ovo je samo za testiranje, ne koristiti u produkciji

-- Kreiranje test korisnika (ako ne postoje)
INSERT INTO users (id, username, email, password) VALUES 
(1, 'testuser1', 'test1@example.com', 'password123'),
(2, 'testuser2', 'test2@example.com', 'password123'),
(3, 'testuser3', 'test3@example.com', 'password123')
ON CONFLICT (id) DO NOTHING;

-- Kreiranje test licenci
-- Licenca koja je važeća (istekne za 6 meseci)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false);

-- Licenca koja će isteći za 15 dana (treba obaveštenje)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(2, CURRENT_TIMESTAMP - INTERVAL '11 months 15 days', CURRENT_TIMESTAMP + INTERVAL '15 days', true, false);

-- Licenca koja će isteći za 5 dana (hitno obaveštenje)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(3, CURRENT_TIMESTAMP - INTERVAL '11 months 25 days', CURRENT_TIMESTAMP + INTERVAL '5 days', true, false);

-- Licenca koja je istekla (treba da bude deaktivirana)
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(4, CURRENT_TIMESTAMP - INTERVAL '13 months', CURRENT_TIMESTAMP - INTERVAL '1 month', true, false);

-- Licenca koja je već deaktivirana
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) VALUES 
(5, CURRENT_TIMESTAMP - INTERVAL '15 months', CURRENT_TIMESTAMP - INTERVAL '3 months', false, true);

-- Testiranje funkcija
SELECT * FROM check_license_validity();
SELECT * FROM get_licenses_expiring_soon();

-- Testiranje ažuriranja isteklih licenci
UPDATE licenses SET is_active = false WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

-- Testiranje označavanja obaveštenja
UPDATE licenses SET notification_sent = true WHERE id IN (
    SELECT id FROM licenses WHERE end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')
);
