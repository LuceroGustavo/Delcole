package com.kiosco.Delcole.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.service.ProductoService;
import com.kiosco.Delcole.service.VentaService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class VentaController {

    private final ProductoService productoService;
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody List<ItemVentaRequest> items) {
        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La venta debe tener al menos un producto"));
        }

        // Validaciones básicas
        for (ItemVentaRequest item : items) {
            if (item.getProductoId() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cada ítem debe tener productoId y cantidad positiva"));
            }
        }

        // Primera pasada: validar existencia y stock suficiente
        for (ItemVentaRequest item : items) {
            Optional<Producto> opt = productoService.buscarPorId(item.getProductoId());
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Producto no encontrado: " + item.getProductoId()));
            }
            Producto p = opt.get();
            if (p.getStockActual() == null || p.getStockActual() < item.getCantidad()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Stock insuficiente de " + p.getNombre()
                                + ". Actual: " + p.getStockActual()));
            }
        }

        // Segunda pasada: descontar stock, calcular total y armar DTOs para guardar venta
        BigDecimal total = BigDecimal.ZERO;
        java.util.List<VentaService.ItemConPrecioDto> itemsParaGuardar = new java.util.ArrayList<>();
        for (ItemVentaRequest item : items) {
            Producto pActualizado = productoService.restarStock(item.getProductoId(), item.getCantidad());
            BigDecimal precio = pActualizado.getPrecioVenta() != null ? pActualizado.getPrecioVenta() : BigDecimal.ZERO;
            total = total.add(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
            itemsParaGuardar.add(new VentaService.ItemConPrecioDto(
                    item.getProductoId(),
                    item.getCantidad(),
                    precio
            ));
        }

        ventaService.guardarVenta(itemsParaGuardar, total);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Venta registrada correctamente");
        respuesta.put("total", total);
        respuesta.put("items", items);

        return ResponseEntity.ok(respuesta);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVentaRequest {
        private Long productoId;
        private Integer cantidad;
    }
}

