-- Migration script to create new combined energy column
-- Changes: Drop both potrosnja_kwh and zagrevana_povrsina_m2 columns and create new combined column
-- Format: "Потрошња у kWh/{kWh_value}/загревана површина у m2/{m2_value}"

-- KORAK 1: Backup existing data (optional - for safety)
-- CREATE TABLE euk.ugrozeno_lice_t1_backup AS SELECT * FROM euk.ugrozeno_lice_t1;

-- KORAK 2: Drop the potrosnja_i_povrsina_combined column if it exists
ALTER TABLE euk.ugrozeno_lice_t1 DROP COLUMN IF EXISTS potrosnja_i_povrsina_combined;

-- KORAK 3: Create new combined column in the correct position
-- PostgreSQL doesn't support ADD COLUMN AFTER, so we'll add it and then reorder
ALTER TABLE euk.ugrozeno_lice_t1 
ADD COLUMN potrosnja_i_povrsina_combined VARCHAR(200);

-- KORAK 4: Populate the new column with combined data from existing columns
-- First check if columns exist and populate accordingly
DO $$
BEGIN
    -- Check if both columns exist
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_schema = 'euk' 
               AND table_name = 'ugrozeno_lice_t1' 
               AND column_name = 'potrosnja_kwh')
    AND EXISTS (SELECT 1 FROM information_schema.columns 
                WHERE table_schema = 'euk' 
                AND table_name = 'ugrozeno_lice_t1' 
                AND column_name = 'zagrevana_povrsina_m2') THEN
        -- Both columns exist
        UPDATE euk.ugrozeno_lice_t1 
        SET potrosnja_i_povrsina_combined = 
            CASE 
                WHEN potrosnja_kwh IS NOT NULL AND zagrevana_povrsina_m2 IS NOT NULL THEN
                    CONCAT('Потрошња у kWh/', potrosnja_kwh::TEXT, '/загревана површина у m2/', zagrevana_povrsina_m2::TEXT)
                WHEN potrosnja_kwh IS NOT NULL THEN
                    CONCAT('Потрошња у kWh/', potrosnja_kwh::TEXT, '/загревана површина у m2/')
                WHEN zagrevana_povrsina_m2 IS NOT NULL THEN
                    CONCAT('Потрошња у kWh//загревана површина у m2/', zagrevana_povrsina_m2::TEXT)
                ELSE
                    'Потрошња у kWh//загревана површина у m2/'
            END;
    ELSIF EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_schema = 'euk' 
                  AND table_name = 'ugrozeno_lice_t1' 
                  AND column_name = 'potrosnja_kwh') THEN
        -- Only potrosnja_kwh exists
        UPDATE euk.ugrozeno_lice_t1 
        SET potrosnja_i_povrsina_combined = 
            CASE 
                WHEN potrosnja_kwh IS NOT NULL THEN
                    CONCAT('Потрошња у kWh/', potrosnja_kwh::TEXT, '/загревана површина у m2/')
                ELSE
                    'Потрошња у kWh//загревана површина у m2/'
            END;
    ELSE
        -- No energy columns exist, set default values
        UPDATE euk.ugrozeno_lice_t1 
        SET potrosnja_i_povrsina_combined = 'Потрошња у kWh//загревана површина у m2/';
    END IF;
END $$;

-- KORAK 5: Drop both old columns (only if they exist)
ALTER TABLE euk.ugrozeno_lice_t1 DROP COLUMN IF EXISTS potrosnja_kwh;
ALTER TABLE euk.ugrozeno_lice_t1 DROP COLUMN IF EXISTS zagrevana_povrsina_m2;

-- KORAK 6: Drop old indexes
DROP INDEX IF EXISTS idx_euk_ugrozeno_lice_t1_potrosnja;
DROP INDEX IF EXISTS idx_euk_ugrozeno_lice_t1_povrsina;

-- KORAK 7: Create new index for the combined column
CREATE INDEX idx_euk_ugrozeno_lice_t1_potrosnja_povrsina_combined 
ON euk.ugrozeno_lice_t1(potrosnja_i_povrsina_combined);

-- KORAK 8: Add comment for documentation
COMMENT ON COLUMN euk.ugrozeno_lice_t1.potrosnja_i_povrsina_combined IS 'Kombinovana kolona: Потрошња у kWh/{kWh_value}/загревана површина у m2/{m2_value}';

