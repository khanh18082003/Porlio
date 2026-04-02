-- ════════════════════════════════════════════
-- EXPAND PORTFOLIO EDITOR SCHEMA
-- Sections, theme settings, publish history, public views
-- ════════════════════════════════════════════

-- ── PORTFOLIO_SECTIONS ──────────────────────
ALTER TABLE portfolio_sections
    ADD COLUMN IF NOT EXISTS section_key VARCHAR(60),
    ADD COLUMN IF NOT EXISTS variant VARCHAR(40),
    ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'manual',
    ADD COLUMN IF NOT EXISTS title VARCHAR(120),
    ADD COLUMN IF NOT EXISTS settings JSONB NOT NULL DEFAULT '{}'::jsonb;

UPDATE portfolio_sections
SET section_key = LEFT(type, 20) || '-' || SUBSTRING(REPLACE(id::text, '-', '') FROM 1 FOR 12)
WHERE section_key IS NULL;

ALTER TABLE portfolio_sections
    ALTER COLUMN section_key SET NOT NULL;

ALTER TABLE portfolio_sections
    DROP CONSTRAINT IF EXISTS chk_section_type;

ALTER TABLE portfolio_sections
    ADD CONSTRAINT chk_section_type
        CHECK (type IN (
            'hero',
            'about',
            'skills',
            'experience',
            'education',
            'projects',
            'certification',
            'testimonials',
            'articles',
            'contact',
            'resume'
        ));

ALTER TABLE portfolio_sections
    DROP CONSTRAINT IF EXISTS chk_section_source;

ALTER TABLE portfolio_sections
    ADD CONSTRAINT chk_section_source
        CHECK (source IN ('manual', 'github', 'system'));

CREATE UNIQUE INDEX IF NOT EXISTS uniq_portfolio_section_key
    ON portfolio_sections (portfolio_id, section_key);

CREATE INDEX IF NOT EXISTS idx_sections_visible
    ON portfolio_sections (portfolio_id, is_visible, order_index);

CREATE INDEX IF NOT EXISTS idx_sections_settings
    ON portfolio_sections USING GIN (settings);

-- ── PORTFOLIO_THEME_SETTINGS ────────────────
CREATE TABLE portfolio_theme_settings
(
    id              UUID PRIMARY KEY,
    portfolio_id    UUID        NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    mode            VARCHAR(20) NOT NULL,
    token_overrides JSONB       NOT NULL DEFAULT '{}'::jsonb,
    font_pair       JSONB       NOT NULL DEFAULT '{}'::jsonb,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_portfolio_theme_mode
        CHECK (mode IN ('light', 'dark')),
    CONSTRAINT uq_portfolio_theme_settings UNIQUE (portfolio_id, mode)
);

CREATE INDEX idx_portfolio_theme_settings_portfolio
    ON portfolio_theme_settings (portfolio_id, mode);

-- ── PORTFOLIO_PUBLISH_HISTORY ───────────────
CREATE TABLE portfolio_publish_history
(
    id           UUID PRIMARY KEY,
    portfolio_id UUID      NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    version      INTEGER   NOT NULL,
    snapshot     JSONB     NOT NULL,
    published_by UUID      REFERENCES users (id) ON DELETE SET NULL,
    published_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_portfolio_publish_version UNIQUE (portfolio_id, version)
);

CREATE INDEX idx_portfolio_publish_history_portfolio
    ON portfolio_publish_history (portfolio_id, published_at DESC);

-- ── PORTFOLIO_VIEWS ─────────────────────────
CREATE TABLE portfolio_views
(
    id              UUID PRIMARY KEY,
    portfolio_id    UUID         NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    referrer        VARCHAR(300),
    country_code    VARCHAR(10),
    ip_hash         VARCHAR(128),
    user_agent_hash VARCHAR(128),
    visited_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_portfolio_views_portfolio_time
    ON portfolio_views (portfolio_id, visited_at DESC);

