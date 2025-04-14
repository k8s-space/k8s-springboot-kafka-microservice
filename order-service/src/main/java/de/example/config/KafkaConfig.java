package de.example.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper.TypePrecedence;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import de.example.events.OrderCreatedEvent;
import de.example.events.PaymentApprovedEvent;
import de.example.events.PaymentFailedEvent;
import de.example.events.PaymentResultEvent;

@Configuration
@EnableKafka
public class KafkaConfig {
	
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");  // Vertrauenswürdige Pakete angeben
		config.put(JsonSerializer.TYPE_MAPPINGS, 
				"orderCreatedEvent:de.example.events.OrderCreatedEvent," +
		        "paymentResultEvent:de.example.events.PaymentResultEvent," +
		        "approved:de.example.events.PaymentApprovedEvent," +
		        "failed:de.example.events.PaymentFailedEvent");
		return new DefaultKafkaProducerFactory<String, Object>(config);
	}
	
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	
	/*@Bean
	public Map<String, Object> consumerFactory() {
	    Map<String, Object> config = new HashMap<>();
	    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
	    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    //config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
	    //config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");  // Vertrauenswürdige Pakete angeben

	    JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(Object.class);	    
	    jsonDeserializer.addTrustedPackages("*");  // Vertrauenswürdige Pakete angeben
	    Map<String, Class<?>> eventTypes = new HashMap<>();
	    eventTypes.put("orderCreatedEvent", OrderCreatedEvent.class);
	    eventTypes.put("paymentResultEvent", PaymentResultEvent.class);
	    eventTypes.put("approved", PaymentApprovedEvent.class);
	    eventTypes.put("failed", PaymentFailedEvent.class);
	    
	    jsonDeserializer.configure(eventTypes, false);
	    
	    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer);

	    return config;	   
	}*/

	@Bean
	public ConsumerFactory<String, Object> multiTypeConsumerFactory() {
	    HashMap<String, Object> props = new HashMap<>();
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
	    return new DefaultKafkaConsumerFactory<>(props);
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> consumerFactory() {		
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new 
				ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(multiTypeConsumerFactory());
		factory.setRecordMessageConverter(multiTypeConverter());
		return factory;
	}
	
	@Bean
	public RecordMessageConverter multiTypeConverter() {
	    JsonMessageConverter converter = new JsonMessageConverter();
	    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
	    typeMapper.setTypePrecedence(TypePrecedence.TYPE_ID);
	    typeMapper.addTrustedPackages("*");
	    Map<String, Class<?>> mappings = new HashMap<>();
	    mappings.put("orderCreatedEvent", OrderCreatedEvent.class);
	    mappings.put("paymentResultEvent", PaymentResultEvent.class);
	    mappings.put("approved", PaymentApprovedEvent.class);
	    mappings.put("failed", PaymentFailedEvent.class);
	    typeMapper.setIdClassMapping(mappings);
	    converter.setTypeMapper(typeMapper);
	    return converter;
	}
}
