-- Kreiraj rute tabelu za upravljanje rutama u sistemu
CREATE TABLE IF NOT EXISTS rute (
    ruta_id SERIAL PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    putanja VARCHAR(500) NOT NULL UNIQUE,
    opis TEXT,
    kategorija VARCHAR(100) NOT NULL,
    metoda VARCHAR(10) DEFAULT 'GET',
    je_aktivna BOOLEAN DEFAULT TRUE,
    zahteva_autentifikaciju BOOLEAN DEFAULT FALSE,
    zahteva_admin_role BOOLEAN DEFAULT FALSE,
    redosled_prikaza INTEGER DEFAULT 0,
    ikona VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Kreiraj indexe za brže pretrage
CREATE INDEX IF NOT EXISTS idx_rute_putanja ON rute(putanja);
CREATE INDEX IF NOT EXISTS idx_rute_kategorija ON rute(kategorija);
CREATE INDEX IF NOT EXISTS idx_rute_aktivna ON rute(je_aktivna);
CREATE INDEX IF NOT EXISTS idx_rute_redosled ON rute(redosled_prikaza);

-- Dodaj trigger za automatsko ažuriranje updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_rute_updated_at 
    BEFORE UPDATE ON rute 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Ubaci sve rute u sistem
INSERT INTO rute (naziv, putanja, opis, kategorija, metoda, je_aktivna, zahteva_autentifikaciju, zahteva_admin_role, redosled_prikaza, ikona) VALUES

-- ADMIN RUTE
('Korisnici', '/admin/korisnici', 'Upravljanje korisnicima sistema', 'admin', 'GET', true, true, true, 1, 'users'),
('Dodaj Korisnika', '/admin/korisnici/dodaj', 'Dodavanje novog korisnika', 'admin', 'POST', true, true, true, 2, 'user-plus'),
('Uredi Korisnika', '/admin/korisnici/uredi/:id', 'Uređivanje postojećeg korisnika', 'admin', 'PUT', true, true, true, 3, 'user-edit'),
('Obriši Korisnika', '/admin/korisnici/obrisi/:id', 'Brisanje korisnika iz sistema', 'admin', 'DELETE', true, true, true, 4, 'user-minus'),

-- EUK KATEGORIJE
('EUK Kategorije', '/euk/kategorije', 'Pregled svih EUK kategorija', 'euk', 'GET', true, false, false, 5, 'folder'),
('Dodaj Kategoriju', '/euk/kategorije/dodaj', 'Dodavanje nove EUK kategorije', 'euk', 'POST', true, false, false, 6, 'folder-plus'),
('Uredi Kategoriju', '/euk/kategorije/uredi/:id', 'Uređivanje postojeće kategorije', 'euk', 'PUT', true, false, false, 7, 'folder-edit'),
('Obriši Kategoriju', '/euk/kategorije/obrisi/:id', 'Brisanje EUK kategorije', 'euk', 'DELETE', true, false, false, 8, 'folder-minus'),

-- EUK PREDMETI
('EUK Predmeti', '/euk/predmeti', 'Pregled svih EUK predmeta', 'euk', 'GET', true, false, false, 9, 'file-text'),
('Dodaj Predmet', '/euk/predmeti/dodaj', 'Dodavanje novog EUK predmeta', 'euk', 'POST', true, false, false, 10, 'file-plus'),
('Uredi Predmet', '/euk/predmeti/uredi/:id', 'Uređivanje postojećeg predmeta', 'euk', 'PUT', true, false, false, 11, 'file-edit'),
('Obriši Predmet', '/euk/predmeti/obrisi/:id', 'Brisanje EUK predmeta', 'euk', 'DELETE', true, false, false, 12, 'file-minus'),
('Detalji Predmeta', '/euk/predmeti/:id', 'Detaljni prikaz EUK predmeta', 'euk', 'GET', true, false, false, 13, 'file-search'),

-- EUK UGROŽENA LICA
('EUK Ugrožena Lica', '/euk/ugrozena-lica', 'Pregled svih ugroženih lica', 'euk', 'GET', true, false, false, 14, 'users'),
('Dodaj Ugroženo Lice', '/euk/ugrozena-lica/dodaj', 'Dodavanje novog ugroženog lica', 'euk', 'POST', true, false, false, 15, 'user-plus'),
('Uredi Ugroženo Lice', '/euk/ugrozena-lica/uredi/:id', 'Uređivanje postojećeg ugroženog lica', 'euk', 'PUT', true, false, false, 16, 'user-edit'),
('Obriši Ugroženo Lice', '/euk/ugrozena-lica/obrisi/:id', 'Brisanje ugroženog lica', 'euk', 'DELETE', true, false, false, 17, 'user-minus'),
('Pretraga po JMBG', '/euk/ugrozena-lica/pretraga/:jmbg', 'Pretraga ugroženog lica po JMBG-u', 'euk', 'GET', true, false, false, 18, 'search'),

-- EUK ŠTAMPANJE
('EUK Štampanje', '/euk/stampanje', 'Glavna stranica za štampanje', 'euk', 'GET', true, false, false, 19, 'printer'),
('Štampaj Predmet', '/euk/stampanje/predmet/:id', 'Štampanje pojedinačnog predmeta', 'euk', 'GET', true, false, false, 20, 'file-text'),
('Štampaj Kategoriju', '/euk/stampanje/kategorija/:id', 'Štampanje predmeta po kategoriji', 'euk', 'GET', true, false, false, 21, 'folder'),
('Štampaj Izveštaj', '/euk/stampanje/izvestaj', 'Štampanje kompletnog izveštaja', 'euk', 'POST', true, false, false, 22, 'bar-chart'),
('Štampaj Ugroženo Lice', '/euk/stampanje/ugrozeno-lice/:id', 'Štampanje podataka o ugroženom licu', 'euk', 'GET', true, false, false, 23, 'user'),

-- API RUTE (za backend)
('API Korisnici', '/api/users', 'API za dohvatanje korisnika', 'api', 'GET', true, false, false, 24, 'database'),
('API Korisnik po ID', '/api/users/:id', 'API za dohvatanje korisnika po ID-u', 'api', 'GET', true, false, false, 25, 'database'),
('API EUK Kategorije', '/api/euk/kategorije', 'API za dohvatanje EUK kategorija', 'api', 'GET', true, false, false, 26, 'database'),
('API EUK Predmeti', '/api/euk/predmeti', 'API za dohvatanje EUK predmeta', 'api', 'GET', true, false, false, 27, 'database'),
('API EUK Ugrožena Lica', '/api/euk/ugrozena-lica', 'API za dohvatanje ugroženih lica', 'api', 'GET', true, false, false, 28, 'database'),

-- AUTHENTIKACIJA
('Prijava', '/auth/prijava', 'Stranica za prijavu korisnika', 'auth', 'GET', true, false, false, 29, 'log-in'),
('Registracija', '/auth/registracija', 'Stranica za registraciju korisnika', 'auth', 'GET', true, false, false, 30, 'user-plus'),
('Odjava', '/auth/odjava', 'Odjava korisnika iz sistema', 'auth', 'POST', true, true, false, 31, 'log-out'),

-- DASHBOARD
('Dashboard', '/dashboard', 'Glavna kontrolna tabla', 'dashboard', 'GET', true, true, false, 32, 'home'),
('Statistike', '/dashboard/statistike', 'Statistički pregled sistema', 'dashboard', 'GET', true, true, false, 33, 'bar-chart'),
('Aktivnost', '/dashboard/aktivnost', 'Pregled aktivnosti korisnika', 'dashboard', 'GET', true, true, false, 34, 'activity'),

-- POSTAVKE
('Postavke', '/postavke', 'Postavke sistema', 'postavke', 'GET', true, true, false, 35, 'settings'),
('Profil', '/postavke/profil', 'Uređivanje korisničkog profila', 'postavke', 'GET', true, true, false, 36, 'user'),
('Lozinka', '/postavke/lozinka', 'Promena lozinke', 'postavke', 'GET', true, true, false, 37, 'lock'),

-- POMOĆ
('Pomoć', '/pomoc', 'Centar za pomoć', 'pomoc', 'GET', true, false, false, 38, 'help-circle'),
('FAQ', '/pomoc/faq', 'Često postavljena pitanja', 'pomoc', 'GET', true, false, false, 39, 'help-circle'),
('Kontakt', '/pomoc/kontakt', 'Kontakt informacije', 'pomoc', 'GET', true, false, false, 40, 'mail')

ON CONFLICT (putanja) DO UPDATE SET
    naziv = EXCLUDED.naziv,
    opis = EXCLUDED.opis,
    kategorija = EXCLUDED.kategorija,
    metoda = EXCLUDED.metoda,
    je_aktivna = EXCLUDED.je_aktivna,
    zahteva_autentifikaciju = EXCLUDED.zahteva_autentifikaciju,
    zahteva_admin_role = EXCLUDED.zahteva_admin_role,
    redosled_prikaza = EXCLUDED.redosled_prikaza,
    ikona = EXCLUDED.ikona,
    updated_at = CURRENT_TIMESTAMP;

-- Kreiraj view za aktivne rute
CREATE OR REPLACE VIEW aktivne_rute AS
SELECT 
    ruta_id,
    naziv,
    putanja,
    opis,
    kategorija,
    metoda,
    zahteva_autentifikaciju,
    zahteva_admin_role,
    redosled_prikaza,
    ikona,
    created_at,
    updated_at
FROM rute 
WHERE je_aktivna = true
ORDER BY kategorija, redosled_prikaza;

-- Kreiraj view za rute po kategorijama
CREATE OR REPLACE VIEW rute_po_kategorijama AS
SELECT 
    kategorija,
    COUNT(*) as broj_ruta,
    COUNT(CASE WHEN zahteva_autentifikaciju = true THEN 1 END) as zahteva_auth,
    COUNT(CASE WHEN zahteva_admin_role = true THEN 1 END) as zahteva_admin
FROM rute 
WHERE je_aktivna = true
GROUP BY kategorija
ORDER BY kategorija;

-- Dodaj komentare na tabelu i kolone
COMMENT ON TABLE rute IS 'Tabela za upravljanje rutama u EUK sistemu';
COMMENT ON COLUMN rute.ruta_id IS 'Jedinstveni identifikator rute';
COMMENT ON COLUMN rute.naziv IS 'Naziv rute za prikaz u UI-u';
COMMENT ON COLUMN rute.putanja IS 'URL putanja rute';
COMMENT ON COLUMN rute.opis IS 'Detaljan opis rute';
COMMENT ON COLUMN rute.kategorija IS 'Kategorija rute (admin, euk, api, auth, dashboard, postavke, pomoc)';
COMMENT ON COLUMN rute.metoda IS 'HTTP metoda (GET, POST, PUT, DELETE)';
COMMENT ON COLUMN rute.je_aktivna IS 'Da li je ruta aktivna';
COMMENT ON COLUMN rute.zahteva_autentifikaciju IS 'Da li ruta zahteva prijavu korisnika';
COMMENT ON COLUMN rute.zahteva_admin_role IS 'Da li ruta zahteva admin rolu';
COMMENT ON COLUMN rute.redosled_prikaza IS 'Redosled prikaza rute u meniju';
COMMENT ON COLUMN rute.ikona IS 'Naziv ikone za rutu (Lucide React ikone)';
COMMENT ON COLUMN rute.created_at IS 'Datum kreiranja rute';
COMMENT ON COLUMN rute.updated_at IS 'Datum poslednjeg ažuriranja rute';

-- Prikaži sve rute
SELECT 
    ruta_id,
    naziv,
    putanja,
    kategorija,
    metoda,
    je_aktivna,
    zahteva_autentifikaciju,
    zahteva_admin_role,
    redosled_prikaza,
    ikona
FROM rute 
ORDER BY kategorija, redosled_prikaza;
