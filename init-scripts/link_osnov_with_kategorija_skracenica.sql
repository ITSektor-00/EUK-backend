-- Povezivanje osnov_sticanja_statusa sa kategorija.skracenica

-- 1. Prvo proveri da li skracenica postoji u kategorija tabeli
SELECT column_name, data_type 
FROM information_schema.columns
WHERE table_schema = 'euk' 
  AND table_name = 'kategorija'
  AND column_name = 'skracenica';

-- 2. Proveri sve skracenice u kategorijama
SELECT skracenica, naziv FROM euk.kategorija ORDER BY skracenica;

-- 3. Proveri unique vrednosti osnov_sticanja_statusa
SELECT DISTINCT osnov_sticanja_statusa 
FROM euk.ugrozeno_lice_t1 
WHERE osnov_sticanja_statusa IS NOT NULL 
  AND osnov_sticanja_statusa != ''
ORDER BY osnov_sticanja_statusa;

-- 4. Proveri da li sve osnov_sticanja_statusa vrednosti postoje u kategorija.skracenica
-- (ovo će pokazati koje vrednosti NEDOSTAJU u kategorijama)
SELECT DISTINCT ul.osnov_sticanja_statusa, COUNT(*) as count
FROM euk.ugrozeno_lice_t1 ul
WHERE ul.osnov_sticanja_statusa IS NOT NULL
  AND ul.osnov_sticanja_statusa != ''
  AND NOT EXISTS (
    SELECT 1 FROM euk.kategorija k 
    WHERE k.skracenica = ul.osnov_sticanja_statusa
  )
GROUP BY ul.osnov_sticanja_statusa
ORDER BY count DESC;

-- 5. Ako želiš da dodaš UNIQUE constraint na skracenica (potrebno za Foreign Key)
-- ALTER TABLE euk.kategorija ADD CONSTRAINT uk_kategorija_skracenica UNIQUE (skracenica);

-- 6. Dodaj Foreign Key constraint (samo ako su svi podaci validni)
-- ALTER TABLE euk.ugrozeno_lice_t1 
--   ADD CONSTRAINT fk_ugrozeno_lice_kategorija 
--   FOREIGN KEY (osnov_sticanja_statusa) 
--   REFERENCES euk.kategorija(skracenica)
--   ON DELETE SET NULL
--   ON UPDATE CASCADE;

-- 7. Proveri da li je Foreign Key kreiran
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

