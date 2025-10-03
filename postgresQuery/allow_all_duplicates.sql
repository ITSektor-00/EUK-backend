-- Uklanjanje svih UNIQUE constrainta sa euk.ugrozeno_lice_t1 tabele
-- Sada SVE kolone mogu da imaju duplikate

-- Ukloni UNIQUE constraint sa JMBG kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_new_jmbg_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_jmbg_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_jmbg;

-- Ukloni UNIQUE constraint sa IME kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_ime_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_ime;

-- Ukloni UNIQUE constraint sa PREZIME kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_prezime_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_prezime;

-- Ukloni UNIQUE constraint sa REDNI_BROJ kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_redni_broj_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_redni_broj;

-- Ukloni UNIQUE constraint sa PTT_BROJ kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_ptt_broj_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_ptt_broj;

-- Ukloni UNIQUE constraint sa GRAD_OPSTINA kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_grad_opstina_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_grad_opstina;

-- Ukloni UNIQUE constraint sa MESTO kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_mesto_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_mesto;

-- Ukloni UNIQUE constraint sa ULICA_I_BROJ kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_ulica_i_broj_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_ulica_i_broj;

-- Ukloni UNIQUE constraint sa BROJ_RACUNA kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_broj_racuna_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_broj_racuna;

-- Ukloni UNIQUE constraint sa ED_BROJ_BROJ_MERNOG_UREDJAJA kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_ed_broj_broj_mernog_uredjaja_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_ed_broj_broj_mernog_uredjaja;

-- Ukloni UNIQUE constraint sa OSNOV_STICANJA_STATUSA kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_osnov_sticanja_statusa_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_osnov_sticanja_statusa;

-- Ukloni UNIQUE constraint sa POTROSNJA_I_POVRSINA_COMBINED kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_potrosnja_i_povrsina_combined_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_potrosnja_i_povrsina_combined;

-- Ukloni UNIQUE constraint sa BROJ_CLANOVA_DOMACINSTVA kolone
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS ugrozeno_lice_t1_broj_clanova_domacinstva_key;
ALTER TABLE euk.ugrozeno_lice_t1 DROP CONSTRAINT IF EXISTS uk_broj_clanova_domacinstva;

-- Ukloni sve UNIQUE INDEX-e (ovo je ključno!)
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_jmbg;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_ime;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_prezime;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_redni_broj;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_ptt_broj;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_t1_broj_racuna;

-- Proveri sve constraint-e na tabeli
SELECT conname, contype, pg_get_constraintdef(oid) AS constraint_definition
FROM pg_constraint
WHERE conrelid = 'euk.ugrozeno_lice_t1'::regclass
ORDER BY contype, conname;

-- Proveri sve index-e na tabeli
SELECT indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'euk' AND tablename = 'ugrozeno_lice_t1'
ORDER BY indexname;
