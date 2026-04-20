-- V7: Add area_unit column to properties table
ALTER TABLE properties ADD COLUMN IF NOT EXISTS area_unit VARCHAR(10) DEFAULT 'SQFT';
