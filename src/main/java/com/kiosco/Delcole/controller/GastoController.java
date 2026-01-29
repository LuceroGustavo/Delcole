package com.kiosco.Delcole.controller;

import com.kiosco.Delcole.model.Gasto;
import com.kiosco.Delcole.service.GastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GastoController {

    private final GastoService gastoService;

    @GetMapping
    public ResponseEntity<List<Gasto>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(gastoService.gastosEntre(desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> porId(@PathVariable Long id) {
        return gastoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Gasto> crear(@RequestBody Gasto gasto) {
        Gasto guardado = gastoService.guardar(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!gastoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        gastoService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Gasto eliminado correctamente"));
    }
}
