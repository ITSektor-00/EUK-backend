-- Test podaci za ugrozeno_lice_t2 tabelu
-- Pokreni ovaj fajl nakon kreiranja tabele

-- Brisanje postojećih test podataka (opciono)
-- DELETE FROM euk.ugrozeno_lice_t2;

-- Dodavanje test podataka
INSERT INTO euk.ugrozeno_lice_t2 (
    redni_broj, 
    ime, 
    prezime, 
    jmbg, 
    ptt_broj, 
    grad_opstina, 
    mesto, 
    ulica_i_broj, 
    ed_broj, 
    pok_vazenja_resenja_o_statusu
) VALUES 
    ('001', 'Marko', 'Marković', '1234567890123', '11000', 'Beograd', 'Beograd', 'Knez Mihailova 1', 'ED123456', '01.01.2024 - 31.12.2024'),
    ('002', 'Ana', 'Anić', '2345678901234', '21000', 'Novi Sad', 'Novi Sad', 'Dunavska 15', 'ED234567', '01.01.2024 - 31.12.2024'),
    ('003', 'Petar', 'Petrović', '3456789012345', '18000', 'Niš', 'Niš', 'Obrenovićeva 25', 'ED345678', '01.02.2024 - 28.02.2025'),
    ('004', 'Milica', 'Milić', '4567890123456', '11000', 'Beograd', 'Zemun', 'Glavna 10', 'ED456789', '01.03.2024 - 31.03.2025'),
    ('005', 'Stefan', 'Stefanović', '5678901234567', '21000', 'Novi Sad', 'Petrovaradin', 'Dunavska 5', 'ED567890', '01.04.2024 - 30.04.2025');

-- Provera da li su podaci uspešno dodati
SELECT 
    ugrozeno_lice_id,
    redni_broj,
    ime,
    prezime,
    jmbg,
    grad_opstina,
    mesto,
    ed_broj,
    created_at
FROM euk.ugrozeno_lice_t2
ORDER BY ugrozeno_lice_id;

-- Prikaz broja zapisa
SELECT COUNT(*) as ukupno_zapisa FROM euk.ugrozeno_lice_t2;
