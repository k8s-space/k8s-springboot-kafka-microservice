package de.example.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    @KafkaListener(topics = "order-events", groupId = "order-service-group")
    public void listen(ConsumerRecord<String, String> record) {
        // Ausgabe der gesamten Nachricht im Konsolen-Log
        System.out.println("Received Kafka message: " + record.value());
    }
}
