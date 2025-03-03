-- Store parent category IDs in variables for reference
DO $$
DECLARE
electronics_id UUID;
    clothing_id UUID;
    home_kitchen_id UUID;
BEGIN
    -- Insert Parent Categories
INSERT INTO categories (name, description)
VALUES ('Electronics', 'Electronic devices and accessories')
    RETURNING id INTO electronics_id;

INSERT INTO categories (name, description)
VALUES ('Clothing', 'Apparel and fashion items')
    RETURNING id INTO clothing_id;

INSERT INTO categories (name, description)
VALUES ('Home & Kitchen', 'Home appliances and kitchen essentials')
    RETURNING id INTO home_kitchen_id;

-- Insert Subcategories
INSERT INTO categories (name, description, parent_id)
VALUES
    ('Smartphones', 'Mobile phones and accessories', electronics_id),
    ('Laptops', 'Laptops and accessories', electronics_id),
    ('Men''s Wear', 'Clothing for men', clothing_id),
    ('Women''s Wear', 'Clothing for women', clothing_id),
    ('Cookware', 'Pots, pans and cooking utensils', home_kitchen_id);
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
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('iPhone 15 Pro', '6.1-inch display, 256GB storage', 999.99, 50,
     smartphones_id, 'https://example.com/iphone15.jpg')
    RETURNING id INTO iphone_id;

INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('Samsung Galaxy S24', '6.8-inch display, 128GB storage', 899.99, 45,
     smartphones_id, 'https://example.com/galaxys24.jpg')
    RETURNING id INTO samsung_id;

-- Laptops
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('MacBook Pro M3', '14-inch Retina display, 16GB RAM, 512GB SSD', 1999.99, 30,
     laptops_id, 'https://example.com/macbookpro.jpg')
    RETURNING id INTO macbook_id;

INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('Dell XPS 15', '15.6-inch 4K display, 32GB RAM, 1TB SSD', 1799.99, 25,
     laptops_id, 'https://example.com/dellxps.jpg');

-- Men's Wear
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('Classic Fit Shirt', '100% cotton casual shirt', 49.99, 100,
     mens_wear_id, 'https://example.com/mensshirt.jpg'),

    ('Slim Fit Jeans', 'Denim jeans with stretch comfort', 59.99, 75,
     mens_wear_id, 'https://example.com/mensjeans.jpg');

-- Women's Wear
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('Summer Dress', 'Floral pattern summer dress', 79.99, 60,
     womens_wear_id, 'https://example.com/summerdress.jpg'),

    ('Yoga Pants', 'High-waist yoga leggings', 39.99, 90,
     womens_wear_id, 'https://example.com/yogapants.jpg');

-- Cookware
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url)
VALUES
    ('Non-Stick Pan Set', 'Set of 3 non-stick frying pans', 89.99, 40,
     cookware_id, 'https://example.com/panset.jpg'),

    ('Chef''s Knife', 'Professional 8-inch chef knife', 69.99, 35,
     cookware_id, 'https://example.com/knife.jpg');

-- Insert Product Attributes
-- iPhone attributes
INSERT INTO product_attributes (name, value, product_id)
VALUES
    ('Color', 'Silver', iphone_id),
    ('Storage', '256GB', iphone_id),
    ('RAM', '8GB', iphone_id);

-- Samsung attributes
INSERT INTO product_attributes (name, value, product_id)
VALUES
    ('Color', 'Black', samsung_id),
    ('Storage', '128GB', samsung_id),
    ('RAM', '12GB', samsung_id);

-- MacBook attributes
INSERT INTO product_attributes (name, value, product_id)
VALUES
    ('Color', 'Space Gray', macbook_id),
    ('CPU', 'Apple M3', macbook_id),
    ('Display', '14-inch Retina', macbook_id);
END $$;