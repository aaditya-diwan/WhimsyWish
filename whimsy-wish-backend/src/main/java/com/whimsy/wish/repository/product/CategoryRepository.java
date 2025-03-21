package com.whimsy.wish.repository.product;

import com.whimsy.wish.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findAllParentCategories();

    List<Category> findByParentId(UUID parentId);
}