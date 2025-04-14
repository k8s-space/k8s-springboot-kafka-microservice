package de.example.dto;

import java.util.UUID;

public record PaymentRequest(
		UUID orderId,
	    String customerId,
	    Double amount
	    ) {

}
