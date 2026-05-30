CREATE TABLE warehouse_stock (
    id                   UUID      NOT NULL DEFAULT gen_random_uuid(),
    warehouse_id         UUID      NOT NULL,
    product_id           UUID      NOT NULL,
    quantity             INT       NOT NULL DEFAULT 0,
    reserved_quantity    INT       NOT NULL DEFAULT 0,
    low_stock_threshold  INT       NOT NULL DEFAULT 10,
    is_available         BOOLEAN   NOT NULL DEFAULT TRUE,
    last_restocked_at    TIMESTAMP,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_warehouse_stock
        PRIMARY KEY (id),
    CONSTRAINT uk_warehouse_stock
        UNIQUE (warehouse_id, product_id),
    CONSTRAINT fk_warehouse_stock_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id),
    CONSTRAINT fk_warehouse_stock_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT chk_quantity
        CHECK (quantity >= 0),
    CONSTRAINT chk_reserved_quantity
        CHECK (reserved_quantity >= 0),
    CONSTRAINT chk_reserved_lte_quantity
        CHECK (reserved_quantity <= quantity)
);

CREATE INDEX idx_warehouse_stock_warehouse
    ON warehouse_stock (warehouse_id);
CREATE INDEX idx_warehouse_stock_product
    ON warehouse_stock (product_id);
CREATE INDEX idx_warehouse_stock_available
    ON warehouse_stock (is_available);