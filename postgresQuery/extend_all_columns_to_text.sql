-- PostgreSQL script za proširenje SVIH kolona na TEXT tip
-- ⚠️ PAŽNJA: Ovo će proširiti sve kolone da prihvataju bilo koju dužinu!

-- KORAK 1: Proširi sve text kolone na TEXT tip (bez ograničenja dužine)
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN redni_broj TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ime TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN prezime TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN jmbg TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ptt_broj TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN grad_opstina TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN mesto TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ulica_i_broj TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN osnov_sticanja_statusa TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ed_broj_broj_mernog_uredjaja TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN potrosnja_i_povrsina_combined TYPE TEXT;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN broj_racuna TYPE TEXT;

-- KORAK 2: Proveri da li su kolone proširene
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'ugrozeno_lice_t1' 
AND table_schema = 'euk'
ORDER BY ordinal_position;

-- KORAK 3: Status
SELECT
    'ALL_COLUMNS_EXTENDED_TO_TEXT' as status,
    'All text columns now accept unlimited length' as message,
    CURRENT_TIMESTAMP as timestamp;
