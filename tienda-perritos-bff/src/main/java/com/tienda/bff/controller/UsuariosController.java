package com.tienda.bff.controller;

import com.tienda.bff.client.UsuariosClient;
import com.tienda.bff.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuariosController {

    private final UsuariosClient usuariosClient;

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> obtenerUsuarioActual(Authentication authentication) {
        log.info("Obteniendo usuario actual");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.obtenerUsuarioActual(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDto> actualizarUsuarioActual(
            @RequestBody UsuarioDto usuario,
            Authentication authentication) {
        log.info("Actualizar usuario actual");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.actualizarUsuarioActual(usuario, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> obtenerTodos(Authentication authentication) {
        log.info("Obteniendo todos los usuarios");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        List<UsuarioDto> usuarios = usuariosClient.obtenerTodosUsuarios(token)
                .collectList()
                .block();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Obteniendo usuario con ID: {}", id);
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.obtenerUsuario(id, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(
            @RequestBody UsuarioDto usuario,
            Authentication authentication) {
        log.info("Crear nuevo usuario");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.crearUsuario(usuario, token)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u))
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .block();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioDto usuario,
            Authentication authentication) {
        log.info("Actualizar usuario con ID: {}", id);
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.actualizarUsuario(id, usuario, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Eliminar usuario con ID: {}", id);
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return usuariosClient.eliminarUsuario(id, token)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }
}
