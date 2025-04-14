package de.example.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import de.example.events.OrderCreatedEvent;
import de.example.events.PaymentApprovedEvent;
import de.example.events.PaymentFailedEvent;
import de.example.events.PaymentResultEvent;
import de.example.model.Order;
import de.example.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@Transactional
	public void createOrder(Order order) {
		order.setStatus("PENDING");
		orderRepository.save(order);
		
		// Start Saga
		OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getCustomerId(), order.getAmount());		
		/*Message<OrderCreatedEvent> message = MessageBuilder
		        .withPayload(event)
		        .setHeader("__TypeId__", "orderCreatedEvent")
		        .build();*/
		
		kafkaTemplate.send("order-events", event);
	}
	
	@KafkaListener(topics = "payment-events", groupId = "order-service-group")
	public void handlePaymentEvent(PaymentResultEvent event) {
		System.out.println("Received PaymentResultEvent with orderId: " + event.orderId());
		
		if (event instanceof PaymentApprovedEvent approved) {
            updateOrderStatus(approved.orderId(), "PAID");
        } else if (event instanceof PaymentFailedEvent failed) {
            updateOrderStatus(failed.orderId(), "CANCELLED");
        }
		
		/*Mono.just(event).flatMap(e -> {
			if(e instanceof PaymentApprovedEvent approved) {
				return processApprovedPayment(approved);
			} else if(e instanceof PaymentFailedEvent failed) {
				return triggerCompensation(failed);
			} else {
				return Mono.error(new IllegalArgumentException("Unsupported event type"));
			}
		}).subscribe();*/
		
	}
	
	private void updateOrderStatus(UUID orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        orderOpt.ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
	
	public Optional<String> getOrderStatus(UUID uuid) {
		return orderRepository.getStatusById(uuid);
	}

	/*private Mono<Void> processApprovedPayment(PaymentApprovedEvent event) {
		return orderRepository.findById(event.orderId())
		        .flatMap(order -> {
		            order.setStatus("PAID");
		            return orderRepository.save(order)
		                .then(kafkaTemplate.send("inventory-commands", 
		                		reserveInventory(event)));
		        });
	}
	
	private Mono<Void> triggerCompensation(PaymentFailedEvent event) {
		return orderRepository.findById(event.orderId())
		        .flatMap(order -> {
		            order.setStatus("CANCELLED");
		            return orderRepository.save(order)
		                .then(kafkaTemplate.send("compensation-events", 
		                    new OrderCancelledEvent(event.orderId(), event.reason())));
		        });
	}
	
	private Mono<Void> reserveInventory(Order order) {
	    ReserveInventoryCommand command = new ReserveInventoryCommand(
	        order.getId(),
	        order.getItems(),
	        Instant.now()
	    );

	    return kafkaTemplate.send("inventory-commands", command)
	           .doOnSuccess(result -> log.info("Inventory command sent: {}", result))
	           .doOnError(ex -> log.error("Failed to send command", ex))
	           .then();
	}*/
}
