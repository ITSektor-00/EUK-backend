-- Kreiranje admin korisnika i postavljanje globalne licence na godinu dana
-- Datum kreiranja: 10. oktobar 2025
-- Licenca važi do: 10. oktobar 2026

-- 1. Kreiraj ADMIN korisnika
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active)
VALUES (
    'admin',
    'admin@euk.rs',
    '$2b$10$vRCKl2tcEsf7guDN7hkLleKtws1VAHiT6S9DIG20vTGtNgl4D.64i',
    'Admin',
    'Administrator',
    'admin',
    true
) ON CONFLICT (username) DO UPDATE SET
    email = EXCLUDED.email,
    password_hash = EXCLUDED.password_hash,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active;

-- 2. Postavi globalnu licencu koja traje godinu dana od danas
INSERT INTO global_license (id, license_key, start_date, end_date, is_active, notification_sent) 
VALUES (
    1,
    'EUK-GLOBAL-LICENSE-2025-2026',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    false
) ON CONFLICT (id) DO UPDATE SET
    license_key = EXCLUDED.license_key,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    is_active = EXCLUDED.is_active,
    notification_sent = EXCLUDED.notification_sent;

-- 3. Prikaži kreiranog admin korisnika
SELECT 
    'Kreiran korisnik:' as info,
    id,
    username,
    email,
    role,
    is_active,
    created_at
FROM users 
WHERE username = 'admin';

-- 4. Prikaži globalnu licencu
SELECT 
    'Globalna licenca:' as info,
    id,
    license_key,
    start_date,
    end_date,
    is_active,
    EXTRACT(DAY FROM (end_date - CURRENT_TIMESTAMP))::INT as days_remaining
FROM global_license
WHERE id = 1;

-- 5. Resetuj sekvencu za users tabelu
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 0) + 1, false);

-- 6. Resetuj sekvencu za global_license tabelu
SELECT setval('global_license_id_seq', COALESCE((SELECT MAX(id) FROM global_license), 0) + 1, false);

