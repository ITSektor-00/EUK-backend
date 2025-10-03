-- Uklanjanje Foreign Key veze između osnov_sticanja_statusa i kategorija.skracenica
-- (Koristi ovo samo ako ne želiš da forsiraš validaciju)

-- Ukloni Foreign Key constraint
ALTER TABLE euk.ugrozeno_lice_t1 
DROP CONSTRAINT IF EXISTS fk_ugrozeno_lice_kategorija;

-- Ukloni UNIQUE constraint sa kategorija.skracenica (opciono)
ALTER TABLE euk.kategorija 
DROP CONSTRAINT IF EXISTS uk_kategorija_skracenica;

-- Proveri da li je uspešno uklonjeno
SELECT
    tc.constraint_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'euk'
  AND tc.table_name = 'ugrozeno_lice_t1'
  AND kcu.column_name = 'osnov_sticanja_statusa';

