package de.example.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import de.example.events.OrderCreatedEvent;
import de.example.events.PaymentApprovedEvent;
import de.example.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentMock {
	
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

    @KafkaListener(topics = "order-events", groupId = "order-service-group")
    public void mockPaymentProcess(OrderCreatedEvent event) {
    	System.out.println("Received OrderCreatedEvent with customerId: " + event.customerId());
    	
        // Simuliere 30% Fehlerrate
        if (Math.random() < 0.3) {
            kafkaTemplate.send("payment-events", 
                new PaymentFailedEvent(event.orderId(), "Mocked payment failure", Instant.now()));
        } else {
            kafkaTemplate.send("payment-events", 
                new PaymentApprovedEvent(event.orderId(), Instant.now()));
        }
    }
}
