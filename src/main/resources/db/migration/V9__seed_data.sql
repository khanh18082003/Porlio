-- ════════════════════════════════════════════
-- SEED DATA
-- ════════════════════════════════════════════

-- ── 1. ROLES ─────────────────────────────────
INSERT INTO roles (id, key, name, description)
VALUES (uuid_generate_v4(), 'ADMIN', 'Administrator', 'Full system access'),
       (uuid_generate_v4(), 'USER', 'User', 'Standard user access');


-- ── 2. PERMISSIONS ───────────────────────────

-- USER MODULE
INSERT INTO permissions (id, key, name, method, url_pattern, description, module)
VALUES
    -- user:read
    (uuid_generate_v4(),
     'user:read',
     'View User Details',
     'GET',
     '/api/v1/admin/users/{id}',
     'View details of any user account',
     'USER'),

    -- user:list
    (uuid_generate_v4(),
     'user:list',
     'List Users',
     'GET',
     '/api/v1/admin/users',
     'List all users with filters and pagination',
     'USER'),

    -- user:ban
    (uuid_generate_v4(),
     'user:ban',
     'Ban / Unban User',
     'PATCH',
     '/api/v1/admin/users/{id}/ban',
     'Ban or unban a user account',
     'USER'),

    -- user:delete
    (uuid_generate_v4(),
     'user:delete',
     'Delete User',
     'DELETE',
     '/api/v1/admin/users/{id}',
     'Permanently delete a user account',
     'USER'),

    -- user:role:assign
    (uuid_generate_v4(),
     'user:role:assign',
     'Assign Role to User',
     'POST',
     '/api/v1/admin/users/{id}/roles',
     'Assign or revoke roles from a user',
     'USER');


-- PORTFOLIO MODULE
INSERT INTO permissions (id, key, name, method, url_pattern, description, module)
VALUES
    -- portfolio:read
    (uuid_generate_v4(),
     'portfolio:read',
     'View Portfolio',
     'GET',
     '/api/v1/admin/portfolios/{id}',
     'View details of any portfolio',
     'PORTFOLIO'),

    -- portfolio:list
    (uuid_generate_v4(),
     'portfolio:list',
     'List Portfolios',
     'GET',
     '/api/v1/admin/portfolios',
     'List all portfolios with filters and pagination',
     'PORTFOLIO'),

    -- portfolio:unpublish
    (uuid_generate_v4(),
     'portfolio:unpublish',
     'Unpublish Portfolio',
     'PATCH',
     '/api/v1/admin/portfolios/{id}/unpublish',
     'Force unpublish a portfolio that violates policy',
     'PORTFOLIO'),

    -- portfolio:delete
    (uuid_generate_v4(),
     'portfolio:delete',
     'Delete Portfolio',
     'DELETE',
     '/api/v1/admin/portfolios/{id}',
     'Permanently delete any portfolio',
     'PORTFOLIO');


-- TEMPLATE MODULE
INSERT INTO permissions (id, key, name, method, url_pattern, description, module)
VALUES
    -- template:create
    (uuid_generate_v4(),
     'template:create',
     'Create Template',
     'POST',
     '/api/v1/admin/templates',
     'Create a new portfolio template',
     'TEMPLATE'),

    -- template:update
    (uuid_generate_v4(),
     'template:update',
     'Update Template',
     'PUT',
     '/api/v1/admin/templates/{id}',
     'Update an existing template',
     'TEMPLATE'),

    -- template:delete
    (uuid_generate_v4(),
     'template:delete',
     'Delete Template',
     'DELETE',
     '/api/v1/admin/templates/{id}',
     'Delete a template from the system',
     'TEMPLATE'),

    -- template:toggle
    (uuid_generate_v4(),
     'template:toggle',
     'Toggle Template Status',
     'PATCH',
     '/api/v1/admin/templates/{id}/toggle',
     'Activate or deactivate a template',
     'TEMPLATE');


-- SYSTEM MODULE
INSERT INTO permissions (id, key, name, method, url_pattern, description, module)
VALUES
    -- system:stats
    (uuid_generate_v4(),
     'system:stats',
     'View System Statistics',
     'GET',
     '/api/v1/admin/stats',
     'View system-wide statistics and dashboard',
     'SYSTEM'),

    -- system:audit
    (uuid_generate_v4(),
     'system:audit',
     'View Audit Logs',
     'GET',
     '/api/v1/admin/audit-logs',
     'View and export admin audit logs',
     'SYSTEM');


-- ── 3. ROLE_PERMISSIONS ───────────────────────
-- ADMIN nhận tất cả permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.key = 'ADMIN';

-- USER không có admin permissions
-- (không cần INSERT gì, mặc định không có quyền nào)


-- ── 4. TEMPLATES ─────────────────────────────
INSERT INTO templates (id, name, slug, category, is_active)
VALUES (uuid_generate_v4(), 'Minimal', 'minimal', 'minimal', TRUE),
       (uuid_generate_v4(), 'Modern', 'modern', 'modern', TRUE),
       (uuid_generate_v4(), 'Creative', 'creative', 'creative', TRUE);


-- ── 5. DEFAULT ADMIN USER ────────────────────
-- password_hash = BCrypt hash của 'Admin@123456'
-- ⚠️ QUAN TRỌNG: đổi password ngay sau khi deploy lần đầu!
WITH new_admin AS (
    INSERT INTO users (id,
                       email,
                       password_hash,
                       username,
                       full_name,
                       is_active,
                       is_deleted)
        VALUES (uuid_generate_v4(),
                'admin@portfolioapp.com',
                '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4oEHFNsKwy',
                'admin',
                'System Administrator',
                TRUE,
                FALSE)
        RETURNING id)
INSERT
INTO user_roles (user_id, role_id)
SELECT new_admin.id, r.id
FROM new_admin,
     roles r
WHERE r.key = 'ADMIN';