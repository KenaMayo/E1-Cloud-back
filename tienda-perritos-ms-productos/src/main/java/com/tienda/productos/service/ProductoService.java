package com.tienda.productos.service;

import com.tienda.productos.dto.CreateProductoRequest;
import com.tienda.productos.dto.ProductoDto;
import com.tienda.productos.entity.Producto;
import com.tienda.productos.exception.ProductoNotFoundException;
import com.tienda.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos
     */
    @Transactional(readOnly = true)
    public List<ProductoDto> obtenerTodos() {
        log.debug("Obteniendo todos los productos");
        return productoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un producto por ID
     */
    @Transactional(readOnly = true)
    public ProductoDto obtenerPorId(Long id) {
        log.debug("Obteniendo producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return toDto(producto);
    }

    /**
     * Crea un nuevo producto
     */
    @Transactional
    public ProductoDto crear(CreateProductoRequest request) {
        log.info("Creando nuevo producto: {}", request.getNombre());
        
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .build();

        Producto saved = productoRepository.save(producto);
        log.info("Producto creado con ID: {}", saved.getId());
        
        return toDto(saved);
    }

    /**
     * Actualiza un producto existente
     */
    @Transactional
    public ProductoDto actualizar(Long id, CreateProductoRequest request) {
        log.info("Actualizando producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());

        Producto updated = productoRepository.save(producto);
        log.info("Producto actualizado: {}", id);
        
        return toDto(updated);
    }

    /**
     * Elimina un producto
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        productoRepository.delete(producto);
        log.info("Producto eliminado: {}", id);
    }

    /**
     * Busca productos por nombre
     */
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorNombre(String nombre) {
        log.debug("Buscando productos por nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte Producto a ProductoDto
     */
    private ProductoDto toDto(Producto producto) {
        return ProductoDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .build();
    }
}
