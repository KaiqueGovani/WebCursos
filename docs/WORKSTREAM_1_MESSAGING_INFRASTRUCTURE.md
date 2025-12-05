# Workstream 1: Messaging Infrastructure

## Overview

This workstream is responsible for establishing the RabbitMQ messaging infrastructure, including queue topology, message producers, and the foundational consumer framework. The goal is to enable asynchronous event-driven communication when a student completes a course.

**Owner:** Developer 1  
**Dependencies:** None (foundational work)  
**Dependents:** Workstream 2 (Consumer Services)

---

## 1. Requirements (EARS Syntax)

### 1.1 Infrastructure Requirements

| ID | Requirement |
|----|-------------|
| **INF-001** | The system shall include a RabbitMQ message broker accessible via Docker Compose. |
| **INF-002** | The system shall expose RabbitMQ management UI on port 15672 for monitoring and debugging. |
| **INF-003** | The system shall configure RabbitMQ with a persistent volume to prevent message loss during container restarts. |

### 1.2 Queue Topology Requirements

| ID | Requirement |
|----|-------------|
| **QUE-001** | The system shall create an exchange named `webcursos.exchange` of type `topic`. |
| **QUE-002** | The system shall create a queue named `curso.concluido.ai-recommendation` for AI recommendation processing. |
| **QUE-003** | The system shall create a queue named `curso.concluido.email-notification` for email notification processing. |
| **QUE-004** | The system shall bind `curso.concluido.ai-recommendation` queue to `webcursos.exchange` with routing key `curso.concluido.#`. |
| **QUE-005** | The system shall bind `curso.concluido.email-notification` queue to `webcursos.exchange` with routing key `curso.concluido.#`. |
| **QUE-006** | The system shall configure queues as durable to survive broker restarts. |

### 1.3 Producer Requirements

| ID | Requirement |
|----|-------------|
| **PRD-001** | When a student completes a course, the system shall publish a `CursoConcluidoEvent` message to `webcursos.exchange`. |
| **PRD-002** | The system shall include the following data in `CursoConcluidoEvent`: `alunoId`, `alunoNome`, `alunoEmail`, `cursoId`, `cursoNome`, `cursoCodigo`, `notaFinal`, `aprovado`, `dataConclusao`. |
| **PRD-003** | The system shall serialize messages to JSON format using Jackson. |
| **PRD-004** | If message publishing fails, the system shall log the error and not affect the course completion transaction. |

### 1.4 Consumer Framework Requirements

| ID | Requirement |
|----|-------------|
| **CON-001** | The system shall provide a base consumer configuration with retry mechanism (3 attempts). |
| **CON-002** | The system shall configure a Dead Letter Queue (DLQ) named `curso.concluido.dlq` for failed messages. |
| **CON-003** | If a message fails processing after all retries, the system shall route it to the DLQ. |
| **CON-004** | The system shall deserialize incoming JSON messages to `CursoConcluidoEvent` objects. |

### 1.5 Configuration Requirements

| ID | Requirement |
|----|-------------|
| **CFG-001** | The system shall externalize RabbitMQ connection settings via application properties. |
| **CFG-002** | The system shall support environment variable overrides for RabbitMQ host, port, username, and password. |
| **CFG-003** | While running in `dev` profile, the system shall connect to RabbitMQ at `localhost:5672`. |

---

## 2. Design (PRD)

### 2.1 System Context

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           WebCursos Application                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────┐      ┌─────────────────────────────────────────┐  │
│  │ MatriculaService │      │         RabbitMQ Broker                 │  │
│  │                  │      │  ┌─────────────────────────────────┐    │  │
│  │  concluir()      │─────▶│  │   webcursos.exchange (topic)    │    │  │
│  │                  │      │  └───────────────┬─────────────────┘    │  │
│  └──────────────────┘      │                  │                       │  │
│           │                │    routing key: curso.concluido.#        │  │
│           │                │                  │                       │  │
│           ▼                │       ┌──────────┴──────────┐            │  │
│  ┌──────────────────┐      │       ▼                    ▼            │  │
│  │  EventPublisher  │      │  ┌─────────────┐    ┌─────────────┐     │  │
│  │                  │      │  │ ai-recom.   │    │ email-notif │     │  │
│  │ publish(event)   │      │  │   queue     │    │   queue     │     │  │
│  └──────────────────┘      │  └──────┬──────┘    └──────┬──────┘     │  │
│                            │         │                  │            │  │
│                            │         ▼                  ▼            │  │
│                            │   [Workstream 2]    [Workstream 2]      │  │
│                            │   AI Consumer       Email Consumer      │  │
│                            └─────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Component Design

