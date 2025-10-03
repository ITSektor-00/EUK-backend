-- Kreiranje Foreign Key veze između osnov_sticanja_statusa i kategorija.skracenica

-- KORAK 0: Obriši kategorije sa praznim skracenicama (duplikati)
DELETE FROM euk.kategorija 
WHERE skracenica IS NULL OR skracenica = '';

-- KORAK 1: Dodaj UNIQUE constraint na kategorija.skracenica (potrebno za FK)
ALTER TABLE euk.kategorija 
ADD CONSTRAINT uk_kategorija_skracenica UNIQUE (skracenica);

-- KORAK 2: Kreiraj Foreign Key constraint
ALTER TABLE euk.ugrozeno_lice_t1 
ADD CONSTRAINT fk_ugrozeno_lice_kategorija 
FOREIGN KEY (osnov_sticanja_statusa) 
REFERENCES euk.kategorija(skracenica)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- KORAK 3: Proveri da li je Foreign Key uspešno kreiran
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

