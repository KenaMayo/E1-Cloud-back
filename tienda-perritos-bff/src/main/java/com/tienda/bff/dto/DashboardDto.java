package com.tienda.bff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private UsuarioDto usuario;
    private Integer totalProductos;
    private Long totalUsuarios;
}
