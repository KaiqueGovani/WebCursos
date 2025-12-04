package com.morangosdoamor.WebCursos.infrastructure.messaging.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Testes unitários para o evento CursoConcluidoEvent.
 * Valida a criação do evento e serialização/deserialização JSON.
 */
class CursoConcluidoEventTest {

    private final ObjectMapper objectMapper;

    CursoConcluidoEventTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void deveCriarEventoComTodosOsCampos() {
        UUID alunoId = UUID.randomUUID();
        UUID cursoId = UUID.randomUUID();
        LocalDateTime dataConclusao = LocalDateTime.now();

        CursoConcluidoEvent event = new CursoConcluidoEvent(
            alunoId,
            "João Silva",
            "joao@email.com",
            cursoId,
            "Programação Java",
            "JAVA001",
            8.5,
            true,
            dataConclusao
        );

        assertThat(event.alunoId()).isEqualTo(alunoId);
        assertThat(event.alunoNome()).isEqualTo("João Silva");
        assertThat(event.alunoEmail()).isEqualTo("joao@email.com");
        assertThat(event.cursoId()).isEqualTo(cursoId);
        assertThat(event.cursoNome()).isEqualTo("Programação Java");
        assertThat(event.cursoCodigo()).isEqualTo("JAVA001");
        assertThat(event.notaFinal()).isEqualTo(8.5);
        assertThat(event.aprovado()).isTrue();
        assertThat(event.dataConclusao()).isEqualTo(dataConclusao);
    }

    @Test
    void deveCriarEventoAprovadoComFactoryMethod() {
        UUID alunoId = UUID.randomUUID();
        UUID cursoId = UUID.randomUUID();
        LocalDateTime dataConclusao = LocalDateTime.now();

        CursoConcluidoEvent event = CursoConcluidoEvent.of(
            alunoId, "Maria", "maria@email.com",
            cursoId, "Spring Boot", "SPRING001",
            9.0, dataConclusao
        );

        assertThat(event.aprovado()).isTrue();
        assertThat(event.notaFinal()).isEqualTo(9.0);
    }

    @Test
    void deveCriarEventoReprovadoComFactoryMethod() {
        UUID alunoId = UUID.randomUUID();
        UUID cursoId = UUID.randomUUID();
        LocalDateTime dataConclusao = LocalDateTime.now();

        CursoConcluidoEvent event = CursoConcluidoEvent.of(
            alunoId, "Pedro", "pedro@email.com",
            cursoId, "React.js", "REACT001",
            5.5, dataConclusao
        );

        assertThat(event.aprovado()).isFalse();
        assertThat(event.notaFinal()).isEqualTo(5.5);
    }

    @Test
    void deveCriarEventoAprovadoComNotaExatamente7() {
        UUID alunoId = UUID.randomUUID();
        UUID cursoId = UUID.randomUUID();
        LocalDateTime dataConclusao = LocalDateTime.now();

        CursoConcluidoEvent event = CursoConcluidoEvent.of(
            alunoId, "Ana", "ana@email.com",
            cursoId, "Python", "PYTHON001",
            7.0, dataConclusao
        );

        assertThat(event.aprovado()).isTrue();
    }

    @Test
    void deveSerializarParaJsonCorretamente() throws Exception {
        UUID alunoId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cursoId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
        LocalDateTime dataConclusao = LocalDateTime.of(2025, 12, 4, 14, 30, 0);

        CursoConcluidoEvent event = new CursoConcluidoEvent(
            alunoId, "João Silva", "joao.silva@email.com",
            cursoId, "Programação Java", "JAVA001",
            8.5, true, dataConclusao
        );

        String json = objectMapper.writeValueAsString(event);

        assertThat(json).contains("\"alunoId\":\"550e8400-e29b-41d4-a716-446655440000\"");
        assertThat(json).contains("\"alunoNome\":\"João Silva\"");
        assertThat(json).contains("\"alunoEmail\":\"joao.silva@email.com\"");
        assertThat(json).contains("\"cursoId\":\"660e8400-e29b-41d4-a716-446655440001\"");
        assertThat(json).contains("\"cursoNome\":\"Programação Java\"");
        assertThat(json).contains("\"cursoCodigo\":\"JAVA001\"");
        assertThat(json).contains("\"notaFinal\":8.5");
        assertThat(json).contains("\"aprovado\":true");
        assertThat(json).contains("\"dataConclusao\":\"2025-12-04T14:30:00\"");
    }

    @Test
    void deveDeserializarDeJsonCorretamente() throws Exception {
        String json = """
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
            """;

        CursoConcluidoEvent event = objectMapper.readValue(json, CursoConcluidoEvent.class);

        assertThat(event.alunoId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(event.alunoNome()).isEqualTo("João Silva");
        assertThat(event.alunoEmail()).isEqualTo("joao.silva@email.com");
        assertThat(event.cursoId()).isEqualTo(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"));
        assertThat(event.cursoNome()).isEqualTo("Programação Java");
        assertThat(event.cursoCodigo()).isEqualTo("JAVA001");
        assertThat(event.notaFinal()).isEqualTo(8.5);
        assertThat(event.aprovado()).isTrue();
        assertThat(event.dataConclusao()).isEqualTo(LocalDateTime.of(2025, 12, 4, 14, 30, 0));
    }
}
