package com.tienda.usuarios.service;

import com.tienda.usuarios.dto.AuthResponse;
import com.tienda.usuarios.entity.Usuario;
import com.tienda.usuarios.repository.ContrasenaRepository;
import com.tienda.usuarios.repository.UsuarioRepository;
import com.tienda.usuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    // Hash válido de una contraseña arbitraria: iguala el tiempo de respuesta cuando el usuario no existe
    private static final String HASH_DUMMY = "$2a$10$OG6W706jLYbEYF/W6DNeEOuhgMmTPK1Z1G0bdDTeH4U.PqiBJ5VEC";

    private final UsuarioRepository usuarioRepository;
    private final ContrasenaRepository contrasenaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public Optional<AuthResponse> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<Usuario> usuario = usuarioRepository.findByUsername(username.trim());
        Optional<String> hash = usuario.flatMap(u -> contrasenaRepository.findByUsuarioId(u.getId()))
                .map(c -> c.getHashContrasena());

        boolean coincide = passwordEncoder.matches(password, hash.orElse(HASH_DUMMY));

        if (usuario.isEmpty() || hash.isEmpty() || !coincide || !usuario.get().isActivo()) {
            return Optional.empty();
        }

        Usuario u = usuario.get();
        return Optional.of(AuthResponse.builder()
                .token(jwtService.generar(u.getUsername(), u.getRol()))
                .username(u.getUsername())
                .rol(u.getRol())
                .build());
    }
}
