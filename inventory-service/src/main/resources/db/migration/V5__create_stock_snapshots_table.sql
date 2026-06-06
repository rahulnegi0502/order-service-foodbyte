CREATE TABLE stock_snapshots (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    warehouse_stock_id  UUID        NOT NULL,
    quantity            INT         NOT NULL,
    reserved_quantity   INT         NOT NULL,
    available_quantity  INT         NOT NULL,
    snapshot_time       TIMESTAMP   NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_stock_snapshots
        PRIMARY KEY (id),
    CONSTRAINT fk_snapshot_warehouse_stock
        FOREIGN KEY (warehouse_stock_id)
        REFERENCES warehouse_stock (id)
);

CREATE INDEX idx_snapshot_warehouse_stock_id
    ON stock_snapshots (warehouse_stock_id);
CREATE INDEX idx_snapshot_time
    ON stock_snapshots (snapshot_time);