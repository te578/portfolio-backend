-- 既存データをsite_idベースの4桁数字(0001, 0002, ...)に振り直す
UPDATE sites SET company_cd = LPAD(site_id::text, 4, '0');
