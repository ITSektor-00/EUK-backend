-- Add admin user with predefined password
-- Password: $2a$12$u5DIn4tfL9ZXjjXhy5YtmeVE5vp2HdFEh8cvTZaRsMTgDSpkUCZhi
-- This corresponds to password: admin123

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
    '$2a$12$u5DIn4tfL9ZXjjXhy5YtmeVE5vp2HdFEh8cvTZaRsMTgDSpkUCZhi', 
    'Admin', 
    'Korisnik', 
    'admin', 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;
