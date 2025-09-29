-- Kreiranje global_license tabele za softversku licencu
-- Ova tabela će imati samo jedan red - globalnu licencu za ceo softver

-- 1. Obriši postojeću licenses tabelu (opciono)
-- DROP TABLE IF EXISTS licenses CASCADE;

-- 2. Kreiraj global_license tabelu
CREATE TABLE IF NOT EXISTS global_license (
    id SERIAL PRIMARY KEY,
    license_key VARCHAR(255) UNIQUE NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    notification_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Ograniči tabelu na samo jedan red
    CONSTRAINT single_global_license CHECK (id = 1)
);

-- 3. Kreiraj indexe
CREATE INDEX IF NOT EXISTS idx_global_license_active ON global_license(is_active);
CREATE INDEX IF NOT EXISTS idx_global_license_end_date ON global_license(end_date);

-- 4. Trigger za automatsko ažuriranje updated_at
CREATE OR REPLACE FUNCTION update_global_license_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_global_license_updated_at
    BEFORE UPDATE ON global_license
    FOR EACH ROW
    EXECUTE FUNCTION update_global_license_updated_at();

-- 5. Funkcija za proveru globalne licence
CREATE OR REPLACE FUNCTION check_global_license_validity()
RETURNS TABLE (
    license_id INT,
    license_key VARCHAR,
    is_expired BOOLEAN,
    days_until_expiry INT,
    is_expiring_soon BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gl.id,
        gl.license_key,
        (CURRENT_TIMESTAMP > gl.end_date) as is_expired,
        EXTRACT(DAY FROM (gl.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry,
        (gl.end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')) as is_expiring_soon
    FROM global_license gl
    WHERE gl.is_active = true
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- 6. Funkcija za dobijanje globalne licence koja treba da istekne
CREATE OR REPLACE FUNCTION get_global_license_expiring_soon()
RETURNS TABLE (
    license_id INT,
    license_key VARCHAR,
    end_date TIMESTAMP,
    days_until_expiry INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gl.id,
        gl.license_key,
        gl.end_date,
        EXTRACT(DAY FROM (gl.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
    FROM global_license gl
    WHERE gl.is_active = true 
    AND gl.end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')
    AND gl.notification_sent = false
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- 7. Kreiraj početnu globalnu licencu (12 meseci)
INSERT INTO global_license (id, license_key, start_date, end_date, is_active, notification_sent) 
VALUES (
    1,
    'GLOBAL-LICENSE-2024',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '12 months',
    true,
    false
) ON CONFLICT (id) DO UPDATE SET
    license_key = EXCLUDED.license_key,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    is_active = EXCLUDED.is_active,
    notification_sent = EXCLUDED.notification_sent;

-- 8. Proveri da li je globalna licenca kreirana
SELECT 'Globalna licenca:' as info;
SELECT * FROM check_global_license_validity();
