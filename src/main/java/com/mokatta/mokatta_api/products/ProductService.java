package com.mokatta.mokatta_api.products;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mokatta.mokatta_api.categories.Category;
import com.mokatta.mokatta_api.categories.CategoryRepository;
import com.mokatta.mokatta_api.products.dtos.CreateProductRequest;
import com.mokatta.mokatta_api.products.dtos.ProductResponse;
import com.mokatta.mokatta_api.products.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> findAll(UUID categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryIdAndActiveTrue(categoryId);
        } else {
            products = productRepository.findByActiveTrue();
        }
        return products.stream().map(this::toResponse).toList();
    }

    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toResponse(product);
    }

    public ProductResponse create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .imageUrl(request.getImageUrl())
                .available(true)
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        return toResponse(productRepository.save(product));
    }

    public void toggleAvailable(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setAvailable(!product.isAvailable());
        productRepository.save(product);
    }

    public void toggleActive(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setActive(!product.isActive());
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId().toString(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId().toString(),
                product.getCategory().getName(),
                product.getImageUrl(),
                product.isAvailable(),
                product.isActive(),
                product.getCreatedAt());
    }
}
