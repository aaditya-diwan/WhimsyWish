package com.whimsy.wish.service.product;

import com.whimsy.wish.dto.product.ProductResponse;
import com.whimsy.wish.dto.product.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.whimsy.wish.dto.product.ProductCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProduct(UUID id);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);

    void deleteProduct(UUID id);

    List<ProductResponse> getLowStockProducts(int threshold);

    boolean updateProductStock(UUID id, int quantityChange);
}