-- KORAK 9: Recreate table with correct column order
-- Create new table with proper column positioning
CREATE TABLE euk.ugrozeno_lice_t1_new (
    ugrozeno_lice_id SERIAL PRIMARY KEY,
    redni_broj VARCHAR(20) NOT NULL,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    jmbg CHAR(13) UNIQUE NOT NULL,
    ptt_broj VARCHAR(10),
    grad_opstina VARCHAR(100),
    mesto VARCHAR(100),
    ulica_i_broj VARCHAR(200),
    broj_clanova_domacinstva INTEGER,
    osnov_sticanja_statusa VARCHAR(50),
    ed_broj_broj_mernog_uredjaja VARCHAR(100),
    potrosnja_i_povrsina_combined VARCHAR(200), -- Positioned between osnov_sticanja_statusa and iznos_umanjenja_sa_pdv
    iznos_umanjenja_sa_pdv NUMERIC(12,2),
    broj_racuna VARCHAR(50),
    datum_izdavanja_racuna DATE,
    datum_trajanja_prava DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Copy data from old table to new table
INSERT INTO euk.ugrozeno_lice_t1_new 
SELECT 
    ugrozeno_lice_id,
    redni_broj,
    ime,
    prezime,
    jmbg,
    ptt_broj,
    grad_opstina,
    mesto,
    ulica_i_broj,
    broj_clanova_domacinstva,
    osnov_sticanja_statusa,
    ed_broj_broj_mernog_uredjaja,
    potrosnja_i_povrsina_combined,
    iznos_umanjenja_sa_pdv,
    broj_racuna,
    datum_izdavanja_racuna,
    datum_trajanja_prava,
    created_at,
    updated_at
FROM euk.ugrozeno_lice_t1;

-- Drop old table and rename new one
DROP TABLE euk.ugrozeno_lice_t1;
ALTER TABLE euk.ugrozeno_lice_t1_new RENAME TO ugrozeno_lice_t1;

-- Recreate indexes
CREATE UNIQUE INDEX idx_euk_ugrozeno_lice_t1_jmbg ON euk.ugrozeno_lice_t1(jmbg);
CREATE INDEX idx_euk_ugrozeno_lice_t1_redni_broj ON euk.ugrozeno_lice_t1(redni_broj);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ime_prezime ON euk.ugrozeno_lice_t1(ime, prezime);
CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_opstina ON euk.ugrozeno_lice_t1(grad_opstina);
CREATE INDEX idx_euk_ugrozeno_lice_t1_mesto ON euk.ugrozeno_lice_t1(mesto);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ptt_broj ON euk.ugrozeno_lice_t1(ptt_broj);
CREATE INDEX idx_euk_ugrozeno_lice_t1_osnov_statusa ON euk.ugrozeno_lice_t1(osnov_sticanja_statusa);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ed_broj ON euk.ugrozeno_lice_t1(ed_broj_broj_mernog_uredjaja);
CREATE INDEX idx_euk_ugrozeno_lice_t1_potrosnja_povrsina_combined ON euk.ugrozeno_lice_t1(potrosnja_i_povrsina_combined);
CREATE INDEX idx_euk_ugrozeno_lice_t1_broj_racuna ON euk.ugrozeno_lice_t1(broj_racuna);
CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna ON euk.ugrozeno_lice_t1(datum_izdavanja_racuna);
CREATE INDEX idx_euk_ugrozeno_lice_t1_umanjenje ON euk.ugrozeno_lice_t1(iznos_umanjenja_sa_pdv);
CREATE INDEX idx_euk_ugrozeno_lice_t1_created_at ON euk.ugrozeno_lice_t1(created_at);
CREATE INDEX idx_euk_ugrozeno_lice_t1_updated_at ON euk.ugrozeno_lice_t1(updated_at);

-- Composite indexes
CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_mesto ON euk.ugrozeno_lice_t1(grad_opstina, mesto);
CREATE INDEX idx_euk_ugrozeno_lice_t1_status_ed ON euk.ugrozeno_lice_t1(osnov_sticanja_statusa, ed_broj_broj_mernog_uredjaja);
CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna_iznos ON euk.ugrozeno_lice_t1(datum_izdavanja_racuna, iznos_umanjenja_sa_pdv);

-- KORAK 10: Verify the changes
SELECT 
    ugrozeno_lice_id,
    ime,
    prezime,
    osnov_sticanja_statusa,
    potrosnja_i_povrsina_combined,
    iznos_umanjenja_sa_pdv
FROM euk.ugrozeno_lice_t1 
LIMIT 5;

-- KORAK 10: Optional - Create a view for backward compatibility (if needed)
-- CREATE OR REPLACE VIEW euk.ugrozeno_lice_t1_compat AS
-- SELECT 
--     ugrozeno_lice_id,
--     redni_broj,
--     ime,
--     prezime,
--     jmbg,
--     ptt_broj,
--     grad_opstina,
--     mesto,
--     ulica_i_broj,
--     broj_clanova_domacinstva,
--     osnov_sticanja_statusa,
--     ed_broj_broj_mernog_uredjaja,
--     -- Extract potrosnja_kwh from combined column
--     CASE 
--         WHEN potrosnja_i_povrsina_combined ~ 'Потрошња у kWh/[^/]+/' THEN
--             CAST(SPLIT_PART(SPLIT_PART(potrosnja_i_povrsina_combined, 'Потрошња у kWh/', 2), '/', 1) AS NUMERIC)
--         ELSE NULL
--     END as potrosnja_kwh_numeric,
--     -- Extract zagrevana_povrsina_m2 from combined column
--     CASE 
--         WHEN potrosnja_i_povrsina_combined ~ 'загревана површина у m2/[^/]*$' THEN
--             CAST(SPLIT_PART(SPLIT_PART(potrosnja_i_povrsina_combined, 'загревана површина у m2/', 2), '/', 1) AS NUMERIC)
--         ELSE NULL
--     END as zagrevana_povrsina_m2,
--     iznos_umanjenja_sa_pdv,
--     broj_racuna,
--     datum_izdavanja_racuna,
--     created_at,
--     updated_at
-- FROM euk.ugrozeno_lice_t1;
