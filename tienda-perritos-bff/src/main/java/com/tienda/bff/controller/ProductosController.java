package com.tienda.bff.controller;

import com.tienda.bff.client.ProductosClient;
import com.tienda.bff.dto.ProductoDto;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductosController {

    private final ProductosClient productosClient;

    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerTodos(Authentication authentication) {
        log.info("Obteniendo todos los productos");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        List<ProductoDto> productos = productosClient.obtenerTodosProductos(token)
                .collectList()
                .block();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Obteniendo producto con ID: {}", id);
       Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return productosClient.obtenerProducto(id, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(
            @RequestBody ProductoDto producto,
            Authentication authentication) {
        log.info("Crear nuevo producto");
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return productosClient.crearProducto(producto, token)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p))
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .block();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoDto producto,
            Authentication authentication) {
        log.info("Actualizar producto con ID: {}", id);
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return productosClient.actualizarProducto(id, producto, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Eliminar producto con ID: {}", id);
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        return productosClient.eliminarProducto(id, token)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }
}
