-- Store parent category IDs in variables for reference
DO $$
DECLARE
electronics_id UUID;
    clothing_id UUID;
    home_kitchen_id UUID;
BEGIN
    -- Insert Parent Categories
INSERT INTO categories (id, name, description, version, created_at, updated_at)
VALUES (gen_random_uuid(), 'Electronics', 'Electronic devices and accessories', 0, NOW(), NOW())
    RETURNING id INTO electronics_id;

INSERT INTO categories (id, name, description, version, created_at, updated_at)
VALUES (gen_random_uuid(), 'Clothing', 'Apparel and fashion items', 0, NOW(), NOW())
    RETURNING id INTO clothing_id;

INSERT INTO categories (id, name, description, version, created_at, updated_at)
VALUES (gen_random_uuid(), 'Home & Kitchen', 'Home appliances and kitchen essentials', 0, NOW(), NOW())
    RETURNING id INTO home_kitchen_id;

-- Insert Subcategories
INSERT INTO categories (id, name, description, parent_id, version, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Smartphones', 'Mobile phones and accessories', electronics_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'Laptops', 'Laptops and accessories', electronics_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'Men''s Wear', 'Clothing for men', clothing_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'Women''s Wear', 'Clothing for women', clothing_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'Cookware', 'Pots, pans and cooking utensils', home_kitchen_id, 0, NOW(), NOW());
END $$;

-- Insert Products for each subcategory
DO $$
DECLARE
smartphones_id UUID;
    laptops_id UUID;
    mens_wear_id UUID;
    womens_wear_id UUID;
    cookware_id UUID;

    iphone_id UUID;
    samsung_id UUID;
    macbook_id UUID;
BEGIN
    -- Get subcategory IDs
SELECT id INTO smartphones_id FROM categories WHERE name = 'Smartphones';
SELECT id INTO laptops_id FROM categories WHERE name = 'Laptops';
SELECT id INTO mens_wear_id FROM categories WHERE name = 'Men''s Wear';
SELECT id INTO womens_wear_id FROM categories WHERE name = 'Women''s Wear';
SELECT id INTO cookware_id FROM categories WHERE name = 'Cookware';

-- Smartphones
INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, active, version, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'iPhone 15 Pro', '6.1-inch display, 256GB storage', 999.99, 50,
     smartphones_id, 'https://example.com/iphone15.jpg', true, 0, NOW(), NOW())
    RETURNING id INTO iphone_id;

INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, active, version, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Samsung Galaxy S24', '6.8-inch display, 128GB storage', 899.99, 45,
     smartphones_id, 'https://example.com/galaxys24.jpg', true, 0, NOW(), NOW())
    RETURNING id INTO samsung_id;

-- Laptops
INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, active, version, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'MacBook Pro M3', '14-inch Retina display, 16GB RAM, 512GB SSD', 1999.99, 30,
     laptops_id, 'https://example.com/macbookpro.jpg', true, 0, NOW(), NOW())
    RETURNING id INTO macbook_id;

-- Insert more products with proper timestamps and version...

-- Insert Product Attributes
-- iPhone attributes
INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Color', 'Silver', iphone_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'Storage', '256GB', iphone_id, 0, NOW(), NOW()),
    (gen_random_uuid(), 'RAM', '8GB', iphone_id, 0, NOW(), NOW());

-- Add other product attributes...
END $$;

-- Add a test user (password is "password" encrypted with BCrypt)
INSERT INTO users (
    id, email, password, first_name, last_name, phone_number,
    role, enabled, account_non_expired, account_non_locked,
    credentials_non_expired, version, created_at, updated_at
)
VALUES (
           gen_random_uuid(), 'test@example.com',
           '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
           'Test', 'User', '+1234567890',
           'ROLE_USER', true, true, true,
           true, 0, NOW(), NOW()
       );