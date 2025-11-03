-- Kreiraj jednostavnu rute tabelu za EUK sistem
CREATE TABLE IF NOT EXISTS rute (
    naziv VARCHAR(255) NOT NULL PRIMARY KEY,
    zahteva_admin_role BOOLEAN DEFAULT FALSE
);

-- Ubaci sve rute
INSERT INTO rute (naziv, zahteva_admin_role) VALUES

-- ADMIN RUTE
('/admin/korisnici', true),
('/admin/korisnici/dodaj', true),
('/admin/korisnici/uredi/:id', true),
('/admin/korisnici/obrisi/:id', true),

-- EUK KATEGORIJE
('/euk/kategorije', false),
('/euk/kategorije/dodaj', false),
('/euk/kategorije/uredi/:id', false),
('/euk/kategorije/obrisi/:id', false),

-- EUK PREDMETI
('/euk/predmeti', false),
('/euk/predmeti/dodaj', false),
('/euk/predmeti/uredi/:id', false),
('/euk/predmeti/obrisi/:id', false),
('/euk/predmeti/:id', false),

-- EUK UGROŽENA LICA
('/euk/ugrozena-lica', false),
('/euk/ugrozena-lica/dodaj', false),
('/euk/ugrozena-lica/uredi/:id', false),
('/euk/ugrozena-lica/obrisi/:id', false),
('/euk/ugrozena-lica/pretraga/:jmbg', false),

-- EUK ŠTAMPANJE
('/euk/stampanje', false),
('/euk/stampanje/predmet/:id', false),
('/euk/stampanje/kategorija/:id', false),
('/euk/stampanje/izvestaj', false),
('/euk/stampanje/ugrozeno-lice/:id', false),

-- API RUTE
('/api/users', false),
('/api/euk/kategorije', false),
('/api/euk/predmeti', false),
('/api/euk/ugrozena-lica', false)

ON CONFLICT (naziv) DO NOTHING;

-- Prikaži sve rute
SELECT * FROM rute ORDER BY naziv;
