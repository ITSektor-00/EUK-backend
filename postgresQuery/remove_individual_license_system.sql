-- Brisanje individualnog licencnog sistema
-- Ovo će obrisati sve vezano za licenses tabelu i ostaviti samo global_license

-- 1. Obriši licenses tabelu i sve vezano za nju (ako postoji)
DROP TABLE IF EXISTS licenses CASCADE;

-- 2. Obriši funkcije vezane za licenses (ako postoje)
DROP FUNCTION IF EXISTS check_license_validity() CASCADE;
DROP FUNCTION IF EXISTS get_licenses_expiring_soon() CASCADE;
DROP FUNCTION IF EXISTS update_licenses_updated_at() CASCADE;

-- 3. Obriši trigger-e vezane za licenses (ako postoje)
DROP TRIGGER IF EXISTS update_licenses_updated_at ON licenses CASCADE;

-- 4. Proveri da li je licenses tabela obrisana
SELECT 'Licenses tabela obrisana:' as info;
SELECT table_name 
FROM information_schema.tables 
WHERE table_name = 'licenses';

-- 5. Proveri da li global_license tabela postoji
SELECT 'Global license tabela:' as info;
SELECT table_name 
FROM information_schema.tables 
WHERE table_name = 'global_license';

-- 6. Proveri da li postoje constraint-ovi za licenses
SELECT 'Constraint-ovi za licenses:' as info;
SELECT constraint_name, table_name, constraint_type
FROM information_schema.table_constraints 
WHERE table_name = 'licenses';

-- 7. Proveri da li postoje indexi za licenses
SELECT 'Indexi za licenses:' as info;
SELECT indexname, tablename 
FROM pg_indexes 
WHERE tablename = 'licenses';

-- 8. Proveri da li global_license radi
SELECT 'Test global_license:' as info;
SELECT * FROM check_global_license_validity();
