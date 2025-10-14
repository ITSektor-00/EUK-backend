-- PostgreSQL script za rešavanje problema sa JMBG kolonom
-- Problem: Hibernate pokušava da promeni tip na VARCHAR(13), ali postoje podaci duži od 13 karaktera

-- KORAK 1: Proveri maksimalnu dužinu postojećih JMBG vrednosti
SELECT 
    'CHECKING_JMBG_LENGTHS' as step,
    MAX(LENGTH(jmbg)) as max_jmbg_length,
    MIN(LENGTH(jmbg)) as min_jmbg_length,
    COUNT(*) as total_records,
    COUNT(CASE WHEN LENGTH(jmbg) > 13 THEN 1 END) as records_longer_than_13
FROM euk.ugrozeno_lice_t1;

-- KORAK 2: Prikaži sve JMBG vrednosti koje su duže od 13 karaktera
SELECT 
    'PROBLEMATIC_JMBG_VALUES' as step,
    jmbg,
    LENGTH(jmbg) as length,
    ime,
    prezime
FROM euk.ugrozeno_lice_t1 
WHERE LENGTH(jmbg) > 13
ORDER BY LENGTH(jmbg) DESC;

-- KORAK 3: Proširi JMBG kolonu da prihvati duže vrednosti
-- (Ovo će rešiti trenutnu grešku)
ALTER TABLE euk.ugrozeno_lice_t1 ALTER COLUMN jmbg TYPE VARCHAR(50);

-- KORAK 4: Proveri da li je kolona uspešno proširena
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'ugrozeno_lice_t1' 
    AND table_schema = 'euk'
    AND column_name = 'jmbg';

-- KORAK 5: Status
SELECT
    'JMBG_COLUMN_FIXED' as status,
    'JMBG column now accepts up to 50 characters' as message,
    CURRENT_TIMESTAMP as timestamp;