#### 2.2.1 Docker Infrastructure

**File:** `docker-compose.yml`

| Service | Image | Ports | Purpose |
|---------|-------|-------|---------|
| `rabbitmq` | `rabbitmq:3-management` | 5672, 15672 | Message broker with management UI |

**Environment Variables:**
- `RABBITMQ_DEFAULT_USER`: rabbitmq
- `RABBITMQ_DEFAULT_PASS`: rabbitmq

#### 2.2.2 Domain Event DTO

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.event`

```java
public record CursoConcluidoEvent(
    UUID alunoId,
    String alunoNome,
    String alunoEmail,
    UUID cursoId,
    String cursoNome,
    String cursoCodigo,
    Double notaFinal,
    boolean aprovado,
    LocalDateTime dataConclusao
) {}
```

#### 2.2.3 RabbitMQ Configuration

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.config`

**Class:** `RabbitMQConfig`

| Bean | Type | Purpose |
|------|------|---------|
| `webcursosExchange()` | TopicExchange | Main exchange for course events |
| `aiRecommendationQueue()` | Queue | Queue for AI service consumption |
| `emailNotificationQueue()` | Queue | Queue for Email service consumption |
| `deadLetterQueue()` | Queue | DLQ for failed messages |
| `aiBinding()` | Binding | Binds AI queue to exchange |
| `emailBinding()` | Binding | Binds Email queue to exchange |
| `messageConverter()` | Jackson2JsonMessageConverter | JSON serialization |

#### 2.2.4 Event Publisher

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.publisher`

**Class:** `CursoConcluidoEventPublisher`

```java
@Component
@RequiredArgsConstructor
public class CursoConcluidoEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publish(CursoConcluidoEvent event) {
        rabbitTemplate.convertAndSend(
            "webcursos.exchange",
            "curso.concluido",
            event
        );
    }
}
```

#### 2.2.5 Integration Point

**Modified Class:** `MatriculaService`

```java
// After line 103: matricula.concluir(notaFinal);
// Add event publishing logic
if (matricula.estaAprovado()) {
    eventPublisher.publish(buildEvent(matricula));
}
```

### 2.3 Queue Topology

```
                    ┌─────────────────────────────┐
                    │    webcursos.exchange       │
                    │        (topic)              │
                    └─────────────┬───────────────┘
                                  │
                    routing key: curso.concluido.#
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │ curso.concluido │ │ curso.concluido │ │ curso.concluido │
    │ .ai-recommend.  │ │ .email-notif.   │ │     .dlq        │
    │     queue       │ │     queue       │ │   (dead letter) │
    └─────────────────┘ └─────────────────┘ └─────────────────┘
           │                    │
           ▼                    ▼
    [AI Consumer]        [Email Consumer]
    (Workstream 2)       (Workstream 2)
```

### 2.4 Message Schema

**CursoConcluidoEvent (JSON)**

```json
{
  "alunoId": "550e8400-e29b-41d4-a716-446655440000",
  "alunoNome": "João Silva",
  "alunoEmail": "joao.silva@email.com",
  "cursoId": "660e8400-e29b-41d4-a716-446655440001",
  "cursoNome": "Programação Java",
  "cursoCodigo": "JAVA001",
  "notaFinal": 8.5,
  "aprovado": true,
  "dataConclusao": "2025-12-04T14:30:00"
}
```

### 2.5 Configuration Properties

**application-dev.properties**

```properties
# RabbitMQ Configuration
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5672}
spring.rabbitmq.username=${RABBITMQ_USER:rabbitmq}
spring.rabbitmq.password=${RABBITMQ_PASS:rabbitmq}

