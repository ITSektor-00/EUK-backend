-- Dodavanje svih nedostajućih kategorija iz osnov_sticanja_statusa

-- 1. Prvo vidi koje vrednosti nedostaju
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

-- 2. Automatski dodaj sve nedostajuće kategorije
INSERT INTO euk.kategorija (skracenica, naziv)
SELECT DISTINCT 
    ul.osnov_sticanja_statusa as skracenica,
    ul.osnov_sticanja_statusa as naziv
FROM euk.ugrozeno_lice_t1 ul
WHERE ul.osnov_sticanja_statusa IS NOT NULL
  AND ul.osnov_sticanja_statusa != ''
  AND NOT EXISTS (
    SELECT 1 FROM euk.kategorija k 
    WHERE k.skracenica = ul.osnov_sticanja_statusa
  );

-- 3. Proveri da li su sve kategorije dodate
SELECT * FROM euk.kategorija ORDER BY skracenica;

-- 4. Proveri da li još ima nedostajućih
SELECT DISTINCT ul.osnov_sticanja_statusa, COUNT(*) as count
FROM euk.ugrozeno_lice_t1 ul
WHERE ul.osnov_sticanja_statusa IS NOT NULL
  AND ul.osnov_sticanja_statusa != ''
  AND NOT EXISTS (
    SELECT 1 FROM euk.kategorija k 
    WHERE k.skracenica = ul.osnov_sticanja_statusa
  )
GROUP BY ul.osnov_sticanja_statusa;

