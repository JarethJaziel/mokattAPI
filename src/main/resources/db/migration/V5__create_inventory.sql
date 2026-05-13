-- V5__create_inventory.sql
-- Manual tracking of raw ingredients

CREATE TABLE ingredients (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)    NOT NULL UNIQUE,
    quantity    DECIMAL(10,2)   NOT NULL DEFAULT 0,
    unit        VARCHAR(20)     NOT NULL,  -- 'kg', 'L', 'units', 'g', 'mL'
    min_stock   DECIMAL(10,2)   NOT NULL DEFAULT 0,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);
