-- Povezivanje osnov_sticanja_statusa sa kategorija.skracenica
-- Ovo će dodati foreign key constraint između tabela

-- Prvo proveri trenutno stanje
SELECT 'Current osnov_sticanja_statusa values:' as info;
SELECT DISTINCT osnov_sticanja_statusa, COUNT(*) as count 
FROM euk.ugrozeno_lice_t1 
WHERE osnov_sticanja_statusa IS NOT NULL 
GROUP BY osnov_sticanja_statusa;

SELECT 'Current kategorija.skracenica values:' as info;
SELECT kategorija_id, naziv, skracenica 
FROM euk.kategorija 
ORDER BY kategorija_id;

-- Dodaj foreign key constraint (opciono)
-- ALTER TABLE euk.ugrozeno_lice_t1 
-- ADD CONSTRAINT fk_ugrozeno_lice_osnov_status_kategorija 
-- FOREIGN KEY (osnov_sticanja_statusa) REFERENCES euk.kategorija(skracenica);

-- Dodaj index za bolje performanse
CREATE INDEX IF NOT EXISTS idx_ugrozeno_lice_osnov_status 
ON euk.ugrozeno_lice_t1(osnov_sticanja_statusa);

-- Proveri da li postoje neusaglašenosti
SELECT 'Mismatched values:' as info;
SELECT DISTINCT u.osnov_sticanja_statusa 
FROM euk.ugrozeno_lice_t1 u 
WHERE u.osnov_sticanja_statusa IS NOT NULL 
AND u.osnov_sticanja_statusa NOT IN (
    SELECT k.skracenica FROM euk.kategorija k WHERE k.skracenica IS NOT NULL
);
