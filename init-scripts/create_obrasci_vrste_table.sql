-- Kreiranje tabele obrasci_vrste
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
