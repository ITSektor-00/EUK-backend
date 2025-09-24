-- PostgreSQL script za upis 1000 test lica u euk.ugrozeno_lice_t1 tabelu
-- Generiše realistične podatke za testiranje

-- KORAK 1: Obriši sve postojeće podatke i restartuj ID sekvencu
DELETE FROM euk.ugrozeno_lice_t1;

-- Kreiraj sekvencu ako ne postoji i restartuj je
CREATE SEQUENCE IF NOT EXISTS euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq START WITH 1;
ALTER SEQUENCE euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq RESTART WITH 1;

-- KORAK 2: Generiši 1000 test lica
INSERT INTO euk.ugrozeno_lice_t1 (
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
    datum_trajanja_prava
)
SELECT 
    'TEST-' || LPAD(ROW_NUMBER() OVER()::TEXT, 4, '0') as redni_broj,
    imena[1 + (ROW_NUMBER() OVER() % array_length(imena, 1))] as ime,
    prezimena[1 + (ROW_NUMBER() OVER() % array_length(prezimena, 1))] as prezime,
    LPAD((ROW_NUMBER() OVER() + 1000000000000)::TEXT, 13, '0') as jmbg,
    ptt_brojevi[1 + (ROW_NUMBER() OVER() % array_length(ptt_brojevi, 1))] as ptt_broj,
    gradovi[1 + (ROW_NUMBER() OVER() % array_length(gradovi, 1))] as grad_opstina,
    mesta[1 + (ROW_NUMBER() OVER() % array_length(mesta, 1))] as mesto,
    ulice[1 + (ROW_NUMBER() OVER() % array_length(ulice, 1))] || ' ' || (1 + (ROW_NUMBER() OVER() % 200)) as ulica_i_broj,
    1 + (ROW_NUMBER() OVER() % 6) as broj_clanova_domacinstva,
    osnovi[1 + (ROW_NUMBER() OVER() % array_length(osnovi, 1))] as osnov_sticanja_statusa,
    'ED-' || LPAD((ROW_NUMBER() OVER() % 10000)::TEXT, 4, '0') as ed_broj_broj_mernog_uredjaja,
    'Потрошња у kWh/' || 
    (1500 + (ROW_NUMBER() OVER() % 3000))::TEXT || 
    '/загревана површина у m2/' || 
    (50 + (ROW_NUMBER() OVER() % 150))::TEXT as potrosnja_i_povrsina_combined,
    (500 + (ROW_NUMBER() OVER() % 2000))::NUMERIC(12,2) as iznos_umanjenja_sa_pdv,
    'RAC-' || LPAD((ROW_NUMBER() OVER() % 100000)::TEXT, 5, '0') as broj_racuna,
    CURRENT_DATE - INTERVAL '1 day' * (ROW_NUMBER() OVER() % 365) as datum_izdavanja_racuna,
    CURRENT_DATE + INTERVAL '1 day' * (ROW_NUMBER() OVER() % 365) as datum_trajanja_prava
FROM (
    SELECT 
        ARRAY['Marko', 'Petar', 'Nikola', 'Stefan', 'Miloš', 'Luka', 'Đorđe', 'Nemanja', 'Aleksandar', 'Vladimir',
              'Ana', 'Milica', 'Jovana', 'Teodora', 'Katarina', 'Jelena', 'Marija', 'Sofija', 'Elena', 'Tamara'] as imena,
        ARRAY['Marković', 'Petrović', 'Nikolić', 'Stefanović', 'Milošević', 'Lukić', 'Đorđević', 'Nemanjić', 'Aleksandrić', 'Vladimirović',
              'Marković', 'Petrović', 'Nikolić', 'Stefanović', 'Milošević', 'Lukić', 'Đorđević', 'Nemanjić', 'Aleksandrić', 'Vladimirović'] as prezimena,
        ARRAY['11000', '21000', '31000', '11070', '11080', '11090', '11100', '11110', '11120', '11130',
              '11140', '11150', '11160', '11170', '11180', '11190', '11200', '11210', '11220', '11230'] as ptt_brojevi,
        ARRAY['Beograd', 'Novi Sad', 'Niš', 'Kragujevac', 'Subotica', 'Zrenjanin', 'Pančevo', 'Kruševac', 'Kraljevo', 'Smederevo',
              'Leskovac', 'Vranje', 'Užice', 'Valjevo', 'Šabac', 'Sombor', 'Požarevac', 'Pirot', 'Zaječar', 'Bor'] as gradovi,
        ARRAY['Centar', 'Stari grad', 'Novi grad', 'Zvezdara', 'Vračar', 'Savski venac', 'Zemun', 'Palilula', 'Voždovac', 'Rakovica',
              'Čukarica', 'Mladenovac', 'Barajevo', 'Grocka', 'Obrenovac', 'Lazarevac', 'Sopot', 'Mladenovac', 'Barajevo', 'Grocka'] as mesta,
        ARRAY['Knez Mihailova', 'Terazije', 'Kralja Milana', 'Nemanjina', 'Kralja Aleksandra', 'Bulevar kralja Aleksandra', 'Bulevar Despota Stefana', 'Bulevar Oslobođenja', 'Bulevar Revolucije', 'Bulevar Zorana Đinđića',
              'Bulevar kralja Aleksandra', 'Bulevar Despota Stefana', 'Bulevar Oslobođenja', 'Bulevar Revolucije', 'Bulevar Zorana Đinđića', 'Bulevar kralja Aleksandra', 'Bulevar Despota Stefana', 'Bulevar Oslobođenja', 'Bulevar Revolucije', 'Bulevar Zorana Đinđića'] as ulice,
        ARRAY['Penzija', 'Invaliditet', 'Socijalna pomoć', 'Porodični dodatak', 'Dodatak za decu', 'Dodatak za nezaposlene', 'Dodatak za studente', 'Dodatak za penzionere', 'Dodatak za invalide', 'Dodatak za siromašne'] as osnovi
) as data
CROSS JOIN generate_series(1, 1000);

-- KORAK 3: Proveri da li su podaci uspešno upisani
SELECT 
    COUNT(*) as ukupno_lice,
    COUNT(CASE WHEN potrosnja_i_povrsina_combined IS NOT NULL THEN 1 END) as sa_energetskim_podacima,
    MIN(datum_izdavanja_racuna) as najstariji_račun,
    MAX(datum_izdavanja_racuna) as najnoviji_račun
FROM euk.ugrozeno_lice_t1 
WHERE redni_broj LIKE 'TEST%';

-- KORAK 4: Prikaži prvih 10 test lica
SELECT 
    redni_broj,
    ime,
    prezime,
    jmbg,
    grad_opstina,
    potrosnja_i_povrsina_combined,
    iznos_umanjenja_sa_pdv
FROM euk.ugrozeno_lice_t1 
WHERE redni_broj LIKE 'TEST%'
ORDER BY redni_broj
LIMIT 10;
