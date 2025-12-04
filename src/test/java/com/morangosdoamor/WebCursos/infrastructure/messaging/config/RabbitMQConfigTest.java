package com.morangosdoamor.WebCursos.infrastructure.messaging.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testes unitários de configuração do RabbitMQ.
 * Valida que os beans são criados corretamente com os valores esperados.
 * Este teste não requer conexão com o broker.
 */
class RabbitMQConfigTest {

    private RabbitMQConfig config;

    private static final String EXCHANGE_NAME = "webcursos.exchange";
    private static final String AI_QUEUE_NAME = "curso.concluido.ai-recommendation";
    private static final String EMAIL_QUEUE_NAME = "curso.concluido.email-notification";
    private static final String DLQ_NAME = "curso.concluido.dlq";
    private static final String ROUTING_KEY = "curso.concluido";

    @BeforeEach
    void setUp() {
        config = new RabbitMQConfig();
        ReflectionTestUtils.setField(config, "exchangeName", EXCHANGE_NAME);
        ReflectionTestUtils.setField(config, "aiRecommendationQueueName", AI_QUEUE_NAME);
        ReflectionTestUtils.setField(config, "emailNotificationQueueName", EMAIL_QUEUE_NAME);
        ReflectionTestUtils.setField(config, "dlqName", DLQ_NAME);
        ReflectionTestUtils.setField(config, "routingKey", ROUTING_KEY);
    }

    @Test
    void deveConfigurarExchangeCorretamente() {
        TopicExchange exchange = config.webcursosExchange();

        assertThat(exchange).isNotNull();
        assertThat(exchange.getName()).isEqualTo(EXCHANGE_NAME);
        assertThat(exchange.getType()).isEqualTo("topic");
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    void deveConfigurarFilaAiRecommendationCorretamente() {
        Queue queue = config.aiRecommendationQueue();

        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(AI_QUEUE_NAME);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments()).containsKey("x-dead-letter-routing-key");
        assertThat(queue.getArguments()).containsKey("x-dead-letter-exchange");
    }

    @Test
    void deveConfigurarFilaEmailNotificationCorretamente() {
        Queue queue = config.emailNotificationQueue();

        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(EMAIL_QUEUE_NAME);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments()).containsKey("x-dead-letter-routing-key");
        assertThat(queue.getArguments()).containsKey("x-dead-letter-exchange");
    }

    @Test
    void deveConfigurarDeadLetterQueueCorretamente() {
        Queue queue = config.deadLetterQueue();

        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(DLQ_NAME);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    void deveConfigurarBindingAiRecommendationCorretamente() {
        Queue queue = config.aiRecommendationQueue();
        TopicExchange exchange = config.webcursosExchange();

        Binding binding = config.aiRecommendationBinding(queue, exchange);

        assertThat(binding).isNotNull();
        assertThat(binding.getExchange()).isEqualTo(EXCHANGE_NAME);
        assertThat(binding.getDestination()).isEqualTo(AI_QUEUE_NAME);
        assertThat(binding.getRoutingKey()).isEqualTo(ROUTING_KEY + ".#");
    }

    @Test
    void deveConfigurarBindingEmailNotificationCorretamente() {
        Queue queue = config.emailNotificationQueue();
        TopicExchange exchange = config.webcursosExchange();

        Binding binding = config.emailNotificationBinding(queue, exchange);

        assertThat(binding).isNotNull();
        assertThat(binding.getExchange()).isEqualTo(EXCHANGE_NAME);
        assertThat(binding.getDestination()).isEqualTo(EMAIL_QUEUE_NAME);
        assertThat(binding.getRoutingKey()).isEqualTo(ROUTING_KEY + ".#");
    }

    @Test
    void deveConfigurarObjectMapperCorretamente() {
        ObjectMapper mapper = config.objectMapper();

        assertThat(mapper).isNotNull();
    }

    @Test
    void deveConfigurarMessageConverterCorretamente() {
        ObjectMapper mapper = config.objectMapper();
        MessageConverter converter = config.jacksonMessageConverter(mapper);

        assertThat(converter).isNotNull();
    }
}
