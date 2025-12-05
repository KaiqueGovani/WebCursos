# Workstream 2: Consumer Services (AI & Email)

## Overview

This workstream is responsible for implementing the consumer services that process course completion events. It includes two microservice-like components: the **AI Recommendation Service** (using LangChain4j + Gemini) and the **Email Notification Service** (using MailHog sandbox). These services consume messages from RabbitMQ queues established by Workstream 1.

**Owner:** Developer 2  
**Dependencies:** Workstream 1 (Messaging Infrastructure)  
**Dependents:** None (end of pipeline)

---

## 1. Requirements (EARS Syntax)

### 1.1 AI Recommendation Service Requirements

| ID | Requirement |
|----|-------------|
| **AI-001** | When a `CursoConcluidoEvent` message is received on `curso.concluido.ai-recommendation` queue, the system shall process it for AI recommendation generation. |
| **AI-002** | The system shall retrieve the student's last 3 completed courses from the database. |
| **AI-003** | The system shall retrieve all available courses that the student has not started or completed. |
| **AI-004** | The system shall call Gemini AI API to generate a personalized recommendation message. |
| **AI-005** | The system shall include in the AI prompt: student name, completed course name, last 3 courses with grades, and list of available courses. |
| **AI-006** | The system shall generate a message that thanks the student and suggests ONE course from available options. |
| **AI-007** | If the student has no available courses, the system shall generate a congratulatory message without recommendation. |
| **AI-008** | The system shall externalize the Gemini API key via environment variable `GEMINI_API_KEY`. |
| **AI-009** | If Gemini API call fails, the system shall log the error and use a fallback template message. |
| **AI-010** | The system shall publish the generated message to `curso.concluido.email-notification` queue for email delivery. |

### 1.2 Email Notification Service Requirements

| ID | Requirement |
|----|-------------|
| **EML-001** | When a message is received on `curso.concluido.email-notification` queue, the system shall send an email to the student. |
| **EML-002** | The system shall use MailHog SMTP server (port 1025) for email delivery in development. |
| **EML-003** | The system shall format emails with subject: "Parabéns pela conclusão do curso {cursoNome}!" |
| **EML-004** | The system shall include the AI-generated message as the email body. |
| **EML-005** | The system shall set the sender email as `noreply@webcursos.com`. |
| **EML-006** | If email sending fails, the system shall retry up to 3 times before routing to DLQ. |
| **EML-007** | The system shall log successful email deliveries with recipient and subject. |

### 1.3 Infrastructure Requirements

| ID | Requirement |
|----|-------------|
| **INF-001** | The system shall include MailHog container in Docker Compose for email sandbox. |
| **INF-002** | The system shall expose MailHog web UI on port 8025 for email preview. |
| **INF-003** | The system shall add LangChain4j Gemini dependency to the project. |
| **INF-004** | The system shall add Spring Boot Mail dependency to the project. |

### 1.4 Data Access Requirements

| ID | Requirement |
|----|-------------|
| **DAT-001** | The system shall provide a repository method to fetch the last N completed courses for a student, ordered by completion date descending. |
| **DAT-002** | The system shall provide a repository method to fetch courses not enrolled by a student. |

---

## 2. Design (PRD)

### 2.1 System Context

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         WORKSTREAM 2 - Consumer Services                      │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│   From Workstream 1                                                           │
│         │                                                                     │
│         ▼                                                                     │
│  ┌─────────────────────┐                                                      │
│  │ curso.concluido.    │                                                      │
│  │ ai-recommendation   │                                                      │
│  │      queue          │                                                      │
│  └──────────┬──────────┘                                                      │
│             │                                                                 │
│             ▼                                                                 │
│  ┌─────────────────────────────────────────────────────────┐                  │
│  │              AI Recommendation Listener                  │                  │
│  │  ┌─────────────────────────────────────────────────┐    │                  │
│  │  │ 1. Fetch student's last 3 completed courses     │    │                  │
│  │  │ 2. Fetch available (unstarted) courses          │    │                  │
│  │  │ 3. Call Gemini AI with context                  │    │                  │
│  │  │ 4. Publish to email queue                       │    │                  │
│  │  └─────────────────────────────────────────────────┘    │                  │
│  └──────────────────────────┬──────────────────────────────┘                  │
│                             │                                                 │
│                             ▼                                                 │
│                  ┌─────────────────────┐                                      │
│                  │ curso.concluido.    │                                      │
│                  │ email-notification  │                                      │
│                  │      queue          │                                      │
│                  └──────────┬──────────┘                                      │
│                             │                                                 │
│                             ▼                                                 │
│  ┌─────────────────────────────────────────────────────────┐                  │
│  │              Email Notification Listener                 │                  │
│  │  ┌─────────────────────────────────────────────────┐    │                  │
│  │  │ 1. Build email from event data                  │    │                  │
│  │  │ 2. Send via SMTP (MailHog)                      │    │                  │
│  │  │ 3. Log delivery status                          │    │                  │
│  │  └─────────────────────────────────────────────────┘    │                  │
│  └──────────────────────────┬──────────────────────────────┘                  │
│                             │                                                 │
│                             ▼                                                 │
│                    ┌─────────────────┐                                        │
│                    │    MailHog      │                                        │
│                    │  SMTP: 1025     │                                        │
│                    │  UI: 8025       │                                        │
│                    └─────────────────┘                                        │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Component Design

