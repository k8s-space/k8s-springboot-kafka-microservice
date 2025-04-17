# 🧠 k8s-springboot-kafka-microservice

Ein Lernprojekt zur Umsetzung einer **eventgesteuerten Microservice-Architektur** mit **Spring Boot**, **Apache Kafka** und **Kubernetes**. Ziel ist es, moderne Architekturprinzipien wie **Domain-driven Design (DDD)**, **Event Sourcing** und **CQRS** praxisnah zu verstehen und Schritt für Schritt umzusetzen.

## 🔍 Überblick

Das Projekt besteht aktuell aus zwei zentralen Microservices, die in einem Maven Projekt bereitgestellt werden:

- **OrderService**  
  Verwaltet Bestellungen, speichert diese in PostgreSQL und sendet Events an Kafka.

- **PaymentService**  
  Reagiert auf Order-Events, simuliert eine Zahlungsabwicklung und sendet ein Bestätigungs-Event zurück.

### 📌 Architektur (vereinfacht)

```plaintext
+-------------+       Kafka Event:        +----------------+
|             |         OrderCreated      |                |
|             |   ------------------->    |                |
| OrderService|                           | PaymentService |
|             |    <------------------    |                |
+------+------+/     Kafka Event:         +--------+-------+
       |               PaymentApproved             |
       |                                           |
       v                                           v
+---------------+                         +----------------+
| PostgreSQL DB |                         |    Kafka       |
+---------------+                         +----------------+
```

## ⚙️ Technologien

- **Spring Boot** (Microservices, Kafka Integration)
- **Apache Kafka** (asynchrones Messaging)
- **PostgreSQL** (persistente Datenspeicherung)
- **Kubernetes / Minikube** (Container-Orchestrierung)
- **Docker** (Containerisierung)
- **ConfigMaps & Secrets** (Konfiguration)
- **PersistentVolumeClaims (PVCs)** (Datenpersistenz)

## 🚀 Setup & Installation

### Voraussetzungen

- [Docker](https://www.docker.com/)
- [Minikube](https://minikube.sigs.k8s.io/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Java 17 / Maven

### 1. Minikube starten

```bash
minikube start
eval $(minikube docker-env)
```

### 2. Docker-Images lokal bauen

```bash
cd orderservice
docker build -t orderservice .
```

### 3. Kubernetes-Ressourcen anwenden

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f -n ordersystem k8s/volumes/zookeeper-pv.yaml
kubectl apply -f -n ordersystem k8s/volumes/zookeeper-pvc.yaml
kubectl apply -f -n ordersystem k8s/stateful-set/postgres-statefulset.yaml
kubectl apply -f -n ordersystem k8s/deployments/zookeeper-deployment.yaml
kubectl apply -f -n ordersystem k8s/deployments/kafka-deployment.yaml
kubectl apply -f -n ordersystem k8s/deployments/order-service-deployment.yaml
kubectl apply -f -n ordersystem k8s/services/zookeeper-service.yaml
kubectl apply -f -n ordersystem k8s/services/kafka-service.yaml
kubectl apply -f -n ordersystem k8s/services/postgres-service.yaml
```

### 4. OrderService testen (via Port-Forwarding)

```bash
kubectl port-forward deployment/orderservice 8080:8080
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"orderId":"d3fdf2b2-bb67-4565-933f-3c8c1cf4cc3b","customerId":"123", "amount": 125}'
```

## 🧪 Beispiel-Ablauf

1. Bestellung wird an den OrderService gesendet
2. OrderService speichert die Bestellung und sendet ein `OrderCreated` Event an Kafka
3. PaymentService verarbeitet das Event und sendet ein `PaymentCompleted` Event zurück
4. OrderService aktualisiert den Bestellstatus


## 📚 Blogbeitrag

👉 Den Blogbeitrag zum Migrationsprozess von Docker Compose zu Kubernetes ist unter [Migration einer Event-Driven-Architektur nach Kubernetes](https://medium.com/@devripper133127/migration-einer-event-driven-architektur-nach-kubernetes-c488dad461e9?source=friends_link&sk=e85acf6cc0033ec36f3cf310e84e2f63) beschrieben.

## 📂 Verzeichnisstruktur

```plaintext
├── orderservice/           # Spring Boot Order-Service und Payment-Service
├── k8s/
│   ├── deployments/        # order-service + Kafka + ZooKeeper YAMLs
│   ├── services/           # PostgreSQL + Kafka + ZooKeeper YAMLs as service manifest
│   ├── stateful-set/       # PostgreSQL StatefulSet
│   ├── volumes/            # PVCs und PVs for ZooKeeper + PostgreSQL
│   └── namespace.yaml      # manifest for namespace
└── README.md
```

## 🧑‍💻 Autor

> Dieses Projekt ist Teil meines Lernweges rund um **Microservices, DDD, Event-getriebene Architektur** und **Cloud-native Entwicklung**. Feedback, Fragen oder Pull Requests sind jederzeit willkommen!