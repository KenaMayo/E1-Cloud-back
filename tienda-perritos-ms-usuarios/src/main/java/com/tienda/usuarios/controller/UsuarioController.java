package com.tienda.usuarios.controller;

import com.tienda.usuarios.dto.CreateUsuarioRequest;
import com.tienda.usuarios.dto.UsuarioDto;
import com.tienda.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
*/
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Value("${app.default-user.email}")
    private String defaultUserEmail;

    @Value("${app.default-user.nombre}")
    private String defaultUserNombre;

    @Value("${app.default-user.apellido}")
    private String defaultUserApellido;

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    /*
    private String emailDe(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaimAsString("preferred_username");
        return email != null ? email : jwt.getClaimAsString("email");
    }
    */

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    /*
    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> obtenerUsuarioActual(Authentication authentication) {
        log.info("Obteniendo usuario actual");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = emailDe(authentication);
        String nombreCompleto = jwt.getClaimAsString("name");
        String[] partes = nombreCompleto == null ? new String[0] : nombreCompleto.trim().split("\\s+", 2);

        String nombre = jwt.getClaimAsString("given_name");
        if (nombre == null) {
            nombre = partes.length > 0 ? partes[0] : email.split("@")[0];
        }
        String apellido = jwt.getClaimAsString("family_name");
        if (apellido == null) {
            apellido = partes.length > 1 ? partes[1] : "";
        }

        return ResponseEntity.ok(usuarioService.obtenerOCrear(email, nombre, apellido));
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
    */

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    // Sin token no hay identidad: se usa el usuario por defecto configurado en app.default-user.*
    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> obtenerUsuarioActual() {
        log.info("Obteniendo usuario actual (usuario por defecto)");
        return ResponseEntity.ok(usuarioService.obtenerOCrear(defaultUserEmail, defaultUserNombre, defaultUserApellido));
    }

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    @PutMapping("/me")
    public ResponseEntity<UsuarioDto> actualizarUsuarioActual(
            @Valid @RequestBody CreateUsuarioRequest request) {
        log.info("Actualizar usuario actual (usuario por defecto)");
        UsuarioDto usuarioActual = usuarioService.obtenerPorEmail(defaultUserEmail);
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

