-- Fix for any potential column mapping issues

-- Ensure phone_number column exists in addresses table with correct type
DO $$
BEGIN
    -- Check if phonenumber column exists (from possible previous version)
    IF EXISTS (
        SELECT FROM information_schema.columns 
        WHERE table_name = 'addresses' AND column_name = 'phonenumber'
    ) THEN
        -- Rename the column to phone_number if it doesn't already exist
        IF NOT EXISTS (
            SELECT FROM information_schema.columns 
            WHERE table_name = 'addresses' AND column_name = 'phone_number'
        ) THEN
            ALTER TABLE addresses RENAME COLUMN phonenumber TO phone_number;
        ELSE
            -- Both columns exist, drop the old one
            ALTER TABLE addresses DROP COLUMN phonenumber;
        END IF;
    ELSIF NOT EXISTS (
        SELECT FROM information_schema.columns 
        WHERE table_name = 'addresses' AND column_name = 'phone_number'
    ) THEN
        -- Add phone_number column if neither exists
        ALTER TABLE addresses ADD COLUMN phone_number VARCHAR(20) NOT NULL DEFAULT '0000000000';
    END IF;
END $$; 