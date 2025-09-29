-- Setup Global License as EXPIRED
-- This script creates a global license that is already expired for testing

-- First, check if the global_license table exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'global_license') THEN
        RAISE EXCEPTION 'Table global_license does not exist. Please run create_global_license_table.sql first.';
    END IF;
END $$;

-- Clear any existing global license
DELETE FROM global_license;

-- Insert an expired global license (expired 1 day ago)
INSERT INTO global_license (
    license_key,
    start_date,
    end_date,
    is_active,
    notification_sent,
    created_at,
    updated_at
) VALUES (
    'GLOBAL-LICENSE-2024-EXPIRED',
    NOW() - INTERVAL '13 months',  -- Started 13 months ago
    NOW() - INTERVAL '1 day',      -- Expired 1 day ago
    false,                         -- Not active
    true,                          -- Notification already sent
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 day'
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
    EXTRACT(DAYS FROM (NOW() - end_date)) as days_since_expiry
FROM global_license;

-- Show license status
SELECT 
    CASE 
        WHEN NOW() > end_date THEN 'EXPIRED'
        WHEN NOW() BETWEEN (end_date - INTERVAL '30 days') AND end_date THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAYS FROM (NOW() - end_date)) as days_since_expiry,
    end_date as expiration_date,
    is_active as is_active
FROM global_license;
