package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.CatalogoMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogoMarcaRepository extends JpaRepository<CatalogoMarca, Long> {

    List<CatalogoMarca> findAllByOrderByNombreAsc();

    Optional<CatalogoMarca> findByNombreIgnoreCase(String nombre);
}
