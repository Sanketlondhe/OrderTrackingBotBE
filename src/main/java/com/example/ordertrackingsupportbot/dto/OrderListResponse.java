package com.example.ordertrackingsupportbot.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderListResponse(
        boolean success,
        String errorMessage,
        int totalOrders,
        List<OrderResponse> orders
) {
    public static OrderListResponse of(List<OrderResponse> orders) {
        return new OrderListResponse(true, null, orders.size(), orders);
    }

    public static OrderListResponse error(String message) {
        return new OrderListResponse(false, message, 0, null);
    }
}
