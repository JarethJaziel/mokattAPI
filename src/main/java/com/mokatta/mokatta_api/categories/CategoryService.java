package com.mokatta.mokatta_api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mokatta.mokatta_api.categories.dtos.CategoryResponse;
import com.mokatta.mokatta_api.categories.dtos.CreateCategoryRequest;
import com.mokatta.mokatta_api.categories.dtos.UpdateCategoryRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return toResponse(category);
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A category with that name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        return toResponse(categoryRepository.save(category));
    }

    public void toggleActive(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId().toString(),
                category.getName(),
                category.isActive(),
                category.getCreatedAt());
    }
}
