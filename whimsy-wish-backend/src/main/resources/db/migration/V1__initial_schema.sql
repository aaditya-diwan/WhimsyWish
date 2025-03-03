-- Create tables for user-related entities
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255),
                       role VARCHAR(50),
                       enabled BOOLEAN DEFAULT TRUE,
                       account_non_expired BOOLEAN DEFAULT TRUE,
                       account_non_locked BOOLEAN DEFAULT TRUE,
                       credentials_non_expired BOOLEAN DEFAULT TRUE,
                       version BIGINT,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       created_by VARCHAR(255),
                       modified_by VARCHAR(255)
);

CREATE TABLE addresses (
                           id UUID PRIMARY KEY,
                           street_address VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           state VARCHAR(255) NOT NULL,
                           postal_code VARCHAR(255) NOT NULL,
                           country VARCHAR(255) NOT NULL,
                           additional_info VARCHAR(255),
                           is_default BOOLEAN DEFAULT FALSE,
                           user_id UUID NOT NULL REFERENCES users(id),
                           version BIGINT,
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
                            version BIGINT,
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
                          active BOOLEAN DEFAULT TRUE,
                          category_id UUID REFERENCES categories(id),
                          version BIGINT,
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
                                    version BIGINT,
                                    created_at TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP NOT NULL,
                                    created_by VARCHAR(255),
                                    modified_by VARCHAR(255)
);