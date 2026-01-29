package com.kiosco.Delcole.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiosco.Delcole.dto.VentaDetalleDto;
import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.model.Venta;
import com.kiosco.Delcole.model.VentaItem;
import com.kiosco.Delcole.repository.VentaRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;

    /**
     * Guarda una venta con sus ítems. Los productos ya deben estar validados y el stock ya descontado.
     * Cada ítem debe tener productoId, cantidad y precioUnitario.
     */
    @Transactional
    public Venta guardarVenta(List<ItemConPrecioDto> items, BigDecimal total) {
        if (items == null || items.isEmpty() || total == null) {
            throw new IllegalArgumentException("Venta debe tener ítems y total");
        }
        Venta venta = Venta.builder()
                .fechaHora(LocalDateTime.now())
                .total(total)
                .build();
        for (ItemConPrecioDto it : items) {
            Producto p = productoService.buscarPorId(it.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + it.getProductoId()));
            VentaItem vi = VentaItem.builder()
                    .venta(venta)
                    .producto(p)
                    .cantidad(it.getCantidad())
                    .precioUnitario(it.getPrecioUnitario())
                    .build();
            venta.getItems().add(vi);
        }
        return ventaRepository.save(venta);
    }

    /**
     * Resumen de ventas para un día: total vendido y cantidad de ventas.
     * fecha en formato YYYY-MM-DD; si es null se usa hoy.
     */
    public Map<String, Object> resumenPorDia(LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX).plusNanos(1);
        BigDecimal total = ventaRepository.sumTotalEntre(desde, hasta);
        long cantidad = ventaRepository.countEntre(desde, hasta);
        return Map.of(
                "fecha", fecha.toString(),
                "totalVendido", total != null ? total : BigDecimal.ZERO,
                "cantidadVentas", cantidad
        );
    }

    public List<Venta> ventasEntre(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta);
    }

    /**
     * Suma total de ventas entre dos fechas (LocalDate).
     */
    public BigDecimal sumarVentasEntre(LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(LocalTime.MAX).plusNanos(1);
        BigDecimal suma = ventaRepository.sumTotalEntre(desdeDateTime, hastaDateTime);
        return suma != null ? suma : BigDecimal.ZERO;
    }

    /**
     * Lista de ventas del día en formato DTO para la API (evita referencias circulares y lazy).
     */
    @Transactional(readOnly = true)
    public List<VentaDetalleDto> ventasDelDiaDto(LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX).plusNanos(1);
        List<Venta> ventas = ventaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta);
        return ventas.stream()
                .map(v -> VentaDetalleDto.builder()
                        .id(v.getId())
                        .fechaHora(v.getFechaHora())
                        .total(v.getTotal())
                        .items(v.getItems().stream()
                                .map(it -> new VentaDetalleDto.VentaItemDto(
                                        it.getProducto().getNombre(),
                                        it.getCantidad(),
                                        it.getPrecioUnitario()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemConPrecioDto {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }
}
