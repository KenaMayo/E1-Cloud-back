package com.tienda.usuarios.service;

import com.tienda.usuarios.dto.UsuarioDto;
import com.tienda.usuarios.entity.Usuario;
import com.tienda.usuarios.exception.UsuarioNotFoundException;
import com.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioDto> obtenerTodos() {
        log.debug("Obteniendo todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorId(Long id) {
        log.debug("Obteniendo usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
        return toDto(usuario);
    }

    /**
     * Obtiene un usuario por email
     */
    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorEmail(String email) {
        log.debug("Obteniendo usuario con email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario con email " + email + " no encontrado"));
        return toDto(usuario);
    }

    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public UsuarioDto crear(String email, String nombre, String apellido, String roles) {
        log.info("Creando nuevo usuario: {}", email);
        
        Usuario usuario = Usuario.builder()
                .email(email)
                .nombre(nombre)
                .apellido(apellido)
                .roles(roles != null ? roles : "ROLE_USER")
                .build();

        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario creado con ID: {}", saved.getId());
        
        return toDto(saved);
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public UsuarioDto actualizar(Long id, String nombre, String apellido, String roles) {
        log.info("Actualizando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        if (roles != null) {
            usuario.setRoles(roles);
        }

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", id);
        
        return toDto(updated);
    }

    /**
     * Elimina un usuario
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: {}", id);
    }

    /**
     * Convierte Usuario a UsuarioDto
     */
    private UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .roles(usuario.getRoles())
                .build();
    }
}
