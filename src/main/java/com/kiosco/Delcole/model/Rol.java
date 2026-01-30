package com.kiosco.Delcole.model;

/**
 * Roles para control de acceso. Permite restringir vistas (ej. Finanzas solo ADMIN).
 */
public enum Rol {
    /** Acceso total: ventas, stock, caja, finanzas. */
    ADMIN,
    /** Solo carga de productos / stock (sin ventas ni finanzas). */
    CARGA,
    /** Ventas y carga (sin finanzas). */
    VENTAS_CARGA
}
