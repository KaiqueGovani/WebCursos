package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;

/**
 * Listener para processamento de recomendações de cursos por IA.
 * 
 * Este componente consome mensagens da fila curso.concluido.ai-recommendation
 * e processa eventos de conclusão de curso para gerar recomendações personalizadas.
 * 
 * NOTA: Esta é uma implementação skeleton para ser completada no Workstream 2.
 * O processamento real de IA deve ser implementado posteriormente.
 * 
 * Responsabilidades futuras (Workstream 2):
 * - Analisar histórico do aluno
 * - Gerar recomendações personalizadas de novos cursos
 * - Atualizar perfil do aluno com novas recomendações
 */
@Component
public class AiRecommendationListener {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationListener.class);

    /**
     * Processa eventos de conclusão de curso para geração de recomendações.
     * 
     * @param event Evento de conclusão de curso contendo dados do aluno e curso
     */
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.ai-recommendation}")
    public void processAiRecommendation(CursoConcluidoEvent event) {
        log.info("=== AI Recommendation Listener ===");
        log.info("Recebido evento de conclusão de curso para processamento de IA");
        log.info("Aluno: {} (ID: {})", event.alunoNome(), event.alunoId());
        log.info("Curso: {} - {} (ID: {})", event.cursoCodigo(), event.cursoNome(), event.cursoId());
        log.info("Nota Final: {} | Aprovado: {}", event.notaFinal(), event.aprovado());
        log.info("Data Conclusão: {}", event.dataConclusao());
        
        // TODO: Implementar lógica de recomendação por IA (Workstream 2)
        // 1. Buscar histórico completo do aluno
        // 2. Analisar padrões de aprendizado
        // 3. Identificar cursos similares ou complementares
        // 4. Gerar lista de recomendações personalizadas
        // 5. Salvar recomendações no perfil do aluno
        
        if (event.aprovado()) {
            log.info("Aluno aprovado! Gerando recomendações de cursos avançados...");
            // TODO: Recomendar cursos mais avançados na mesma área
        } else {
            log.info("Aluno não aprovado. Gerando recomendações de reforço...");
            // TODO: Recomendar cursos de reforço ou revisão
        }
        
        log.info("=== Fim do processamento AI Recommendation ===");
    }
}
