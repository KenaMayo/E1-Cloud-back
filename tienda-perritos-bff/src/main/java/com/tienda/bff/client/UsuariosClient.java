package com.tienda.bff.client;

import com.tienda.bff.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuariosClient {

    private final WebClient webClient;

    @Value("${microservices.usuarios.url}")
    private String usuariosUrl;

    public Mono<UsuarioDto> obtenerUsuarioActual(String token) {
        return webClient.get()
                .uri(usuariosUrl + "/usuarios/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .doOnError(e -> log.error("Error obteniendo usuario actual: {}", e.getMessage()));
    }

    public Mono<UsuarioDto> obtenerUsuario(Long id, String token) {
        return webClient.get()
                .uri(usuariosUrl + "/usuarios/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .doOnError(e -> log.error("Error obteniendo usuario {}: {}", id, e.getMessage()));
    }

    public Flux<UsuarioDto> obtenerTodosUsuarios(String token) {
        return webClient.get()
                .uri(usuariosUrl + "/usuarios")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(UsuarioDto.class)
                .doOnError(e -> log.error("Error obteniendo usuarios: {}", e.getMessage()));
    }

    public Mono<UsuarioDto> crearUsuario(UsuarioDto usuario, String token) {
        return webClient.post()
                .uri(usuariosUrl + "/usuarios")
                .header("Authorization", "Bearer " + token)
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .doOnError(e -> log.error("Error creando usuario: {}", e.getMessage()));
    }

    public Mono<UsuarioDto> actualizarUsuarioActual(UsuarioDto usuario, String token) {
        return webClient.put()
                .uri(usuariosUrl + "/usuarios/me")
                .header("Authorization", "Bearer " + token)
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .doOnError(e -> log.error("Error actualizando usuario actual: {}", e.getMessage()));
    }

    public Mono<UsuarioDto> actualizarUsuario(Long id, UsuarioDto usuario, String token) {
        return webClient.put()
                .uri(usuariosUrl + "/usuarios/{id}", id)
                .header("Authorization", "Bearer " + token)
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .doOnError(e -> log.error("Error actualizando usuario {}: {}", id, e.getMessage()));
    }

    public Mono<Void> eliminarUsuario(Long id, String token) {
        return webClient.delete()
                .uri(usuariosUrl + "/usuarios/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("Error eliminando usuario {}: {}", id, e.getMessage()));
    }
}
