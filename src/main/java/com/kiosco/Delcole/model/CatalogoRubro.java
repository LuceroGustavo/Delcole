package com.kiosco.Delcole.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "catalogo_rubro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoRubro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
}
