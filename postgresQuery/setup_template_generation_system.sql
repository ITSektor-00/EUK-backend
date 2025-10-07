-- Master skripta za setup template generation sistema
-- Pokrenuti u redosledu

-- 1. Kreiranje tabele obrasci_vrste
CREATE TABLE IF NOT EXISTS euk.obrasci_vrste (
    id SERIAL PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL UNIQUE,
    opis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertovanje podataka za obrasci_vrste
INSERT INTO euk.obrasci_vrste (naziv, opis) VALUES
('negativno', 'Negativni obrasci'),
('neograniceno', 'Neograničeni obrasci'),
('ograniceno', 'Ograničeni obrasci'),
('borci', 'Obrasci za borce'),
('penzioneri', 'Obrasci za penzionere'),
('obustave', 'Obrasci za obustave')
ON CONFLICT (naziv) DO NOTHING;

-- 2. Kreiranje tabele organizaciona_struktura
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

-- 3. Dodavanje kolona u predmet tabelu
ALTER TABLE euk.predmet 
ADD COLUMN IF NOT EXISTS template_file_path VARCHAR(500),
ADD COLUMN IF NOT EXISTS template_generated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS template_status VARCHAR(50) DEFAULT 'pending';

-- 4. Kreiranje indeksa za bolje performanse
CREATE INDEX IF NOT EXISTS idx_obrasci_vrste_naziv ON euk.obrasci_vrste(naziv);
CREATE INDEX IF NOT EXISTS idx_organizaciona_struktura_naziv ON euk.organizaciona_struktura(naziv);
CREATE INDEX IF NOT EXISTS idx_predmet_template_status ON euk.predmet(template_status);

-- 5. Kreiranje direktorijuma za template fajlove (ovo se radi na aplikacijskom nivou)
-- templates/ direktorijum će biti kreiran automatski kada se prvi template generiše

-- 6. Test podaci (opciono)
-- Možete dodati test podatke ovde ako je potrebno

COMMIT;
