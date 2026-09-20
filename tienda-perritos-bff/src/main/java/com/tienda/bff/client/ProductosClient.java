package com.tienda.bff.client;

import com.tienda.bff.dto.ProductoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductosClient {

    private final WebClient webClient;

    @Value("${microservices.productos.url}")
    private String productosUrl;

    public Mono<ProductoDto> obtenerProducto(Long id, String authorizationHeader) {
        return webClient.get()
                .uri(productosUrl + "/productos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(ProductoDto.class)
                .doOnError(e -> log.error("Error obteniendo producto {}: {}", id, e.getMessage()));
    }

    public Flux<ProductoDto> obtenerTodosProductos(String authorizationHeader) {
        return webClient.get()
                .uri(productosUrl + "/productos")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToFlux(ProductoDto.class)
                .doOnError(e -> log.error("Error obteniendo productos: {}", e.getMessage()));
    }

    public Mono<ProductoDto> crearProducto(ProductoDto producto, String authorizationHeader) {
        return webClient.post()
                .uri(productosUrl + "/productos")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(ProductoDto.class)
                .doOnError(e -> log.error("Error creando producto: {}", e.getMessage()));
    }

    public Mono<ProductoDto> actualizarProducto(Long id, ProductoDto producto, String authorizationHeader) {
        return webClient.put()
                .uri(productosUrl + "/productos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(ProductoDto.class)
                .doOnError(e -> log.error("Error actualizando producto {}: {}", id, e.getMessage()));
    }

    public Mono<Void> eliminarProducto(Long id, String authorizationHeader) {
        return webClient.delete()
                .uri(productosUrl + "/productos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("Error eliminando producto {}: {}", id, e.getMessage()));
    }
}
