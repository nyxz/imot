ALTER TABLE properties ADD COLUMN provider_website    VARCHAR(256) NOT NULL DEFAULT 'imot.bg';

ALTER TABLE properties ALTER COLUMN provider_website    DROP DEFAULT;
