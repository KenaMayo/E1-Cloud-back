package com.tienda.chat.service;

import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.SenderRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Component;

@Component
public class ChatDeliveryConsumer {
    private static final Logger log = LoggerFactory.getLogger(ChatDeliveryConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatDeliveryConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "chat.delivery.queue")
    public void entregar(ChatMessageDto message) {
        String destination = "/topic/chat." + message.conversationId();
        try {
            messagingTemplate.convertAndSend(destination, message);
            if (message.senderRole() == SenderRole.USER) {
                messagingTemplate.convertAndSend("/topic/chat.admin", message);
            }
        } catch (MessageDeliveryException exception) {
            log.error("[ERROR] No se pudo entregar messageId={} destino={}",
                    message.messageId(), destination, exception);
            throw exception;
        }
        log.info("[OK] Entregado messageId={} destino={}", message.messageId(), destination);
    }
}
