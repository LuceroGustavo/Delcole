package com.kiosco.Delcole.service;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public Optional<Producto> buscarPorCodigoBarra(String codigoBarra) {
        if (codigoBarra == null || codigoBarra.isBlank()) {
            return Optional.empty();
        }
        return productoRepository.findByCodigoBarra(codigoBarra.trim());
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarActivos();
        }
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre.trim());
    }

    public List<String> listarMarcas() {
        return productoRepository.findDistinctMarcas();
    }

    public List<String> listarRubros() {
        return productoRepository.findDistinctRubros();
    }

    /** Lista productos activos con filtros opcionales (nombre, marca, rubro, solo con stock bajo). */
    public List<Producto> listarConFiltros(String nombre, String marca, String rubro, Boolean soloStockBajo) {
        String n = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
        String m = (marca != null && !marca.isBlank()) ? marca.trim() : null;
        String r = (rubro != null && !rubro.isBlank()) ? rubro.trim() : null;
        return productoRepository.findConFiltros(n, m, r, Boolean.TRUE.equals(soloStockBajo) ? true : null);
    }

    @Transactional
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    /** Suma cantidad al stock actual. */
    @Transactional
    public Producto sumarStock(Long productoId, int cantidad) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a sumar debe ser positiva");
        }
        p.setStockActual(p.getStockActual() + cantidad);
        return productoRepository.save(p);
    }

    /** Resta cantidad del stock (venta o salida). Devuelve el producto actualizado o lanza si no hay stock suficiente. */
    @Transactional
    public Producto restarStock(Long productoId, int cantidad) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a restar debe ser positiva");
        }
        int nuevoStock = p.getStockActual() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalStateException("Stock insuficiente de " + p.getNombre() + ". Actual: " + p.getStockActual());
        }
        p.setStockActual(nuevoStock);
        return productoRepository.save(p);
    }

    /** Baja lógica: marca el producto como inactivo. */
    @Transactional
    public Producto darDeBaja(Long productoId) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        p.setActivo(false);
        return productoRepository.save(p);
    }
}
