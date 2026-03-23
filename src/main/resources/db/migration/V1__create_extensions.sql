-- Bật các extension cần thiết của PostgreSQL
-- Extension phải được tạo TRƯỚC khi dùng

-- uuid-ossp: Cho phép dùng hàm uuid_generate_v4()
-- để tự sinh UUID làm primary key
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- pgcrypto: Cung cấp hàm mã hóa (gen_salt, crypt)
-- Dùng để hash password hoặc encrypt sensitive data
CREATE EXTENSION IF NOT EXISTS "pgcrypto";