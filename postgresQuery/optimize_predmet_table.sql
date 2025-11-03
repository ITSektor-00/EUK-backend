-- Optimizacija tabele predmet za velike baze podataka (1M+ zapisa)
-- Kreiranje indeksa za brže pretraživanje i filtriranje

-- Osnovni indeksi za glavne filtere
CREATE INDEX IF NOT EXISTS idx_predmet_id_organ_oj ON predmet(ID_Organ_OJ);
CREATE INDEX IF NOT EXISTS idx_predmet_id_odsek ON predmet(ID_Odsek);
CREATE INDEX IF NOT EXISTS idx_predmet_id_klasifikacija ON predmet(ID_Klasifikacija);
CREATE INDEX IF NOT EXISTS idx_predmet_id_vrsta_predmeta ON predmet(ID_Vrsta_Predmeta);
CREATE INDEX IF NOT EXISTS idx_predmet_id_opis_klasifikacije ON predmet(ID_Opis_Klasifikacije);
CREATE INDEX IF NOT EXISTS idx_predmet_id_opstina ON predmet(ID_Opstina);

-- Indeksi za tekstualne pretrage
CREATE INDEX IF NOT EXISTS idx_predmet_naziv_podnosioca ON predmet(Naziv_Podnosioca);
CREATE INDEX IF NOT EXISTS idx_predmet_maticni_broj ON predmet(Maticni_Broj);
CREATE INDEX IF NOT EXISTS idx_predmet_pib ON predmet(PIB);
CREATE INDEX IF NOT EXISTS idx_predmet_strani_broj ON predmet(Strani_Broj);
CREATE INDEX IF NOT EXISTS idx_predmet_jedinstveni_kod ON predmet(Jedinstveni_Kod);

-- Indeksi za datum i godinu
CREATE INDEX IF NOT EXISTS idx_predmet_datum_predmeta ON predmet(Datum_Predmeta);
CREATE INDEX IF NOT EXISTS idx_predmet_godina ON predmet(Godina);
CREATE INDEX IF NOT EXISTS idx_predmet_datum_unosa ON predmet(Datum_Unosa);

-- Indeksi za numeričke vrednosti
CREATE INDEX IF NOT EXISTS idx_predmet_vrednost_takse ON predmet(Vrednost_Takse);

-- Kompozitni indeksi za česte kombinacije filtera
CREATE INDEX IF NOT EXISTS idx_predmet_godina_organ ON predmet(Godina, ID_Organ_OJ);
CREATE INDEX IF NOT EXISTS idx_predmet_datum_organ ON predmet(Datum_Predmeta, ID_Organ_OJ);
CREATE INDEX IF NOT EXISTS idx_predmet_godina_odsek ON predmet(Godina, ID_Odsek);
CREATE INDEX IF NOT EXISTS idx_predmet_klasifikacija_vrsta ON predmet(ID_Klasifikacija, ID_Vrsta_Predmeta);

-- Indeksi za sortiranje
CREATE INDEX IF NOT EXISTS idx_predmet_id_predmet_desc ON predmet(ID_Predmet DESC);
CREATE INDEX IF NOT EXISTS idx_predmet_datum_predmeta_desc ON predmet(Datum_Predmeta DESC);
CREATE INDEX IF NOT EXISTS idx_predmet_vrednost_takse_desc ON predmet(Vrednost_Takse DESC);

-- Indeksi za tekstualne pretrage sa ILIKE (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_predmet_naziv_podnosioca_lower ON predmet(LOWER(Naziv_Podnosioca));
CREATE INDEX IF NOT EXISTS idx_predmet_maticni_broj_lower ON predmet(LOWER(Maticni_Broj));
CREATE INDEX IF NOT EXISTS idx_predmet_pib_lower ON predmet(LOWER(PIB));
CREATE INDEX IF NOT EXISTS idx_predmet_strani_broj_lower ON predmet(LOWER(Strani_Broj));
CREATE INDEX IF NOT EXISTS idx_predmet_jedinstveni_kod_lower ON predmet(LOWER(Jedinstveni_Kod));

-- Indeksi za napomene (ako se često pretražuju)
CREATE INDEX IF NOT EXISTS idx_predmet_napomena ON predmet(Napomena);
CREATE INDEX IF NOT EXISTS idx_predmet_dodatna_napomena ON predmet(Dodatna_Napomena);

-- Indeksi za tip podnosioca
CREATE INDEX IF NOT EXISTS idx_predmet_tip_podnosioca ON predmet(Tip_Podnosioca);

-- Indeksi za ekstemi ID
CREATE INDEX IF NOT EXISTS idx_predmet_ekstemi_id ON predmet(Ekstemi_ID_Podnosioca);

-- Indeksi za ovlašćeno lice
CREATE INDEX IF NOT EXISTS idx_predmet_id_ovlasceno_lice ON predmet(ID_Ovlasceno_Lice);

-- Indeksi za ID_Baza
CREATE INDEX IF NOT EXISTS idx_predmet_id_baza ON predmet(ID_Baza);

-- Indeksi za popis akata
CREATE INDEX IF NOT EXISTS idx_predmet_popis_akata ON predmet(Popis_Akata);

-- Indeksi za katastarske podatke
CREATE INDEX IF NOT EXISTS idx_predmet_katastarski_podaci ON predmet(Katastarski_Podaci);

-- Indeksi za adresu
CREATE INDEX IF NOT EXISTS idx_predmet_adresa ON predmet(Adresa);

-- Kompozitni indeksi za kompleksne pretrage
CREATE INDEX IF NOT EXISTS idx_predmet_godina_organ_odsek ON predmet(Godina, ID_Organ_OJ, ID_Odsek);
CREATE INDEX IF NOT EXISTS idx_predmet_datum_godina ON predmet(Datum_Predmeta, Godina);
CREATE INDEX IF NOT EXISTS idx_predmet_organ_odsek_klasifikacija ON predmet(ID_Organ_OJ, ID_Odsek, ID_Klasifikacija);

-- Indeksi za range pretrage
CREATE INDEX IF NOT EXISTS idx_predmet_datum_range ON predmet(Datum_Predmeta, ID_Predmet);
CREATE INDEX IF NOT EXISTS idx_predmet_vrednost_range ON predmet(Vrednost_Takse, ID_Predmet);

-- Indeksi za statistike
CREATE INDEX IF NOT EXISTS idx_predmet_godina_count ON predmet(Godina) WHERE Godina IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_predmet_organ_count ON predmet(ID_Organ_OJ) WHERE ID_Organ_OJ IS NOT NULL;

-- Analiza performansi tabele
ANALYZE predmet;

-- Statistike o indeksima
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'predmet'
ORDER BY indexname;

-- Provera veličine tabele i indeksa
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE tablename = 'predmet';

-- Provera korišćenja indeksa
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes 
WHERE tablename = 'predmet'
ORDER BY idx_scan DESC;
