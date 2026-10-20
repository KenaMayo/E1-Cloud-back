package com.tienda.bff.controller;

import com.tienda.bff.client.UsuariosClient;
import com.tienda.bff.dto.AuthResponse;
import com.tienda.bff.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

// Login por base de datos: el BFF delega la validación de credenciales en ms-usuarios.
// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft (versión MSAL al final del archivo)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String MENSAJE_LOGIN = "Usuario o contraseña incorrectos";

    private final UsuariosClient usuariosClient;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse respuesta = usuariosClient.login(request).block();
            return ResponseEntity.ok(respuesta);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", MENSAJE_LOGIN));
            }
            throw e;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual(Authentication authentication) {
        return usuariosClient.authMe((String) authentication.getCredentials())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}

// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
package com.tienda.bff.controller;

import com.tienda.bff.client.UsuariosClient;
import com.tienda.bff.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuariosClient usuariosClient;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual(Authentication authentication) {
        log.info("Obteniendo información del usuario autenticado");

        String token = ((Jwt) authentication.getPrincipal()).getTokenValue();

        return usuariosClient.obtenerUsuarioActual(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }
}
*/
