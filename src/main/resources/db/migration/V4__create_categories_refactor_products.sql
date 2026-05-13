-- V4__create_categories_refactor_products.sql
-- Create categories table and replace products.category VARCHAR with FK reference

CREATE TABLE categories (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100)    NOT NULL UNIQUE,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Replace the category VARCHAR column with a FK to categories
ALTER TABLE products DROP COLUMN category;
ALTER TABLE products ADD COLUMN category_id UUID NOT NULL REFERENCES categories(id);
