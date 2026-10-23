package com.tienda.chat.service;

import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.ChatMessageRequest;
import com.tienda.chat.dto.ChatMessageStatus;
import com.tienda.chat.dto.ChatMessageType;
import com.tienda.chat.dto.ConversationSummaryDto;
import com.tienda.chat.dto.SenderRole;
import com.tienda.chat.entity.ChatMessage;
import com.tienda.chat.repository.ChatMessageRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatApplicationService {
    private final ChatMessageRepository repository;
    private final ChatProducer producer;

    public ChatApplicationService(ChatMessageRepository repository, ChatProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public ChatMessageDto publicarComoUsuario(Authentication authentication, ChatMessageRequest request) {
        validarRol(authentication, "USER");
        // El identificador de conversación viene del subject JWT, nunca del cuerpo del cliente.
        return publicar(authentication.getName(), SenderRole.USER, request);
    }

    public ChatMessageDto publicarComoAdmin(Authentication authentication, String userId,
                                            ChatMessageRequest request) {
        validarRol(authentication, "ADMIN");
        return publicar(userId, SenderRole.ADMIN, request);
    }

    public List<ChatMessageDto> historial(Authentication authentication, String userId) {
        if (!esAdmin(authentication) && !authentication.getName().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede leer otra conversación");
        }
        return repository.findByConversationIdOrderByCreatedAtAsc(userId).stream()
                .map(ChatApplicationService::toDto)
                .toList();
    }

    public List<ConversationSummaryDto> conversaciones(Authentication authentication) {
        validarRol(authentication, "ADMIN");
        return repository.findConversationSummaries().stream()
                .map(summary -> {
                    ChatMessage latest = repository
                            .findFirstByConversationIdOrderByCreatedAtDesc(summary.getConversationId())
                            .orElseThrow();
                    return new ConversationSummaryDto(
                            summary.getConversationId(), resumen(latest), summary.getLastActivity());
                })
                .toList();
    }

    private ChatMessageDto publicar(String userId, SenderRole role, ChatMessageRequest request) {
        validarContenido(request, role);
        ChatMessageDto message = new ChatMessageDto(
                UUID.randomUUID(), userId, role, request.type(), request.content(),
                request.orderId(), request.price(), request.comment(), Instant.now(), ChatMessageStatus.QUEUED);
        producer.publicar(message);
        return message;
    }

    private static void validarContenido(ChatMessageRequest request, SenderRole role) {
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de mensaje es obligatorio");
        }
        switch (request.type()) {
            case TEXT -> exigirTexto(request.content(), "El mensaje no puede estar vacío");
            case QUOTE_REQUEST -> {
                if (role != SenderRole.USER || request.orderId() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Una cotización requiere un orderId y debe enviarla un usuario");
                }
                if (request.content() != null && request.content().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede estar vacío");
                }
            }
            case QUOTE_RESPONSE -> {
                if (role != SenderRole.ADMIN || request.price() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "La respuesta de cotización requiere un precio y un administrador");
                }
            }
        }
    }

    private static void exigirTexto(String content, String error) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static void validarRol(Authentication authentication, String role) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado para esta operación");
        }
    }

    private static boolean esAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private static ChatMessageDto toDto(ChatMessage message) {
        return new ChatMessageDto(message.getMessageId(), message.getConversationId(), message.getSenderRole(),
                message.getType(), message.getContent(), message.getOrderId(), message.getPrice(),
                message.getComment(), message.getCreatedAt(), message.getStatus());
    }

    private static String resumen(ChatMessage message) {
        return switch (message.getType()) {
            case TEXT -> message.getContent();
            case QUOTE_REQUEST -> "Solicitud de cotización para pedido #" + message.getOrderId();
            case QUOTE_RESPONSE -> "Cotización: $" + message.getPrice();
        };
    }
}
