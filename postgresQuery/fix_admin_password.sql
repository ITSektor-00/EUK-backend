-- ===============================================
-- FIX ADMIN PASSWORD - POSTAVI JEDINSTVENU LOZINKU
-- ===============================================

-- 1. Ažuriraj admin korisnika sa poznatom lozinkom
UPDATE users 
SET password_hash = '$2a$12$lMnPIsnQXLO.2qmRdW2Ed.s1q4shct5MApQRtccXDJHIEOKhXCaUe',
    updated_at = NOW()
WHERE username = 'admin';

-- 2. Ako admin korisnik ne postoji, kreiraj ga
INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at)
SELECT 'admin', 'admin@euk.rs', '$2a$12$lMnPIsnQXLO.2qmRdW2Ed.s1q4shct5MApQRtccXDJHIEOKhXCaUe', 'Admin', 'Korisnik', 'admin', true, NOW(), NOW()
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
    'ADMIN_PASSWORD_FIXED' as status,
    'Admin password set to: admin123' as message,
    CURRENT_TIMESTAMP as timestamp;
