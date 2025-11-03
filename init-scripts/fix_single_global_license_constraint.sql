-- Fix Single Global License Constraint
-- This script removes the constraint that prevents multiple global licenses

-- First, check if the constraint exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'single_global_license' 
        AND table_name = 'global_license'
    ) THEN
        -- Drop the constraint
        ALTER TABLE global_license DROP CONSTRAINT single_global_license;
        RAISE NOTICE 'Constraint single_global_license dropped successfully';
    ELSE
        RAISE NOTICE 'Constraint single_global_license does not exist';
    END IF;
END $$;

-- Verify the constraint was dropped
SELECT 
    constraint_name, 
    constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'global_license' 
AND constraint_name = 'single_global_license';
