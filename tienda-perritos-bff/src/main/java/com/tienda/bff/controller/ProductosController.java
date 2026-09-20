package com.tienda.bff.controller;

import com.tienda.bff.client.ProductosClient;
import com.tienda.bff.dto.ProductoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductosController {

    private final ProductosClient productosClient;

    @GetMapping
        public ResponseEntity<List<ProductoDto>> obtenerTodos(
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Obteniendo todos los productos");

                List<ProductoDto> productos = productosClient.obtenerTodosProductos(authorizationHeader)
                .collectList()
                .block();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerPorId(
            @PathVariable Long id,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Obteniendo producto con ID: {}", id);

                return productosClient.obtenerProducto(id, authorizationHeader)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(
            @RequestBody ProductoDto producto,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Crear nuevo producto");

                return productosClient.crearProducto(producto, authorizationHeader)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p))
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .block();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoDto producto,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Actualizar producto con ID: {}", id);

                return productosClient.actualizarProducto(id, producto, authorizationHeader)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Eliminar producto con ID: {}", id);

                return productosClient.eliminarProducto(id, authorizationHeader)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .block();
    }
}
