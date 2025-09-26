-- Simple INSERT for 3000 test records
-- Execute this in PostgreSQL to insert test data

-- Clear existing test data first
DELETE FROM euk.ugrozeno_lice_t1 WHERE redni_broj LIKE 'RB%';

-- Insert 3000 test records using a loop
DO $$
DECLARE
    i INTEGER;
    first_names TEXT[] := ARRAY['Marko', 'Ana', 'Petar', 'Milica', 'Stefan', 'Jovana', 'Nikola', 'Sara', 'Luka', 'Maja', 'Aleksandar', 'Jelena', 'Miloš', 'Tamara', 'Nemanja', 'Katarina', 'Dušan', 'Milena', 'Vladimir', 'Jasmina'];
    last_names TEXT[] := ARRAY['Marković', 'Anić', 'Petrović', 'Milić', 'Stefanović', 'Jovanović', 'Nikolić', 'Sarić', 'Lukić', 'Majić', 'Aleksandrić', 'Jelenić', 'Milošević', 'Tamarović', 'Nemanjić', 'Katarinić', 'Dušanić', 'Milenić', 'Vladimirović', 'Jasminić'];
    cities TEXT[] := ARRAY['Beograd', 'Novi Sad', 'Niš', 'Kragujevac', 'Subotica', 'Zrenjanin', 'Pančevo', 'Čačak', 'Kraljevo', 'Smederevo', 'Leskovac', 'Valjevo', 'Kruševac', 'Vranje', 'Šabac', 'Užice', 'Sombor', 'Požarevac', 'Pirot', 'Zaječar'];
    streets TEXT[] := ARRAY['Knez Mihailova', 'Terazije', 'Kralja Milana', 'Nemanjina', 'Bulevar Kralja Aleksandra', 'Vračar', 'Zvezdara', 'Palilula', 'Stari Grad', 'Savski Venac', 'Novi Beograd', 'Zemun', 'Voždovac', 'Rakovica', 'Mladenovac'];
    statuses TEXT[] := ARRAY['MP', 'NSP', 'DD', 'UDTNP'];
    first_name TEXT;
    last_name TEXT;
    jmbg TEXT;
    redni_broj TEXT;
    ptt_broj TEXT;
    city TEXT;
    street TEXT;
    street_number INTEGER;
    household_members INTEGER;
    status TEXT;
    ed_number TEXT;
    consumption DECIMAL;
    area DECIMAL;
    reduction_amount DECIMAL;
    account_number TEXT;
    issue_date DATE;
    expiry_date DATE;
BEGIN
    FOR i IN 1..3000 LOOP
        -- Generate random data
        first_name := first_names[1 + (random() * (array_length(first_names, 1) - 1))::INTEGER];
        last_name := last_names[1 + (random() * (array_length(last_names, 1) - 1))::INTEGER];
        jmbg := LPAD((1000000000000 + (random() * 8999999999999))::BIGINT::TEXT, 13, '0');
        redni_broj := 'RB' || LPAD(i::TEXT, 4, '0');
        ptt_broj := LPAD((10000 + (random() * 89999))::INTEGER::TEXT, 5, '0');
        city := cities[1 + (random() * (array_length(cities, 1) - 1))::INTEGER];
        street := streets[1 + (random() * (array_length(streets, 1) - 1))::INTEGER];
        street_number := (1 + (random() * 199))::INTEGER;
        household_members := (1 + (random() * 5))::INTEGER;
        status := statuses[1 + (random() * (array_length(statuses, 1) - 1))::INTEGER];
        ed_number := 'ED' || LPAD(i::TEXT, 4, '0');
        consumption := ROUND((1000 + (random() * 4000))::DECIMAL, 2);
        area := ROUND((40 + (random() * 110))::DECIMAL, 1);
        reduction_amount := ROUND((1000 + (random() * 9000))::DECIMAL, 2);
        account_number := 'RAC' || LPAD(i::TEXT, 4, '0');
        issue_date := CURRENT_DATE - (random() * 365)::INTEGER;
        expiry_date := issue_date + INTERVAL '1 year';
        
        -- Insert record
        INSERT INTO euk.ugrozeno_lice_t1 (
            redni_broj, ime, prezime, jmbg, ptt_broj, grad_opstina, mesto,
            ulica_i_broj, broj_clanova_domacinstva, osnov_sticanja_statusa,
            ed_broj_broj_mernog_uredjaja, potrosnja_i_povrsina_combined,
            iznos_umanjenja_sa_pdv, broj_racuna, datum_izdavanja_racuna,
            datum_trajanja_prava, created_at, updated_at
        ) VALUES (
            redni_broj, first_name, last_name, jmbg, ptt_broj, city, city,
            street || ' ' || street_number, household_members, status,
            ed_number, 'Potrošnja u kWh/' || consumption || '/zagrevana površina u m2/' || area, 
            reduction_amount, account_number, issue_date, expiry_date, NOW(), NOW()
        );
        
        -- Progress indicator every 100 records
        IF i % 100 = 0 THEN
            RAISE NOTICE 'Inserted % records...', i;
        END IF;
    END LOOP;
    
    RAISE NOTICE 'Successfully inserted 3000 test records!';
END $$;
