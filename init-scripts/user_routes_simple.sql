-- Pojednostavljeni sistem za povezivanje korisnika i ruta

-- 1. Kreiraj povezujuću tabelu
CREATE TABLE IF NOT EXISTS user_routes (
    user_id INTEGER NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    can_access BOOLEAN DEFAULT TRUE,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_name) REFERENCES rute(naziv) ON DELETE CASCADE,
    
    -- Primary key - jedan korisnik može imati jedan pristup jednoj ruti
    PRIMARY KEY (user_id, route_name)
);

-- 2. Kreiraj indexe
CREATE INDEX IF NOT EXISTS idx_user_routes_user_id ON user_routes(user_id);
CREATE INDEX IF NOT EXISTS idx_user_routes_route_name ON user_routes(route_name);

-- 3. Funkcija za dodavanje pristupa
CREATE OR REPLACE FUNCTION grant_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO user_routes (user_id, route_name) 
    VALUES (user_id, route_name)
    ON CONFLICT (user_id, route_name) DO NOTHING;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 4. Funkcija za oduzimanje pristupa
CREATE OR REPLACE FUNCTION revoke_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
BEGIN
    DELETE FROM user_routes WHERE user_id = user_id AND route_name = route_name;
    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- 5. Funkcija za proveru pristupa
CREATE OR REPLACE FUNCTION can_access(user_id INTEGER, route_name VARCHAR(255)) 
RETURNS BOOLEAN AS $$
DECLARE
    user_role VARCHAR(20);
    route_requires_admin BOOLEAN;
BEGIN
    -- Proveri rolu korisnika
    SELECT role INTO user_role FROM users WHERE id = user_id;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Proveri da li ruta zahteva admin rolu
    SELECT zahteva_admin_role INTO route_requires_admin FROM rute WHERE naziv = route_name;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Ako ruta zahteva admin rolu, korisnik mora biti admin
    IF route_requires_admin AND user_role != 'ADMIN' THEN
        RETURN FALSE;
    END IF;
    
    -- Proveri eksplicitni pristup
    IF EXISTS (SELECT 1 FROM user_routes WHERE user_id = user_id AND route_name = route_name AND can_access = true) THEN
        RETURN TRUE;
    END IF;
    
    -- Ako nema eksplicitnog pristupa, dozvoli pristup EUK rutama
    RETURN NOT route_requires_admin;
END;
$$ LANGUAGE plpgsql;

-- 6. Automatski podesi pristupe za sve korisnike
-- Admin korisnici dobijaju pristup SAMO admin rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role = 'ADMIN' AND r.zahteva_admin_role = true
ON CONFLICT (user_id, route_name) DO NOTHING;

-- Obicni korisnici dobijaju pristup EUK rutama
INSERT INTO user_routes (user_id, route_name)
SELECT u.id, r.naziv
FROM users u, rute r
WHERE u.role != 'ADMIN' AND r.zahteva_admin_role = false
ON CONFLICT (user_id, route_name) DO NOTHING;

-- 7. Prikaži sve pristupe
SELECT 
    u.username,
    u.role,
    r.naziv as route,
    r.zahteva_admin_role,
    ur.can_access,
    ur.granted_at
FROM users u
JOIN user_routes ur ON u.id = ur.user_id
JOIN rute r ON ur.route_name = r.naziv
ORDER BY u.username, r.naziv;
