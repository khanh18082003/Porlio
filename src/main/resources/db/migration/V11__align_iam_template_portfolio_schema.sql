-- ════════════════════════════════════════════
-- ALIGN USERS / TEMPLATES / PORTFOLIOS
-- Đồng bộ schema cốt lõi theo thiết kế Porlio MVP
-- ════════════════════════════════════════════

-- ── USERS ───────────────────────────────────
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS headline VARCHAR(160),
    ADD COLUMN IF NOT EXISTS bio TEXT,
    ADD COLUMN IF NOT EXISTS profession_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS location VARCHAR(120),
    ADD COLUMN IF NOT EXISTS website_url TEXT,
    ADD COLUMN IF NOT EXISTS is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verified_at TIMESTAMP;

UPDATE users
SET is_verified = FALSE
WHERE is_verified IS NULL;

-- ── TEMPLATES ───────────────────────────────
ALTER TABLE templates
    ADD COLUMN IF NOT EXISTS summary VARCHAR(200),
    ADD COLUMN IF NOT EXISTS industry_tags JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN IF NOT EXISTS template_payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN IF NOT EXISTS theme_modes JSONB NOT NULL DEFAULT '["light", "dark"]'::jsonb,
    ADD COLUMN IF NOT EXISTS schema_version INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS sort_order SMALLINT NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_templates_category_active
    ON templates (category, is_active);

CREATE INDEX IF NOT EXISTS idx_templates_featured
    ON templates (is_featured, sort_order)
    WHERE is_active = TRUE;

CREATE INDEX IF NOT EXISTS idx_templates_industry_tags
    ON templates USING GIN (industry_tags);

-- ── PORTFOLIOS ──────────────────────────────
ALTER TABLE portfolios
    ADD COLUMN IF NOT EXISTS status VARCHAR(30),
    ADD COLUMN IF NOT EXISTS og_image_url TEXT,
    ADD COLUMN IF NOT EXISTS current_theme_mode VARCHAR(20),
    ADD COLUMN IF NOT EXISTS last_edited_at TIMESTAMP;

UPDATE portfolios
SET status = CASE WHEN is_public THEN 'published' ELSE 'draft' END
WHERE status IS NULL;

UPDATE portfolios
SET current_theme_mode = 'light'
WHERE current_theme_mode IS NULL;

UPDATE portfolios
SET last_edited_at = updated_at
WHERE last_edited_at IS NULL;

ALTER TABLE portfolios
    ALTER COLUMN status SET DEFAULT 'draft',
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN current_theme_mode SET DEFAULT 'light',
    ALTER COLUMN current_theme_mode SET NOT NULL,
    ALTER COLUMN last_edited_at SET DEFAULT NOW(),
    ALTER COLUMN last_edited_at SET NOT NULL,
    ALTER COLUMN view_count TYPE BIGINT USING view_count::bigint;

ALTER TABLE portfolios
    DROP COLUMN IF EXISTS is_public;

ALTER TABLE portfolios
    DROP CONSTRAINT IF EXISTS chk_portfolios_status;

ALTER TABLE portfolios
    ADD CONSTRAINT chk_portfolios_status
        CHECK (status IN ('draft', 'published', 'archived'));

ALTER TABLE portfolios
    DROP CONSTRAINT IF EXISTS chk_portfolios_theme_mode;

ALTER TABLE portfolios
    ADD CONSTRAINT chk_portfolios_theme_mode
        CHECK (current_theme_mode IN ('light', 'dark'));

CREATE INDEX IF NOT EXISTS idx_portfolios_status
    ON portfolios (status);

CREATE INDEX IF NOT EXISTS idx_portfolios_template_id
    ON portfolios (template_id);

