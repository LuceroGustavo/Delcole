package com.kiosco.Delcole.service;

import com.kiosco.Delcole.model.CatalogoMarca;
import com.kiosco.Delcole.model.CatalogoRubro;
import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.repository.CatalogoMarcaRepository;
import com.kiosco.Delcole.repository.CatalogoRubroRepository;
import com.kiosco.Delcole.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CatalogoMarcaRepository catalogoMarcaRepository;
    private final CatalogoRubroRepository catalogoRubroRepository;

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

    /** Marcas: las que usan productos + las del catálogo (para crear nuevas desde Marcas y Rubros). */
    public List<String> listarMarcas() {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(productoRepository.findDistinctMarcas());
        set.addAll(catalogoMarcaRepository.findAllByOrderByNombreAsc().stream()
                .map(CatalogoMarca::getNombre).collect(Collectors.toList()));
        return new ArrayList<>(set);
    }

    /** Rubros: los que usan productos + los del catálogo. */
    public List<String> listarRubros() {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(productoRepository.findDistinctRubros());
        set.addAll(catalogoRubroRepository.findAllByOrderByNombreAsc().stream()
                .map(CatalogoRubro::getNombre).collect(Collectors.toList()));
        return new ArrayList<>(set);
    }

    @Transactional
    public String crearMarca(String nombre) {
        if (nombre == null || nombre.isBlank()) return "El nombre no puede estar vacío.";
        String n = nombre.trim();
        if (catalogoMarcaRepository.findByNombreIgnoreCase(n).isPresent()) return "Esa marca ya existe.";
        if (productoRepository.findDistinctMarcas().stream().anyMatch(m -> m != null && n.equalsIgnoreCase(m)))
            return "Esa marca ya existe (en productos).";
        catalogoMarcaRepository.save(CatalogoMarca.builder().nombre(n).build());
        return null;
    }

    @Transactional
    public String crearRubro(String nombre) {
        if (nombre == null || nombre.isBlank()) return "El nombre no puede estar vacío.";
        String n = nombre.trim();
        if (catalogoRubroRepository.findByNombreIgnoreCase(n).isPresent()) return "Ese rubro ya existe.";
        if (productoRepository.findDistinctRubros().stream().anyMatch(r -> r != null && n.equalsIgnoreCase(r)))
            return "Ese rubro ya existe (en productos).";
        catalogoRubroRepository.save(CatalogoRubro.builder().nombre(n).build());
        return null;
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

    /** Renombra una marca en todos los productos que la usan. También actualiza el catálogo si existe. */
    @Transactional
    public int renombrarMarca(String vieja, String nueva) {
        if (vieja == null || vieja.isBlank() || nueva == null || nueva.isBlank()) return 0;
        String v = vieja.trim();
        String n = nueva.trim();
        if (v.equalsIgnoreCase(n)) return 0;
        catalogoMarcaRepository.findByNombreIgnoreCase(v).ifPresent(c -> { c.setNombre(n); catalogoMarcaRepository.save(c); });
        return productoRepository.actualizarMarcaEnProductos(v, n);
    }

    /** Renombra un rubro en todos los productos que lo usan. También actualiza el catálogo si existe. */
    @Transactional
    public int renombrarRubro(String viejo, String nuevo) {
        if (viejo == null || viejo.isBlank() || nuevo == null || nuevo.isBlank()) return 0;
        String v = viejo.trim();
        String n = nuevo.trim();
        if (v.equalsIgnoreCase(n)) return 0;
        catalogoRubroRepository.findByNombreIgnoreCase(v).ifPresent(c -> { c.setNombre(n); catalogoRubroRepository.save(c); });
        return productoRepository.actualizarRubroEnProductos(v, n);
    }

    /** Quita la marca de todos los productos que la tienen (quedan "sin marca"). También la quita del catálogo si existe. */
    @Transactional
    public int quitarMarca(String marca) {
        if (marca == null || marca.isBlank()) return 0;
        String m = marca.trim();
        catalogoMarcaRepository.findByNombreIgnoreCase(m).ifPresent(catalogoMarcaRepository::delete);
        return productoRepository.quitarMarcaEnProductos(m);
    }

    /** Quita el rubro de todos los productos que lo tienen (quedan "sin rubro"). También lo quita del catálogo si existe. */
    @Transactional
    public int quitarRubro(String rubro) {
        if (rubro == null || rubro.isBlank()) return 0;
        String r = rubro.trim();
        catalogoRubroRepository.findByNombreIgnoreCase(r).ifPresent(catalogoRubroRepository::delete);
        return productoRepository.quitarRubroEnProductos(r);
    }
}
