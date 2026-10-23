package com.tienda.usuarios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contrasenas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contrasena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "hash_contrasena", nullable = false, length = 100)
    private String hashContrasena;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    @PreUpdate
    protected void touch() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
