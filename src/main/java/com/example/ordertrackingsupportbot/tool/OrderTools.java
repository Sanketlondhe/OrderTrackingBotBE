package com.example.ordertrackingsupportbot.tool;

import com.example.ordertrackingsupportbot.dto.OrderResponse;
import com.example.ordertrackingsupportbot.service.OrderService;
import com.example.ordertrackingsupportbot.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Tool definitions for the AI agent.
 *
 * Best Practices Applied:
 * - @Tool descriptions kept under 50 words (concise for fewer tokens)
 * - Return structured objects, never plain strings
 * - No exceptions propagated to AI (errors wrapped in response)
 * - Each tool has single responsibility
 * - Parameters validated in OrderService before DB calls
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTools {

    private final OrderService orderService;

    /**
     * TOOL 1: Track order by ID
     * Use when: user provides an order ID like ORD-1001
     */
    @Tool(description = "Fetch order details by order ID. Use when user provides an order ID (e.g., ORD-1001). Returns status, tracking, product info, and delivery date.")
    public OrderResponse trackOrderById(
            @ToolParam(description = "The order ID, e.g. ORD-1001") String orderId
    ) {
        return orderService.getOrderById(orderId);
    }

    /**
     * TOOL 2: Get all orders by customer email
     * Use when: user wants to see all their orders
     */
    @Tool(description = "Get all orders for a customer by email. Use when user asks 'show my orders' or 'what did I order'. Returns full order history.")
    public OrderListResponse getAllOrdersByEmail(
            @ToolParam(description = "Customer's registered email address") String email
    ) {
        return orderService.getOrdersByEmail(email);
    }

    /**
     * TOOL 3: Get only active/pending orders
     * Composite tool — reduces API round trips vs filtering on AI side
     */
    @Tool(description = "Get only active (in-progress) orders for a customer by email. Excludes delivered and cancelled. Use for 'where is my order' type questions.")
    public OrderListResponse getActiveOrders(
            @ToolParam(description = "Customer's registered email address") String email
    ) {
        return orderService.getActiveOrders(email);
    }

    /**
     * TOOL 4: Request return or cancellation
     * Single responsibility: only handles return/cancel flow
     */
    @Tool(description = "Request a return or cancellation for an order by order ID. Use when user explicitly asks to cancel or return. Validates eligibility before updating.")
    public OrderResponse requestReturnOrCancellation(
            @ToolParam(description = "The order ID to cancel or return, e.g. ORD-1001") String orderId
    ) {
        return orderService.requestReturn(orderId);
    }
}
