package com.tienda.bff.security;

// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
import com.tienda.bff.client.UsuariosClient;
import com.tienda.bff.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UsuarioRolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final UsuariosClient usuariosClient;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = usuariosClient.obtenerUsuarioActual(jwt.getTokenValue())
                .map(UsuarioDto::getRoles)
                .map(this::toAuthorities)
                .onErrorReturn(List.of())
                .blockOptional(TIMEOUT)
                .orElse(List.of());

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private List<GrantedAuthority> toAuthorities(String roles) {
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(rol -> !rol.isEmpty())
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}
*/