#### 2.2.1 Docker Infrastructure

**File:** `docker-compose.yml` (additions)

| Service | Image | Ports | Purpose |
|---------|-------|-------|---------|
| `mailhog` | `mailhog/mailhog` | 1025, 8025 | Email sandbox (SMTP + Web UI) |

#### 2.2.2 New Dependencies (pom.xml)

```xml
<!-- LangChain4j with Gemini -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-google-ai-gemini</artifactId>
    <version>0.36.2</version>
</dependency>

<!-- Spring Boot Mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

#### 2.2.3 Message DTOs

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.event`

```java
// Input from Workstream 1
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

// Message between AI and Email services
public record EmailNotificationEvent(
    String destinatario,
    String assunto,
    String corpo,
    UUID alunoId,
    UUID cursoId
) {}
```

#### 2.2.4 AI Recommendation Service

**Package:** `com.morangosdoamor.WebCursos.application.service`

**Class:** `AiRecommendationService`

```java
@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    
    private final ChatLanguageModel geminiModel;
    
    public String generateRecommendation(
            String alunoNome,
            String cursoConcluidoNome,
            List<CursoCompletoDTO> ultimosCursos,
            List<CursoDisponivelDTO> cursosDisponiveis
    ) {
        String prompt = buildPrompt(alunoNome, cursoConcluidoNome, 
                                    ultimosCursos, cursosDisponiveis);
        return geminiModel.generate(prompt);
    }
    
    private String buildPrompt(...) {
        // Structured prompt for Gemini
    }
}
```

**AI Prompt Template:**

```
Você é um assistente educacional da plataforma WebCursos.

O aluno {alunoNome} acabou de concluir o curso "{cursoConcluidoNome}".

Histórico dos últimos cursos concluídos:
{foreach ultimosCursos}
- {cursoNome} (Nota: {nota})
{/foreach}

Cursos disponíveis para matrícula:
{foreach cursosDisponiveis}
- {cursoNome}: {descricao} ({cargaHoraria}h)
{/foreach}

Escreva uma mensagem curta (máximo 3 parágrafos) em português brasileiro que:
1. Parabenize o aluno pela conclusão
2. Analise brevemente seu histórico de cursos
3. Sugira UM curso da lista de disponíveis que combine com seu perfil

Tom: Amigável, encorajador e profissional.
```

#### 2.2.5 Gemini Configuration

**Package:** `com.morangosdoamor.WebCursos.infrastructure.ai`

**Class:** `GeminiConfig`

```java
@Configuration
public class GeminiConfig {
    
    @Value("${gemini.api-key}")
    private String apiKey;
    
    @Bean
    public ChatLanguageModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
            .apiKey(apiKey)
            .modelName("gemini-1.5-flash")
            .temperature(0.7)
            .build();
    }
}
```

#### 2.2.6 Email Service

**Package:** `com.morangosdoamor.WebCursos.application.service`

**Class:** `EmailService`

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@webcursos.com}")
    private String fromAddress;
    
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
        log.info("Email sent to {} with subject: {}", to, subject);
    }
}
```

#### 2.2.7 AI Recommendation Listener

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.listener`

