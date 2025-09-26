-- Proveri da li je licenses tabela povezana sa users tabelom
-- Pokreni ovo da vidiš da li sve radi

-- 1. Proveri da li postoji licenses tabela
SELECT table_name, column_name, data_type, is_nullable
FROM information_schema.columns 
WHERE table_name = 'licenses'
ORDER BY ordinal_position;

-- 2. Proveri foreign key constraint
SELECT 
    tc.constraint_name, 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'licenses';

-- 3. Proveri da li postoje korisnici
SELECT id, username, email FROM users LIMIT 5;

-- 4. Proveri da li postoje licence
SELECT id, user_id, start_date, end_date, is_active FROM licenses LIMIT 5;

-- 5. Testiranje veze - JOIN query
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    l.id as license_id,
    l.start_date,
    l.end_date,
    l.is_active,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP)) as days_until_expiry
FROM users u
LEFT JOIN licenses l ON u.id = l.user_id
ORDER BY u.id;

-- 6. Proveri da li foreign key radi
-- Ovo će pokazati grešku ako pokušaš da ubaciš nepostojeći user_id
-- INSERT INTO licenses (user_id, start_date, end_date) VALUES (999, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 year');
