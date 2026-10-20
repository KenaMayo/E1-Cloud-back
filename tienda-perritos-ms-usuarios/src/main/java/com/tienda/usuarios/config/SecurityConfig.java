package com.tienda.usuarios.config;

import com.tienda.usuarios.security.JwtAuthenticationFilter;
import com.tienda.usuarios.security.JwtService;
import com.tienda.usuarios.security.UsuarioDetailsService;
// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
import com.tienda.usuarios.security.UsuarioRolesConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
*/
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
/*
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
*/
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
// [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
// @RequiredArgsConstructor
public class SecurityConfig {

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    /*
    private final UsuarioRolesConverter usuarioRolesConverter;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${azure.ad.audience}")
    private String expectedAudience;
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // Login por base de datos con JWT propio (reemplazo temporal de MSAL)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService,
                                                   UsuarioDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/logout").permitAll()
                        .requestMatchers("/auth/me", "/usuarios/me").authenticated()
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> escribir(response, HttpStatus.UNAUTHORIZED, "No autenticado"))
                        .accessDeniedHandler((request, response, e) -> escribir(response, HttpStatus.FORBIDDEN, "Acceso denegado")))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void escribir(HttpServletResponse response, HttpStatus status, String mensaje)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status.value() + ",\"message\":\"" + mensaje + "\"}");
    }

    // [MSAL-DISABLED] motivo: fallas en Azure, pendiente soporte Microsoft
    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/usuarios/me").authenticated()
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(usuarioRolesConverter)));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = jwt ->
                jwt.getAudience().contains(expectedAudience)
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "El token no fue emitido para esta audiencia (aud)", null));

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuerUri), audienceValidator));

        return jwtDecoder;
    }
    */
}
