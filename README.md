# Microservices Architecture Project
# AWS Branch
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





## Key Dependencies

### API Gateway Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

### Authentication Dependencies
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### gRPC Dependencies
```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.69.0</version>
</dependency>
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-spring-boot-starter</artifactId>
    <version>3.1.0.RELEASE</version>
</dependency>
```

### Kafka Dependencies
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.3.0</version>
</dependency>
```

### Protocol Buffers
```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>4.29.1</version>
</dependency>
```

## Security Features

### JWT Token Structure
- **Algorithm:** HS256 (HMAC with SHA-256)
- **Secret Key:** Base64-encoded 256-bit key
- **Expiration:** 10 hours from issuance
- **Claims:** Subject (email), Role, Issued At, Expiration

### API Gateway Security
- Custom JWT validation filter for protected routes
- WebClient-based token validation against Auth Service
- Automatic 401 Unauthorized response for invalid tokens
- Path-based security (auth endpoints are public, patient endpoints are protected)

### Password Security
- BCrypt hashing with random salt
- Minimum 8 character password requirement
- Secure password comparison

## Design Patterns & Best Practices

- **API Gateway Pattern** - Single entry point for all client requests
- **Authentication/Authorization** - Centralized JWT-based security
- **Repository Pattern** - Data access abstraction via Spring Data JPA
- **DTO Pattern** - Separation of internal models from API contracts
- **Mapper Pattern** - Manual DTO-Entity conversion
- **Service Layer** - Business logic encapsulation
- **Global Exception Handling** - Centralized error handling with `@ControllerAdvice`
- **Validation Groups** - Context-specific validation rules
- **Synchronous RPC** - gRPC for inter-service communication
- **Event-Driven Architecture** - Kafka for asynchronous messaging
- **Multi-Stage Docker Builds** - Optimized container images
- **Reactive Programming** - Spring WebFlux in API Gateway

## Request Flow Example

### Creating a Patient with Full Flow

1. **Client authenticates:**
   ```
   POST /auth/login → Auth Service
   Returns: JWT Token
   ```

2. **Client creates patient:**
   ```
   POST /api/patients (with JWT) → API Gateway
   → Validates JWT with Auth Service
   → Routes to Patient Service
   ```

3. **Patient Service processes:**
   ```
   - Validates request data
   - Checks email uniqueness
   - Saves patient to PostgreSQL
   - Calls Billing Service via gRPC
   - Publishes event to Kafka
   - Returns patient data
   ```

4. **Billing Service:**
   ```
   - Receives gRPC call
   - Creates billing account
   - Returns account details
   ```

5. **Analytics Service:**
   ```
   - Consumes Kafka event
   - Logs patient creation
   - Performs analytics processing
   ```

## Service Ports Summary

| Service | HTTP Port | Additional Ports | Purpose |
|---------|-----------|------------------|---------|
| API Gateway | 4004 | - | Entry point for all client requests |
| Auth Service | 4005 | - | JWT authentication and validation |
| Patient Service | 4000 | - | Patient CRUD operations |
| Billing Service | 4001 | 9001 (gRPC) | Billing account management |
| Analytics Service | 4002 | - | Event processing and analytics |
| PostgreSQL (Patient) | 5000 | - | Patient service database |
| PostgreSQL (Auth) | 5001 | - | Auth service database |
| Kafka | 9094 | 9092 (internal) | Event streaming |

## Monitoring & Logging

All services use SLF4J with Logback for logging:
- **Patient Service:** Logs REST requests, gRPC calls, and Kafka events
- **Billing Service:** Logs incoming gRPC requests
- **Analytics Service:** Logs consumed Kafka events
- **Auth Service:** Logs authentication attempts and token validation
- **API Gateway:** Logs routing decisions and validation failures

## Troubleshooting

### Common Issues

**API Gateway 401 Unauthorized:**
- Ensure JWT token is valid and not expired
- Check Authorization header format: `Bearer <token>`
- Verify Auth Service is running and accessible
- Check JWT_SECRET environment variable matches across services

**Kafka Connection Refused:**
- Ensure Kafka is running and accessible
- Check `SPRING_KAFKA_BOOTSTRAP_SERVERS` environment variable
- Verify network connectivity between services
- Wait for Kafka to fully start (can take 30-60 seconds)

**gRPC Connection Failed:**
- Ensure billing-service is running on port 9001
- Check `BILLING_SERVICE_ADDRESS` and `BILLING_SERVICE_GRPC_PORT` configuration
- Verify network connectivity between services

**Database Connection Failed:**
- Ensure PostgreSQL is running
- Verify database credentials
- Check database URL configuration
- Wait for PostgreSQL to fully start

**Proto Compilation Errors:**
- Run `mvn clean compile` to regenerate proto classes
- Ensure protobuf-maven-plugin is properly configured
- Check that proto files are in `src/main/proto/` directory

**Auth Service JWT Errors:**
- Verify JWT_SECRET is properly base64-encoded
- Ensure secret is at least 256 bits (32 characters when decoded)
- Check that the same secret is used across all services

## Environment Variables Reference

### API Gateway
- `AUTH_SERVICE_URL` - URL of the authentication service (default: http://localhost:4005)

### Auth Service
- `JWT_SECRET` - Base64-encoded secret key for JWT signing (required)
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

### Patient Service
- `BILLING_SERVICE_ADDRESS` - Billing service hostname (default: localhost)
- `BILLING_SERVICE_GRPC_PORT` - Billing service gRPC port (default: 9001)
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka broker address (default: localhost:9092)
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

### Analytics Service
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka broker address (default: localhost:9092)

## Sample Data

### Pre-loaded Users (Auth Service)
- **Email:** testuser@test.com
- **Password:** password123
- **Role:** ADMIN

### Pre-loaded Patients (Patient Service)
15 sample patients with UUIDs ranging from `123e4567-e89b-12d3-a456-426614174000` to `223e4567-e89b-12d3-a456-426614174014`


## License

This is a demonstration project showcasing microservices architecture patterns with Spring Boot, Spring Cloud Gateway, gRPC, Apache Kafka, and JWT authentication.