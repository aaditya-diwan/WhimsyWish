-- Enable UUID extension (moved from V0 for completeness)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables for user-related entities
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255),
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE,
                       account_non_expired BOOLEAN DEFAULT TRUE,
                       account_non_locked BOOLEAN DEFAULT TRUE,
                       credentials_non_expired BOOLEAN DEFAULT TRUE,
                       version BIGINT DEFAULT 0,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       created_by VARCHAR(255),
                       modified_by VARCHAR(255)
);

CREATE TABLE addresses (
                           id UUID PRIMARY KEY,
                           street_address VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           phone_number VARCHAR(12) NOT NULL,
                           state VARCHAR(255) NOT NULL,
                           postal_code VARCHAR(255) NOT NULL,
                           country VARCHAR(255) NOT NULL,
                           additional_info VARCHAR(255),
                           is_default BOOLEAN DEFAULT FALSE,
                           user_id UUID NOT NULL REFERENCES users(id),
                           version BIGINT DEFAULT 0,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,
                           created_by VARCHAR(255),
                           modified_by VARCHAR(255)
);

-- Create tables for product-related entities
CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description TEXT,
                            parent_id UUID REFERENCES categories(id),
                            version BIGINT DEFAULT 0,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            created_by VARCHAR(255),
                            modified_by VARCHAR(255)
);

CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(19, 2) NOT NULL,
                          image_url VARCHAR(255) NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          active BOOLEAN DEFAULT TRUE,
                          category_id UUID REFERENCES categories(id),
                          version BIGINT DEFAULT 0,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(255),
                          modified_by VARCHAR(255)
);

CREATE TABLE product_attributes (
                                    id UUID PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    value VARCHAR(255) NOT NULL,
                                    product_id UUID NOT NULL REFERENCES products(id),
                                    version BIGINT DEFAULT 0,
                                    created_at TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP NOT NULL,
                                    created_by VARCHAR(255),
                                    modified_by VARCHAR(255)
);

-- Create tables for order-related entities
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id),
    shipping_address_id UUID NOT NULL REFERENCES addresses(id),
    billing_address_id UUID NOT NULL REFERENCES addresses(id),
    subtotal DECIMAL(19, 2) NOT NULL,
    tax DECIMAL(19, 2) NOT NULL,
    shipping_cost DECIMAL(19, 2) NOT NULL,
    discount DECIMAL(19, 2) NOT NULL,
    total DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_intent_id VARCHAR(255),
    payment_date TIMESTAMP,
    shipping_date TIMESTAMP,
    delivery_date TIMESTAMP,
    notes VARCHAR(1000),
    cancel_reason VARCHAR(500),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    modified_by VARCHAR(255)
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    product_id UUID NOT NULL REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(100) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    customization VARCHAR(500),
    product_details VARCHAR(1000),
    subtotal DECIMAL(19, 2) NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    modified_by VARCHAR(255)
);

CREATE TABLE order_status_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    previous_status VARCHAR(50),
    timestamp VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    comment VARCHAR(500),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    modified_by VARCHAR(255)
);