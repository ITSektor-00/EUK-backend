-- PostgreSQL script za proširenje JMBG kolone da prihvati duže vrednosti
-- ⚠️ PAŽNJA: Ovo će proširiti JMBG kolonu da prihvati bilo koju dužinu!

-- KORAK 1: Proširi JMBG kolonu da prihvati duže vrednosti
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN jmbg TYPE VARCHAR(50);

-- KORAK 2: Proširi i ostale kolone da prihvataju duže vrednosti
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN redni_broj TYPE VARCHAR(50);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ime TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN prezime TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ptt_broj TYPE VARCHAR(20);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN grad_opstina TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN mesto TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ulica_i_broj TYPE VARCHAR(200);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN osnov_sticanja_statusa TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ed_broj_broj_mernog_uredjaja TYPE VARCHAR(100);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN potrosnja_i_povrsina_combined TYPE VARCHAR(200);
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN broj_racuna TYPE VARCHAR(50);

-- KORAK 3: Proveri da li su kolone proširene
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'ugrozeno_lice_t1' 
AND table_schema = 'euk'
AND column_name IN ('jmbg', 'redni_broj', 'ime', 'prezime', 'ptt_broj')
ORDER BY ordinal_position;

-- KORAK 4: Status
SELECT
    'COLUMN_LENGTHS_EXTENDED' as status,
    'All text columns now accept longer values' as message,
    CURRENT_TIMESTAMP as timestamp;
