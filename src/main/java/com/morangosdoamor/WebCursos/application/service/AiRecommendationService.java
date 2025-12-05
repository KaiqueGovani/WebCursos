package com.morangosdoamor.WebCursos.application.service;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.morangosdoamor.WebCursos.application.dto.CursoCompletoDTO;
import com.morangosdoamor.WebCursos.application.dto.CursoDisponivelDTO;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por gerar recomendações personalizadas usando IA (Google Gemini).
 * 
 * Princípios aplicados:
 * - Clean Architecture: encapsula lógica de integração com IA
 * - Graceful degradation: fallback para mensagem padrão se IA indisponível
 * - Prompt engineering: construção estruturada de prompts para melhor qualidade
 * 
 * Responsabilidades:
 * - Construir prompts contextualizados com histórico do aluno
 * - Chamar API do Gemini para gerar recomendações
 * - Prover fallback se a IA não estiver disponível
 */
@Service
@Slf4j
public class AiRecommendationService {

    private final ChatLanguageModel chatModel;

    public AiRecommendationService(@Nullable ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        if (chatModel == null) {
            log.warn("AiRecommendationService inicializado sem modelo de IA. Usando mensagens de fallback.");
        }
    }

    /**
     * Gera uma mensagem de recomendação personalizada para o aluno.
     * 
     * @param alunoNome Nome do aluno
     * @param cursoConcluidoNome Nome do curso recém-concluído
     * @param notaFinal Nota obtida no curso concluído
     * @param ultimosCursos Lista dos últimos cursos concluídos (até 3)
     * @param cursosDisponiveis Lista de cursos disponíveis para matrícula
     * @return Mensagem personalizada gerada pela IA ou mensagem de fallback
     */
    public String generateRecommendation(
            String alunoNome,
            String cursoConcluidoNome,
            Double notaFinal,
            List<CursoCompletoDTO> ultimosCursos,
            List<CursoDisponivelDTO> cursosDisponiveis
    ) {
        if (chatModel == null) {
            log.info("Modelo de IA não disponível. Gerando mensagem de fallback para: {}", alunoNome);
            return generateFallbackMessage(alunoNome, cursoConcluidoNome, notaFinal, cursosDisponiveis);
        }

        try {
            String prompt = buildPrompt(alunoNome, cursoConcluidoNome, notaFinal, ultimosCursos, cursosDisponiveis);
            log.debug("Prompt gerado para IA: {}", prompt);

            String response = chatModel.generate(prompt);
            log.info("Recomendação gerada com sucesso para aluno: {}", alunoNome);

            return response;
        } catch (Exception e) {
            log.error("Erro ao gerar recomendação com IA para aluno: {}. Usando fallback.", alunoNome, e);
            return generateFallbackMessage(alunoNome, cursoConcluidoNome, notaFinal, cursosDisponiveis);
        }
    }

    /**
     * Constrói o prompt estruturado para o modelo de IA.
     */
    private String buildPrompt(
            String alunoNome,
            String cursoConcluidoNome,
            Double notaFinal,
            List<CursoCompletoDTO> ultimosCursos,
            List<CursoDisponivelDTO> cursosDisponiveis
    ) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Você é um assistente educacional amigável da plataforma WebCursos.\n\n");

        prompt.append("CONTEXTO:\n");
        prompt.append(String.format("O aluno %s acabou de concluir o curso \"%s\" com nota %.1f.\n\n",
                alunoNome, cursoConcluidoNome, notaFinal));

        if (!ultimosCursos.isEmpty()) {
            prompt.append("HISTÓRICO DOS ÚLTIMOS CURSOS CONCLUÍDOS:\n");
            for (CursoCompletoDTO curso : ultimosCursos) {
                prompt.append(String.format("- %s (%s): Nota %.1f\n",
                        curso.nome(), curso.codigo(), curso.nota()));
            }
            prompt.append("\n");
        }

        if (!cursosDisponiveis.isEmpty()) {
            prompt.append("CURSOS DISPONÍVEIS PARA MATRÍCULA:\n");
            for (CursoDisponivelDTO curso : cursosDisponiveis) {
                prompt.append(String.format("- %s (%s): %s [%dh]\n",
                        curso.nome(), curso.codigo(), truncateDescription(curso.descricao(), 80), curso.cargaHoraria()));
            }
            prompt.append("\n");
        }

        prompt.append("TAREFA:\n");
        prompt.append("Escreva uma mensagem curta (máximo 3 parágrafos) em português brasileiro que:\n");
        prompt.append("1. Parabenize o aluno pela conclusão do curso com entusiasmo\n");
        prompt.append("2. Comente brevemente sobre seu desempenho baseado na nota\n");

        if (!cursosDisponiveis.isEmpty()) {
            prompt.append("3. Analise o histórico de cursos e sugira UM curso específico da lista de disponíveis que combine com seu perfil, explicando o motivo\n");
        } else {
            prompt.append("3. Parabenize-o por ter completado todos os cursos disponíveis na plataforma\n");
        }

        prompt.append("\nTOM: Amigável, encorajador e profissional. Não use formatação markdown.\n");
        prompt.append("IMPORTANTE: Responda APENAS com a mensagem, sem introduções ou explicações adicionais.");

        return prompt.toString();
    }

    /**
     * Gera mensagem de fallback quando a IA não está disponível.
     */
    private String generateFallbackMessage(
            String alunoNome,
            String cursoConcluidoNome,
            Double notaFinal,
            List<CursoDisponivelDTO> cursosDisponiveis
    ) {
        StringBuilder message = new StringBuilder();

        message.append(String.format("Olá, %s!\n\n", alunoNome));

        message.append(String.format("Parabéns pela conclusão do curso \"%s\"", cursoConcluidoNome));
        if (notaFinal >= 9.0) {
            message.append(String.format(" com uma nota excelente de %.1f! Seu desempenho foi excepcional!", notaFinal));
        } else if (notaFinal >= 7.0) {
            message.append(String.format(" com nota %.1f! Ótimo trabalho!", notaFinal));
        } else {
            message.append(String.format(" com nota %.1f. Continue se dedicando!", notaFinal));
        }
        message.append("\n\n");

        if (!cursosDisponiveis.isEmpty()) {
            CursoDisponivelDTO sugestao = cursosDisponiveis.get(0);
            message.append(String.format("Que tal continuar sua jornada de aprendizado? " +
                    "Recomendamos o curso \"%s\" (%dh) como próximo passo. %s\n\n",
                    sugestao.nome(), sugestao.cargaHoraria(), truncateDescription(sugestao.descricao(), 100)));
        } else {
            message.append("Você completou todos os cursos disponíveis! Parabéns por essa conquista incrível!\n\n");
        }

        message.append("Continue aprendendo conosco na WebCursos!");

        return message.toString();
    }

    private String truncateDescription(String description, int maxLength) {
        if (description == null || description.length() <= maxLength) {
            return description != null ? description : "";
        }
        return description.substring(0, maxLength - 3) + "...";
    }
}


