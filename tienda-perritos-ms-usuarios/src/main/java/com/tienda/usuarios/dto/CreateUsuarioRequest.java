package com.tienda.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUsuarioRequest {

    @Size(max = 50, message = "El username admite hasta 50 caracteres")
    private String username;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @Pattern(regexp = "^ROLE_(USER|ADMIN)(,ROLE_(USER|ADMIN))*$", message = "Roles válidos: ROLE_USER, ROLE_ADMIN")
    private String roles;

    // Opcional: si se envía, se guarda su hash BCrypt. Nunca se devuelve.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    private String password;
}
