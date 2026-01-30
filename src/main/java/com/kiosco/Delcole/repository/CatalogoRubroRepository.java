package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.CatalogoRubro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogoRubroRepository extends JpaRepository<CatalogoRubro, Long> {

    List<CatalogoRubro> findAllByOrderByNombreAsc();

    Optional<CatalogoRubro> findByNombreIgnoreCase(String nombre);
}
