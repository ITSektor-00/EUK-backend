-- Dodavanje kolone za čuvanje generisanog "ODBIJA SE NSP" dokumenta
-- Datum: 2025-10-09

-- Dodaj kolonu za čuvanje Word dokumenta (.docx)
ALTER TABLE euk.predmet 
ADD COLUMN IF NOT EXISTS odbija_se_nsp_dokument BYTEA;

-- Dodaj kolonu za ime fajla
ALTER TABLE euk.predmet 
ADD COLUMN IF NOT EXISTS odbija_se_nsp_dokument_naziv VARCHAR(255);

-- Dodaj kolonu za datum generisanja dokumenta
ALTER TABLE euk.predmet 
ADD COLUMN IF NOT EXISTS odbija_se_nsp_dokument_datum TIMESTAMP;

-- Dodaj komentar
COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument IS 'Generisani Word dokument (ODBIJA SE NSP,UNSP,DD,UDTNP) u .docx formatu';
COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument_naziv IS 'Naziv generisanog dokumenta';
COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument_datum IS 'Datum i vreme kada je dokument generisan';

