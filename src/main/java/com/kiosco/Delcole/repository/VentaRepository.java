package com.kiosco.Delcole.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kiosco.Delcole.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fechaHora >= :desde AND v.fechaHora < :hasta")
    java.math.BigDecimal sumTotalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaHora >= :desde AND v.fechaHora < :hasta")
    long countEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
