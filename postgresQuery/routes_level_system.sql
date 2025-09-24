-- 1. Kreiraj rute tabelu sa nivoima
CREATE TABLE IF NOT EXISTS rute (
    id SERIAL PRIMARY KEY,
    ruta VARCHAR(255) NOT NULL UNIQUE,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT,
    sekcija VARCHAR(100),
    nivo_min INTEGER NOT NULL DEFAULT 1,
    nivo_max INTEGER NOT NULL DEFAULT 10,
    aktivna BOOLEAN NOT NULL DEFAULT true,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Ažuriraj users tabelu da ima nivo pristupa
ALTER TABLE users ADD COLUMN IF NOT EXISTS nivo_pristupa INTEGER DEFAULT 1;

-- 3. Ažuriraj user_routes tabelu da koristi nivo umesto boolean
DROP TABLE IF EXISTS user_routes CASCADE;

CREATE TABLE user_routes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    nivo_dozvola INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES rute(id) ON DELETE CASCADE,
    UNIQUE (user_id, route_id)
);

-- 4. Kreiraj indexe
CREATE INDEX idx_rute_sekcija ON rute(sekcija);
CREATE INDEX idx_rute_aktivna ON rute(aktivna);
CREATE INDEX idx_rute_nivo_min ON rute(nivo_min);
CREATE INDEX idx_rute_nivo_max ON rute(nivo_max);

CREATE INDEX idx_user_routes_user_id ON user_routes(user_id);
CREATE INDEX idx_user_routes_route_id ON user_routes(route_id);
CREATE INDEX idx_user_routes_nivo ON user_routes(nivo_dozvola);

CREATE INDEX idx_users_nivo_pristupa ON users(nivo_pristupa);

-- 5. Dodaj trigger za automatsko ažuriranje updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_routes_updated_at 
    BEFORE UPDATE ON user_routes 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 6. Ubaci osnovne rute
INSERT INTO rute (ruta, naziv, opis, sekcija, nivo_min, nivo_max) VALUES 
    ('euk/kategorije', 'Kategorije', 'Upravljanje kategorijama predmeta', 'EUK', 1, 5),
    ('euk/predmeti', 'Predmeti', 'Upravljanje predmetima', 'EUK', 1, 5),
    ('euk/ugrozena-lica', 'Ugrožena lica', 'Upravljanje ugroženim licima', 'EUK', 1, 5),
    ('euk/stampanje', 'Štampanje', 'Štampanje dokumenata', 'EUK', 2, 5),
    ('reports', 'Izveštaji', 'Generisanje izveštaja', 'ADMIN', 3, 5),
    ('analytics', 'Analitika', 'Analitički podaci', 'ADMIN', 4, 5),
    ('settings', 'Podešavanja', 'Sistemska podešavanja', 'ADMIN', 5, 5),
    ('profile', 'Profil', 'Korisnički profil', 'USER', 1, 5)
ON CONFLICT (ruta) DO NOTHING;

-- 7. Postavi nivo pristupa za postojeće korisnike
UPDATE users SET nivo_pristupa = 5 WHERE role = 'admin';
UPDATE users SET nivo_pristupa = 3 WHERE role = 'obradjivaci predmeta';
UPDATE users SET nivo_pristupa = 2 WHERE role = 'potpisnik';

-- 8. Funkcija za proveru pristupa
CREATE OR REPLACE FUNCTION can_user_access_route(
    p_user_id INTEGER,
    p_route_id INTEGER
) RETURNS BOOLEAN AS $$
DECLARE
    v_user_nivo INTEGER;
    v_route_nivo_min INTEGER;
    v_route_nivo_max INTEGER;
    v_route_aktivna BOOLEAN;
    v_user_nivo_dozvola INTEGER;
BEGIN
    -- Proveri da li korisnik postoji i dohvati njegov nivo
    SELECT nivo_pristupa INTO v_user_nivo
    FROM users WHERE id = p_user_id;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Korisnik ne postoji
    END IF;
    
    -- Proveri da li ruta postoji i dohvati njene nivoe
    SELECT nivo_min, nivo_max, aktivna INTO v_route_nivo_min, v_route_nivo_max, v_route_aktivna
    FROM rute WHERE id = p_route_id;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Ruta ne postoji
    END IF;
    
    -- Proveri da li je ruta aktivna
    IF NOT v_route_aktivna THEN
        RETURN FALSE;
    END IF;
    
    -- Proveri eksplicitnu dozvolu u user_routes
    SELECT nivo_dozvola INTO v_user_nivo_dozvola
    FROM user_routes 
    WHERE user_id = p_user_id AND route_id = p_route_id;
    
    -- Ako postoji eksplicitna dozvola, koristi je
    IF v_user_nivo_dozvola IS NOT NULL THEN
        RETURN v_user_nivo_dozvola >= v_route_nivo_min AND v_user_nivo_dozvola <= v_route_nivo_max;
    END IF;
    
    -- Ako nema eksplicitne dozvole, koristi nivo korisnika
    RETURN v_user_nivo >= v_route_nivo_min AND v_user_nivo <= v_route_nivo_max;
END;
$$ LANGUAGE plpgsql;

-- 9. Funkcija za dodeljivanje nivoa pristupa
CREATE OR REPLACE FUNCTION grant_route_access(
    p_user_id INTEGER,
    p_route_id INTEGER,
    p_nivo_dozvola INTEGER
) RETURNS BOOLEAN AS $$
BEGIN
    -- Proveri da li korisnik postoji
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'Korisnik sa ID % ne postoji', p_user_id;
    END IF;
    
    -- Proveri da li ruta postoji
    IF NOT EXISTS (SELECT 1 FROM rute WHERE id = p_route_id) THEN
        RAISE EXCEPTION 'Ruta sa ID % ne postoji', p_route_id;
    END IF;
    
    -- Dodaj ili ažuriraj pristup
    INSERT INTO user_routes (user_id, route_id, nivo_dozvola)
    VALUES (p_user_id, p_route_id, p_nivo_dozvola)
    ON CONFLICT (user_id, route_id) 
    DO UPDATE SET
        nivo_dozvola = p_nivo_dozvola;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
