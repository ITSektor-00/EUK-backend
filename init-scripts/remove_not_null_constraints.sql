-- PostgreSQL script za uklanjanje NOT NULL constraint-a sa svih kolona
-- ⚠️ PAŽNJA: Ovo će dozvoliti NULL vrednosti u svim kolonama!

-- KORAK 1: Ukloni NOT NULL constraint sa svih kolona u ugrozeno_lice_t1
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN redni_broj DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ime DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN prezime DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN jmbg DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ptt_broj DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN grad_opstina DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN mesto DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ulica_i_broj DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN broj_clanova_domacinstva DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN osnov_sticanja_statusa DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ed_broj_broj_mernog_uredjaja DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN potrosnja_i_povrsina_combined DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN iznos_umanjenja_sa_pdv DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN broj_racuna DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN datum_izdavanja_racuna DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN datum_trajanja_prava DROP NOT NULL;

-- KORAK 2: Proveri da li su constraint-ovi uklonjeni
SELECT 
    column_name,
    is_nullable,
    data_type
FROM information_schema.columns 
WHERE table_name = 'ugrozeno_lice_t1' 
AND table_schema = 'euk'
ORDER BY ordinal_position;

-- KORAK 3: Status
SELECT
    'NOT_NULL_CONSTRAINTS_REMOVED' as status,
    'All columns now allow NULL values' as message,
    CURRENT_TIMESTAMP as timestamp;
