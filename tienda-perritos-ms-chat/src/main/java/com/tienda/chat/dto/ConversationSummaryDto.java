package com.tienda.chat.dto;

import java.time.Instant;

public record ConversationSummaryDto(String userId, String lastMessage, Instant lastActivity) {
}
