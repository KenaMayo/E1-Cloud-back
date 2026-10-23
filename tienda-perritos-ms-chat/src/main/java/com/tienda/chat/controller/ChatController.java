package com.tienda.chat.controller;

import com.tienda.chat.dto.ChatMessageDto;
import com.tienda.chat.dto.ChatMessageRequest;
import com.tienda.chat.dto.ConversationSummaryDto;
import com.tienda.chat.service.ChatApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatApplicationService chatService;

    public ChatController(ChatApplicationService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/me/messages")
    public List<ChatMessageDto> historialPropio(Authentication authentication) {
        return chatService.historial(authentication, authentication.getName());
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChatMessageDto publicarComoUsuario(Authentication authentication,
                                               @Valid @RequestBody ChatMessageRequest request) {
        return chatService.publicarComoUsuario(authentication, request);
    }

    @GetMapping("/conversations")
    public List<ConversationSummaryDto> conversaciones(Authentication authentication) {
        return chatService.conversaciones(authentication);
    }

    @GetMapping("/conversations/{userId}/messages")
    public List<ChatMessageDto> historialUsuario(Authentication authentication, @PathVariable String userId) {
        return chatService.historial(authentication, userId);
    }

    @PostMapping("/conversations/{userId}/messages")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChatMessageDto responder(Authentication authentication, @PathVariable String userId,
                                    @Valid @RequestBody ChatMessageRequest request) {
        return chatService.publicarComoAdmin(authentication, userId, request);
    }
}