**Class:** `AiRecommendationListener`

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationListener {
    
    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final AiRecommendationService aiService;
    private final RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.ai-recommendation}")
    public void processAiRecommendation(CursoConcluidoEvent event) {
        log.info("Processing AI recommendation for aluno: {}", event.alunoId());
        
        // 1. Fetch last 3 completed courses
        List<Matricula> lastCourses = matriculaRepository
            .findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                event.alunoId(), MatriculaStatus.CONCLUIDO);
        
        // 2. Fetch available courses
        List<Curso> availableCourses = cursoRepository
            .findCursosNotEnrolledByAluno(event.alunoId());
        
        // 3. Generate AI recommendation
        String recommendation = aiService.generateRecommendation(
            event.alunoNome(),
            event.cursoNome(),
            mapToCursoCompletoDTO(lastCourses),
            mapToCursoDisponivelDTO(availableCourses)
        );
        
        // 4. Publish to email queue
        EmailNotificationEvent emailEvent = new EmailNotificationEvent(
            event.alunoEmail(),
            "Parabéns pela conclusão do curso " + event.cursoNome() + "!",
            recommendation,
            event.alunoId(),
            event.cursoId()
        );
        
        rabbitTemplate.convertAndSend(
            "webcursos.exchange",
            "curso.concluido.email",
            emailEvent
        );
    }
}
```

#### 2.2.8 Email Notification Listener

**Package:** `com.morangosdoamor.WebCursos.infrastructure.messaging.listener`

**Class:** `EmailNotificationListener`

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationListener {
    
    private final EmailService emailService;
    
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.email-notification}")
    public void processEmailNotification(EmailNotificationEvent event) {
        log.info("Sending email to: {}", event.destinatario());
        
        emailService.sendEmail(
            event.destinatario(),
            event.assunto(),
            event.corpo()
        );
        
        log.info("Email sent successfully to aluno: {}", event.alunoId());
    }
}
```

### 2.3 Data Flow

```
┌────────────────────────────────────────────────────────────────────────────┐
│                            COMPLETE DATA FLOW                               │
└────────────────────────────────────────────────────────────────────────────┘

1. Course Completion Event Received
   ┌─────────────────────────────────────────────────────────────────────┐
   │  CursoConcluidoEvent                                                │
   │  {                                                                  │
   │    "alunoId": "uuid",                                               │
   │    "alunoNome": "João",                                             │
   │    "alunoEmail": "joao@email.com",                                  │
   │    "cursoNome": "Spring Framework",                                 │
   │    "notaFinal": 8.5,                                                │
   │    "aprovado": true                                                 │
   │  }                                                                  │
   └─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
2. AI Listener Fetches Context
   ┌─────────────────────────────────────────────────────────────────────┐
   │  Last 3 Courses:                    Available Courses:              │
   │  ├─ Java (9.0)                      ├─ React.js (45h)               │
   │  ├─ Spring (8.5)                    ├─ Docker (30h)                 │
   │  └─ Web Dev (7.5)                   └─ Kubernetes (40h)             │
   └─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
3. Gemini AI Generates Message
   ┌─────────────────────────────────────────────────────────────────────┐
   │  "Olá João! Parabéns pela conclusão do curso Spring Framework com   │
   │   nota 8.5! Você demonstrou excelente desempenho em tecnologias     │
   │   backend. Baseado no seu histórico, recomendamos o curso de        │
   │   React.js para expandir suas habilidades para o frontend..."       │
   └─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
4. Email Event Published
   ┌─────────────────────────────────────────────────────────────────────┐
   │  EmailNotificationEvent                                             │
   │  {                                                                  │
   │    "destinatario": "joao@email.com",                                │
   │    "assunto": "Parabéns pela conclusão do curso Spring Framework!", │
   │    "corpo": "<AI generated message>"                                │
   │  }                                                                  │
   └─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
5. Email Sent via MailHog
   ┌─────────────────────────────────────────────────────────────────────┐
   │  📧 Email delivered to MailHog                                      │
   │  View at: http://localhost:8025                                     │
   └─────────────────────────────────────────────────────────────────────┘
```

### 2.4 Configuration Properties

**application-dev.properties** (additions)

```properties
# Gemini AI Configuration
gemini.api-key=${GEMINI_API_KEY:}
gemini.model=gemini-1.5-flash
gemini.temperature=0.7

# Email Configuration (MailHog)
spring.mail.host=${MAIL_HOST:localhost}
spring.mail.port=${MAIL_PORT:1025}
spring.mail.from=noreply@webcursos.com
# MailHog doesn't require authentication
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

### 2.5 Repository Extensions

**MatriculaRepository.java** (additions)

```java
// Get last N completed courses for a student
List<Matricula> findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
    UUID alunoId, 
    MatriculaStatus status
);

