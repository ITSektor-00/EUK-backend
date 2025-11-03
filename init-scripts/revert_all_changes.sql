-- Vraćanje baze u prethodno stanje - uklanjanje svih constraint-ova

-- 1. Ukloni Foreign Key constraint između ugrozeno_lice_t1 i kategorija
ALTER TABLE euk.ugrozeno_lice_t1 
DROP CONSTRAINT IF EXISTS fk_ugrozeno_lice_kategorija;

-- 2. Ukloni UNIQUE constraint sa kategorija.skracenica
ALTER TABLE euk.kategorija 
DROP CONSTRAINT IF EXISTS uk_kategorija_skracenica;

-- 3. Proveri da li su constraint-i uklonjeni
SELECT
    tc.constraint_name,
    tc.constraint_type,
    tc.table_name,
    kcu.column_name
FROM information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'euk'
  AND tc.table_name IN ('ugrozeno_lice_t1', 'kategorija')
  AND tc.constraint_type IN ('FOREIGN KEY', 'UNIQUE')
ORDER BY tc.table_name, tc.constraint_type;

-- 4. Obriši dodate kategorije (ako si pokrenuo add_missing_kategorije.sql)
-- DELETE FROM euk.kategorija WHERE naziv = skracenica;

-- 5. Proveri stanje tabela
SELECT 'ugrozeno_lice_t1' as tabela, COUNT(*) as broj_redova FROM euk.ugrozeno_lice_t1
UNION ALL
SELECT 'kategorija' as tabela, COUNT(*) as broj_redova FROM euk.kategorija;

