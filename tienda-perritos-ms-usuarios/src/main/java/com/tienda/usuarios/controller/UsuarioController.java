package com.tienda.usuarios.controller;

import com.tienda.usuarios.dto.CreateUsuarioRequest;
import com.tienda.usuarios.dto.UsuarioDto;
import com.tienda.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    private final UsuarioService usuarioService;

    private String emailDe(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaimAsString("preferred_username");
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> obtenerUsuarioActual(Authentication authentication) {
        log.info("Obteniendo usuario actual");
        String email = emailDe(authentication);
        UsuarioDto usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDto> actualizarUsuarioActual(
            Authentication authentication,
            @Valid @RequestBody CreateUsuarioRequest request) {
        log.info("Actualizar usuario actual");
        String email = emailDe(authentication);
        UsuarioDto usuarioActual = usuarioService.obtenerPorEmail(email);
        UsuarioDto usuario = usuarioService.actualizar(
                usuarioActual.getId(),
                request.getNombre(),
                request.getApellido(),
                null
        );
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");
        List<UsuarioDto> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDto> obtenerPorEmail(@PathVariable String email) {
        log.info("Obteniendo usuario con email: {}", email);
        UsuarioDto usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo usuario con ID: {}", id);
        UsuarioDto usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody CreateUsuarioRequest request) {
        log.info("Crear nuevo usuario: {}", request.getEmail());

        UsuarioDto usuario = usuarioService.crear(
                request.getEmail(),
                request.getNombre(),
                request.getApellido(),
                request.getRoles()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CreateUsuarioRequest request) {

        log.info("Actualizar usuario con ID: {}", id);
        UsuarioDto usuario = usuarioService.actualizar(
                id,
                request.getNombre(),
                request.getApellido(),
                request.getRoles()
        );

        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Eliminar usuario con ID: {}", id);
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