# Queue names
webcursos.rabbitmq.exchange=webcursos.exchange
webcursos.rabbitmq.queue.ai-recommendation=curso.concluido.ai-recommendation
webcursos.rabbitmq.queue.email-notification=curso.concluido.email-notification
webcursos.rabbitmq.queue.dlq=curso.concluido.dlq
webcursos.rabbitmq.routing-key=curso.concluido
```

---

## 3. Task List

### Phase 1: Infrastructure Setup

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 1.1 | Add `spring-boot-starter-amqp` dependency to `pom.xml` | 15 min | None |
| 1.2 | Update `docker-compose.yml` with RabbitMQ service | 30 min | None |
| 1.3 | Add RabbitMQ properties to `application-dev.properties` | 15 min | None |
| 1.4 | Create `application-docker.properties` for Docker environment | 15 min | 1.3 |

### Phase 2: Queue Topology

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 2.1 | Create `RabbitMQConfig` class with exchanges | 30 min | 1.1 |
| 2.2 | Add queue declarations (AI, Email, DLQ) | 30 min | 2.1 |
| 2.3 | Configure bindings between exchange and queues | 20 min | 2.2 |
| 2.4 | Add Jackson JSON message converter | 15 min | 2.1 |
| 2.5 | Write integration test for queue topology | 45 min | 2.3 |

### Phase 3: Domain Event

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 3.1 | Create `CursoConcluidoEvent` record in messaging package | 20 min | None |
| 3.2 | Write unit tests for event serialization/deserialization | 30 min | 3.1 |

### Phase 4: Event Publisher

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 4.1 | Create `CursoConcluidoEventPublisher` component | 30 min | 2.4, 3.1 |
| 4.2 | Write unit tests for publisher (mock RabbitTemplate) | 30 min | 4.1 |
| 4.3 | Write integration test for message publishing | 45 min | 4.1 |

### Phase 5: Service Integration

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 5.1 | Inject `CursoConcluidoEventPublisher` into `MatriculaService` | 15 min | 4.1 |
| 5.2 | Add event building logic in `MatriculaService` | 30 min | 5.1 |
| 5.3 | Modify `concluir()` to publish event on approval | 30 min | 5.2 |
| 5.4 | Update existing `MatriculaService` tests | 45 min | 5.3 |
| 5.5 | Write integration test for full flow | 60 min | 5.4 |

### Phase 6: Consumer Framework (Skeleton for Workstream 2)

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 6.1 | Create base `@RabbitListener` configuration | 20 min | 2.4 |
| 6.2 | Create skeleton `AiRecommendationListener` (stub) | 20 min | 6.1 |
| 6.3 | Create skeleton `EmailNotificationListener` (stub) | 20 min | 6.1 |
| 6.4 | Document consumer interface contract for Workstream 2 | 30 min | 6.2, 6.3 |

---

## 4. Acceptance Criteria

### AC-1: RabbitMQ Infrastructure
- [ ] RabbitMQ container starts successfully with `docker-compose up`
- [ ] Management UI accessible at `http://localhost:15672`
- [ ] Can login with configured credentials (rabbitmq/rabbitmq)

### AC-2: Queue Topology
- [ ] Exchange `webcursos.exchange` visible in RabbitMQ UI
- [ ] All 3 queues visible: ai-recommendation, email-notification, dlq
- [ ] Bindings correctly configured with routing keys

### AC-3: Event Publishing
- [ ] Completing a course with nota ≥ 7.0 publishes message to exchange
- [ ] Message appears in both AI and Email queues
- [ ] Message contains all required fields in correct JSON format

### AC-4: Consumer Framework
- [ ] Skeleton consumers receive messages from queues
- [ ] Messages are logged for verification
- [ ] Failed messages route to DLQ after retries

---

## 5. Deliverables

| Deliverable | Description |
|-------------|-------------|
| `pom.xml` | Updated with Spring AMQP dependency |
| `docker-compose.yml` | Updated with RabbitMQ service |
| `application-dev.properties` | RabbitMQ configuration added |
| `RabbitMQConfig.java` | Queue topology configuration |
| `CursoConcluidoEvent.java` | Domain event DTO |
| `CursoConcluidoEventPublisher.java` | Event publisher service |
| `MatriculaService.java` | Modified to publish events |
| `AiRecommendationListener.java` | Skeleton consumer (stub) |
| `EmailNotificationListener.java` | Skeleton consumer (stub) |
| Unit & Integration Tests | Full test coverage for messaging |

---

## 6. Handoff to Workstream 2

Upon completion, Workstream 2 will receive:

1. **Working RabbitMQ infrastructure** with queues ready for consumption
2. **Event contract** (`CursoConcluidoEvent`) with all necessary data
3. **Skeleton listeners** ready for implementation
4. **Documentation** on how to consume messages and access student/course data

**Interface Contract:**

```java
// Workstream 2 must implement the processing logic in:

@RabbitListener(queues = "${webcursos.rabbitmq.queue.ai-recommendation}")
public void processAiRecommendation(CursoConcluidoEvent event) {
    // TODO: Implement AI processing (Workstream 2)
}

@RabbitListener(queues = "${webcursos.rabbitmq.queue.email-notification}")
public void processEmailNotification(CursoConcluidoEvent event) {
    // TODO: Implement email sending (Workstream 2)
}
```

