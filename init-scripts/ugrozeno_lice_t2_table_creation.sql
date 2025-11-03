-- Kreiranje tabele ugrozeno_lice_t2 u EUK šemi
-- Tabela za čuvanje podataka o ugroženim licima sa specifičnim kolonama

-- KORAK 1: Kreiranje tabele ugrozeno_lice_t2
CREATE TABLE euk.ugrozeno_lice_t2 (
    ugrozeno_lice_id SERIAL PRIMARY KEY,
    
    -- Osnovne informacije o licu
    redni_broj VARCHAR(20) NOT NULL,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    jmbg CHAR(13) UNIQUE NOT NULL,
    
    -- Adresne informacije
    ptt_broj VARCHAR(10),
    grad_opstina VARCHAR(100),
    mesto VARCHAR(100),
    ulica_i_broj VARCHAR(200),
    
    -- Energetski podaci
    ed_broj VARCHAR(100), -- ED broj
    
    -- Informacije o važenju rešenja
    pok_vazenja_resenja_o_statusu VARCHAR(200), -- Period važenja rešenja o statusu
    
    -- Audit kolone
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- KORAK 2: Kreiranje indexa za performanse
-- Osnovni indexi
CREATE UNIQUE INDEX idx_euk_ugrozeno_lice_t2_jmbg ON euk.ugrozeno_lice_t2(jmbg);
CREATE INDEX idx_euk_ugrozeno_lice_t2_redni_broj ON euk.ugrozeno_lice_t2(redni_broj);
CREATE INDEX idx_euk_ugrozeno_lice_t2_ime_prezime ON euk.ugrozeno_lice_t2(ime, prezime);
CREATE INDEX idx_euk_ugrozeno_lice_t2_grad_opstina ON euk.ugrozeno_lice_t2(grad_opstina);
CREATE INDEX idx_euk_ugrozeno_lice_t2_mesto ON euk.ugrozeno_lice_t2(mesto);
CREATE INDEX idx_euk_ugrozeno_lice_t2_ptt_broj ON euk.ugrozeno_lice_t2(ptt_broj);

-- Energetski indexi
CREATE INDEX idx_euk_ugrozeno_lice_t2_ed_broj ON euk.ugrozeno_lice_t2(ed_broj);
CREATE INDEX idx_euk_ugrozeno_lice_t2_pok_vazenja ON euk.ugrozeno_lice_t2(pok_vazenja_resenja_o_statusu);

-- Audit indexi
CREATE INDEX idx_euk_ugrozeno_lice_t2_created_at ON euk.ugrozeno_lice_t2(created_at);
CREATE INDEX idx_euk_ugrozeno_lice_t2_updated_at ON euk.ugrozeno_lice_t2(updated_at);

-- KORAK 3: Dodavanje komentara na tabelu i kolone
COMMENT ON TABLE euk.ugrozeno_lice_t2 IS 'Tabela za čuvanje podataka o ugroženim licima - verzija T2';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.ugrozeno_lice_id IS 'Primarni ključ - ID ugroženog lica';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.redni_broj IS 'Redni broj ugroženog lica';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.ime IS 'Ime ugroženog lica';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.prezime IS 'Prezime ugroženog lica';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.jmbg IS 'JMBG ugroženog lica (13 cifara)';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.ptt_broj IS 'PTT broj';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.grad_opstina IS 'Grad/Opština';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.mesto IS 'Mesto';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.ulica_i_broj IS 'Ulica i broj';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.ed_broj IS 'ED broj';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.pok_vazenja_resenja_o_statusu IS 'Period važenja rešenja o statusu';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.created_at IS 'Datum i vreme kreiranja zapisa';
COMMENT ON COLUMN euk.ugrozeno_lice_t2.updated_at IS 'Datum i vreme poslednje izmene zapisa';

-- KORAK 4: Kreiranje trigger-a za automatsko ažuriranje updated_at kolone
CREATE OR REPLACE FUNCTION update_ugrozeno_lice_t2_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_ugrozeno_lice_t2_updated_at
    BEFORE UPDATE ON euk.ugrozeno_lice_t2
    FOR EACH ROW
    EXECUTE FUNCTION update_ugrozeno_lice_t2_updated_at();

-- KORAK 5: Dodavanje test podataka (opciono)
-- INSERT INTO euk.ugrozeno_lice_t2 (redni_broj, ime, prezime, jmbg, ptt_broj, grad_opstina, mesto, ulica_i_broj, ed_broj, pok_vazenja_resenja_o_statusu)
-- VALUES 
--     ('001', 'Marko', 'Marković', '1234567890123', '11000', 'Beograd', 'Beograd', 'Knez Mihailova 1', 'ED123456', '01.01.2024 - 31.12.2024'),
--     ('002', 'Ana', 'Anić', '2345678901234', '21000', 'Novi Sad', 'Novi Sad', 'Dunavska 15', 'ED234567', '01.01.2024 - 31.12.2024');

-- KORAK 6: Provera kreiranja tabele
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'euk' 
    AND table_name = 'ugrozeno_lice_t2'
ORDER BY ordinal_position;
