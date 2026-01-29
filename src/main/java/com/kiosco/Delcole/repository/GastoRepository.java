package com.kiosco.Delcole.repository;

import com.kiosco.Delcole.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.fecha >= :desde AND g.fecha <= :hasta")
    java.math.BigDecimal sumMontoEntre(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
