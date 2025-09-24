-- SQL skript za brisanje postojeće tabele ugrozeno_lice i kreiranje nove ugrozeno_lice_t1
-- Datum: $(date)
-- Opis: Kreiranje tabele ugrozeno_lice_t1 u euk schema sa novim kolonama za energetsku efikasnost

-- KORAK 1: Brisanje postojeće tabele ugrozeno_lice i svih povezanih objekata
-- PAŽNJA: Ova operacija će obrisati sve postojeće podatke u tabeli ugrozeno_lice!

-- Prvo brišemo indexe za postojeću tabelu ugrozeno_lice
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_jmbg;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_predmet;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_ime_prezime;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_datum_rodjenja;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_mesto;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_created_at;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_predmet_ime;
DROP INDEX IF EXISTS euk.idx_euk_ugrozeno_lice_datum_mesto;

-- Brisanje postojeće tabele ugrozeno_lice
DROP TABLE IF EXISTS euk.ugrozeno_lice;

-- KORAK 2: Kreiranje nove tabele ugrozeno_lice_t1 sa specifičnim kolonama
CREATE TABLE euk.ugrozeno_lice_t1 (
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
    
    -- Informacije o domaćinstvu
    broj_clanova_domacinstva INTEGER,
    
    -- Energetski status
    osnov_sticanja_statusa VARCHAR(50), -- MP, NSP, DD, UDTNP
    ed_broj_broj_mernog_uredjaja VARCHAR(100), -- ED broj/broj mernog uređaja za gas/šifra korisnika
    
    -- Energetski podaci
    potrosnja_kwh NUMERIC(10,2), -- Potrošnja u kWh
    zagrevana_povrsina_m2 NUMERIC(10,2), -- zagrevana površina u m2
    
    -- Finansijski podaci
    iznos_umanjenja_sa_pdv NUMERIC(12,2), -- Iznos umanjenja sa PDV
    broj_racuna VARCHAR(50),
    datum_izdavanja_racuna DATE,
    
    -- Audit kolone
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- KORAK 3: Kreiranje indexa za performanse
-- Osnovni indexi
CREATE UNIQUE INDEX idx_euk_ugrozeno_lice_t1_jmbg ON euk.ugrozeno_lice_t1(jmbg);
CREATE INDEX idx_euk_ugrozeno_lice_t1_redni_broj ON euk.ugrozeno_lice_t1(redni_broj);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ime_prezime ON euk.ugrozeno_lice_t1(ime, prezime);
CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_opstina ON euk.ugrozeno_lice_t1(grad_opstina);
CREATE INDEX idx_euk_ugrozeno_lice_t1_mesto ON euk.ugrozeno_lice_t1(mesto);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ptt_broj ON euk.ugrozeno_lice_t1(ptt_broj);

-- Energetski indexi
CREATE INDEX idx_euk_ugrozeno_lice_t1_osnov_statusa ON euk.ugrozeno_lice_t1(osnov_sticanja_statusa);
CREATE INDEX idx_euk_ugrozeno_lice_t1_ed_broj ON euk.ugrozeno_lice_t1(ed_broj_broj_mernog_uredjaja);
CREATE INDEX idx_euk_ugrozeno_lice_t1_potrosnja ON euk.ugrozeno_lice_t1(potrosnja_kwh);
CREATE INDEX idx_euk_ugrozeno_lice_t1_povrsina ON euk.ugrozeno_lice_t1(zagrevana_povrsina_m2);

-- Finansijski indexi
CREATE INDEX idx_euk_ugrozeno_lice_t1_broj_racuna ON euk.ugrozeno_lice_t1(broj_racuna);
CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna ON euk.ugrozeno_lice_t1(datum_izdavanja_racuna);
CREATE INDEX idx_euk_ugrozeno_lice_t1_umanjenje ON euk.ugrozeno_lice_t1(iznos_umanjenja_sa_pdv);

-- Audit indexi
CREATE INDEX idx_euk_ugrozeno_lice_t1_created_at ON euk.ugrozeno_lice_t1(created_at);
CREATE INDEX idx_euk_ugrozeno_lice_t1_updated_at ON euk.ugrozeno_lice_t1(updated_at);

-- Composite indexi za česte kombinacije pretrage
CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_mesto ON euk.ugrozeno_lice_t1(grad_opstina, mesto);
CREATE INDEX idx_euk_ugrozeno_lice_t1_status_ed ON euk.ugrozeno_lice_t1(osnov_sticanja_statusa, ed_broj_broj_mernog_uredjaja);
CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna_iznos ON euk.ugrozeno_lice_t1(datum_izdavanja_racuna, iznos_umanjenja_sa_pdv);

-- KORAK 4: Dodavanje komentara za dokumentaciju
COMMENT ON TABLE euk.ugrozeno_lice_t1 IS 'Tabela za evidenciju ugroženih lica sa energetskim podacima';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.redni_broj IS 'Redni broj u evidenciji';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.osnov_sticanja_statusa IS 'Osnov sticanja statusa (MP, NSP, DD, UDTNP)';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.ed_broj_broj_mernog_uredjaja IS 'ED broj/broj mernog uređaja za gas/šifra korisnika (identifikacioni broj)';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.potrosnja_kwh IS 'Potrošnja u kWh';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.zagrevana_povrsina_m2 IS 'Zagrevana površina u m2';
COMMENT ON COLUMN euk.ugrozeno_lice_t1.iznos_umanjenja_sa_pdv IS 'Iznos umanjenja sa PDV';

-- KORAK 5: Test podaci (opciono - zakomentariši ako ne želiš test podatke)
/*
INSERT INTO euk.ugrozeno_lice_t1 (
    redni_broj, ime, prezime, jmbg, ptt_broj, grad_opstina, mesto, 
    ulica_i_broj, broj_clanova_domacinstva, osnov_sticanja_statusa, 
    ed_broj_broj_mernog_uredjaja, potrosnja_kwh, zagrevana_povrsina_m2,
    iznos_umanjenja_sa_pdv, broj_racuna, datum_izdavanja_racuna
) VALUES 
('001', 'Marko', 'Marković', '1234567890123', '11000', 'Beograd', 'Stari grad', 
 'Knez Mihailova 1', 3, 'MP', 'ED123456', 2500.50, 75.5, 
 1250.25, 'RAC-2024-001', '2024-01-15'),
('002', 'Ana', 'Anić', '9876543210987', '21000', 'Novi Sad', 'Centar', 
 'Zmaj Jovina 15', 4, 'NSP', 'ED789012', 3200.75, 95.0, 
 1600.38, 'RAC-2024-002', '2024-01-16');
*/

-- KORAK 6: Verifikacija kreiranja tabele
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'euk' 
    AND table_name = 'ugrozeno_lice_t1'
ORDER BY ordinal_position;

-- Prikaz broja kreiranih indexa
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'euk' 
    AND tablename = 'ugrozeno_lice_t1'
ORDER BY indexname;

-- Poruka o uspešnom kreiranju tabele
DO $$
BEGIN
    RAISE NOTICE 'Tabela ugrozeno_lice_t1 je uspešno kreirana sa novim kolonama!';
END $$;
