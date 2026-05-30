package com.foodbyte.inventory.repository;

import com.foodbyte.inventory.entity.Category;
import com.foodbyte.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // search by name — partial match
    List<Product> findByNameContainingAndIsActiveTrue(String name);

    // filter by category
    List<Product> findByCategoryAndIsActiveTrue(Category category);

    // filter by brand
    List<Product> findByBrandAndIsActiveTrue(String brand);

    // find by SKU — used by Order Service
    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    // duplicate SKU check before creating
    boolean existsBySku(String sku);

    // all active products — admin dashboard
    List<Product> findByIsActiveTrue();

    // search by name AND category combined
    List<Product> findByNameContainingAndCategoryAndIsActiveTrue(
            String name, Category category
    );
}