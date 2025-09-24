-- SQL Query-je za brisanje nivoa pristupa kolona
-- Pokreni ove query-je u PostgreSQL bazi

-- 1. Brisanje kolone nivo_pristupa iz users tabele
ALTER TABLE users DROP COLUMN IF EXISTS nivo_pristupa;

-- 2. Brisanje kolone nivo_dozvole iz user_routes tabele  
ALTER TABLE user_routes DROP COLUMN IF EXISTS nivo_dozvole;

-- 3. Brisanje kolona nivo_min i nivo_max iz rute tabele
ALTER TABLE rute DROP COLUMN IF EXISTS nivo_min;
ALTER TABLE rute DROP COLUMN IF EXISTS nivo_max;

-- 4. Proveri da li su kolone obrisane
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name LIKE '%nivo%';

SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'user_routes' AND column_name LIKE '%nivo%';

SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'rute' AND column_name LIKE '%nivo%';

-- 5. Proveri trenutnu strukturu tabela
\d users;
\d user_routes; 
\d rute;
