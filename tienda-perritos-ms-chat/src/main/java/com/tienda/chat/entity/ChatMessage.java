package com.tienda.chat.entity;

import com.tienda.chat.dto.ChatMessageStatus;
import com.tienda.chat.dto.ChatMessageType;
import com.tienda.chat.dto.SenderRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_message_message_id", columnNames = "message_id")
})
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "conversation_id", nullable = false, length = 255)
    private String conversationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", nullable = false, length = 10)
    private SenderRole senderRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageType type;

    @Column(length = 2000)
    private String content;

    @Column(name = "order_id")
    private Long orderId;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageStatus status;

    protected ChatMessage() {
    }

    public ChatMessage(UUID messageId, String conversationId, SenderRole senderRole, ChatMessageType type,
                       String content, Long orderId, BigDecimal price, String comment, Instant createdAt,
                       ChatMessageStatus status) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderRole = senderRole;
        this.type = type;
        this.content = content;
        this.orderId = orderId;
        this.price = price;
        this.comment = comment;
        this.createdAt = createdAt;
        this.status = status;
    }

    @PrePersist
    void establecerFecha() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public UUID getMessageId() { return messageId; }
    public String getConversationId() { return conversationId; }
    public SenderRole getSenderRole() { return senderRole; }
    public ChatMessageType getType() { return type; }
    public String getContent() { return content; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getPrice() { return price; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
    public ChatMessageStatus getStatus() { return status; }
}