// Alternative with @Query for flexibility
@Query("""
    SELECT m FROM Matricula m 
    WHERE m.aluno.id = :alunoId 
    AND m.status = 'CONCLUIDO' 
    ORDER BY m.dataConclusao DESC
    LIMIT 3
""")
List<Matricula> findLastCompletedCourses(@Param("alunoId") UUID alunoId);
```

**CursoRepository.java** (additions)

```java
// Get courses not enrolled by student
@Query("""
    SELECT c FROM Curso c 
    WHERE c.id NOT IN (
        SELECT m.curso.id FROM Matricula m 
        WHERE m.aluno.id = :alunoId
    )
    ORDER BY c.nome
""")
List<Curso> findCursosNotEnrolledByAluno(@Param("alunoId") UUID alunoId);
```

### 2.6 Helper DTOs

**Package:** `com.morangosdoamor.WebCursos.application.dto`

```java
// For AI context
public record CursoCompletoDTO(
    String nome,
    String codigo,
    Double nota
) {}

public record CursoDisponivelDTO(
    String nome,
    String codigo,
    String descricao,
    int cargaHoraria
) {}
```

---

## 3. Task List

### Phase 1: Infrastructure Setup

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 1.1 | Add `spring-boot-starter-mail` dependency to `pom.xml` | 10 min | Workstream 1 complete |
| 1.2 | Add `langchain4j-google-ai-gemini` dependency to `pom.xml` | 10 min | None |
| 1.3 | Update `docker-compose.yml` with MailHog service | 20 min | None |
| 1.4 | Add mail and Gemini properties to `application-dev.properties` | 15 min | 1.1, 1.2 |
| 1.5 | Test MailHog container connectivity | 15 min | 1.3 |

### Phase 2: Repository Extensions

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 2.1 | Add `findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc` to MatriculaRepository | 20 min | None |
| 2.2 | Add `findCursosNotEnrolledByAluno` to CursoRepository | 20 min | None |
| 2.3 | Write unit tests for new repository methods | 45 min | 2.1, 2.2 |

### Phase 3: AI Service

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 3.1 | Create `GeminiConfig` configuration class | 30 min | 1.2 |
| 3.2 | Create helper DTOs (`CursoCompletoDTO`, `CursoDisponivelDTO`) | 20 min | None |
| 3.3 | Create `AiRecommendationService` with prompt building | 60 min | 3.1, 3.2 |
| 3.4 | Implement fallback message for API failures | 30 min | 3.3 |
| 3.5 | Write unit tests for AI service (mock Gemini) | 45 min | 3.3 |
| 3.6 | Manual integration test with real Gemini API | 30 min | 3.3 |

### Phase 4: Email Service

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 4.1 | Create `EmailService` with JavaMailSender | 30 min | 1.1 |
| 4.2 | Write unit tests for email service (mock sender) | 30 min | 4.1 |
| 4.3 | Integration test with MailHog | 30 min | 4.1, 1.5 |

### Phase 5: Message DTOs

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 5.1 | Create `EmailNotificationEvent` record | 15 min | None |
| 5.2 | Verify `CursoConcluidoEvent` from Workstream 1 | 10 min | Workstream 1 |

### Phase 6: AI Recommendation Listener

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 6.1 | Implement `AiRecommendationListener` skeleton | 30 min | Workstream 1 listener skeleton |
| 6.2 | Add data fetching logic (last courses, available courses) | 45 min | 2.1, 2.2, 6.1 |
| 6.3 | Integrate AI service call | 30 min | 3.3, 6.2 |
| 6.4 | Add email event publishing | 20 min | 5.1, 6.3 |
| 6.5 | Write unit tests for listener | 60 min | 6.4 |
| 6.6 | Write integration test for full AI flow | 60 min | 6.5 |

### Phase 7: Email Notification Listener

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 7.1 | Implement `EmailNotificationListener` | 30 min | 4.1, 5.1 |
| 7.2 | Write unit tests for listener | 30 min | 7.1 |
| 7.3 | Write integration test for email flow | 45 min | 7.2 |

### Phase 8: End-to-End Testing

| # | Task | Estimated Time | Dependencies |
|---|------|---------------|--------------|
| 8.1 | Full E2E test: Course completion → AI → Email | 90 min | All previous |
| 8.2 | Test error scenarios (API failure, email failure) | 60 min | 8.1 |
| 8.3 | Verify DLQ routing for failures | 30 min | 8.2 |

---

## 4. Acceptance Criteria

### AC-1: MailHog Infrastructure
- [ ] MailHog container starts successfully with `docker-compose up`
- [ ] Web UI accessible at `http://localhost:8025`
- [ ] Test email can be sent and viewed in MailHog UI

