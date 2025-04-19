# 🧠 k8s-springboot-kafka-microservice

A learning project to implement an event-driven microservice architecture with Spring Boot, Apache Kafka and Kubernetes. 
The aim is to understand modern architectural principles such as domain-driven design (DDD), event sourcing and CQRS in a practical way and to implement them step by step.

## 🔍 Overview

The project currently consists of two central microservices that are provided in a Maven project.

- **OrderService**  
  Manages orders, stores them in PostgreSQL and sends events to Kafka.

- **PaymentService**  
  Responds to order events, simulates payment processing and sends back a confirmation event.

### 📌 Architecture (simplified)

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

## ⚙️ Technologies

- **Spring Boot** (Microservices, Kafka integration)
- **Apache Kafka** (asynchronous messaging)
- **PostgreSQL** (persistent datastorage)
- **Kubernetes / Minikube** (Container orchestration)
- **Docker** (Containerization)
- **ConfigMaps & Secrets** (Configuration)
- **PersistentVolumeClaims (PVCs)** (Data persistence)

## 🚀 Setup & Installation

### Requirements

- [Docker](https://www.docker.com/)
- [Minikube](https://minikube.sigs.k8s.io/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Java 17 / Maven

### 1. start Minikube

```bash
minikube start
eval $(minikube docker-env)
```

### 2. locale build Docker-Image

```bash
cd orderservice
docker build -t orderservice .
```

### 3. apply Kubernetes ressources

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

### 4. Testing orderService (ala port forwarding)

```bash
kubectl port-forward deployment/orderservice 8080:8080
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"orderId":"d3fdf2b2-bb67-4565-933f-3c8c1cf4cc3b","customerId":"123", "amount": 125}'
```

## 🧪 example process

1. Order is sent to the OrderService
2. OrderService saves the order and sends an `OrderCreated` event to Kafka
3. PaymentService processes the event and sends back a `PaymentCompleted` event
4. OrderService updates the order status


## 📚 Blog post

👉 The blog post on the migration process from Docker Compose to Kubernetes is available at [Migration of an event-driven architecture to Kubernetes](https://medium.com/@devripper133127/migration-einer-event-driven-architektur-nach-kubernetes-c488dad461e9?source=friends_link&sk=e85acf6cc0033ec36f3cf310e84e2f63).

## 📂 Directory structure

```plaintext
├── orderservice/           # Spring Boot order service and payment service
├── k8s/
│   ├── deployments/        # order service + Kafka + ZooKeeper YAMLs
│   ├── services/           # PostgreSQL + Kafka + ZooKeeper YAMLs as service manifest
│   ├── stateful-set/       # PostgreSQL StatefulSet
│   ├── volumes/            # PVCs und PVs for ZooKeeper + PostgreSQL
│   └── namespace.yaml      # manifest for namespace
└── README.md
```

## 🧑‍💻 Author

> This project is part of my learning path around **microservices, DDD, event-driven architecture** and **cloud-native development**. Feedback, questions or pull requests are always welcome!

## 💬 Feedback & Fragen  
- **Diskutiere mit** via [GitHub Issues](https://github.com/k8s-space/k8s-springboot-kafka-microservice/issues).  
- **Verbesserungsvorschlag?** Ich freue mich über PRs!

## 🛠️ Mitmachen  
1. Fork das Repo  
2. Erstelle einen Branch (`git checkout -b feature/xyz`)  
3. Commit & Push  
4. Öffne einen PR  
