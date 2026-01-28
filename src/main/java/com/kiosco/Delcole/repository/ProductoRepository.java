package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