### AC-2: Gemini AI Integration
- [ ] `GeminiConfig` correctly initializes ChatLanguageModel
- [ ] AI prompt generates relevant, Portuguese course recommendations
- [ ] Fallback message works when API is unavailable

### AC-3: AI Recommendation Listener
- [ ] Receives `CursoConcluidoEvent` from queue
- [ ] Fetches last 3 completed courses correctly
- [ ] Fetches available courses correctly
- [ ] Publishes `EmailNotificationEvent` to email queue

### AC-4: Email Notification Listener
- [ ] Receives `EmailNotificationEvent` from queue
- [ ] Sends email via MailHog SMTP
- [ ] Email visible in MailHog web UI with correct subject/body

### AC-5: End-to-End Flow
- [ ] Complete course with nota ≥ 7.0 triggers full pipeline
- [ ] Email received in MailHog within 30 seconds
- [ ] Email contains personalized AI-generated content

### AC-6: Error Handling
- [ ] Gemini API failure uses fallback message
- [ ] Email send failure retries 3 times
- [ ] Failed messages route to DLQ after retries

---

## 5. Deliverables

| Deliverable | Description |
|-------------|-------------|
| `pom.xml` | Updated with LangChain4j and Spring Mail dependencies |
| `docker-compose.yml` | Updated with MailHog service |
| `application-dev.properties` | Email and Gemini configuration added |
| `GeminiConfig.java` | AI model configuration |
| `AiRecommendationService.java` | AI prompt and recommendation logic |
| `EmailService.java` | Email sending service |
| `EmailNotificationEvent.java` | DTO for email queue messages |
| `CursoCompletoDTO.java` | Helper DTO for AI context |
| `CursoDisponivelDTO.java` | Helper DTO for AI context |
| `AiRecommendationListener.java` | Full implementation |
| `EmailNotificationListener.java` | Full implementation |
| `MatriculaRepository.java` | Extended with new query methods |
| `CursoRepository.java` | Extended with new query methods |
| Unit & Integration Tests | Full test coverage for services and listeners |

---

## 6. Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `GEMINI_API_KEY` | **Yes** | - | Google AI Gemini API key |
| `MAIL_HOST` | No | `localhost` | SMTP server host |
| `MAIL_PORT` | No | `1025` | SMTP server port |

---

## 7. Testing Strategy

### Unit Tests
- Mock `ChatLanguageModel` for AI tests
- Mock `JavaMailSender` for email tests
- Mock `RabbitTemplate` for publisher tests
- Use `@DataJpaTest` for repository tests

### Integration Tests
- Use `@SpringBootTest` with embedded RabbitMQ (via Testcontainers optional)
- Test with real MailHog container
- Mock Gemini API with WireMock or use real API with test key

### Manual E2E Test Script

```bash
# 1. Start all services
docker-compose up -d

# 2. Create a student
curl -X POST http://localhost:8080/api/v1/alunos \
  -H "Content-Type: application/json" \
  -d '{"nome":"João Silva","email":"joao@test.com","matricula":"2024001"}'

# 3. Enroll in a course
curl -X POST http://localhost:8080/api/v1/alunos/{alunoId}/matriculas \
  -H "Content-Type: application/json" \
  -d '{"codigoCurso":"JAVA001"}'

# 4. Complete the course with passing grade
curl -X POST http://localhost:8080/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/conclusao \
  -H "Content-Type: application/json" \
  -d '{"notaFinal": 8.5}'

# 5. Check MailHog for email
open http://localhost:8025
```

---

## 8. Coordination with Workstream 1

### Interface Contract

**Input:** `CursoConcluidoEvent` from `curso.concluido.ai-recommendation` queue

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

### Sync Points with Workstream 1

| Sync Point | Description | When |
|------------|-------------|------|
| **SP-1** | Event DTO finalized | Before Phase 5 |
| **SP-2** | Queues created and accessible | Before Phase 6 |
| **SP-3** | Test message publishing | Before Phase 8 |

### Communication Channels

- [ ] Slack/Discord channel for async coordination
- [ ] Daily sync meetings during integration phase
- [ ] Shared test data setup scripts

