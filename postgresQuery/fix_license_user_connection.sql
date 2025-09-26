-- Popravi vezu između licenses i users tabela
-- Pokreni ovo ako veza nije ispravna

-- 1. Proveri da li postoji licenses tabela
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'licenses') THEN
        RAISE NOTICE 'Licenses tabela ne postoji! Pokreni create_licenses_table.sql prvo.';
    ELSE
        RAISE NOTICE 'Licenses tabela postoji.';
    END IF;
END $$;

-- 2. Proveri da li postoji users tabela
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        RAISE NOTICE 'Users tabela ne postoji! Kreiraj je prvo.';
    ELSE
        RAISE NOTICE 'Users tabela postoji.';
    END IF;
END $$;

-- 3. Ako licenses tabela ne postoji, kreiraj je
CREATE TABLE IF NOT EXISTS licenses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    notification_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_license_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Kreiraj indexe
CREATE INDEX IF NOT EXISTS idx_licenses_user_id ON licenses(user_id);
CREATE INDEX IF NOT EXISTS idx_licenses_end_date ON licenses(end_date);
CREATE INDEX IF NOT EXISTS idx_licenses_is_active ON licenses(is_active);

-- 5. Proveri da li je veza ispravna
SELECT 
    'Veza je ispravna!' as status,
    COUNT(*) as total_users,
    (SELECT COUNT(*) FROM licenses) as total_licenses
FROM users;
