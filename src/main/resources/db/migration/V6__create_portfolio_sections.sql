-- ════════════════════════════════════════════
-- BẢNG: portfolio_sections
-- Các section trong portfolio (hero, skills, experience...)
-- Mỗi section có content linh hoạt dạng JSONB
-- ════════════════════════════════════════════
CREATE TABLE portfolio_sections
(
    id           UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),

    -- THUỘC PORTFOLIO NÀO
    portfolio_id UUID        NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    -- ON DELETE CASCADE: xóa portfolio → xóa luôn tất cả sections

    -- LOẠI SECTION
    type         VARCHAR(50) NOT NULL,
    -- Các giá trị hợp lệ được kiểm soát bởi ENUM ở app level:
    -- 'hero'        → Tên, title, bio, avatar, location
    -- 'skills'      → Danh sách kỹ năng theo nhóm
    -- 'experience'  → Kinh nghiệm làm việc
    -- 'education'   → Học vấn
    -- 'projects'    → Dự án (manual hoặc từ GitHub)
    -- 'contact'     → Thông tin liên hệ & mạng xã hội

    -- NỘI DUNG LINH HOẠT (JSONB)
    content      JSONB       NOT NULL DEFAULT '{}',
    -- JSONB vs JSON:
    --   JSON  = lưu dạng text, parse mỗi lần query
    --   JSONB = lưu dạng binary, parse 1 lần khi INSERT
    --         → query nhanh hơn, có thể đánh index
    -- Cấu trúc content khác nhau cho mỗi type (xem V9 seed data)

    -- THỨ TỰ HIỂN THỊ
    order_index  SMALLINT    NOT NULL DEFAULT 0,
    -- SMALLINT (2 bytes): đủ cho thứ tự 0-32767, tiết kiệm hơn INTEGER
    -- Số nhỏ hiển thị trước: order_index 0 → hiển thị đầu tiên
    -- Khi drag-drop: cập nhật lại order_index của các section liên quan

    -- HIỂN THỊ HAY ẨN
    is_visible   BOOLEAN     NOT NULL DEFAULT TRUE,
    -- User có thể tạm ẩn 1 section mà không cần xóa
    -- FALSE: section vẫn tồn tại nhưng không render trên public page

    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- CONSTRAINT: type chỉ được là các giá trị hợp lệ
ALTER TABLE portfolio_sections
    ADD CONSTRAINT chk_section_type
        CHECK (type IN ('hero', 'skills', 'experience', 'education', 'projects', 'certification', 'contact'));

-- CONSTRAINT: mỗi portfolio chỉ được có 1 section kiểu 'hero'
-- (hero là section đặc biệt, luôn ở đầu, không được duplicate)
CREATE UNIQUE INDEX uniq_portfolio_hero
    ON portfolio_sections (portfolio_id)
    WHERE type = 'hero';
-- Partial unique index: chỉ enforce unique cho type = 'hero'
-- Các type khác (skills, experience...) có thể có nhiều section

-- INDEXES
CREATE INDEX idx_sections_portfolio_id
    ON portfolio_sections (portfolio_id, order_index);
-- Composite index: query "lấy tất cả sections của portfolio X, sắp xếp theo order"
-- Đây là query phổ biến nhất → index phải match đúng

CREATE INDEX idx_sections_content
    ON portfolio_sections USING GIN (content);
-- GIN index (Generalized Inverted Index): dành riêng cho JSONB
-- Cho phép search WITHIN JSONB content nhanh
-- Ví dụ: tìm tất cả sections có skill 'React' trong content