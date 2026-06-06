CREATE TABLE stock_adjustments (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    warehouse_stock_id  UUID        NOT NULL,
    adjustment_type     VARCHAR(20) NOT NULL,
    quantity_change     INT         NOT NULL,
    previous_quantity   INT         NOT NULL,
    new_quantity        INT         NOT NULL,
    reference_id        VARCHAR(100),
    reason              VARCHAR(255),
    created_by          UUID,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_stock_adjustments
        PRIMARY KEY (id),
    CONSTRAINT fk_adj_warehouse_stock
        FOREIGN KEY (warehouse_stock_id)
        REFERENCES warehouse_stock (id),
    CONSTRAINT chk_adj_type CHECK (adjustment_type IN (
        'ORDER', 'RESTOCK', 'DAMAGED',
        'CORRECTION', 'RESERVATION', 'RELEASE'
    ))
);

CREATE INDEX idx_adj_warehouse_stock_id
    ON stock_adjustments (warehouse_stock_id);
CREATE INDEX idx_adj_reference_id
    ON stock_adjustments (reference_id);
CREATE INDEX idx_adj_type
    ON stock_adjustments (adjustment_type);
CREATE INDEX idx_adj_created_at
    ON stock_adjustments (created_at);