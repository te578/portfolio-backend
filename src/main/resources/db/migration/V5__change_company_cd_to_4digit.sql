-- 既存データをsite_idベースの4桁数字(0001, 0002, ...)に振り直す
UPDATE sites SET company_cd = LPAD(site_id::text, 4, '0');

ALTER TABLE sites ALTER COLUMN company_cd TYPE CHAR(4);
ALTER TABLE sites ADD CONSTRAINT company_cd_format CHECK (company_cd ~ '^[0-9]{4}$');
