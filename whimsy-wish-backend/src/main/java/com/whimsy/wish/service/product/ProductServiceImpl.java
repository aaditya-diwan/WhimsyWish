package com.whimsy.wish.service.product;

import com.whimsy.wish.domain.product.Category;
import com.whimsy.wish.domain.product.Product;
import com.whimsy.wish.domain.product.ProductAttribute;
import com.whimsy.wish.dto.product.ProductCreateRequest;
import com.whimsy.wish.dto.product.ProductUpdateRequest;
import com.whimsy.wish.dto.product.ProductResponse;
import com.whimsy.wish.exception.ResourceNotFoundException;
import com.whimsy.wish.repository.product.CategoryRepository;
import com.whimsy.wish.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.whimsy.wish.dto.product.ProductAttributeDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = (Category) categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(true)
                .build();

        Set<ProductAttribute> attributes = new HashSet<>();
        if (request.getAttributes() != null) {
            attributes = request.getAttributes().stream()
                    .map(attr -> ProductAttribute.builder()
                            .name(attr.getName())
                            .value(attr.getValue())
                            .product(product)
                            .build())
                    .collect(Collectors.toSet());
        }
        product.setAttributes((HashSet<ProductAttribute>) attributes);

        Product savedProduct = productRepository.save(product);
        log.info("Created new product: {}", savedProduct.getId());

        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        if (request.getCategoryId() != null) {
            Category category = (Category) categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        // Handle attributes if provided
        if (request.getAttributes() != null) {
            // Remove all existing attributes and add new ones
            product.getAttributes().clear();

            Set<ProductAttribute> attributes = request.getAttributes().stream()
                    .map(attr -> ProductAttribute.builder()
                            .name(attr.getName())
                            .value(attr.getValue())
                            .product(product)
                            .build())
                    .collect(Collectors.toSet());

            product.setAttributes((HashSet<ProductAttribute>) attributes);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product: {}", updatedProduct.getId());

        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Soft delete - set active to false
        product.setActive(false);
        productRepository.save(product);
        log.info("Soft deleted product: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findProductsWithLowStock(threshold).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateProductStock(UUID id, int quantityChange) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newQuantity = product.getStockQuantity() + quantityChange;

        if (newQuantity < 0) {
            log.warn("Cannot update stock for product {} to negative value", id);
            return false;
        }

        product.setStockQuantity(newQuantity);
        productRepository.save(product);
        log.info("Updated stock for product {}: {} -> {}", id, product.getStockQuantity() - quantityChange, product.getStockQuantity());

        return true;
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .attributes(product.getAttributes().stream()
                        .map(attr -> new ProductAttributeDto(attr.getName(), attr.getValue()))
                        .collect(Collectors.toSet()))
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}