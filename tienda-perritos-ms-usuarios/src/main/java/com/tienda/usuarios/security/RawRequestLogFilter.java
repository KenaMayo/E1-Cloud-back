package com.tienda.usuarios.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

// Filtro temporal de diagnóstico: se ejecuta antes que cualquier otro filtro (incluida Spring Security)
@Configuration
@Slf4j
public class RawRequestLogFilter {

    @Bean
    public FilterRegistrationBean<Filter> rawRequestLogFilterRegistration() {
        FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
        reg.setFilter((ServletRequest request, ServletResponse response, FilterChain chain) -> {
            HttpServletRequest http = (HttpServletRequest) request;
            String headers = Collections.list(http.getHeaderNames()).stream()
                    .map(h -> h + "=" + http.getHeader(h))
                    .collect(Collectors.joining(" | "));
            log.info("RAW-DEBUG-TEMP {} {} headers=[{}]", http.getMethod(), http.getRequestURI(), headers);
            chain.doFilter(request, response);
        });
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
