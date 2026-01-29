package com.kiosco.Delcole.controller;

import com.kiosco.Delcole.service.GastoService;
import com.kiosco.Delcole.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/finanzas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinanzasController {

    private final VentaService ventaService;
    private final GastoService gastoService;

    /**
     * Resumen financiero de un período: ventas, gastos y ganancia neta.
     * Parámetros desde/hasta opcionales en formato YYYY-MM-DD.
     * Si no se especifican, se usa el mes actual.
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        // Si no se especifican fechas, usar el mes actual
        if (desde == null && hasta == null) {
            LocalDate hoy = LocalDate.now();
            desde = hoy.withDayOfMonth(1);
            hasta = hoy;
        }
        if (desde == null) {
            desde = LocalDate.of(2000, 1, 1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }

        // Ventas del período
        BigDecimal totalVentas = ventaService.sumarVentasEntre(desde, hasta);

        // Gastos del período
        BigDecimal gastosPeriodo = gastoService.sumarGastosEntre(desde, hasta);

        // Ganancia neta
        BigDecimal gananciaNeta = totalVentas.subtract(gastosPeriodo);

        return ResponseEntity.ok(Map.of(
                "desde", desde.toString(),
                "hasta", hasta.toString(),
                "ventasPeriodo", totalVentas,
                "gastosPeriodo", gastosPeriodo,
                "gananciaNeta", gananciaNeta
        ));
    }
}
