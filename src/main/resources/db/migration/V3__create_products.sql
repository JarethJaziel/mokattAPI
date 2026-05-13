-- V3__create_products.sql
-- Product catalog with category as a string field

CREATE TABLE products (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)    NOT NULL,
    description TEXT,
    price       DECIMAL(10,2)   NOT NULL,
    category    VARCHAR(100)    NOT NULL,
    image_url   VARCHAR(500),
    available   BOOLEAN         NOT NULL DEFAULT TRUE,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);
