package com.mokatta.mokatta_api.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

    List<Product> findByAvailableTrueAndActiveTrue();
}
