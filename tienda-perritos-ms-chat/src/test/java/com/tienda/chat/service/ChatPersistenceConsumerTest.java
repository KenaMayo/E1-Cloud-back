package com.tienda.chat.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.ChatMessageStatus;
import com.tienda.chat.dto.ChatMessageType;
import com.tienda.chat.dto.SenderRole;
import com.tienda.chat.repository.ChatMessageRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChatPersistenceConsumerTest {
    private final ChatMessageRepository repository = org.mockito.Mockito.mock(ChatMessageRepository.class);
    private final ChatPersistenceConsumer consumer = new ChatPersistenceConsumer(repository);

    @Test
    void ignoraMessageIdQueYaFueProcesado() {
        UUID messageId = UUID.randomUUID();
        when(repository.existsByMessageId(messageId)).thenReturn(true);
        ChatMessageDto message = new ChatMessageDto(
                messageId, "cliente", SenderRole.USER, ChatMessageType.TEXT, "Hola",
                null, null, null, Instant.now(), ChatMessageStatus.QUEUED);

        consumer.persistir(message);

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
