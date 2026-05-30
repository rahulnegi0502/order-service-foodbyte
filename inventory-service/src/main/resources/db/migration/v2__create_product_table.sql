CREATE TABLE products (
    id          UUID             NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(150)     NOT NULL,
    description VARCHAR(500),
    image_url   VARCHAR(255),
    price       DECIMAL(10, 2)   NOT NULL,
    sku         VARCHAR(50)      NOT NULL,
    category    VARCHAR(30)      NOT NULL,
    unit        VARCHAR(50)      NOT NULL,
    brand       VARCHAR(100),
    created_by  UUID             NOT NULL,
    is_active   BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP        NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_products      PRIMARY KEY (id),
    CONSTRAINT uk_products_sku  UNIQUE (sku),
    CONSTRAINT chk_products_category CHECK (category IN (
        'FRUITS_VEGETABLES', 'DAIRY', 'SNACKS', 'BEVERAGES',
        'PERSONAL_CARE', 'HOUSEHOLD', 'BAKERY', 'FROZEN',
        'MEAT_SEAFOOD', 'STAPLES'
    ))
);

CREATE INDEX idx_products_category  ON products (category);
CREATE INDEX idx_products_sku       ON products (sku);
CREATE INDEX idx_products_active    ON products (is_active);
CREATE INDEX idx_products_brand     ON products (brand);