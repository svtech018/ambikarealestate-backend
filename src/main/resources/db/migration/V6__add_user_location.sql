-- V6: Add location column to users table
ALTER TABLE users ADD COLUMN location VARCHAR(255);

-- Set default location for existing admin user
UPDATE users SET location = 'Mumbai, India' WHERE username = 'admin';
