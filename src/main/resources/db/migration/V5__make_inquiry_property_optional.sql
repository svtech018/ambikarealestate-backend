-- V5: Make property_id optional in inquiries (allows general contact inquiries)
ALTER TABLE inquiries ALTER COLUMN property_id DROP NOT NULL;
ALTER TABLE inquiries DROP CONSTRAINT IF EXISTS fk_inquiry_property;
ALTER TABLE inquiries ADD CONSTRAINT fk_inquiry_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL;
