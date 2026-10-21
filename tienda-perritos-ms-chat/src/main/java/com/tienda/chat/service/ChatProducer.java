package com.tienda.chat.service;

import com.tienda.chat.config.RabbitMQConfig;
import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.SenderRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;

@Service
public class ChatProducer {
    private static final Logger log = LoggerFactory.getLogger(ChatProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public ChatProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(ChatMessageDto message) {
        String sender = message.senderRole() == SenderRole.USER ? "user" : "admin";
        String routingKey = "chat." + sender + "." + message.conversationId();
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, message);
        } catch (AmqpException exception) {
            log.error("[ERROR] No se pudo publicar messageId={} routingKey={}",
                    message.messageId(), routingKey, exception);
            throw exception;
        }
        log.info("[OK] Publicado messageId={} routingKey={}", message.messageId(), routingKey);
    }
}
