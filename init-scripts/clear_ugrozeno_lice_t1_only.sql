-- PostgreSQL script za brisanje podataka samo iz euk.ugrozeno_lice_t1 tabele
-- ⚠️ PAŽNJA: Ovo će obrisati SVE podatke iz ugrozeno_lice_t1 tabele!

-- KORAK 1: Obriši sve podatke iz tabele
DELETE FROM euk.ugrozeno_lice_t1;

-- KORAK 2: Restartuj ID sekvencu
ALTER SEQUENCE IF EXISTS euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq RESTART WITH 1;

-- KORAK 3: Proveri da li je tabela prazna
SELECT COUNT(*) as record_count FROM euk.ugrozeno_lice_t1;

-- KORAK 4: Proveri ID sekvencu
SELECT last_value as current_id_value FROM euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq;

-- KORAK 5: Status
SELECT 
    'UGROZENO_LICE_T1_CLEARED' as status,
    'All data deleted and ID sequence restarted' as message,
    CURRENT_TIMESTAMP as timestamp;
