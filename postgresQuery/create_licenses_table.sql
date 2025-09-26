-- Kreiranje licenses tabele za licencni sistem
CREATE TABLE IF NOT EXISTS licenses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    notification_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint (ako postoji users tabela)
    CONSTRAINT fk_license_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index za brže pretrage
CREATE INDEX IF NOT EXISTS idx_licenses_user_id ON licenses(user_id);
CREATE INDEX IF NOT EXISTS idx_licenses_end_date ON licenses(end_date);
CREATE INDEX IF NOT EXISTS idx_licenses_is_active ON licenses(is_active);

-- Trigger za automatsko ažuriranje updated_at
CREATE OR REPLACE FUNCTION update_licenses_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_licenses_updated_at
    BEFORE UPDATE ON licenses
    FOR EACH ROW
    EXECUTE FUNCTION update_licenses_updated_at();

-- Funkcija za proveru važenja licence
CREATE OR REPLACE FUNCTION check_license_validity()
RETURNS TABLE (
    license_id INT,
    user_id INT,
    is_expired BOOLEAN,
    days_until_expiry INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        l.id,
        l.user_id,
        (CURRENT_TIMESTAMP > l.end_date) as is_expired,
        EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
    FROM licenses l
    WHERE l.is_active = true;
END;
$$ LANGUAGE plpgsql;

-- Funkcija za dobijanje licenci koje treba da isteknu za 30 dana
CREATE OR REPLACE FUNCTION get_licenses_expiring_soon()
RETURNS TABLE (
    license_id INT,
    user_id INT,
    end_date TIMESTAMP,
    days_until_expiry INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        l.id,
        l.user_id,
        l.end_date,
        EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
    FROM licenses l
    WHERE l.is_active = true 
    AND l.end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')
    AND l.notification_sent = false;
END;
$$ LANGUAGE plpgsql;
