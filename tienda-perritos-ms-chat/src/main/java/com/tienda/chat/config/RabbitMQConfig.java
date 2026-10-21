package com.tienda.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    public static final String EXCHANGE = "chat.exchange";
    public static final String PERSIST_QUEUE = "chat.persist.queue";
    public static final String DELIVERY_QUEUE = "chat.delivery.queue";
    public static final String DEAD_LETTER_QUEUE = "chat.dlq";

    @Bean
    TopicExchange chatExchange() {
        // Cada cola enlazada obtiene su propia copia del mensaje publicado.
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue persistenceQueue() {
        // Al agotar los reintentos, RabbitMQ redirige el mensaje a la cola de errores.
        return QueueBuilder.durable(PERSIST_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    Queue deliveryQueue() {
        return QueueBuilder.durable(DELIVERY_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding persistenceBinding(@Qualifier("persistenceQueue") Queue persistenceQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(persistenceQueue).to(chatExchange).with("chat.#");
    }

    @Bean
    Binding deliveryBinding(@Qualifier("deliveryQueue") Queue deliveryQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(deliveryQueue).to(chatExchange).with("chat.#");
    }

    @Bean
    MessageConverter chatMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
