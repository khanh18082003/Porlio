-- ════════════════════════════════════════════
-- BẢNG: audit_logs
-- Ghi lại toàn bộ hành động của admin
-- Append-only: KHÔNG BAO GIỜ UPDATE hay DELETE dòng trong bảng này
-- ════════════════════════════════════════════
CREATE TABLE audit_logs
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),

    -- AI THỰC HIỆN HÀNH ĐỘNG
    actor_id    UUID         REFERENCES users (id) ON DELETE SET NULL,
    -- NULLABLE: system action không có actor (ví dụ: auto cleanup)
    -- ON DELETE SET NULL: admin bị xóa → log vẫn còn, chỉ mất actor reference

    actor_email VARCHAR(255),
    -- Lưu THÊM email tại thời điểm thực hiện
    -- Lý do: nếu user đổi email sau này, vẫn biết được là ai đã làm
    -- Đây là "denormalization" có chủ đích cho audit trail

    -- HÀNH ĐỘNG GÌ
    action      VARCHAR(100) NOT NULL,
    -- Dùng SCREAMING_SNAKE_CASE:
    -- 'USER_BANNED', 'USER_UNBANNED', 'USER_DELETED'
    -- 'USER_ROLE_ASSIGNED', 'USER_ROLE_REVOKED'
    -- 'PORTFOLIO_UNPUBLISHED', 'PORTFOLIO_DELETED'
    -- 'TEMPLATE_CREATED', 'TEMPLATE_UPDATED', 'TEMPLATE_DELETED'

    -- ĐỐI TƯỢNG BỊ TÁC ĐỘNG
    target_type VARCHAR(50),
    -- Loại object: 'USER' | 'PORTFOLIO' | 'TEMPLATE'

    target_id   UUID,
    -- ID của object bị tác động
    -- Cả 2 NULLABLE vì một số action không có target cụ thể

    -- CHI TIẾT BỔ SUNG
    detail      JSONB        NOT NULL DEFAULT '{}',
    -- Thông tin thêm tùy theo action:
    -- USER_BANNED:    { "reason": "Spam content", "bannedUntil": null }
    -- ROLE_ASSIGNED:  { "roleName": "MODERATOR", "previousRoles": ["USER"] }
    -- PORTFOLIO_UNPUBLISHED: { "reason": "...", "portfolioSlug": "nguyenvana" }

    -- THÔNG TIN NETWORK
    ip_address  VARCHAR(45),
    -- VARCHAR(45): đủ cho cả IPv4 (15 chars) và IPv6 (45 chars)
    -- Hữu ích khi điều tra security incident

    user_agent  VARCHAR(500),
    -- Browser/client info, giúp phát hiện bot hay automation

    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
    -- Không có updated_at vì audit log là immutable
);

-- INDEXES — audit log thường query theo 3 cách:
-- 1. Xem admin X đã làm gì gần đây?
CREATE INDEX idx_audit_actor_id ON audit_logs (actor_id, created_at DESC);

-- 2. Chuyện gì đã xảy ra với user/portfolio Y?
CREATE INDEX idx_audit_target ON audit_logs (target_type, target_id);

-- 3. Lọc theo khoảng thời gian
CREATE INDEX idx_audit_created ON audit_logs (created_at DESC);

-- 4. Lọc theo action type
CREATE INDEX idx_audit_action ON audit_logs (action);