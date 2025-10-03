-- Provera veze između osnov_sticanja_statusa i kategorija tabele

-- 1. Proveri strukturu tabele ugrozeno_lice_t1
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema = 'euk' AND table_name = 'ugrozeno_lice_t1'
  AND column_name = 'osnov_sticanja_statusa'
ORDER BY ordinal_position;

-- 2. Proveri strukturu tabele kategorija
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema = 'euk' AND table_name = 'kategorija'
ORDER BY ordinal_position;

-- 3. Proveri Foreign Key constrainte između tabela
SELECT
    tc.constraint_name,
    tc.table_schema,
    tc.table_name,
    kcu.column_name,
    ccu.table_schema AS foreign_table_schema,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'euk'
  AND tc.table_name = 'ugrozeno_lice_t1'
  AND kcu.column_name = 'osnov_sticanja_statusa';

-- 4. Proveri sve kategorije
SELECT * FROM euk.kategorija ORDER BY naziv;

-- 5. Proveri unique vrednosti osnov_sticanja_statusa u ugrozeno_lice_t1
SELECT 
    osnov_sticanja_statusa, 
    COUNT(*) as count
FROM euk.ugrozeno_lice_t1
GROUP BY osnov_sticanja_statusa
ORDER BY count DESC
LIMIT 20;

-- 6. Proveri da li postoji skracenica kolona u kategorija
SELECT column_name 
FROM information_schema.columns
WHERE table_schema = 'euk' 
  AND table_name = 'kategorija'
  AND column_name LIKE '%skracenica%';

-- 7. Ako postoji skracenica, proveri sve vrednosti
SELECT * FROM euk.kategorija ORDER BY naziv;

-- 8. Proveri koliko osnov_sticanja_statusa vrednosti NISU u kategorijama (ako postoji veza)
SELECT DISTINCT ul.osnov_sticanja_statusa
FROM euk.ugrozeno_lice_t1 ul
WHERE ul.osnov_sticanja_statusa IS NOT NULL
  AND ul.osnov_sticanja_statusa != ''
  AND NOT EXISTS (
    SELECT 1 FROM euk.kategorija k 
    WHERE k.skracenica = ul.osnov_sticanja_statusa
       OR k.naziv = ul.osnov_sticanja_statusa
  )
LIMIT 20;

