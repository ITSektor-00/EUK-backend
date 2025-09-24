-- ===============================================
-- MIGRACIJA BAZE - POJEDNOSTAVLJENJE ROLA
-- ===============================================

-- 1. Ažuriraj sve postojeće role u 'admin' ili 'korisnik'
UPDATE users 
SET role = CASE 
    WHEN role = 'admin' THEN 'admin'
    WHEN role = 'ADMIN' THEN 'admin'
    WHEN role = 'obradjivaci predmeta' THEN 'korisnik'
    WHEN role = 'OBRADJIVAC' THEN 'korisnik'
    WHEN role = 'potpisnik' THEN 'korisnik'
    WHEN role = 'POTPISNIK' THEN 'korisnik'
    ELSE 'korisnik'  -- sve ostale role postavi na 'korisnik'
END;

-- 2. Obriši sve podatke iz user_routes tabele (ako postoji)
DELETE FROM user_routes;

-- 3. Obriši sve podatke iz rute tabele (ako postoji)
DELETE FROM rute;

-- 4. Obriši user_routes tabelu (ako postoji)
DROP TABLE IF EXISTS user_routes;

-- 5. Obriši rute tabelu (ako postoji)
DROP TABLE IF EXISTS rute;

-- 6. Dodaj constraint da role može biti samo 'admin' ili 'korisnik'
ALTER TABLE users 
ADD CONSTRAINT chk_user_role 
CHECK (role IN ('admin', 'korisnik'));

-- 7. Postavi default role na 'korisnik'
ALTER TABLE users 
ALTER COLUMN role SET DEFAULT 'korisnik';

-- 8. Kreiraj admin korisnika ako ne postoji
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at)
SELECT 'admin', 'admin@euk.rs', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'Korisnik', 'admin', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'admin');

-- 9. Prikaži finalne rezultate
SELECT 
    role,
    COUNT(*) as broj_korisnika
FROM users 
GROUP BY role
ORDER BY role;

-- 10. Prikaži sve korisnike sa novim rolama
SELECT 
    id,
    username,
    email,
    first_name,
    last_name,
    role,
    is_active,
    created_at
FROM users 
ORDER BY role, username;
