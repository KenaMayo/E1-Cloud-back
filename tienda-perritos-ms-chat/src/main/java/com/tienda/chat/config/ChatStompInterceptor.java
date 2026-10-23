package com.tienda.chat.config;

import com.tienda.chat.security.JwtService;
import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ChatStompInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    public ChatStompInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AccessDeniedException("Encabezados STOMP inválidos");
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // El JWT del frame CONNECT autentica el WebSocket, igual que el bearer del API REST.
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new AccessDeniedException("Se requiere JWT para abrir el chat");
            }
            JwtService.TokenInfo info = jwtService.validar(header.substring(7).trim())
                    .orElseThrow(() -> new AccessDeniedException("JWT inválido para el chat"));
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    info.username(), null, List.of(new SimpleGrantedAuthority("ROLE_" + info.rol()))));
            return message;
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            throw new AccessDeniedException("Los mensajes se publican por la API REST autenticada");
        }
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validarSuscripcion(accessor);
        }
        return message;
    }

    private void validarSuscripcion(StompHeaderAccessor accessor) {
        var principal = accessor.getUser();
        String destino = accessor.getDestination();
        if (principal == null || destino == null) {
            throw new AccessDeniedException("Suscripción STOMP no autenticada");
        }
        if (!(principal instanceof Authentication authentication)) {
            throw new AccessDeniedException("Principal STOMP no autenticado");
        }
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (admin && (destino.equals("/topic/chat.admin") || destino.startsWith("/topic/chat."))) {
            return;
        }
        // Un usuario nunca puede cambiar el identificador de su topic para leer otra conversación.
        if (!admin && destino.equals("/topic/chat." + principal.getName())) {
            return;
        }
        throw new AccessDeniedException("No tiene permiso para suscribirse a esta conversación");
    }
}
