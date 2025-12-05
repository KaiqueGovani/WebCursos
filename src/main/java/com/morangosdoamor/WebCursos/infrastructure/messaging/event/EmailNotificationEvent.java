package com.morangosdoamor.WebCursos.infrastructure.messaging.event;

import java.util.UUID;

/**
 * Evento que representa uma notificação de email a ser enviada.
 * Publicado pelo AiRecommendationListener após gerar a recomendação.
 * Consumido pelo EmailNotificationListener para envio efetivo do email.
 * 
 * Este design permite:
 * - Separação de responsabilidades (AI vs Email)
 * - Retry independente para falhas de email
 * - Evita duplicação de emails
 * 
 * @param destinatario Email do destinatário
 * @param nomeDestinatario Nome do destinatário
 * @param assunto Assunto do email
 * @param corpo Corpo do email (mensagem gerada pela IA ou template)
 * @param alunoId ID do aluno (para rastreabilidade)
 * @param cursoId ID do curso concluído (para rastreabilidade)
 */
public record EmailNotificationEvent(
    String destinatario,
    String nomeDestinatario,
    String assunto,
    String corpo,
    UUID alunoId,
    UUID cursoId
) {
    /**
     * Construtor de conveniência para criar evento de notificação de conclusão de curso.
     */
    public static EmailNotificationEvent forCourseCompletion(
            String destinatario,
            String nomeDestinatario,
            String cursoNome,
            String mensagemRecomendacao,
            UUID alunoId,
            UUID cursoId
    ) {
        return new EmailNotificationEvent(
            destinatario,
            nomeDestinatario,
            String.format("Parabéns pela conclusão do curso %s!", cursoNome),
            mensagemRecomendacao,
            alunoId,
            cursoId
        );
    }
}

