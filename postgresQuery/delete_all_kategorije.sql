-- Brisanje svih kategorija iz euk.kategorija tabele
-- PAŽNJA: Ovo će obrisati SVE kategorije i sve povezane podatke!

-- Prvo proveri koliko kategorija ima
SELECT COUNT(*) as broj_kategorija FROM euk.kategorija;

-- Proveri koliko predmeta referencira kategorije
SELECT COUNT(*) as broj_predmeta_sa_kategorijama FROM euk.predmet WHERE kategorija_id IS NOT NULL;

-- OPCIJA 1: Obriši sve predmete prvo, pa onda kategorije
-- DELETE FROM euk.predmet;
-- DELETE FROM euk.kategorija;

-- OPCIJA 2: Postavi kategorija_id na NULL u predmetima, pa obriši kategorije
UPDATE euk.predmet SET kategorija_id = NULL;
DELETE FROM euk.kategorija;

-- OPCIJA 3: Koristi CASCADE (ako je dozvoljeno)
-- DELETE FROM euk.kategorija CASCADE;

-- Proveri da li su sve obrisane
SELECT COUNT(*) as broj_kategorija_nakon_brisanja FROM euk.kategorija;
SELECT COUNT(*) as broj_predmeta_nakon_brisanja FROM euk.predmet;

-- Resetuj auto-increment ID za obe tabele
ALTER SEQUENCE euk.kategorija_kategorija_id_seq RESTART WITH 1;
ALTER SEQUENCE euk.predmet_predmet_id_seq RESTART WITH 1;
