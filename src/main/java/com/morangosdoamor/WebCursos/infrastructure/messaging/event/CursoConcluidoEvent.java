package com.morangosdoamor.WebCursos.infrastructure.messaging.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio que representa a conclusão de um curso por um aluno.
 * 
 * Este evento é publicado no RabbitMQ quando um aluno conclui um curso,
 * permitindo que outros serviços (AI Recommendation, Email Notification)
 * processem a informação de forma assíncrona.
 * 
 * @param alunoId ID único do aluno que concluiu o curso
 * @param alunoNome Nome completo do aluno
 * @param alunoEmail Email do aluno para notificações
 * @param cursoId ID único do curso concluído
 * @param cursoNome Nome do curso concluído
 * @param cursoCodigo Código único do curso (ex: JAVA001)
 * @param notaFinal Nota final obtida pelo aluno (0.0 a 10.0)
 * @param aprovado Indica se o aluno foi aprovado (nota >= 7.0)
 * @param dataConclusao Data e hora da conclusão do curso
 */
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
) {
    
    /**
     * Factory method para criar um evento a partir dos dados de conclusão.
     * 
     * @param alunoId ID do aluno
     * @param alunoNome Nome do aluno
     * @param alunoEmail Email do aluno
     * @param cursoId ID do curso
     * @param cursoNome Nome do curso
     * @param cursoCodigo Código do curso
     * @param notaFinal Nota final
     * @param dataConclusao Data de conclusão
     * @return Novo evento de conclusão de curso
     */
    public static CursoConcluidoEvent of(
            UUID alunoId,
            String alunoNome,
            String alunoEmail,
            UUID cursoId,
            String cursoNome,
            String cursoCodigo,
            Double notaFinal,
            LocalDateTime dataConclusao) {
        
        boolean aprovado = notaFinal != null && notaFinal >= 7.0;
        return new CursoConcluidoEvent(
            alunoId, alunoNome, alunoEmail,
            cursoId, cursoNome, cursoCodigo,
            notaFinal, aprovado, dataConclusao
        );
    }
}
