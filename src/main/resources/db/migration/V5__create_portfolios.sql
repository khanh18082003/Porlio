-- ════════════════════════════════════════════
-- BẢNG: templates
-- Các template portfolio có sẵn để user chọn
-- ════════════════════════════════════════════
CREATE TABLE templates
(
    id            UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),

    -- TÊN TEMPLATE
    name          VARCHAR(100) NOT NULL,
    -- Tên hiển thị: "Minimal", "Modern Dark", "Creative"

    -- SLUG — dùng trong code để reference template
    slug          VARCHAR(50)  NOT NULL UNIQUE,
    -- Ví dụ: 'minimal', 'modern-dark', 'creative'
    -- Dùng slug thay vì hardcode UUID trong frontend

    -- ẢNH XEM TRƯỚC
    thumbnail_url TEXT,
    -- Ảnh nhỏ hiển thị trong trang chọn template (400x300px)

    preview_url   TEXT,
    -- Link đến trang demo đầy đủ của template

    -- PHÂN LOẠI
    category      VARCHAR(50)  NOT NULL DEFAULT 'minimal',
    -- Giá trị: 'minimal' | 'modern' | 'creative'
    -- Dùng để filter template theo loại

    -- TRẠNG THÁI
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    -- FALSE: template bị ẩn, user không thể chọn nữa
    -- (nhưng user đang dùng vẫn không bị ảnh hưởng)

    -- THỐNG KÊ
    usage_count   INTEGER      NOT NULL DEFAULT 0,
    -- Đếm số portfolio đang dùng template này
    -- Dùng để block xóa template đang được dùng

    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ════════════════════════════════════════════
-- BẢNG: portfolios
-- Mỗi user có đúng 1 portfolio
-- ════════════════════════════════════════════
CREATE TABLE portfolios
(
    id              UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),

    -- LIÊN KẾT VỚI USER
    user_id         UUID      NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    -- UNIQUE: đảm bảo 1 user chỉ có đúng 1 portfolio
    -- ON DELETE CASCADE: xóa user → xóa luôn portfolio của họ

    -- TEMPLATE ĐANG DÙNG
    template_id     UUID      REFERENCES templates (id) ON DELETE SET NULL,
    -- NULLABLE: tạm thời null khi mới tạo, chưa chọn template
    -- ON DELETE SET NULL: template bị xóa → portfolio vẫn còn,
    -- chỉ mất liên kết (fallback về default template ở app level)

    -- TRẠNG THÁI PUBLISH
    is_public       BOOLEAN   NOT NULL DEFAULT FALSE,
    -- FALSE: portfolio private, chỉ owner xem được
    -- TRUE: public, ai có link cũng xem được

    published_at    TIMESTAMP,
    -- NULLABLE: null nếu chưa từng publish
    -- Ghi lại thời điểm publish ĐẦU TIÊN (không update lại)

    -- CUSTOM SLUG (tùy chọn)
    custom_slug     VARCHAR(50) UNIQUE,
    -- NULLABLE: nếu null thì dùng username làm slug
    -- Nếu có giá trị: yourapp.com/{custom_slug}
    -- UNIQUE: đảm bảo không trùng với slug của người khác
    -- Phải check trùng với cả username của users khác nữa (app level)

    -- SEO METADATA
    seo_title       VARCHAR(100),
    -- Tiêu đề hiển thị trên tab browser và Google
    -- Nếu null: dùng "{full_name} - Portfolio" làm default

    seo_description VARCHAR(300),
    -- Mô tả ngắn hiển thị trên Google search result
    -- Khuyến nghị 150-160 ký tự

    -- THỐNG KÊ
    view_count      INTEGER   NOT NULL DEFAULT 0,
    -- Tổng số lượt xem từ khi public

    -- TIMESTAMPS
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- CONSTRAINT: custom_slug theo đúng format (giống username)
ALTER TABLE portfolios
    ADD CONSTRAINT chk_custom_slug_format
        CHECK (custom_slug IS NULL OR custom_slug ~ '^[a-z0-9_-]{3,50}$');

-- INDEX: hay query portfolio theo user_id
CREATE INDEX idx_portfolios_user_id ON portfolios (user_id);
CREATE INDEX idx_portfolios_custom_slug ON portfolios (custom_slug)
    WHERE custom_slug IS NOT NULL;
-- Partial index: chỉ index các row có custom_slug (tránh index NULL values)