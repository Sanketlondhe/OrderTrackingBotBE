package com.example.ordertrackingsupportbot.service;

import com.example.ordertrackingsupportbot.dto.OrderListResponse;
import com.example.ordertrackingsupportbot.dto.OrderResponse;
import com.example.ordertrackingsupportbot.entity.Order;
import com.example.ordertrackingsupportbot.model.OrderStatus;
import com.example.ordertrackingsupportbot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Fetch a single order by orderId.
     * Cached for performance — avoids repeat DB hits for same order.
     */
    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order: {}", orderId);
        if (orderId == null || orderId.isBlank()) {
            return OrderResponse.error("Order ID cannot be empty.");
        }

        Optional<Order> order = orderRepository.findByOrderId(orderId.trim().toUpperCase());
        return order
                .map(OrderResponse::from)
                .orElse(OrderResponse.error("No order found with ID: " + orderId));
    }

    /**
     * Fetch all orders for a customer by email.
     */
    public OrderListResponse getOrdersByEmail(String email) {
        log.info("Fetching orders for email: {}", email);
        if (email == null || email.isBlank()) {
            return OrderListResponse.error("Email cannot be empty.");
        }

        List<Order> orders = orderRepository.findByCustomerEmailIgnoreCase(email.trim());
        if (orders.isEmpty()) {
            return OrderListResponse.error("No orders found for email: " + email);
        }

        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .toList();
        return OrderListResponse.of(responses);
    }

    /**
     * Get only active (non-delivered, non-cancelled) orders for a customer.
     */
    public OrderListResponse getActiveOrders(String email) {
        log.info("Fetching active orders for email: {}", email);
        if (email == null || email.isBlank()) {
            return OrderListResponse.error("Email cannot be empty.");
        }

        List<Order> allOrders = orderRepository.findByCustomerEmailIgnoreCase(email.trim());
        List<OrderResponse> active = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED
                        && o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.RETURNED)
                .map(OrderResponse::from)
                .toList();

        if (active.isEmpty()) {
            return OrderListResponse.error("No active orders found for: " + email);
        }
        return OrderListResponse.of(active);
    }

    /**
     * Request a return/cancellation for an order.
     */
    public OrderResponse requestReturn(String orderId) {
        log.info("Return requested for order: {}", orderId);
        if (orderId == null || orderId.isBlank()) {
            return OrderResponse.error("Order ID cannot be empty.");
        }

        Optional<Order> optOrder = orderRepository.findByOrderId(orderId.trim().toUpperCase());
        if (optOrder.isEmpty()) {
            return OrderResponse.error("No order found with ID: " + orderId);
        }

        Order order = optOrder.get();
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return OrderResponse.error("Order is already cancelled.");
        }
        if (order.getStatus() == OrderStatus.RETURN_REQUESTED || order.getStatus() == OrderStatus.RETURNED) {
            return OrderResponse.error("Return already requested for this order.");
        }
        if (order.getStatus() == OrderStatus.PROCESSING || order.getStatus() == OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if (order.getStatus() == OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.RETURN_REQUESTED);
        } else {
            return OrderResponse.error("Cannot request return for order in status: " + order.getStatus());
        }

        orderRepository.save(order);
        return OrderResponse.from(order);
    }
}
