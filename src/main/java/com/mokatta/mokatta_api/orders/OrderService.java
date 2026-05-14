package com.mokatta.mokatta_api.orders;

import com.mokatta.mokatta_api.orders.dtos.CreateOrderRequest;
import com.mokatta.mokatta_api.orders.dtos.OrderItemRequest;
import com.mokatta.mokatta_api.orders.dtos.OrderItemResponse;
import com.mokatta.mokatta_api.orders.dtos.OrderResponse;
import com.mokatta.mokatta_api.products.Product;
import com.mokatta.mokatta_api.products.ProductRepository;
import com.mokatta.mokatta_api.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Order order = new Order();
        order.setUser(currentUser);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found: " + itemReq.getProductId()));

            if (!product.isActive() || !product.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not available for sale: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);

            total = total.add(subtotal);
        }

        order.setSubtotal(total);
        order.setTotal(total); // Right now subtotal and total are the same (no tax/discount)

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId().toString(),
                        item.getProduct().getId().toString(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId().toString(),
                order.getUser().getId().toString(),
                order.getUser().getEmail(),
                order.getPaymentMethod().name(),
                order.getSubtotal(),
                order.getTotal(),
                order.getNotes(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
