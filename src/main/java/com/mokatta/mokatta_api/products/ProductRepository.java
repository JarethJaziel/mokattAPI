package com.mokatta.mokatta_api.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryAndActiveTrue(String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findDistinctCategories();

    boolean existsByNameAndActiveTrue(String name);
}
