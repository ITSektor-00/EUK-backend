-- Add default categories for EUK system

INSERT INTO euk.kategorija (naziv, skracenica) VALUES 
    ('Upravni postupak', 'UP'),
    ('Građanski postupak', 'GP'),
    ('Krivični postupak', 'KP'),
    ('Prekršajni postupak', 'PP'),
    ('Izvršni postupak', 'IP'),
    ('Upravni spor', 'US'),
    ('Konstitucionalni postupak', 'KON'),
    ('Međunarodni postupak', 'MP'),
    ('Arbitražni postupak', 'ARB'),
    ('Ostalo', 'OST')
ON CONFLICT (kategorija_id) DO NOTHING;
