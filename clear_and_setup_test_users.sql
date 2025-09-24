-- Brisanje svih korisnika iz users tabele
DELETE FROM public.users;

-- Restartovanje ID sekvence
ALTER SEQUENCE public.users_id_seq RESTART WITH 1;

-- Dodavanje admin korisnika
INSERT INTO public.users (
    username, 
    email, 
    password_hash, 
    first_name, 
    last_name, 
    role, 
    is_active, 
    created_at, 
    updated_at
) VALUES (
    'admin',
    'admin@euk.rs',
    '$2a$12$lMnPIsnQXLO.2qmRdW2Ed.s1q4shct5MApQRtccXDJHIEOKhXCaUe', -- password: admin123
    'Admin',
    'Korisnik',
    'admin',
    true,
    NOW(),
    NOW()
);

-- Dodavanje običnog korisnika
INSERT INTO public.users (
    username, 
    email, 
    password_hash, 
    first_name, 
    last_name, 
    role, 
    is_active, 
    created_at, 
    updated_at
) VALUES (
    'korisnik',
    'korisnik@euk.rs',
    '$2a$12$lMnPIsnQXLO.2qmRdW2Ed.s1q4shct5MApQRtccXDJHIEOKhXCaUe', -- password: admin123
    'Obični',
    'Korisnik',
    'korisnik',
    true,
    NOW(),
    NOW()
);

-- Provera da li su korisnici dodati
SELECT id, username, email, first_name, last_name, role, is_active 
FROM public.users 
ORDER BY id;
