package com.mokatta.mokatta_api.products;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mokatta.mokatta_api.products.dtos.CreateProductRequest;
import com.mokatta.mokatta_api.products.dtos.ProductResponse;
import com.mokatta.mokatta_api.products.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAll(String category) {
        List<Product> products;
        if (category != null && !category.isBlank()) {
            products = productRepository.findByCategoryAndActiveTrue(category);
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

    public List<String> findCategories() {
        return productRepository.findDistinctCategories();
    }

    public ProductResponse create(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
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
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
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
                product.getCategory(),
                product.getImageUrl(),
                product.isAvailable(),
                product.isActive(),
                product.getCreatedAt());
    }
}
