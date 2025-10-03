-- =====================================================
-- SISTEM FORMULARE - DATABASE SCHEMA
-- =====================================================

-- 1. Tabela za formulare
CREATE TABLE IF NOT EXISTS euk.formular (
    formular_id SERIAL PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT,
    kategorija_id INTEGER REFERENCES euk.kategorija(kategorija_id) ON DELETE CASCADE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    datum_azuriranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aktivna BOOLEAN DEFAULT TRUE,
    verzija INTEGER DEFAULT 1,
    created_by INTEGER REFERENCES users(id),
    updated_by INTEGER REFERENCES users(id)
);

-- 2. Tabela za polja formulare
CREATE TABLE IF NOT EXISTS euk.formular_polja (
    polje_id SERIAL PRIMARY KEY,
    formular_id INTEGER REFERENCES euk.formular(formular_id) ON DELETE CASCADE,
    naziv_polja VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    tip_polja VARCHAR(50) NOT NULL, -- 'text', 'textarea', 'number', 'date', 'datetime', 'select', 'checkbox', 'radio', 'file'
    obavezno BOOLEAN DEFAULT FALSE,
    redosled INTEGER NOT NULL DEFAULT 0,
    placeholder VARCHAR(255),
    opis TEXT,
    validacija TEXT, -- JSON string za validaciju (min, max, pattern, etc.)
    opcije TEXT, -- JSON string za select/checkbox/radio opcije
    default_vrednost TEXT,
    readonly BOOLEAN DEFAULT FALSE,
    visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabela za podatke formulare (vrednosti)
CREATE TABLE IF NOT EXISTS euk.predmet_formular_podaci (
    podatak_id SERIAL PRIMARY KEY,
    predmet_id INTEGER REFERENCES euk.predmet(predmet_id) ON DELETE CASCADE,
    polje_id INTEGER REFERENCES euk.formular_polja(polje_id) ON DELETE CASCADE,
    vrednost TEXT,
    datum_unosa TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unio_korisnik INTEGER REFERENCES users(id),
    UNIQUE(predmet_id, polje_id)
);

-- 4. Tabela za istoriju izmena formulare
CREATE TABLE IF NOT EXISTS euk.formular_istorija (
    istorija_id SERIAL PRIMARY KEY,
    formular_id INTEGER REFERENCES euk.formular(formular_id) ON DELETE CASCADE,
    akcija VARCHAR(50) NOT NULL, -- 'created', 'updated', 'field_added', 'field_removed', 'field_updated'
    opis TEXT,
    korisnik_id INTEGER REFERENCES users(id),
    datum TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stara_vrednost TEXT,
    nova_vrednost TEXT
);

-- 5. Tabela za priložene dokumente formulare
CREATE TABLE IF NOT EXISTS euk.formular_dokumenti (
    dokument_id SERIAL PRIMARY KEY,
    predmet_id INTEGER REFERENCES euk.predmet(predmet_id) ON DELETE CASCADE,
    polje_id INTEGER REFERENCES euk.formular_polja(polje_id) ON DELETE CASCADE,
    originalno_ime VARCHAR(255) NOT NULL,
    putanja VARCHAR(500) NOT NULL,
    tip_fajla VARCHAR(100),
    velicina_fajla BIGINT,
    datum_uploada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    upload_korisnik INTEGER REFERENCES users(id)
);

-- =====================================================
-- INDEXI ZA PERFORMANSE
-- =====================================================

-- Osnovni indexi
CREATE INDEX IF NOT EXISTS idx_formular_kategorija ON euk.formular(kategorija_id);
CREATE INDEX IF NOT EXISTS idx_formular_aktivna ON euk.formular(aktivna);
CREATE INDEX IF NOT EXISTS idx_formular_datum_kreiranja ON euk.formular(datum_kreiranja);

CREATE INDEX IF NOT EXISTS idx_formular_polja_formular ON euk.formular_polja(formular_id);
CREATE INDEX IF NOT EXISTS idx_formular_polja_redosled ON euk.formular_polja(redosled);
CREATE INDEX IF NOT EXISTS idx_formular_polja_tip ON euk.formular_polja(tip_polja);

CREATE INDEX IF NOT EXISTS idx_predmet_formular_podaci_predmet ON euk.predmet_formular_podaci(predmet_id);
CREATE INDEX IF NOT EXISTS idx_predmet_formular_podaci_polje ON euk.predmet_formular_podaci(polje_id);
CREATE INDEX IF NOT EXISTS idx_predmet_formular_podaci_datum ON euk.predmet_formular_podaci(datum_unosa);

CREATE INDEX IF NOT EXISTS idx_formular_istorija_formular ON euk.formular_istorija(formular_id);
CREATE INDEX IF NOT EXISTS idx_formular_istorija_datum ON euk.formular_istorija(datum);

CREATE INDEX IF NOT EXISTS idx_formular_dokumenti_predmet ON euk.formular_dokumenti(predmet_id);
CREATE INDEX IF NOT EXISTS idx_formular_dokumenti_polje ON euk.formular_dokumenti(polje_id);

-- =====================================================
-- TRIGGERI ZA AUTOMATSKO AŽURIRANJE
-- =====================================================

-- Trigger za ažuriranje updated_at u formular tabeli
CREATE OR REPLACE FUNCTION update_formular_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.datum_azuriranja = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_formular_updated_at
    BEFORE UPDATE ON euk.formular
    FOR EACH ROW
    EXECUTE FUNCTION update_formular_updated_at();

-- Trigger za ažuriranje updated_at u formular_polja tabeli
CREATE OR REPLACE FUNCTION update_formular_polja_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_formular_polja_updated_at
    BEFORE UPDATE ON euk.formular_polja
    FOR EACH ROW
    EXECUTE FUNCTION update_formular_polja_updated_at();

-- =====================================================
-- TEST PODACI
-- =====================================================

-- Kreiraj test formulare za postojeće kategorije
INSERT INTO euk.formular (naziv, opis, kategorija_id, aktivna) VALUES
    ('Formular za Krivična dela', 'Osnovni formular za krivična dela', 1, true),
    ('Formular za Građanske sporove', 'Formular za građanske sporove', 2, true),
    ('Formular za Upravne sporove', 'Formular za upravne sporove', 3, true),
    ('Formular za Privredne sporove', 'Formular za privredne sporove', 4, true)
ON CONFLICT DO NOTHING;

-- Kreiraj test polja za prvi formular (Krivična dela)
INSERT INTO euk.formular_polja (formular_id, naziv_polja, label, tip_polja, obavezno, redosled, opis) VALUES
    (1, 'mesto_delicta', 'Mesto delikta', 'text', true, 1, 'Mesto gde je počinjen delikt'),
    (1, 'vreme_delicta', 'Vreme delikta', 'datetime', true, 2, 'Datum i vreme kada je počinjen delikt'),
    (1, 'okrivljeni_osoba', 'Osoba okrivljena', 'text', true, 3, 'Ime i prezime okrivljene osobe'),
    (1, 'okrivljeni_jmbg', 'JMBG okrivljene osobe', 'text', false, 4, 'JMBG okrivljene osobe'),
    (1, 'tip_delicta', 'Tip delikta', 'select', true, 5, 'Kategorija delikta'),
    (1, 'ozbiljnost', 'Ozbiljnost delikta', 'radio', true, 6, 'Ozbiljnost delikta'),
    (1, 'dokazi', 'Dokazi', 'textarea', false, 7, 'Opis dokaza'),
    (1, 'prilozi', 'Prilozi', 'file', false, 8, 'Priloženi dokumenti')
ON CONFLICT DO NOTHING;

-- Kreiraj test polja za drugi formular (Građanski sporovi)
INSERT INTO euk.formular_polja (formular_id, naziv_polja, label, tip_polja, obavezno, redosled, opis) VALUES
    (2, 'tuzenik', 'Tuženik', 'text', true, 1, 'Ime i prezime tuženika'),
    (2, 'tuzilac', 'Tužilac', 'text', true, 2, 'Ime i prezime tužioca'),
    (2, 'predmet_spora', 'Predmet spora', 'textarea', true, 3, 'Opis predmeta spora'),
    (2, 'iznos_spora', 'Iznos spora', 'number', false, 4, 'Novčani iznos spora'),
    (2, 'datum_spora', 'Datum nastanka spora', 'date', true, 5, 'Kada je nastao spor'),
    (2, 'tip_spora', 'Tip spora', 'select', true, 6, 'Kategorija spora')
ON CONFLICT DO NOTHING;

-- =====================================================
-- VIEW ZA LAKŠE PRETRAGE
-- =====================================================

-- View za kompletne informacije o formulare
CREATE OR REPLACE VIEW euk.formular_detalji AS
SELECT 
    f.formular_id,
    f.naziv as formular_naziv,
    f.opis as formular_opis,
    f.datum_kreiranja,
    f.datum_azuriranja,
    f.aktivna,
    f.verzija,
    k.kategorija_id,
    k.naziv as kategorija_naziv,
    k.skracenica as kategorija_skracenica,
    COUNT(fp.polje_id) as broj_polja
FROM euk.formular f
LEFT JOIN euk.kategorija k ON f.kategorija_id = k.kategorija_id
LEFT JOIN euk.formular_polja fp ON f.formular_id = fp.formular_id
GROUP BY f.formular_id, f.naziv, f.opis, f.datum_kreiranja, f.datum_azuriranja, f.aktivna, f.verzija, k.kategorija_id, k.naziv, k.skracenica;

-- View za polja formulare sa detaljima
CREATE OR REPLACE VIEW euk.formular_polja_detalji AS
SELECT 
    fp.polje_id,
    fp.formular_id,
    f.naziv as formular_naziv,
    fp.naziv_polja,
    fp.label,
    fp.tip_polja,
    fp.obavezno,
    fp.redosled,
    fp.placeholder,
    fp.opis,
    fp.validacija,
    fp.opcije,
    fp.default_vrednost,
    fp.readonly,
    fp.visible
FROM euk.formular_polja fp
JOIN euk.formular f ON fp.formular_id = f.formular_id
WHERE f.aktivna = true
ORDER BY fp.formular_id, fp.redosled;

-- =====================================================
-- FUNKCIJE ZA POMOĆ
-- =====================================================

-- Funkcija za dohvatanje formulare po kategoriji
CREATE OR REPLACE FUNCTION euk.get_formular_by_kategorija(kategorija_id_param INTEGER)
RETURNS TABLE (
    formular_id INTEGER,
    naziv VARCHAR,
    opis TEXT,
    datum_kreiranja TIMESTAMP,
    aktivna BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT f.formular_id, f.naziv, f.opis, f.datum_kreiranja, f.aktivna
    FROM euk.formular f
    WHERE f.kategorija_id = kategorija_id_param AND f.aktivna = true
    ORDER BY f.datum_kreiranja DESC;
END;
$$ LANGUAGE plpgsql;

-- Funkcija za dohvatanje polja formulare
CREATE OR REPLACE FUNCTION euk.get_formular_polja(formular_id_param INTEGER)
RETURNS TABLE (
    polje_id INTEGER,
    naziv_polja VARCHAR,
    label VARCHAR,
    tip_polja VARCHAR,
    obavezno BOOLEAN,
    redosled INTEGER,
    placeholder VARCHAR,
    opis TEXT,
    validacija TEXT,
    opcije TEXT,
    default_vrednost TEXT,
    readonly BOOLEAN,
    visible BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT fp.polje_id, fp.naziv_polja, fp.label, fp.tip_polja, fp.obavezno, fp.redosled,
           fp.placeholder, fp.opis, fp.validacija, fp.opcije, fp.default_vrednost, fp.readonly, fp.visible
    FROM euk.formular_polja fp
    WHERE fp.formular_id = formular_id_param
    ORDER BY fp.redosled;
END;
$$ LANGUAGE plpgsql;
