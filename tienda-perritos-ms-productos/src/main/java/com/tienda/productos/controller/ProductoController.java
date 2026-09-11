package com.tienda.productos.controller;

import com.tienda.productos.dto.CreateProductoRequest;
import com.tienda.productos.dto.ProductoDto;
import com.tienda.productos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private final ProductoService productoService;

    /**
     * GET /api/v1/productos
     * Obtiene todos los productos
     */
    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerTodos(
            @RequestParam(required = false) String nombre) {
        
        List<ProductoDto> productos;
        
        if (nombre != null && !nombre.isEmpty()) {
            productos = productoService.buscarPorNombre(nombre);
            log.info("Búsqueda de productos por nombre: {}", nombre);
        } else {
            productos = productoService.obtenerTodos();
            log.info("Obteniendo todos los productos");
        }
        
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/v1/productos/{id}
     * Obtiene un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo producto con ID: {}", id);
        ProductoDto producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * POST /api/v1/productos
     * Crea un nuevo producto
     */
    @PostMapping
    public ResponseEntity<ProductoDto> crear(@Valid @RequestBody CreateProductoRequest request) {
        log.info("Crear nuevo producto: {}", request.getNombre());
        ProductoDto producto = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * PUT /api/v1/productos/{id}
     * Actualiza un producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductoRequest request) {
        
        log.info("Actualizar producto con ID: {}", id);
        ProductoDto producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(producto);
    }

    /**
     * DELETE /api/v1/productos/{id}
     * Elimina un producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Eliminar producto con ID: {}", id);
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
