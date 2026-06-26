# Microservices Architecture Project

A comprehensive Spring Boot microservices implementation demonstrating modern architectural patterns including API Gateway, JWT authentication, service-to-service communication (gRPC), event-driven architecture (Kafka), and REST APIs.

## Architecture Overview

This project consists of five microservices that communicate using REST, gRPC, and Kafka through an API Gateway:

- **API Gateway** - Entry point routing requests to backend services with JWT validation
- **Auth Service** - Authentication service providing JWT token generation and validation
- **Patient Service** - Primary service exposing REST API endpoints, gRPC client, and Kafka producer
- **Billing Service** - Secondary service exposing gRPC endpoints for billing account creation
- **Analytics Service** - Event-driven service consuming patient events via Kafka

```
                              ┌─────────────────────┐
                              │    API Gateway      │
                              │    Port 4004        │
                              └──────────┬──────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
                    ▼                    ▼                    │
        ┌─────────────────────┐  ┌─────────────────────┐      │
        │   Auth Service      │  │  Patient Service    │      │
        │   Port 4005         │  │  Port 4000          │      │
        │  (JWT Auth)         │  │  (REST API)         │      │
        └─────────────────────┘  └──────────┬──────────┘      │
                                            │                 │
                                   gRPC     │  Kafka          │
                                            │  Events         │
                    ┌───────────────────────┼────────┐        │
                    ▼                       ▼        ▼        │
        ┌─────────────────────┐    ┌──────────────────────┐   │
        │  Billing Service    │    │ Analytics Service    │   │
        │  (gRPC Server)      │    │ (Kafka Consumer)     │   │
        │  Port 4001          │    │ Port 4002            │   │
        │  gRPC Port 9001     │    └──────────────────────┘   │
        └─────────────────────┘             ▲                 │
                                            │                 │
                                       ┌────────┐             │
                                       │ Kafka  │  ◄──────────┘
                                       │ Broker │
                                       └────────┘
```

## Technology Stack

### Backend Framework
- **Spring Boot 3.4.x** - Main application framework
- **Spring Cloud Gateway** - API Gateway implementation
- **Spring Security** - Authentication and authorization
- **Java 21** - Programming language
- **Maven** - Dependency management and build tool

### Communication Protocols
- **REST API** - HTTP-based API for client communication
- **gRPC** - High-performance RPC framework for inter-service communication
- **Apache Kafka** - Distributed event streaming platform for asynchronous messaging
- **Protocol Buffers (Proto3)** - Interface definition and serialization

### Security
- **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- **BCrypt** - Password hashing
- **Spring Security** - Security framework

### Data Persistence
- **Spring Data JPA** - Data access abstraction
- **PostgreSQL** - Production database (configurable)
- **H2 Database** - In-memory database for testing (optional)

### Validation & Documentation
- **Jakarta Validation** - Request validation with custom validation groups
- **SpringDoc OpenAPI 3** (Swagger) - API documentation and testing UI

### Infrastructure
- **Docker** - Containerization with multi-stage builds
- **Docker Compose** - Multi-container orchestration (optional)
- **Maven Wrapper** - Ensures consistent Maven version across environments


## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+** (or use included Maven wrapper)
- **Docker** (optional, for containerized deployment)
- **PostgreSQL** (for patient-service and auth-service databases)
- **Apache Kafka** (for event streaming)



### Running with Docker

#### 1. Create Docker Network

```bash
docker network create internal
```

#### 2. Build Docker Images

```bash
# Build all service images
for service in auth-service patient-service billing-service analytics-service api-gateway; do
  cd $service
  docker build -t $service:latest .
  cd ..
done
```

#### 3. Run Containers

**PostgreSQL Databases:**
```bash
# Patient Service Database
docker run -d \
  --name patient-service-db \
  --network internal \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=patientdb \
  -p 5000:5432 \
  postgres:latest

# Auth Service Database
docker run -d \
  --name auth-service-db \
  --network internal \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=authdb \
  -p 5001:5432 \
  postgres:latest
```

**Kafka:**
```bash
docker run -d \
  --name kafka \
  --network internal \
  -p 9092:9092 \
  -p 9094:9094 \
  -e KAFKA_CFG_NODE_ID=0 \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094 \
  -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  bitnamilegacy/kafka:latest
```

**Auth Service:**
```bash
docker run -d \
  --name auth-service \
  --network internal \
  -p 4005:4005 \
  -e JWT_SECRET=5ZveJML9b0iaZQ2NT/sDUdUCcWaRhZ74ck1Rc6kHLh4= \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://auth-service-db:5432/db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e SPRING_SQL_INIT_MODE=always \
  auth-service:latest
```

**Billing Service:**
```bash
docker run -d \
  --name billing-service \
  --network internal \
  -p 4001:4001 \
  -p 9001:9001 \
  billing-service:latest
```

**Analytics Service:**
```bash
docker run -d \
  --name analytics-service \
  --network internal \
  -p 4002:4002 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  analytics-service:latest
```

**Patient Service:**
```bash
docker run -d \
  --name patient-service \
  --network internal \
  -p 4000:4000 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://patient-service-db:5432/db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e SPRING_SQL_INIT_MODE=always \
  -e BILLING_SERVICE_ADDRESS=billing-service \
  -e BILLING_SERVICE_GRPC_PORT=9001 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  patient-service:latest
```

**API Gateway:**
```bash
docker run -d \
  --name api-gateway \
  --network internal \
  -p 4004:4004 \
  -e AUTH_SERVICE_URL=http://auth-service:4005 \
  api-gateway:latest
```