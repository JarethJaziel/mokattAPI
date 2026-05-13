package com.mokatta.mokatta_api.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mokatta.mokatta_api.inventory.dtos.AdjustStockRequest;
import com.mokatta.mokatta_api.inventory.dtos.CreateIngredientRequest;
import com.mokatta.mokatta_api.inventory.dtos.IngredientResponse;
import com.mokatta.mokatta_api.inventory.dtos.UpdateIngredientRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Raw ingredients inventory management (requires JWT)")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "List all active ingredients", responses = {
            @ApiResponse(responseCode = "200", description = "Ingredient list")
    })
    public ResponseEntity<List<IngredientResponse>> findAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "List low stock ingredients", description = "Returns active ingredients where quantity <= minStock.", responses = {
            @ApiResponse(responseCode = "200", description = "Low stock ingredient list")
    })
    public ResponseEntity<List<IngredientResponse>> findLowStock() {
        return ResponseEntity.ok(inventoryService.findLowStock());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ingredient by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Ingredient found",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ingredient not found", content = @Content)
    })
    public ResponseEntity<IngredientResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create ingredient", responses = {
            @ApiResponse(responseCode = "201", description = "Ingredient created",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
    })
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody CreateIngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update ingredient details", description = "Updates name, unit, or minStock. Does NOT update quantity.", responses = {
            @ApiResponse(responseCode = "200", description = "Ingredient updated",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ingredient not found", content = @Content)
    })
    public ResponseEntity<IngredientResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateIngredientRequest request) {
        return ResponseEntity.ok(inventoryService.update(id, request));
    }

    @PatchMapping("/{id}/adjust")
    @Operation(summary = "Adjust ingredient stock", description = "Adds or subtracts quantity from the current stock.", responses = {
            @ApiResponse(responseCode = "200", description = "Stock adjusted",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ingredient not found or insufficient stock", content = @Content)
    })
    public ResponseEntity<IngredientResponse> adjustStock(@PathVariable UUID id,
            @Valid @RequestBody AdjustStockRequest request) {
        return ResponseEntity.ok(inventoryService.adjustStock(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle active status", description = "Soft delete an ingredient.", responses = {
            @ApiResponse(responseCode = "204", description = "Status toggled"),
            @ApiResponse(responseCode = "400", description = "Ingredient not found", content = @Content)
    })
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        inventoryService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }
}
