package com.tienda.chat.repository;

import com.tienda.chat.entity.ChatMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    boolean existsByMessageId(UUID messageId);

    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    Optional<ChatMessage> findFirstByConversationIdOrderByCreatedAtDesc(String conversationId);

    @Query("""
            select m.conversationId as conversationId, max(m.createdAt) as lastActivity
            from ChatMessage m
            group by m.conversationId
            order by max(m.createdAt) desc
            """)
    List<ConversationSummaryProjection> findConversationSummaries();
}
