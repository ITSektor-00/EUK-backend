-- PostgreSQL script za brisanje svih podataka i restartovanje ID sekvenci
-- ⚠️ PAŽNJA: Ovo će obrisati SVE podatke iz baze!

-- KORAK 1: Obriši sve podatke iz svih tabela
DELETE FROM euk.ugrozeno_lice_t1;
DELETE FROM euk.predmet;
DELETE FROM euk.users;

-- KORAK 2: Restartuj sve ID sekvence
-- Ugrožena lica T1
ALTER SEQUENCE IF EXISTS euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq RESTART WITH 1;

-- Predmeti
ALTER SEQUENCE IF EXISTS euk.predmet_predmet_id_seq RESTART WITH 1;

-- Korisnici
ALTER SEQUENCE IF EXISTS euk.users_user_id_seq RESTART WITH 1;

-- KORAK 3: Proveri da li su sekvence restartovane
SELECT 
    'euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq' as sequence_name,
    last_value as current_value
FROM euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq
UNION ALL
SELECT 
    'euk.predmet_predmet_id_seq' as sequence_name,
    last_value as current_value
FROM euk.predmet_predmet_id_seq
UNION ALL
SELECT 
    'euk.users_user_id_seq' as sequence_name,
    last_value as current_value
FROM euk.users_user_id_seq;

-- KORAK 4: Proveri da li su tabele prazne
SELECT 
    'euk.ugrozeno_lice_t1' as table_name,
    COUNT(*) as record_count
FROM euk.ugrozeno_lice_t1
UNION ALL
SELECT 
    'euk.predmet' as table_name,
    COUNT(*) as record_count
FROM euk.predmet
UNION ALL
SELECT 
    'euk.users' as table_name,
    COUNT(*) as record_count
FROM euk.users;

-- KORAK 5: Prikaži status
SELECT 
    'DATABASE_CLEARED' as status,
    'All data deleted and ID sequences restarted' as message,
    CURRENT_TIMESTAMP as timestamp;
