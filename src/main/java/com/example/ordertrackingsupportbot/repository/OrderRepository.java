package com.example.ordertrackingsupportbot.repository;



import com.example.ordertrackingsupportbot.entity.Order;
import com.example.ordertrackingsupportbot.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByCustomerEmailIgnoreCase(String email);

    List<Order> findByCustomerEmailIgnoreCaseAndStatus(String email, OrderStatus status);
}
