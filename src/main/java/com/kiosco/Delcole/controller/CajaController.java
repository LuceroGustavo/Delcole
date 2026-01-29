package com.kiosco.Delcole.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kiosco.Delcole.dto.VentaDetalleDto;
import com.kiosco.Delcole.service.VentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CajaController {

    private final VentaService ventaService;

    /**
     * Resumen de ventas de un día: total vendido y cantidad de operaciones.
     * Parámetro fecha opcional en formato YYYY-MM-DD; si no se envía, se usa hoy.
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Object> resumen = ventaService.resumenPorDia(fecha);
        return ResponseEntity.ok(resumen);
    }

    /**
     * Lista de ventas de un día, con sus ítems (DTO para JSON limpio, sin referencias circulares).
     */
    @GetMapping("/ventas")
    public ResponseEntity<List<VentaDetalleDto>> ventasDelDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ventaService.ventasDelDiaDto(fecha));
    }
}
