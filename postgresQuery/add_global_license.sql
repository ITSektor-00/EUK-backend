-- ===============================================
-- DODAVANJE GLOBALNE LICENCE U POSTGRESQL
-- ===============================================

-- 1. Kreiraj global_license tabelu ako ne postoji
CREATE TABLE IF NOT EXISTS global_license (
    id SERIAL PRIMARY KEY,
    license_key VARCHAR(255) NOT NULL UNIQUE,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    notification_sent BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Dodaj globalnu licencu koja traje godinu dana od danas
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

-- 3. Prikaži globalnu licencu
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

-- 4. Resetuj sekvencu za global_license tabelu
SELECT setval('global_license_id_seq', COALESCE((SELECT MAX(id) FROM global_license), 0) + 1, false);

-- 5. Status
SELECT
    'GLOBAL_LICENSE_ADDED' as status,
    'Global license added successfully' as message,
    CURRENT_TIMESTAMP as timestamp;
