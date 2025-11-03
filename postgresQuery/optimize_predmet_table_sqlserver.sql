-- Optimizacija tabele predmet za SQL Server bazu podataka (1M+ zapisa)
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

-- Indeksi za tekstualne pretrage sa LIKE (case-insensitive)
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

-- Indeksi za popis akata (bit polje)
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
UPDATE STATISTICS predmet;

-- Statistike o indeksima
SELECT 
    t.name AS table_name,
    i.name AS index_name,
    i.type_desc,
    i.is_unique,
    i.is_primary_key,
    i.is_unique_constraint
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
WHERE t.name = 'predmet'
ORDER BY i.name;

-- Provera veličine tabele i indeksa
SELECT 
    t.name AS table_name,
    p.rows AS row_count,
    CAST(ROUND(((SUM(a.total_pages) * 8) / 1024.00), 2) AS NUMERIC(36, 2)) AS total_space_mb,
    CAST(ROUND(((SUM(a.used_pages) * 8) / 1024.00), 2) AS NUMERIC(36, 2)) AS used_space_mb
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
INNER JOIN sys.partitions p ON i.object_id = p.object_id AND i.index_id = p.index_id
INNER JOIN sys.allocation_units a ON p.partition_id = a.container_id
WHERE t.name = 'predmet'
GROUP BY t.name, p.rows;

-- Provera korišćenja indeksa
SELECT 
    t.name AS table_name,
    i.name AS index_name,
    s.user_seeks,
    s.user_scans,
    s.user_lookups,
    s.user_updates,
    s.last_user_seek,
    s.last_user_scan,
    s.last_user_lookup
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
LEFT JOIN sys.dm_db_index_usage_stats s ON i.object_id = s.object_id AND i.index_id = s.index_id
WHERE t.name = 'predmet'
ORDER BY s.user_seeks + s.user_scans + s.user_lookups DESC;

-- Provera fragmentacije indeksa
SELECT 
    t.name AS table_name,
    i.name AS index_name,
    s.avg_fragmentation_in_percent,
    s.page_count,
    s.avg_page_space_used_in_percent
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
INNER JOIN sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'LIMITED') s ON i.object_id = s.object_id AND i.index_id = s.index_id
WHERE t.name = 'predmet'
ORDER BY s.avg_fragmentation_in_percent DESC;
