package com.morangosdoamor.WebCursos.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuração do modelo de linguagem Google Gemini AI via LangChain4j.
 * 
 * Responsabilidades:
 * - Configurar o ChatLanguageModel com as credenciais do Gemini
 * - Definir parâmetros de geração (modelo, temperatura)
 * - Prover bean para injeção no serviço de recomendação
 * 
 * Requer variável de ambiente GEMINI_API_KEY configurada.
 */
@Configuration
@Slf4j
public class GeminiConfig {

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String modelName;

    @Value("${gemini.temperature:0.7}")
    private double temperature;

    /**
     * Cria o bean do modelo de chat Gemini.
     * Se a API key não estiver configurada, retorna null e loga um aviso.
     * 
     * @return ChatLanguageModel configurado ou null se API key ausente
     */
    @Bean
    public ChatLanguageModel geminiChatModel() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY não configurada. Serviço de IA desabilitado. " +
                     "Configure a variável de ambiente GEMINI_API_KEY para habilitar recomendações com IA.");
            return null;
        }

        log.info("Configurando Gemini AI com modelo: {}, temperatura: {}", modelName, temperature);

        return GoogleAiGeminiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .temperature(temperature)
            .build();
    }
}


