-- Rekreiraj licenses tabelu sa ispravnim constraint-ovima
-- Pokreni ovo u svojoj PostgreSQL bazi

-- 1. Obriši postojeću licenses tabelu (OPREZ: ovo će obrisati sve podatke!)
DROP TABLE IF EXISTS licenses CASCADE;

-- 2. Kreiraj licenses tabelu sa unique constraint na user_id
CREATE TABLE licenses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,  -- Svaki korisnik može imati samo jednu licencu
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    notification_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_license_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Kreiraj indexe
CREATE INDEX IF NOT EXISTS idx_licenses_user_id ON licenses(user_id);
CREATE INDEX IF NOT EXISTS idx_licenses_end_date ON licenses(end_date);
CREATE INDEX IF NOT EXISTS idx_licenses_is_active ON licenses(is_active);

-- 4. Dodaj licence za postojeće korisnike
INSERT INTO licenses (user_id, start_date, end_date, is_active, notification_sent) 
VALUES 
    (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false),
    (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '6 months', true, false);

-- 5. Proveri rezultat
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
ORDER BY l.user_id;
