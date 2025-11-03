-- Čišćenje individualnog licencnog sistema
-- Ova skripta sigurno briše sve vezano za individualni licencni sistem

-- 1. Proveri da li licenses tabela postoji
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'licenses') THEN
        RAISE NOTICE 'Licenses tabela postoji - brišem je...';
        DROP TABLE licenses CASCADE;
        RAISE NOTICE 'Licenses tabela obrisana.';
    ELSE
        RAISE NOTICE 'Licenses tabela ne postoji - nema šta da brišem.';
    END IF;
END $$;

-- 2. Obriši funkcije vezane za licenses (bez grešaka)
DO $$
BEGIN
    -- Proveri i obriši check_license_validity funkciju
    IF EXISTS (SELECT 1 FROM information_schema.routines WHERE routine_name = 'check_license_validity') THEN
        DROP FUNCTION check_license_validity() CASCADE;
        RAISE NOTICE 'check_license_validity funkcija obrisana.';
    END IF;
    
    -- Proveri i obriši get_licenses_expiring_soon funkciju
    IF EXISTS (SELECT 1 FROM information_schema.routines WHERE routine_name = 'get_licenses_expiring_soon') THEN
        DROP FUNCTION get_licenses_expiring_soon() CASCADE;
        RAISE NOTICE 'get_licenses_expiring_soon funkcija obrisana.';
    END IF;
    
    -- Proveri i obriši update_licenses_updated_at funkciju
    IF EXISTS (SELECT 1 FROM information_schema.routines WHERE routine_name = 'update_licenses_updated_at') THEN
        DROP FUNCTION update_licenses_updated_at() CASCADE;
        RAISE NOTICE 'update_licenses_updated_at funkcija obrisana.';
    END IF;
END $$;

-- 3. Proveri da li je licenses tabela obrisana
SELECT 'Status licenses tabele:' as info;
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'licenses') 
        THEN 'Licenses tabela još uvek postoji'
        ELSE 'Licenses tabela je obrisana'
    END as status;

-- 4. Proveri da li global_license tabela postoji
SELECT 'Status global_license tabele:' as info;
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'global_license') 
        THEN 'Global_license tabela postoji'
        ELSE 'Global_license tabela ne postoji - pokreni create_global_license_table.sql'
    END as status;

-- 5. Proveri da li global_license radi
SELECT 'Test global_license:' as info;
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'global_license') 
        THEN 'Global_license tabela postoji - testiraj funkcije'
        ELSE 'Global_license tabela ne postoji'
    END as status;
