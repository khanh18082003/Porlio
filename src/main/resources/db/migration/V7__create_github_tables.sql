-- ════════════════════════════════════════════
-- BẢNG: github_oauth
-- Lưu access token GitHub của user
-- ════════════════════════════════════════════
CREATE TABLE github_oauth
(
    id             UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),

    -- LIÊN KẾT USER
    user_id        UUID        NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    -- UNIQUE: 1 user chỉ connect 1 GitHub account

    -- ACCESS TOKEN
    access_token   TEXT        NOT NULL,
    -- TEXT vì token có thể dài (GitHub fine-grained token ~93 chars)
    -- QUAN TRỌNG: nên encrypt trước khi lưu (dùng pgcrypto hoặc app-level AES)
    -- Lưu raw token là security risk nếu DB bị leak

    -- THÔNG TIN TOKEN
    token_type     VARCHAR(50) NOT NULL DEFAULT 'bearer',
    -- GitHub hiện tại chỉ dùng 'bearer'

    scope          VARCHAR(500),
    -- Danh sách quyền đã được cấp, phân cách bằng dấu phẩy
    -- Ví dụ: "read:user,public_repo,read:org"
    -- VARCHAR(500): scope có thể dài nếu nhiều quyền

    -- THÔNG TIN GITHUB USER
    github_login   VARCHAR(100),
    -- Username trên GitHub (khác với username trên hệ thống của mình)
    -- Lưu lại để hiển thị "Connected as @github_login"

    github_user_id BIGINT      NOT NULL,
    -- ID của user trên GitHub, dùng để verify

    connected_at   TIMESTAMP   NOT NULL DEFAULT NOW(),

    -- THỜI HẠN TOKEN (GitHub OAuth token không hết hạn nhưng để sẵn)
    expires_at     TIMESTAMP
    -- NULLABLE: null = không hết hạn (trường hợp GitHub classic token)
);

-- ════════════════════════════════════════════
-- BẢNG: github_cache
-- Cache dữ liệu từ GitHub API để tránh gọi quá nhiều
-- ════════════════════════════════════════════
CREATE TABLE github_cache
(
    id              UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),

    user_id         UUID      NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,

    -- THÔNG TIN CƠ BẢN GITHUB
    github_login    VARCHAR(100),
    github_bio      TEXT,
    github_blog     VARCHAR(300),
    -- Blog/website của user trên GitHub profile

    -- REPOSITORIES
    repos           JSONB     NOT NULL DEFAULT '[]',
    -- Mảng các repo objects:
    -- [{
    --   "id": 123, "name": "portfolio-app",
    --   "description": "...", "url": "https://github.com/...",
    --   "homepage": "https://...",
    --   "language": "TypeScript", "stars": 42, "forks": 5,
    --   "isPrivate": false, "isFork": false,
    --   "topics": ["react", "portfolio"],
    --   "updatedAt": "2025-03-01T00:00:00Z"
    -- }]

    pinned_repos    JSONB     NOT NULL DEFAULT '[]',
    -- Tối đa 6 repos được ghim trên GitHub profile
    -- Cùng format với repos

    -- THỐNG KÊ TỔNG HỢP
    total_repos     INTEGER   NOT NULL DEFAULT 0,
    total_stars     INTEGER   NOT NULL DEFAULT 0,
    -- Tổng stars nhận được từ tất cả repos

    total_forks     INTEGER   NOT NULL DEFAULT 0,

    -- NGÔN NGỮ LẬP TRÌNH
    languages       JSONB     NOT NULL DEFAULT '{}',
    -- Map language → tổng bytes code:
    -- { "TypeScript": 45000, "Java": 120000, "Python": 8000 }
    -- Dùng để tính % và hiển thị chart ngôn ngữ

    -- FOLLOWERS
    followers_count INTEGER   NOT NULL DEFAULT 0,
    following_count INTEGER   NOT NULL DEFAULT 0,

    -- THỜI GIAN SYNC
    last_synced_at  TIMESTAMP NOT NULL DEFAULT NOW()
    -- Dùng để kiểm tra cache có cũ quá không
    -- Rule: nếu last_synced_at > 1 giờ → suggest user sync lại
);