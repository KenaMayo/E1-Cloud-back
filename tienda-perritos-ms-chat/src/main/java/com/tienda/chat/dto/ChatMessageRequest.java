package com.tienda.chat.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ChatMessageRequest(
        ChatMessageType type,
        @Size(max = 2000) String content,
        @Positive Long orderId,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @Size(max = 1000) String comment) {
}
