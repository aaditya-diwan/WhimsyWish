-- Store parent category IDs in variables for reference
DO $$
DECLARE
    electronics_id UUID;
    clothing_id UUID;
    home_kitchen_id UUID;
    
    -- Product category IDs
    smartphones_id UUID;
    laptops_id UUID;
    mens_wear_id UUID;
    womens_wear_id UUID;
    cookware_id UUID;
    
    -- Product IDs
    iphone_id UUID;
    samsung_id UUID;
    macbook_id UUID;
    mens_shirt_id UUID;
    womens_dress_id UUID;
    
    -- User and address IDs
    test_user_id UUID;
    shipping_address_id UUID;
    billing_address_id UUID;
    
    -- Order IDs
    test_order_id UUID;
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
        
    -- Get subcategory IDs
    SELECT id INTO smartphones_id FROM categories WHERE name = 'Smartphones';
    SELECT id INTO laptops_id FROM categories WHERE name = 'Laptops';
    SELECT id INTO mens_wear_id FROM categories WHERE name = 'Men''s Wear';
    SELECT id INTO womens_wear_id FROM categories WHERE name = 'Women''s Wear';
    SELECT id INTO cookware_id FROM categories WHERE name = 'Cookware';
    
    -- Insert Products
    -- Smartphones
    INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, sku, active, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'iPhone 15 Pro', '6.1-inch display, 256GB storage', 999.99, 50,
        smartphones_id, 'https://example.com/iphone15.jpg', 'IPHONE15PRO-256', true, 0, NOW(), NOW())
    RETURNING id INTO iphone_id;

    INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, sku, active, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Samsung Galaxy S24', '6.8-inch display, 128GB storage', 899.99, 45,
        smartphones_id, 'https://example.com/galaxys24.jpg', 'SAMSUNGS24-128', true, 0, NOW(), NOW())
    RETURNING id INTO samsung_id;

    -- Laptops
    INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, sku, active, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'MacBook Pro M3', '14-inch Retina display, 16GB RAM, 512GB SSD', 1999.99, 30,
        laptops_id, 'https://example.com/macbookpro.jpg', 'MACBOOKM3-512', true, 0, NOW(), NOW())
    RETURNING id INTO macbook_id;
    
    -- Men's Clothing
    INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, sku, active, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Classic Oxford Shirt', 'Premium cotton button-down shirt', 59.99, 100,
        mens_wear_id, 'https://example.com/oxford-shirt.jpg', 'MENOXFORD-M', true, 0, NOW(), NOW())
    RETURNING id INTO mens_shirt_id;
    
    -- Women's Clothing
    INSERT INTO products (id, name, description, price, stock_quantity, category_id, image_url, sku, active, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Summer Floral Dress', 'Lightweight midi dress with floral pattern', 79.99, 75,
        womens_wear_id, 'https://example.com/floral-dress.jpg', 'WOMENFLORAL-M', true, 0, NOW(), NOW())
    RETURNING id INTO womens_dress_id;

    -- Insert Product Attributes
    -- iPhone attributes
    INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Color', 'Silver', iphone_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Storage', '256GB', iphone_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'RAM', '8GB', iphone_id, 0, NOW(), NOW());
        
    -- Samsung attributes
    INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Color', 'Black', samsung_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Storage', '128GB', samsung_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'RAM', '12GB', samsung_id, 0, NOW(), NOW());
        
    -- MacBook attributes
    INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Color', 'Space Gray', macbook_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Storage', '512GB', macbook_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'RAM', '16GB', macbook_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Processor', 'M3 Pro', macbook_id, 0, NOW(), NOW());
        
    -- Men's shirt attributes
    INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Color', 'Blue', mens_shirt_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Size', 'Medium', mens_shirt_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Material', '100% Cotton', mens_shirt_id, 0, NOW(), NOW());
        
    -- Women's dress attributes
    INSERT INTO product_attributes (id, name, value, product_id, version, created_at, updated_at)
    VALUES
        (gen_random_uuid(), 'Color', 'Multicolor', womens_dress_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Size', 'Medium', womens_dress_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Material', 'Viscose', womens_dress_id, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Pattern', 'Floral', womens_dress_id, 0, NOW(), NOW());

    -- Add test users
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
    )
    RETURNING id INTO test_user_id;
    
    INSERT INTO users (
        id, email, password, first_name, last_name, phone_number,
        role, enabled, account_non_expired, account_non_locked,
        credentials_non_expired, version, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(), 'admin@example.com',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
        'Admin', 'User', '+1987654321',
        'ROLE_ADMIN', true, true, true,
        true, 0, NOW(), NOW()
    );
    
    -- Add addresses for test user
    INSERT INTO addresses (
        id, street_address, city, state, postal_code, country,
        additional_info, is_default, user_id, version, created_at, updated_at, phone_number
    )
    VALUES (
        gen_random_uuid(), '123 Main St', 'Anytown', 'CA', '12345', 'USA',
        'Apartment 4B', true, test_user_id, 0, NOW(), NOW(), '9423088745'
    )
    RETURNING id INTO shipping_address_id;
    
    INSERT INTO addresses (
        id, street_address, city, state, postal_code, country,
        additional_info, is_default, user_id, version, created_at, updated_at, phone_number
    )
    VALUES (
        gen_random_uuid(), '123 Main St', 'Anytown', 'CA', '12345', 'USA',
        'Apartment 4B', false, test_user_id, 0, NOW(), NOW(), '9423088745'
    )
    RETURNING id INTO billing_address_id;
    
    -- Create a sample order
    INSERT INTO orders (
        id, order_number, user_id, shipping_address_id, billing_address_id,
        subtotal, tax, shipping_cost, discount, total, status,
        payment_intent_id, payment_date, notes, version, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(), 'ORD-2023-0001', test_user_id, shipping_address_id, billing_address_id,
        1059.98, 84.80, 15.00, 0.00, 1159.78, 'PLACED',
        'pi_123456789', NOW(), 'First order', 0, NOW(), NOW()
    )
    RETURNING id INTO test_order_id;
    
    -- Add order items
    INSERT INTO order_items (
        id, order_id, product_id, product_name, product_sku, 
        price, quantity, subtotal, version, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(), test_order_id, iphone_id, 'iPhone 15 Pro', 'IPHONE15PRO-256',
        999.99, 1, 999.99, 0, NOW(), NOW()
    );
    
    INSERT INTO order_items (
        id, order_id, product_id, product_name, product_sku, 
        price, quantity, subtotal, version, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(), test_order_id, mens_shirt_id, 'Classic Oxford Shirt', 'MENOXFORD-M',
        59.99, 1, 59.99, 0, NOW(), NOW()
    );
    
    -- Add order status history
    INSERT INTO order_status_history (
        id, order_id, previous_status, timestamp, new_status, 
        comment, version, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(), test_order_id, NULL, NOW()::text, 'PLACED',
        'Order placed successfully', 0, NOW(), NOW()
    );
END $$;