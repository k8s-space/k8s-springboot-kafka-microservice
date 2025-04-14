package de.example.events;

import java.time.Instant;
import java.util.UUID;

public sealed interface PaymentResultEvent permits PaymentApprovedEvent, PaymentFailedEvent {

	UUID orderId();
	Instant timestamp();
}