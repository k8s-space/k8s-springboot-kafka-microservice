package de.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.example.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	List<Order> findByStatus(String status);
	Optional<String> getStatusById(UUID id); // Für Statusabfragen
}
