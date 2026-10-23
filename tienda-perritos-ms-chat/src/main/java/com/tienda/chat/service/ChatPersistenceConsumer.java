package com.tienda.chat.service;

import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.ChatMessageStatus;
import com.tienda.chat.entity.ChatMessage;
import com.tienda.chat.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChatPersistenceConsumer {
    private static final Logger log = LoggerFactory.getLogger(ChatPersistenceConsumer.class);
    private final ChatMessageRepository repository;

    public ChatPersistenceConsumer(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "chat.persist.queue")
    @Transactional
    public void persistir(ChatMessageDto message) {
        // El UUID único vuelve seguro el reintento del contenedor ante fallos transitorios.
        if (repository.existsByMessageId(message.messageId())) {
            log.info("[OK] Mensaje duplicado ignorado messageId={}", message.messageId());
            return;
        }
        try {
            repository.saveAndFlush(new ChatMessage(
                    message.messageId(), message.conversationId(), message.senderRole(), message.type(),
                    message.content(), message.orderId(), message.price(), message.comment(),
                    message.createdAt(), ChatMessageStatus.PERSISTED));
        } catch (DataAccessException exception) {
            log.error("[ERROR] No se pudo persistir messageId={}", message.messageId(), exception);
            throw exception;
        }
        log.info("[OK] Persistido messageId={} userId={}", message.messageId(), message.conversationId());
    }
}
