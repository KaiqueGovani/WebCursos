package com.morangosdoamor.WebCursos.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuração do RabbitMQ para o sistema de mensageria.
 * 
 * Define a topologia de filas:
 * - Exchange tipo topic para roteamento flexível
 * - Filas para AI Recommendation e Email Notification
 * - Dead Letter Queue (DLQ) para mensagens com falha
 * - Bindings com routing keys apropriadas
 * 
 * @see <a href="https://www.rabbitmq.com/tutorials/tutorial-five-java.html">RabbitMQ Topics</a>
 */
@Configuration
public class RabbitMQConfig {

    @Value("${webcursos.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${webcursos.rabbitmq.queue.ai-recommendation}")
    private String aiRecommendationQueueName;

    @Value("${webcursos.rabbitmq.queue.email-notification}")
    private String emailNotificationQueueName;

    @Value("${webcursos.rabbitmq.queue.dlq}")
    private String dlqName;

    @Value("${webcursos.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${webcursos.rabbitmq.routing-key.email:curso.concluido.email}")
    private String emailRoutingKey;

    // ==================== Exchange ====================

    /**
     * Cria o exchange principal do tipo topic.
     * Topic exchanges permitem roteamento baseado em padrões de routing key.
     * 
     * @return TopicExchange configurado como durável
     */
    @Bean
    public TopicExchange webcursosExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    // ==================== Queues ====================

    /**
     * Cria a Dead Letter Queue para mensagens que falharam após todas as tentativas.
     * Configurada como durável para sobreviver a reinícios do broker.
     * 
     * @return Queue DLQ durável
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    /**
     * Cria a fila para processamento de recomendações por IA.
     * Configurada com DLQ para mensagens que falharem.
     * 
     * @return Queue para AI Recommendation com dead-letter configurado
     */
    @Bean
    public Queue aiRecommendationQueue() {
        return QueueBuilder.durable(aiRecommendationQueueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }

    /**
     * Cria a fila para envio de notificações por email.
     * Configurada com DLQ para mensagens que falharem.
     * 
     * @return Queue para Email Notification com dead-letter configurado
     */
    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable(emailNotificationQueueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }

    // ==================== Bindings ====================

    /**
     * Vincula a fila de AI Recommendation ao exchange com routing key exata.
     * Usa routing key "curso.concluido" para receber apenas eventos de conclusão de curso.
     * 
     * @param aiRecommendationQueue Fila de AI Recommendation
     * @param webcursosExchange Exchange principal
     * @return Binding entre fila e exchange
     */
    @Bean
    public Binding aiRecommendationBinding(Queue aiRecommendationQueue, TopicExchange webcursosExchange) {
        return BindingBuilder.bind(aiRecommendationQueue)
                .to(webcursosExchange)
                .with(routingKey);
    }

    /**
     * Vincula a fila de Email Notification ao exchange com routing key específica.
     * Usa routing key "curso.concluido.email" para receber apenas eventos de email
     * publicados após o processamento de IA.
     * 
     * @param emailNotificationQueue Fila de Email Notification
     * @param webcursosExchange Exchange principal
     * @return Binding entre fila e exchange
     */
    @Bean
    public Binding emailNotificationBinding(Queue emailNotificationQueue, TopicExchange webcursosExchange) {
        return BindingBuilder.bind(emailNotificationQueue)
                .to(webcursosExchange)
                .with(emailRoutingKey);
    }

    // ==================== Message Converter ====================

    /**
     * Configura o ObjectMapper para serialização JSON com suporte a Java 8 Date/Time.
     * 
     * @return ObjectMapper configurado
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Configura o conversor de mensagens para JSON usando Jackson.
     * Permite serialização/deserialização automática de objetos Java para JSON.
     * 
     * @param objectMapper ObjectMapper configurado
     * @return MessageConverter para JSON
     */
    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Configura o RabbitTemplate com o conversor JSON.
     * RabbitTemplate é o componente principal para envio de mensagens.
     * 
     * @param connectionFactory Factory de conexões do RabbitMQ
     * @param jacksonMessageConverter Conversor JSON
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter jacksonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter);
        return rabbitTemplate;
    }
}
