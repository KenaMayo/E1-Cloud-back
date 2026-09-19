package com.tienda.usuarios.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String authHeader = request.getHeader("Authorization");
            log.info("DEBUG-TEMP Authorization header recibido: [{}] longitud={}", authHeader, authHeader == null ? -1 : authHeader.length());

            if (auth != null && auth.isAuthenticated()) {
                log.debug("JWT Token validado para usuario: {}", auth.getName());
                log.debug("Authorities: {}", auth.getAuthorities());
            } else {
                log.info("DEBUG-TEMP No autenticado. auth={}", auth);
            }

        } catch (Exception e) {
            log.error("Error procesando JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
