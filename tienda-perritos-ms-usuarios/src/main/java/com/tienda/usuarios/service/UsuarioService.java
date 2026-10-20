package com.tienda.usuarios.service;

import com.tienda.usuarios.dto.UsuarioDto;
import com.tienda.usuarios.entity.Contrasena;
import com.tienda.usuarios.entity.Usuario;
import com.tienda.usuarios.exception.UsuarioNotFoundException;
import com.tienda.usuarios.repository.ContrasenaRepository;
import com.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContrasenaRepository contrasenaRepository;
    private final PasswordEncoder passwordEncoder;

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
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario con email " + email + " no encontrado"));
        return toDto(usuario);
    }

    /**
     * Obtiene un usuario por username
     */
    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorUsername(String username) {
        log.debug("Obteniendo usuario con username: {}", username);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario " + username + " no encontrado"));
        return toDto(usuario);
    }

    /**
     * Obtiene un usuario por email o lo aprovisiona en el primer acceso
     */
    @Transactional
    public UsuarioDto obtenerOCrear(String email, String nombre, String apellido) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .map(this::toDto)
                .orElseGet(() -> crear(null, email, nombre, apellido, null, null));
    }

    /**
     * Crea un nuevo usuario. Si se entrega contraseña, se guarda solo su hash BCrypt.
     */
    @Transactional
    public UsuarioDto crear(String username, String email, String nombre, String apellido, String roles, String password) {
        String usernameFinal = username != null && !username.isBlank()
                ? username.trim()
                : email.split("@")[0];
        if (usernameFinal.length() > 50) {
            usernameFinal = usernameFinal.substring(0, 50);
        }
        log.info("Creando nuevo usuario: {}", usernameFinal);

        if (usuarioRepository.existsByUsername(usernameFinal)) {
            throw new IllegalArgumentException("El username ya existe");
        }
        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("El email ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(usernameFinal)
                .email(email)
                .nombre(nombre)
                .apellido(apellido)
                .rol(rolDesde(roles))
                .build();

        Usuario saved = usuarioRepository.save(usuario);

        if (password != null && !password.isBlank()) {
            contrasenaRepository.save(Contrasena.builder()
                    .usuario(saved)
                    .hashContrasena(passwordEncoder.encode(password))
                    .build());
        }
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
            usuario.setRol(rolDesde(roles));
        }

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", id);
        
        return toDto(updated);
    }

    /**
     * Elimina un usuario (su contraseña se elimina por ON DELETE CASCADE)
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: {}", id);
    }

    private String rolDesde(String roles) {
        return roles != null && roles.contains("ROLE_ADMIN") ? Usuario.ROL_ADMIN : Usuario.ROL_USER;
    }

    /**
     * Convierte Usuario a UsuarioDto. "roles" conserva el formato histórico ("ROLE_ADMIN,ROLE_USER")
     * para no romper al frontend; "rol" es el valor almacenado (ADMIN o USER).
     */
    private UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .roles(Usuario.ROL_ADMIN.equals(usuario.getRol()) ? "ROLE_ADMIN,ROLE_USER" : "ROLE_USER")
                .rol(usuario.getRol())
                .build();
    }
}
