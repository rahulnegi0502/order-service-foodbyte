CREATE TABLE warehouses (
    id          UUID            NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(100)    NOT NULL,
    address     VARCHAR(255)    NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    city        VARCHAR(100)    NOT NULL,
    pincode     VARCHAR(10)     NOT NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_warehouses            PRIMARY KEY (id),
    CONSTRAINT uk_warehouse_name_city   UNIQUE (name, city)
);

CREATE INDEX idx_warehouses_city    ON warehouses (city);
CREATE INDEX idx_warehouses_pincode ON warehouses (pincode);
CREATE INDEX idx_warehouses_active  ON warehouses (is_active);