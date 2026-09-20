package com.tienda.bff.controller;

import com.tienda.bff.client.ProductosClient;
import com.tienda.bff.client.UsuariosClient;
import com.tienda.bff.dto.DashboardDto;
import com.tienda.bff.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final ProductosClient productosClient;
    private final UsuariosClient usuariosClient;

    @GetMapping
    public ResponseEntity<?> obtenerDashboard(Authentication authentication) {
        log.info("Obteniendo datos del dashboard");

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        Mono<UsuarioDto> usuarioMono = usuariosClient.obtenerUsuarioActual(token);

        Mono<Integer> productosCountMono = productosClient.obtenerTodosProductos(token)
                .collectList()
                .map(List::size)
                .onErrorReturn(0);

        Mono<Long> usuariosCountMono = usuariosClient.obtenerTodosUsuarios(token)
                .collectList()
                .map(list -> (long) list.size())
                .onErrorReturn(0L);

        return Mono.zip(usuarioMono, productosCountMono, usuariosCountMono)
                .map(tuple -> DashboardDto.builder()
                        .usuario(tuple.getT1())
                        .totalProductos(tuple.getT2())
                        .totalUsuarios(tuple.getT3())
                        .build())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }
}

