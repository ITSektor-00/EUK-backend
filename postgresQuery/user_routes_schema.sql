-- Kreiraj povezujuću tabelu za korisnike i rute
CREATE TABLE IF NOT EXISTS user_routes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    can_access BOOLEAN DEFAULT TRUE,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by INTEGER, -- ID korisnika koji je dao pristup
    expires_at TIMESTAMP, -- Opciono: datum isteka pristupa
    notes TEXT, -- Opciono: napomene o pristupu
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_name) REFERENCES rute(naziv) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Unique constraint - jedan korisnik može imati jedan pristup jednoj ruti
    UNIQUE(user_id, route_name)
);

-- Kreiraj indexe za brže pretrage
CREATE INDEX IF NOT EXISTS idx_user_routes_user_id ON user_routes(user_id);
CREATE INDEX IF NOT EXISTS idx_user_routes_route_name ON user_routes(route_name);
CREATE INDEX IF NOT EXISTS idx_user_routes_can_access ON user_routes(can_access);

-- Kreiraj view za pregled pristupa korisnika
CREATE OR REPLACE VIEW user_route_access AS
SELECT 
    u.id as user_id,
    u.username,
    u.first_name,
    u.last_name,
    u.role,
    ur.route_name,
    r.zahteva_admin_role as route_requires_admin,
    ur.can_access,
    ur.granted_at,
    ur.expires_at,
    ur.notes,
    CASE 
        WHEN ur.expires_at IS NOT NULL AND ur.expires_at < CURRENT_TIMESTAMP 
        THEN 'EXPIRED'
        WHEN ur.can_access = false 
        THEN 'DENIED'
        WHEN r.zahteva_admin_role = true AND u.role != 'ADMIN' 
        THEN 'INSUFFICIENT_ROLE'
        ELSE 'ACCESSIBLE'
    END as access_status
FROM users u
CROSS JOIN rute r
LEFT JOIN user_routes ur ON u.id = ur.user_id AND r.naziv = ur.route_name
ORDER BY u.username, r.naziv;

-- Kreiraj view za admin rute koje korisnik može pristupiti
CREATE OR REPLACE VIEW user_admin_routes AS
SELECT 
    u.id as user_id,
    u.username,
    u.first_name,
    u.last_name,
    u.role,
    r.naziv as route_name,
    ur.can_access,
    ur.granted_at,
    ur.expires_at
FROM users u
JOIN user_routes ur ON u.id = ur.user_id
JOIN rute r ON ur.route_name = r.naziv
WHERE r.zahteva_admin_role = true
    AND ur.can_access = true
    AND (ur.expires_at IS NULL OR ur.expires_at > CURRENT_TIMESTAMP)
ORDER BY u.username, r.naziv;

-- Kreiraj view za sve rute koje korisnik može pristupiti
CREATE OR REPLACE VIEW user_accessible_routes AS
SELECT 
    u.id as user_id,
    u.username,
    u.first_name,
    u.last_name,
    u.role,
    r.naziv as route_name,
    r.zahteva_admin_role,
    ur.can_access,
    ur.granted_at,
    ur.expires_at
FROM users u
JOIN user_routes ur ON u.id = ur.user_id
JOIN rute r ON ur.route_name = r.naziv
WHERE ur.can_access = true
    AND (ur.expires_at IS NULL OR ur.expires_at > CURRENT_TIMESTAMP)
ORDER BY u.username, r.naziv;

-- Funkcija za dodavanje pristupa korisniku
CREATE OR REPLACE FUNCTION grant_route_access(
    p_user_id INTEGER,
    p_route_name VARCHAR(255),
    p_granted_by INTEGER DEFAULT NULL,
    p_expires_at TIMESTAMP DEFAULT NULL,
    p_notes TEXT DEFAULT NULL
) RETURNS BOOLEAN AS $$
BEGIN
    -- Proveri da li korisnik postoji
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'Korisnik sa ID % ne postoji', p_user_id;
    END IF;
    
    -- Proveri da li ruta postoji
    IF NOT EXISTS (SELECT 1 FROM rute WHERE naziv = p_route_name) THEN
        RAISE EXCEPTION 'Ruta % ne postoji', p_route_name;
    END IF;
    
    -- Proveri da li korisnik koji daje pristup postoji
    IF p_granted_by IS NOT NULL AND NOT EXISTS (SELECT 1 FROM users WHERE id = p_granted_by) THEN
        RAISE EXCEPTION 'Korisnik koji daje pristup sa ID % ne postoji', p_granted_by;
    END IF;
    
    -- Dodaj ili ažuriraj pristup
    INSERT INTO user_routes (user_id, route_name, granted_by, expires_at, notes)
    VALUES (p_user_id, p_route_name, p_granted_by, p_expires_at, p_notes)
    ON CONFLICT (user_id, route_name) 
    DO UPDATE SET
        can_access = TRUE,
        granted_by = p_granted_by,
        expires_at = p_expires_at,
        notes = p_notes;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Funkcija za oduzimanje pristupa korisniku
