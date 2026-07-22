CREATE TABLE IF NOT EXISTS sites (
    site_id    SERIAL PRIMARY KEY,
    company_cd VARCHAR(50)  NOT NULL UNIQUE,
    company_nm VARCHAR(100) NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
