-- ============================================
-- ROLES & USERS
-- ============================================

-- Migration user: chịu trách nhiệm DDL (schema changes)
CREATE USER migration_user WITH
    PASSWORD :'MIGRATION_PASSWORD'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    LOGIN;

-- App user: chịu trách nhiệm DML (queries từ backend)
CREATE USER app_user WITH
    PASSWORD :'APP_PASSWORD'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    LOGIN;

-- ============================================
-- GRANT PERMISSIONS
-- ============================================

-- migration_user: toàn quyền trên schema public (DDL)
GRANT ALL PRIVILEGES ON DATABASE porlio_db TO migration_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO migration_user;
ALTER USER migration_user SET search_path = public;

-- app_user: chỉ DML, không được đụng vào schema
GRANT CONNECT ON DATABASE porlio_db TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;

-- DML permissions cho các table hiện tại
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- Tự động áp dụng cho các table được tạo sau này bởi migration_user
ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;

ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO app_user;