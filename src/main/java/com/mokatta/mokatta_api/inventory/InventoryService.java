package com.mokatta.mokatta_api.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mokatta.mokatta_api.inventory.dtos.AdjustStockRequest;
import com.mokatta.mokatta_api.inventory.dtos.CreateIngredientRequest;
import com.mokatta.mokatta_api.inventory.dtos.IngredientResponse;
import com.mokatta.mokatta_api.inventory.dtos.UpdateIngredientRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;

    public List<IngredientResponse> findAll() {
        return ingredientRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IngredientResponse> findLowStock() {
        return ingredientRepository.findLowStockIngredients().stream()
                .map(this::toResponse)
                .toList();
    }

    public IngredientResponse findById(UUID id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        return toResponse(ingredient);
    }

    public IngredientResponse create(CreateIngredientRequest request) {
        if (ingredientRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new IllegalArgumentException("An active ingredient with that name already exists");
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .minStock(request.getMinStock())
                .active(true)
                .build();

        return toResponse(ingredientRepository.save(ingredient));
    }

    public IngredientResponse update(UUID id, UpdateIngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        if (request.getName() != null) {
            ingredient.setName(request.getName());
        }
        if (request.getUnit() != null) {
            ingredient.setUnit(request.getUnit());
        }
        if (request.getMinStock() != null) {
            ingredient.setMinStock(request.getMinStock());
        }

        return toResponse(ingredientRepository.save(ingredient));
    }

    public IngredientResponse adjustStock(UUID id, AdjustStockRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        BigDecimal newQuantity;
        if (request.getType() == AdjustStockRequest.AdjustType.ADD) {
            newQuantity = ingredient.getQuantity().add(request.getAmount());
        } else {
            newQuantity = ingredient.getQuantity().subtract(request.getAmount());
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cannot subtract more than current stock");
            }
        }

        ingredient.setQuantity(newQuantity);
        return toResponse(ingredientRepository.save(ingredient));
    }

    public void toggleActive(UUID id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        ingredient.setActive(!ingredient.isActive());
        ingredientRepository.save(ingredient);
    }

    private IngredientResponse toResponse(Ingredient ingredient) {
        boolean isLowStock = ingredient.getQuantity().compareTo(ingredient.getMinStock()) <= 0;
        return new IngredientResponse(
                ingredient.getId().toString(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit(),
                ingredient.getMinStock(),
                ingredient.isActive(),
                isLowStock,
                ingredient.getUpdatedAt());
    }
}
