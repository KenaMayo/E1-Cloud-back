package com.tienda.usuarios.security;

// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
import com.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UsuarioRolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaimAsString("preferred_username");
        if (email == null) {
            email = jwt.getClaimAsString("email");
        }

        List<GrantedAuthority> authorities = email == null
                ? List.of()
                : usuarioRepository.findByEmailIgnoreCase(email)
                        .map(usuario -> toAuthorities(usuario.getRoles()))
                        .orElse(List.of());

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private List<GrantedAuthority> toAuthorities(String roles) {
        if (roles == null) {
            return List.of();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(rol -> !rol.isEmpty())
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}
*/
