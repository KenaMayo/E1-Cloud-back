package com.tienda.usuarios.security;

import com.tienda.usuarios.entity.Usuario;
import com.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // La autenticación con contraseña se hace en AuthService; aquí solo se cargan identidad y rol.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsername())
                .password("")
                .authorities("ROLE_" + usuario.getRol())
                .disabled(!usuario.isActivo())
                .build();
    }
}
