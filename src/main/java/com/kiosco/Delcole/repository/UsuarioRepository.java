package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    boolean existsByUsername(String username);

    long count();
}
