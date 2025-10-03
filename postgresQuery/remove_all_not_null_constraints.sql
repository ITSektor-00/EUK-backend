-- Uklanjanje svih NOT NULL ograničenja sa euk.ugrozeno_lice_t1 tabele
-- Sada sve kolone mogu da budu NULL

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
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN created_at DROP NOT NULL;
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN updated_at DROP NOT NULL;

-- Provera da li su ograničenja uklonjena
SELECT column_name, is_nullable, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_schema = 'euk' AND table_name = 'ugrozeno_lice_t1'
ORDER BY ordinal_position;

