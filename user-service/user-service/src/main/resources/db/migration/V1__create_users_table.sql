-- ─────────────────────────────────────────────
--  V1__create_users_table.sql
--  Creates the core users table
-- ─────────────────────────────────────────────

CREATE TABLE users (
    id                 UUID         NOT NULL DEFAULT gen_random_uuid(),
    name               VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL,
    phone              VARCHAR(15)  NOT NULL,
    password_hash      TEXT         NOT NULL,
    role               VARCHAR(20)  NOT NULL,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    is_phone_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users             PRIMARY KEY (id),
    CONSTRAINT uk_users_email       UNIQUE (email),
    CONSTRAINT uk_users_phone       UNIQUE (phone),
    CONSTRAINT chk_users_role       CHECK (role IN ('CUSTOMER', 'RESTAURANT_ADMIN', 'ADMIN'))
);

-- Index on email — used on every login query
CREATE INDEX idx_users_email ON users (email);

-- Index on phone — used for OTP lookup
CREATE INDEX idx_users_phone ON users (phone);

-- Index on role — used for admin queries
CREATE INDEX idx_users_role ON users (role);

COMMENT ON TABLE  users                IS 'Core user accounts for FoodByte platform';
COMMENT ON COLUMN users.id             IS 'UUID primary key — no sequential IDs exposed';
COMMENT ON COLUMN users.password_hash  IS 'BCrypt hashed password, never store plaintext';
COMMENT ON COLUMN users.role           IS 'CUSTOMER | RESTAURANT_ADMIN | ADMIN';
COMMENT ON COLUMN users.is_active      IS 'Soft delete — false means account is deactivated';
COMMENT ON COLUMN users.is_phone_verified IS 'True after OTP verification';