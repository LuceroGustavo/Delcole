package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.marca IS NOT NULL AND TRIM(p.marca) != '' ORDER BY p.marca")
    List<String> findDistinctMarcas();

    @Query("SELECT DISTINCT p.rubro FROM Producto p WHERE p.rubro IS NOT NULL AND TRIM(p.rubro) != '' ORDER BY p.rubro")
    List<String> findDistinctRubros();

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:nombre IS NULL OR :nombre = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:marca IS NULL OR :marca = '' OR p.marca = :marca) " +
           "AND (:rubro IS NULL OR :rubro = '' OR p.rubro = :rubro) " +
           "AND (:soloStockBajo IS NULL OR :soloStockBajo = false OR p.stockActual < p.stockMinimo) " +
           "ORDER BY p.nombre")
    List<Producto> findConFiltros(@Param("nombre") String nombre, @Param("marca") String marca,
                                  @Param("rubro") String rubro, @Param("soloStockBajo") Boolean soloStockBajo);
}
