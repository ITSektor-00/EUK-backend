-- Dodavanje kolone datum_trajanja_prava u tabelu ugrozeno_lice_t1
-- Ovaj skript dodaje novu kolonu u postojeću tabelu

-- KORAK 1: Dodavanje kolone
ALTER TABLE euk.ugrozeno_lice_t1 
ADD COLUMN datum_trajanja_prava DATE;

-- KORAK 2: Dodavanje komentara za dokumentaciju
COMMENT ON COLUMN euk.ugrozeno_lice_t1.datum_trajanja_prava IS 'Datum trajanja prava za ugroženo lice';

-- KORAK 3: Kreiranje indexa za performanse (opciono)
CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_trajanja_prava ON euk.ugrozeno_lice_t1(datum_trajanja_prava);

-- KORAK 4: Verifikacija dodavanja kolone
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'euk'
    AND table_name = 'ugrozeno_lice_t1'
    AND column_name = 'datum_trajanja_prava';

-- Poruka o uspešnom dodavanju
DO $$
BEGIN
    RAISE NOTICE 'Kolina datum_trajanja_prava je uspešno dodana u tabelu ugrozeno_lice_t1!';
END $$;
