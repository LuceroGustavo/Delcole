package com.kiosco.Delcole.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 200)
    private String concepto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TipoGasto tipo = TipoGasto.OTRO;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @PrePersist
    private void prePersist() {
        fechaAlta = LocalDateTime.now();
    }
}
