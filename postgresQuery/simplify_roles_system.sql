-- ===============================================
-- POJEDNOSTAVLJENJE SISTEMA ROLA - SAMO ADMIN/KORISNIK
-- ===============================================

-- 1. Ažuriraj postojeće korisnike da imaju samo 'admin' ili 'korisnik' role
UPDATE users 
SET role = CASE 
    WHEN role = 'admin' THEN 'admin'
    WHEN role = 'ADMIN' THEN 'admin'
    ELSE 'korisnik'
END;

-- 2. Obriši sve podatke iz user_routes tabele
DELETE FROM user_routes;

-- 3. Obriši sve podatke iz rute tabele
DELETE FROM rute;

-- 4. Obriši user_routes tabelu
DROP TABLE IF EXISTS user_routes;

-- 5. Obriši rute tabelu
DROP TABLE IF EXISTS rute;

-- 6. Dodaj constraint da role može biti samo 'admin' ili 'korisnik'
ALTER TABLE users 
ADD CONSTRAINT chk_user_role 
CHECK (role IN ('admin', 'korisnik'));

-- 7. Postavi default role na 'korisnik'
ALTER TABLE users 
ALTER COLUMN role SET DEFAULT 'korisnik';

-- 8. Ažuriraj sve postojeće korisnike koji nemaju validnu rolu
UPDATE users 
SET role = 'korisnik' 
WHERE role NOT IN ('admin', 'korisnik');

-- 9. Kreiraj admin korisnika ako ne postoji
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at)
SELECT 'admin', 'admin@euk.rs', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'Korisnik', 'admin', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'admin');

-- 10. Prikaži finalne rezultate
SELECT 
    role,
    COUNT(*) as broj_korisnika
FROM users 
GROUP BY role
ORDER BY role;
