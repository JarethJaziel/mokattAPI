package com.mokatta.mokatta_api.categories;

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

import com.mokatta.mokatta_api.categories.dtos.CategoryResponse;
import com.mokatta.mokatta_api.categories.dtos.CreateCategoryRequest;
import com.mokatta.mokatta_api.categories.dtos.UpdateCategoryRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category management (requires JWT)")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories", description = "Returns all categories.", responses = {
            @ApiResponse(responseCode = "200", description = "Category list")
    })
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content)
    })
    public ResponseEntity<CategoryResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create category", responses = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name", content = @Content)
    })
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", responses = {
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content)
    })
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle category active status",
            description = "Soft delete — toggles whether the category is active.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Active status toggled"),
                    @ApiResponse(responseCode = "400", description = "Category not found", content = @Content)
            })
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        categoryService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }
}
