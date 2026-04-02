-- ════════════════════════════════════════════
-- RESHAPE GITHUB SCHEMA
-- Tách connection / profile / repositories / sync jobs
-- ════════════════════════════════════════════

-- ── RENAME LEGACY TABLES ────────────────────
ALTER TABLE github_oauth
    RENAME TO github_connections;

ALTER TABLE github_cache
    RENAME TO github_profiles;

-- ── GITHUB_CONNECTIONS ──────────────────────
ALTER TABLE github_connections
    RENAME COLUMN access_token TO encrypted_access_token;

ALTER TABLE github_connections
    ADD COLUMN IF NOT EXISTS last_synced_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS uq_github_connections_github_user_id
    ON github_connections (github_user_id);

-- ── GITHUB_PROFILES ─────────────────────────
ALTER TABLE github_profiles
    RENAME COLUMN github_bio TO bio;

ALTER TABLE github_profiles
    RENAME COLUMN github_blog TO blog_url;

ALTER TABLE github_profiles
    RENAME COLUMN total_repos TO public_repo_count;

ALTER TABLE github_profiles
    ADD COLUMN IF NOT EXISTS name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS avatar_url TEXT,
    ADD COLUMN IF NOT EXISTS raw_payload JSONB NOT NULL DEFAULT '{}'::jsonb;

-- Đồng bộ last_synced_at cho github_connections từ profile hiện có nếu có.
UPDATE github_connections gc
SET last_synced_at = gp.last_synced_at
FROM github_profiles gp
WHERE gc.user_id = gp.user_id
  AND gc.last_synced_at IS NULL;

-- ── GITHUB_REPOSITORIES ─────────────────────
CREATE TABLE github_repositories
(
    id               UUID PRIMARY KEY,
    user_id          UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    github_repo_id   BIGINT,
    name             VARCHAR(150) NOT NULL,
    full_name        VARCHAR(255) NOT NULL,
    description      TEXT,
    html_url         TEXT         NOT NULL,
    homepage_url     TEXT,
    primary_language VARCHAR(80),
    stars_count      INTEGER      NOT NULL DEFAULT 0,
    forks_count      INTEGER      NOT NULL DEFAULT 0,
    topics           JSONB        NOT NULL DEFAULT '[]'::jsonb,
    is_fork          BOOLEAN      NOT NULL DEFAULT FALSE,
    is_private       BOOLEAN      NOT NULL DEFAULT FALSE,
    is_selected      BOOLEAN      NOT NULL DEFAULT FALSE,
    pushed_at        TIMESTAMP,
    raw_payload      JSONB        NOT NULL DEFAULT '{}'::jsonb,
    last_synced_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

INSERT INTO github_repositories (
    id,
    user_id,
    github_repo_id,
    name,
    full_name,
    description,
    html_url,
    homepage_url,
    primary_language,
    stars_count,
    forks_count,
    topics,
    is_fork,
    is_private,
    is_selected,
    pushed_at,
    raw_payload,
    last_synced_at
)
SELECT uuid_generate_v4(),
       gp.user_id,
       NULLIF(repo ->> 'id', '')::bigint,
       COALESCE(repo ->> 'name', 'unknown-repo'),
       COALESCE(repo ->> 'fullName', repo ->> 'full_name', repo ->> 'name', 'unknown-repo'),
       repo ->> 'description',
       COALESCE(repo ->> 'url', repo ->> 'htmlUrl', repo ->> 'html_url', 'https://github.com'),
       NULLIF(COALESCE(repo ->> 'homepage', repo ->> 'homepageUrl', repo ->> 'homepage_url'), ''),
       NULLIF(COALESCE(repo ->> 'language', repo ->> 'primaryLanguage', repo ->> 'primary_language'), ''),
       COALESCE(NULLIF(repo ->> 'stars', '')::integer, 0),
       COALESCE(NULLIF(repo ->> 'forks', '')::integer, 0),
       COALESCE(repo -> 'topics', '[]'::jsonb),
       COALESCE((repo ->> 'isFork')::boolean, FALSE),
       COALESCE((repo ->> 'isPrivate')::boolean, FALSE),
       FALSE,
       NULLIF(COALESCE(repo ->> 'updatedAt', repo ->> 'pushedAt', repo ->> 'pushed_at'), '')::timestamp,
       repo,
       gp.last_synced_at
FROM github_profiles gp
         CROSS JOIN LATERAL jsonb_array_elements(gp.repos) AS repo
WHERE gp.repos <> '[]'::jsonb;

CREATE UNIQUE INDEX uq_github_repositories_user_repo
    ON github_repositories (user_id, github_repo_id);

CREATE INDEX idx_github_repositories_selected
    ON github_repositories (user_id, is_selected, pushed_at DESC);

-- ── GITHUB_SYNC_JOBS ────────────────────────
CREATE TABLE github_sync_jobs
(
    id               UUID PRIMARY KEY,
    user_id          UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status           VARCHAR(30) NOT NULL,
    synced_repo_count INTEGER    NOT NULL DEFAULT 0,
    error_message    TEXT,
    started_at       TIMESTAMP,
    finished_at      TIMESTAMP,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_github_sync_job_status
        CHECK (status IN ('queued', 'running', 'success', 'failed'))
);

CREATE INDEX idx_github_sync_jobs_user_status
    ON github_sync_jobs (user_id, status, created_at DESC);

-- ── CLEAN UP LEGACY AGGREGATE COLUMNS ───────
ALTER TABLE github_profiles
    DROP COLUMN IF EXISTS repos,
    DROP COLUMN IF EXISTS pinned_repos,
    DROP COLUMN IF EXISTS total_stars,
    DROP COLUMN IF EXISTS total_forks,
    DROP COLUMN IF EXISTS languages,
    DROP COLUMN IF EXISTS github_login;

-- public_repo_count đã được rename từ total_repos.
-- Giữ lại followers_count / following_count / bio / blog_url / raw_payload.

