-- Brisanje svih lica iz T1 tabele i restartovanje ID sekvence
-- ⚠️  PAŽNJA: Ovo će obrisati SVE podatke iz ugrozeno_lice_t1 tabele!

-- 1. Brisanje svih podataka iz tabele
DELETE FROM euk.ugrozeno_lice_t1;

-- 2. Restartovanje sekvence za ugrozeno_lice_id da počne od 1
-- Proverava da li sekvenca postoji i kreira je ako ne postoji
DO $$
BEGIN
    -- Proverava da li sekvenca postoji
    IF NOT EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'euk' AND sequencename = 'ugrozeno_lice_t1_ugrozeno_lice_id_seq') THEN
        -- Kreira sekvencu ako ne postoji
        CREATE SEQUENCE euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq;
    END IF;
    
    -- Restartuje sekvencu na 1
    ALTER SEQUENCE euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq RESTART WITH 1;
    
    -- Postavlja sekvencu kao default vrednost za kolonu
    ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN ugrozeno_lice_id SET DEFAULT nextval('euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq');
END $$;

-- 3. Provera da li je sve u redu
SELECT 
    'Tabela je obrisana' as status,
    COUNT(*) as broj_zapisa
FROM euk.ugrozeno_lice_t1;

-- 4. Provera sekvence
SELECT 
    'Sekvenca je restartovana' as status,
    last_value,
    is_called
FROM euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq;

-- 5. Test - dodavanje jednog test zapisa da proverimo da li ID počinje od 1
INSERT INTO euk.ugrozeno_lice_t1 (
    redni_broj, ime, prezime, jmbg, ulica_i_broj, ptt_broj, 
    grad_opstina, mesto, osnov_sticanja_statusa, datum_izdavanja_racuna, 
    iznos_umanjenja_sa_pdv, broj_racuna, ed_broj_broj_mernog_uredjaja
) VALUES (
    'TEST-001', 'Test', 'Korisnik', '1234567890123', 'Test Ulica 1', '11000',
    'Beograd', 'Stari grad', 'MP', CURRENT_DATE, 1000.00, 'TEST-RACUN-001', 'ED-001'
);

-- 6. Provera da li je test zapis kreiran sa ID = 1
SELECT 
    'Test zapis kreiran' as status,
    ugrozeno_lice_id,
    ime,
    prezime
FROM euk.ugrozeno_lice_t1 
WHERE redni_broj = 'TEST-001';

-- 7. Brisanje test zapisa
DELETE FROM euk.ugrozeno_lice_t1 WHERE redni_broj = 'TEST-001';

-- 8. Finalna provera
SELECT 
    'Finalna provera' as status,
    COUNT(*) as broj_zapisa,
    'Tabela je prazna i spremna za nove podatke' as poruka
FROM euk.ugrozeno_lice_t1;
