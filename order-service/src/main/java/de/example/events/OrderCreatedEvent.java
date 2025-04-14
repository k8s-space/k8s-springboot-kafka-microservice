package de.example.events;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, String customerId, double amount) {

}