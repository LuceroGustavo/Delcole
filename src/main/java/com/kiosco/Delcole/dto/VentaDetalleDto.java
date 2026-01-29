package com.kiosco.Delcole.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDetalleDto {

    private Long id;
    private LocalDateTime fechaHora;
    private BigDecimal total;
    private List<VentaItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaItemDto {
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }
}
