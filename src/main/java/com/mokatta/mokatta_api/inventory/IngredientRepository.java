package com.mokatta.mokatta_api.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {

    List<Ingredient> findByActiveTrue();

    @Query("SELECT i FROM Ingredient i WHERE i.active = true AND i.quantity <= i.minStock")
    List<Ingredient> findLowStockIngredients();

    boolean existsByNameAndActiveTrue(String name);
}
