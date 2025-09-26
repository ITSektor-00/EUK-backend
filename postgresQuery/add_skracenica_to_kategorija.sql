-- Dodavanje kolone skracenica u euk.kategorija tabelu
-- Ova kolona će sadržavati kratku verziju naziva kategorije

-- Dodaj kolonu skracenica
ALTER TABLE euk.kategorija 
ADD COLUMN skracenica VARCHAR(10) NOT NULL DEFAULT '';

-- Dodaj komentar za kolonu
COMMENT ON COLUMN euk.kategorija.skracenica IS 'Kratka verzija naziva kategorije (maksimalno 10 karaktera)';

-- Opciono: Dodaj unique constraint za skracenica
-- ALTER TABLE euk.kategorija ADD CONSTRAINT uk_kategorija_skracenica UNIQUE (skracenica);

-- Proveri da li je kolona dodana
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_schema = 'euk' 
AND table_name = 'kategorija' 
AND column_name = 'skracenica';
