-- Kreiranje tabele organizaciona_struktura
CREATE TABLE IF NOT EXISTS euk.organizaciona_struktura (
    id SERIAL PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL UNIQUE,
    opis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertovanje podataka za organizaciona_struktura
INSERT INTO euk.organizaciona_struktura (naziv, opis) VALUES
('sekretar', 'Sekretar'),
('podsekretar', 'Podsekretar')
ON CONFLICT (naziv) DO NOTHING;
