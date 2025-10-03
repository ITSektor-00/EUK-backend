-- Čišćenje duplikata u kategorija.skracenica pre kreiranja UNIQUE constraint-a

-- 1. Proveri duplikate u skracenica koloni
SELECT skracenica, COUNT(*) as count
FROM euk.kategorija
GROUP BY skracenica
HAVING COUNT(*) > 1
ORDER BY count DESC;

-- 2. Proveri sve kategorije sa praznim skracenicama
SELECT * FROM euk.kategorija 
WHERE skracenica IS NULL OR skracenica = '';

-- 3. OPCIJA A: Obriši kategorije sa praznim skracenicama (ako nisu potrebne)
-- DELETE FROM euk.kategorija WHERE skracenica IS NULL OR skracenica = '';

-- 4. OPCIJA B: Ažuriraj prazne skracenice da budu unique (dodaj sufiks)
-- UPDATE euk.kategorija 
-- SET skracenica = 'UNKNOWN_' || COALESCE(naziv, 'NO_NAME')
-- WHERE skracenica IS NULL OR skracenica = '';

-- 5. OPCIJA C: Postavi skracenica na naziv ako je prazna
-- UPDATE euk.kategorija 
-- SET skracenica = naziv
-- WHERE skracenica IS NULL OR skracenica = '';

-- 6. OPCIJA D: Obriši duplikate, zadrži samo prvi zapis
WITH numbered_rows AS (
  SELECT ctid, 
         ROW_NUMBER() OVER (PARTITION BY skracenica ORDER BY naziv) as rn
  FROM euk.kategorija
  WHERE skracenica IS NOT NULL AND skracenica != ''
)
SELECT * FROM numbered_rows WHERE rn > 1;

-- Ako želiš da obrišeš duplikate (zadrži samo prvi):
-- DELETE FROM euk.kategorija
-- WHERE ctid IN (
--   SELECT ctid
--   FROM (
--     SELECT ctid, 
--            ROW_NUMBER() OVER (PARTITION BY skracenica ORDER BY naziv) as rn
--     FROM euk.kategorija
--     WHERE skracenica IS NOT NULL AND skracenica != ''
--   ) t
--   WHERE rn > 1
-- );

-- 7. Proveri rezultat posle čišćenja
SELECT skracenica, COUNT(*) as count
FROM euk.kategorija
GROUP BY skracenica
ORDER BY count DESC;

