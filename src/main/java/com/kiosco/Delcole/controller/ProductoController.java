package com.kiosco.Delcole.controller;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private final ProductoService productoService;

    /** Buscar producto por código de barras (para escaneo). */
    @GetMapping("/codigo/{codigoBarra}")
    public ResponseEntity<Producto> porCodigoBarra(@PathVariable String codigoBarra) {
        return productoService.buscarPorCodigoBarra(codigoBarra)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Listar marcas distintas (para menú desplegable). */
    @GetMapping("/marcas")
    public List<String> listarMarcas() {
        return productoService.listarMarcas();
    }

    /** Listar rubros distintos (para menú desplegable). */
    @GetMapping("/rubros")
    public List<String> listarRubros() {
        return productoService.listarRubros();
    }

    /** Obtener un producto por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> porId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Listar todos los productos activos, opcionalmente filtrar por nombre. */
    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String nombre) {
        return productoService.buscarPorNombre(nombre != null ? nombre : "");
    }

    /** Crear o actualizar producto. */
    @PostMapping
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /** Sumar stock. Body: { "cantidad": 5 } */
    @PostMapping("/{id}/stock/sumar")
    public ResponseEntity<?> sumarStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer cantidad = body != null ? body.get("cantidad") : null;
        if (cantidad == null || cantidad <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "cantidad debe ser un número positivo"));
        }
        try {
            Producto p = productoService.sumarStock(id, cantidad);
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Restar stock (venta/salida). Body: { "cantidad": 2 } */
    @PostMapping("/{id}/stock/restar")
    public ResponseEntity<?> restarStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer cantidad = body != null ? body.get("cantidad") : null;
        if (cantidad == null || cantidad <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "cantidad debe ser un número positivo"));
        }
        try {
            Producto p = productoService.restarStock(id, cantidad);
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    /** Baja lógica del producto. */
    @PostMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Long id) {
        try {
            Producto p = productoService.darDeBaja(id);
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
