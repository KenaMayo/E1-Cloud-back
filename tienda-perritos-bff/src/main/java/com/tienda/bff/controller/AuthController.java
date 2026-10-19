package com.tienda.bff.controller;

import com.tienda.bff.client.UsuariosClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
// Versión temporal sin autenticación: no hay JWT, se invoca el microservicio sin token (null).
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuariosClient usuariosClient;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual() {
        log.info("Obteniendo información del usuario por defecto");

        return usuariosClient.obtenerUsuarioActual(null)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
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
