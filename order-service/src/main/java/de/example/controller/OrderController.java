package de.example.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.example.dto.PaymentRequest;
import de.example.model.Order;
import de.example.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody PaymentRequest request) {
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setAmount(request.amount());
        orderService.createOrder(order);
        return ResponseEntity.accepted().body(order);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getOrderStatus(@PathVariable UUID id) {
        return orderService.getOrderStatus(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
