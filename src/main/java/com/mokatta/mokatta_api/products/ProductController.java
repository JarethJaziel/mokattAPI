package com.mokatta.mokatta_api.products;

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

import com.mokatta.mokatta_api.products.dtos.CreateProductRequest;
import com.mokatta.mokatta_api.products.dtos.ProductResponse;
import com.mokatta.mokatta_api.products.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management (requires JWT)")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products", description = "Returns all active products. Optionally filter by category ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Product list")
    })
    public ResponseEntity<List<ProductResponse>> findAll(
            @RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(productService.findAll(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Adds a new product to the catalog.", responses = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or Category not found", content = @Content)
    })
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates product fields. Only non-null fields are applied.", responses = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Product or Category not found", content = @Content)
    })
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-available")
    @Operation(summary = "Toggle product availability",
            description = "Toggles whether the product can be ordered right now.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Availability toggled"),
                    @ApiResponse(responseCode = "400", description = "Product not found", content = @Content)
            })
    public ResponseEntity<Void> toggleAvailable(@PathVariable UUID id) {
        productService.toggleAvailable(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle product active status",
            description = "Soft delete — toggles whether the product exists in the system.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Active status toggled"),
                    @ApiResponse(responseCode = "400", description = "Product not found", content = @Content)
            })
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        productService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }
}
