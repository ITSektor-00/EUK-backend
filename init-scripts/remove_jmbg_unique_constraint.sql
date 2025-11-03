-- PostgreSQL script za uklanjanje unique constraint-a sa JMBG kolone
-- ⚠️ PAŽNJA: Ovo će dozvoliti duplikate JMBG-a u bazi!

-- KORAK 1: Ukloni unique constraint sa JMBG kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_new_jmbg_key;

-- KORAK 2: Proveri da li je constraint uklonjen
SELECT 
    conname as constraint_name,
    contype as constraint_type
FROM pg_constraint 
WHERE conrelid = 'euk.ugrozeno_lice_t1'::regclass 
AND conname LIKE '%jmbg%';

-- KORAK 3: Status
SELECT
    'JMBG_UNIQUE_CONSTRAINT_REMOVED' as status,
    'JMBG duplicates are now allowed' as message,
    CURRENT_TIMESTAMP as timestamp;