CREATE OR REPLACE FUNCTION revoke_route_access(
    p_user_id INTEGER,
    p_route_name VARCHAR(255)
) RETURNS BOOLEAN AS $$
BEGIN
    UPDATE user_routes 
    SET can_access = FALSE 
    WHERE user_id = p_user_id AND route_name = p_route_name;
    
    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Funkcija za proveru da li korisnik može pristupiti ruti
CREATE OR REPLACE FUNCTION can_user_access_route(
    p_user_id INTEGER,
    p_route_name VARCHAR(255)
) RETURNS BOOLEAN AS $$
DECLARE
    v_route_requires_admin BOOLEAN;
    v_user_role VARCHAR(20);
    v_has_explicit_access BOOLEAN;
    v_access_expired BOOLEAN;
BEGIN
    -- Proveri da li ruta zahteva admin rolu
    SELECT zahteva_admin_role INTO v_route_requires_admin
    FROM rute WHERE naziv = p_route_name;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Ruta ne postoji
    END IF;
    
    -- Proveri rolu korisnika
    SELECT role INTO v_user_role
    FROM users WHERE id = p_user_id;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Korisnik ne postoji
    END IF;
    
    -- Proveri eksplicitni pristup
    SELECT 
        can_access,
        (expires_at IS NOT NULL AND expires_at < CURRENT_TIMESTAMP)
    INTO v_has_explicit_access, v_access_expired
    FROM user_routes 
    WHERE user_id = p_user_id AND route_name = p_route_name;
    
    -- Ako ruta zahteva admin rolu, korisnik mora biti admin
    IF v_route_requires_admin AND v_user_role != 'ADMIN' THEN
        RETURN FALSE;
    END IF;
    
    -- Ako postoji eksplicitni pristup, proveri da li je aktivan
    IF v_has_explicit_access IS NOT NULL THEN
        RETURN v_has_explicit_access AND NOT v_access_expired;
    END IF;
    
    -- Ako nema eksplicitnog pristupa, dozvoli pristup ako ruta ne zahteva admin rolu
    RETURN NOT v_route_requires_admin;
END;
$$ LANGUAGE plpgsql;

-- Ubaci početne pristupe za admin korisnike
-- Ova funkcija će dati pristup svim rutama admin korisnicima
CREATE OR REPLACE FUNCTION setup_admin_access() RETURNS VOID AS $$
DECLARE
    admin_user RECORD;
    route_record RECORD;
BEGIN
    -- Za svakog admin korisnika
    FOR admin_user IN SELECT id FROM users WHERE role = 'ADMIN' LOOP
        -- Daj pristup svim rutama
        FOR route_record IN SELECT naziv FROM rute LOOP
            PERFORM grant_route_access(
                admin_user.id, 
                route_record.naziv, 
                admin_user.id, 
                NULL, -- Ne ističe
                'Automatski pristup za admin korisnika'
            );
        END LOOP;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Ubaci početne pristupe za obične korisnike
-- Ova funkcija će dati pristup EUK rutama običnim korisnicima
CREATE OR REPLACE FUNCTION setup_user_access() RETURNS VOID AS $$
DECLARE
    user_record RECORD;
    route_record RECORD;
BEGIN
    -- Za svakog običnog korisnika
    FOR user_record IN SELECT id FROM users WHERE role != 'ADMIN' LOOP
        -- Daj pristup EUK rutama (koje ne zahtevaju admin rolu)
        FOR route_record IN SELECT naziv FROM rute WHERE zahteva_admin_role = false LOOP
            PERFORM grant_route_access(
                user_record.id, 
                route_record.naziv, 
                user_record.id, 
                NULL, -- Ne ističe
                'Automatski pristup za obične korisnike'
            );
        END LOOP;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Prikaži sve korisnike i njihove pristupe
SELECT 
    u.username,
    u.role,
    COUNT(ur.route_name) as accessible_routes,
    COUNT(CASE WHEN r.zahteva_admin_role = true THEN 1 END) as admin_routes,
    COUNT(CASE WHEN r.zahteva_admin_role = false THEN 1 END) as user_routes
FROM users u
LEFT JOIN user_routes ur ON u.id = ur.user_id AND ur.can_access = true
LEFT JOIN rute r ON ur.route_name = r.naziv
GROUP BY u.id, u.username, u.role
ORDER BY u.role, u.username;
