package com.example.ordertrackingsupportbot.service;


import com.example.ordertrackingsupportbot.tool.OrderTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final OrderTools orderTools;



    private static final String SYSTEM_PROMPT = """
            You are an Order Tracking Support Bot for an e-commerce platform.
            
            Your capabilities:
            - Track orders by Order ID (format: ORD-XXXX)
            - Show all orders for a customer by email
            - Show only active/in-progress orders
            - Help with return or cancellation requests
            
            Tool usage guidance:
            - Use trackOrderById when user provides an Order ID
            - Use getAllOrdersByEmail when user wants full order history
            - Use getActiveOrders for "where is my order" or "pending orders" queries
            - Use requestReturnOrCancellation ONLY when user explicitly asks to cancel/return
            
            Behavior rules:
            - Always ask for Order ID or email if not provided
            - Never make up order details — only use tool results
            - If a tool returns success=false, explain the issue politely
            - Be concise, friendly, and professional
            - Format currency amounts in INR (₹)
            - For tracking numbers, mention the user can track on courier website
            """;

    public String chat(String userMessage, String sessionId) {
        log.info("Chat request | session={} | message={}", sessionId, userMessage);

        String reply = chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .tools(orderTools)
                .call()
                .content();

        return reply;
    }
}
