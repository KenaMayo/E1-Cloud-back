package com.tienda.chat.repository;

import java.time.Instant;

public interface ConversationSummaryProjection {
    String getConversationId();
    Instant getLastActivity();
}
