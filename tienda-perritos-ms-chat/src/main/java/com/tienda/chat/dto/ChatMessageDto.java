package com.tienda.chat.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ChatMessageDto(
        UUID messageId,
        String conversationId,
        SenderRole senderRole,
        ChatMessageType type,
        String content,
        Long orderId,
        BigDecimal price,
        String comment,
        Instant createdAt,
        ChatMessageStatus status) {
}
