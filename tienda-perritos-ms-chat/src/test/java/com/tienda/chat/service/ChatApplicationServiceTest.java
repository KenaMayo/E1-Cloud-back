package com.tienda.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.tienda.chat.dto.ChatMessageRequest;
import com.tienda.chat.dto.ChatMessageType;
import com.tienda.chat.repository.ChatMessageRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

class ChatApplicationServiceTest {
    private final ChatMessageRepository repository = org.mockito.Mockito.mock(ChatMessageRepository.class);
    private final ChatProducer producer = org.mockito.Mockito.mock(ChatProducer.class);
    private final ChatApplicationService service = new ChatApplicationService(repository, producer);

    @Test
    void usuarioPublicaIdentificadoPorElJwt() {
        var user = new UsernamePasswordAuthenticationToken(
                "cliente", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        var result = service.publicarComoUsuario(user, new ChatMessageRequest(ChatMessageType.TEXT, "Hola", null, null, null));

        assertEquals("cliente", result.conversationId());
        verify(producer).publicar(result);
    }

    @Test
    void usuarioNoPuedeLeerUnaConversacionAjena() {
        var user = new UsernamePasswordAuthenticationToken(
                "cliente", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThrows(ResponseStatusException.class, () -> service.historial(user, "otra-persona"));
        verify(repository, org.mockito.Mockito.never()).findByConversationIdOrderByCreatedAtAsc("otra-persona");
    }

    @Test
    void listaConversacionesSoloEsParaAdmin() {
        var user = new UsernamePasswordAuthenticationToken(
                "cliente", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThrows(ResponseStatusException.class, () -> service.conversaciones(user));
        verify(repository, org.mockito.Mockito.never()).findConversationSummaries();
    }
}
