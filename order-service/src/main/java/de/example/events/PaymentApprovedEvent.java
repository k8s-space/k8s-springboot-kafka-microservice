package de.example.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentApprovedEvent(UUID orderId, Instant timestamp) implements PaymentResultEvent {

}