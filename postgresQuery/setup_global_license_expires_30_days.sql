-- Setup Global License to Expire in 30 Days
-- This script creates a global license that expires in 30 days for testing

-- First, check if the global_license table exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'global_license') THEN
        RAISE EXCEPTION 'Table global_license does not exist. Please run create_global_license_table.sql first.';
    END IF; 
END $$;

-- Clear any existing global license
DELETE FROM global_license;

-- Insert a new global license that expires in 30 days
INSERT INTO global_license (
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at
) VALUES (
    'GLOBAL-LICENSE-2024-30DAYS',
    NOW(),
    NOW() + INTERVAL '30 days',
    true,
    false,
    NOW(),
    NOW()
);

-- Verify the license was created
SELECT 
    id,
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at,
    EXTRACT(DAYS FROM (end_date - NOW())) as days_until_expiry
FROM global_license;

-- Show license status
SELECT 
    CASE 
        WHEN NOW() > end_date THEN 'EXPIRED'
        WHEN NOW() BETWEEN (end_date - INTERVAL '30 days') AND end_date THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAYS FROM (end_date - NOW())) as days_until_expiry,
    end_date as expiration_date
FROM global_license;
