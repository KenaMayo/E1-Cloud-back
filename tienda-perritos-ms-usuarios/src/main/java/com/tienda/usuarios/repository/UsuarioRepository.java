package com.tienda.usuarios.repository;

import com.tienda.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
