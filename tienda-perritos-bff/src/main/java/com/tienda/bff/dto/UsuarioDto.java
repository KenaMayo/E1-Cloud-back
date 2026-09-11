package com.tienda.bff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String roles;
}
