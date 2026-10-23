package com.tienda.bff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private String roles;
    private String rol;

    // Solo entrada (creación de usuarios): el microservicio nunca la devuelve
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
}
