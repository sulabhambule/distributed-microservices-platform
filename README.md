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