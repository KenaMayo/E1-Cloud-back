package com.tienda.usuarios.repository;

import com.tienda.usuarios.entity.Contrasena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContrasenaRepository extends JpaRepository<Contrasena, Long> {
    Optional<Contrasena> findByUsuarioId(Long usuarioId);
}
