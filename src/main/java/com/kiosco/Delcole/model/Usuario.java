package com.kiosco.Delcole.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /** Preferencia: usar huella para ingresar cuando la app móvil lo soporte. */
    @Column(name = "huella_habilitada")
    @Builder.Default
    private Boolean huellaHabilitada = false;

    /** Getters/setters explícitos para password y huella (evitan errores de IDE si Lombok no se procesa). */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getHuellaHabilitada() { return huellaHabilitada; }
    public void setHuellaHabilitada(Boolean huellaHabilitada) { this.huellaHabilitada = huellaHabilitada; }
}
