-- ════════════════════════════════════════════
-- MEDIA ASSETS
-- Quản lý avatar / resume / image upload
-- ════════════════════════════════════════════

CREATE TABLE media_assets
(
    id            UUID PRIMARY KEY,
    owner_user_id UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    portfolio_id  UUID         REFERENCES portfolios (id) ON DELETE SET NULL,
    type          VARCHAR(30)  NOT NULL,
    provider      VARCHAR(30)  NOT NULL,
    storage_key   VARCHAR(255) NOT NULL,
    public_url    TEXT         NOT NULL,
    mime_type     VARCHAR(100) NOT NULL,
    size_bytes    BIGINT       NOT NULL,
    checksum      VARCHAR(128),
    metadata      JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_media_asset_type
        CHECK (type IN ('avatar', 'resume', 'template_thumbnail', 'image')),
    CONSTRAINT chk_media_asset_provider
        CHECK (provider IN ('s3', 'cloudinary', 'local'))
);

CREATE INDEX idx_media_assets_owner_type
    ON media_assets (owner_user_id, type, created_at DESC);

CREATE INDEX idx_media_assets_portfolio
    ON media_assets (portfolio_id, created_at DESC);

