-- Create Tables

-- Enable UUID extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Categories Table
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description TEXT,
                            parent_id UUID,
                            version BIGINT DEFAULT 0,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            created_by VARCHAR(255),
                            modified_by VARCHAR(255),
                            CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- Products Table
CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(19, 2) NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          image_url VARCHAR(255),
                          category_id UUID NOT NULL,
                          active BOOLEAN DEFAULT TRUE,
                          version BIGINT DEFAULT 0,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(255),
                          modified_by VARCHAR(255),
                          CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Product Attributes Table
CREATE TABLE product_attributes (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    name VARCHAR(255) NOT NULL,
                                    value VARCHAR(255) NOT NULL,
                                    product_id UUID NOT NULL,
                                    version BIGINT DEFAULT 0,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    created_by VARCHAR(255),
                                    modified_by VARCHAR(255),
                                    CONSTRAINT fk_attribute_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create Indexes
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_active ON products(active);
CREATE INDEX idx_category_parent ON categories(parent_id);
CREATE INDEX idx_product_attribute_product ON product_attributes(product_id);