-- Ažuriranje euk.predmet tabele da uključi skracenica iz euk.kategorija
-- Ovo će dodati kolonu kategorija_skracenica u predmet tabelu

-- Dodaj kolonu kategorija_skracenica u predmet tabelu
ALTER TABLE euk.predmet 
ADD COLUMN kategorija_skracenica VARCHAR(10);

-- Popuni kategorija_skracenica na osnovu postojećih veza
UPDATE euk.predmet 
SET kategorija_skracenica = k.skracenica
FROM euk.kategorija k 
WHERE euk.predmet.kategorija_id = k.kategorija_id;

-- Dodaj foreign key constraint (opciono)
-- ALTER TABLE euk.predmet 
-- ADD CONSTRAINT fk_predmet_kategorija_skracenica 
-- FOREIGN KEY (kategorija_skracenica) REFERENCES euk.kategorija(skracenica);

-- Dodaj index za bolje performanse
CREATE INDEX idx_predmet_kategorija_skracenica ON euk.predmet(kategorija_skracenica);

-- Proveri rezultate
SELECT p.predmet_id, p.naziv_predmeta, p.kategorija_id, p.kategorija_skracenica, k.naziv as kategorija_naziv
FROM euk.predmet p
LEFT JOIN euk.kategorija k ON p.kategorija_id = k.kategorija_id
LIMIT 10;
