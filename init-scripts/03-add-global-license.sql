-- Add global license with current date as start date and 1 year validity
-- License key will be generated based on current timestamp

INSERT INTO public.global_license (
    license_key, 
    start_date, 
    end_date, 
    is_active, 
    notification_sent, 
    created_at, 
    updated_at
) VALUES (
    'EUK-' || EXTRACT(YEAR FROM CURRENT_TIMESTAMP) || '-' || LPAD(EXTRACT(DOY FROM CURRENT_TIMESTAMP)::TEXT, 3, '0') || '-' || LPAD(EXTRACT(HOUR FROM CURRENT_TIMESTAMP)::TEXT, 2, '0') || LPAD(EXTRACT(MINUTE FROM CURRENT_TIMESTAMP)::TEXT, 2, '0'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (license_key) DO NOTHING;
