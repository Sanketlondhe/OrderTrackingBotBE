package com.example.ordertrackingsupportbot.dto;


import com.example.ordertrackingsupportbot.entity.Order;
import com.example.ordertrackingsupportbot.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Structured response returned by tools to the AI.
 * Never throw exceptions — always return success/error in this object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        boolean success,
        String errorMessage,
        String orderId,
        String customerName,
        String customerEmail,
        String productName,
        Integer quantity,
        BigDecimal totalAmount,
        OrderStatus status,
        String statusLabel,
        LocalDate estimatedDelivery,
        String trackingNumber,
        LocalDateTime createdAt
) {
    /** Build from a real Order entity */
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                true, null,
                order.getOrderId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                humanReadableStatus(order.getStatus()),
                order.getEstimatedDelivery(),
                order.getTrackingNumber(),
                order.getCreatedAt()
        );
    }

    /** Build an error response */
    public static OrderResponse error(String message) {
        return new OrderResponse(
                false, message,
                null, null, null, null, null, null, null, null, null, null, null
        );
    }

    private static String humanReadableStatus(OrderStatus status) {
        return switch (status) {
            case PROCESSING      -> "Order is being processed";
            case SHIPPED         -> "Order has been shipped";
            case OUT_FOR_DELIVERY-> "Out for delivery today";
            case DELIVERED       -> "Order delivered";
            case CANCELLED       -> "Order cancelled";
            case RETURN_REQUESTED-> "Return request raised";
            case RETURNED        -> "Order returned";
        };
    }
}
