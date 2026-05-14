package com.mokatta.mokatta_api.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
                SELECT p
                FROM Product p
                LEFT JOIN FETCH p.category
                WHERE p.active = true
            """)
    List<Product> findByActiveTrue();

    @Query("""
                SELECT p
                FROM Product p
                LEFT JOIN FETCH p.category
                WHERE p.category.id = :categoryId
                AND p.active = true
            """)
    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

    @Query("""
                SELECT p
                FROM Product p
                LEFT JOIN FETCH p.category
                WHERE p.available = true
                AND p.active = true
            """)
    List<Product> findByAvailableTrueAndActiveTrue();
}
