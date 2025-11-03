-- ===============================================
-- UPDATE ADMIN PASSWORD - POSTAVI admin123 LOZINKU
-- ===============================================

-- 1. Ažuriraj admin korisnika sa novim hash-om za lozinku 'admin123'
-- Hash je kreiran sa BCrypt strength 12
UPDATE users 
SET password_hash = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi',
    updated_at = NOW()
WHERE username = 'admin';

-- 2. Ako admin korisnik ne postoji, kreiraj ga
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at)
SELECT 'admin', 'admin@euk.rs', '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'Korisnik', 'admin', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- 3. Prikaži admin korisnika
SELECT 
    'Admin korisnik:' as info,
    id,
    username,
    email,
    first_name,
    last_name,
    role,
    is_active,
    created_at,
    updated_at
FROM users 
WHERE username = 'admin';

-- 4. Status
SELECT
    'ADMIN_PASSWORD_UPDATED' as status,
    'Admin password set to: admin123' as message,
    CURRENT_TIMESTAMP as timestamp;
