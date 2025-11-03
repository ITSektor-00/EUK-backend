-- Dodavanje kolone osnov_sticanja_statusa u euk.ugrozeno_lice_t1 tabelu
-- Ova kolona će sadržavati osnov sticanja statusa ugroženog lica

-- Dodaj kolonu osnov_sticanja_statusa
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'euk' 
        AND table_name = 'ugrozeno_lice_t1' 
        AND column_name = 'osnov_sticanja_statusa'
    ) THEN
        ALTER TABLE euk.ugrozeno_lice_t1 
        ADD COLUMN osnov_sticanja_statusa VARCHAR(255);
    END IF;
END $$;

-- Dodaj komentar za kolonu
COMMENT ON COLUMN euk.ugrozeno_lice_t1.osnov_sticanja_statusa IS 'Osnov sticanja statusa ugroženog lica';

-- Proveri da li je kolona dodana
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_schema = 'euk' 
AND table_name = 'ugrozeno_lice_t1' 
AND column_name = 'osnov_sticanja_statusa';

-- Proveri trenutno stanje tabele
SELECT COUNT(*) as broj_ugrozenih_lica FROM euk.ugrozeno_lice_t1;
