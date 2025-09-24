-- SQL upit za brisanje allowed_roles kolone iz rute tabele
-- Pokreni ovaj upit u PostgreSQL bazi

-- 1. Brisanje kolone allowed_roles iz rute tabele
ALTER TABLE rute DROP COLUMN IF EXISTS allowed_roles;

-- 2. Proveri da li je kolona obrisana
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'rute' AND column_name = 'allowed_roles';

-- 3. Proveri trenutnu strukturu rute tabele
\d rute;
