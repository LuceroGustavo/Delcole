package com.kiosco.Delcole.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoWebController {

    private final ProductoService productoService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String rubro,
            @RequestParam(required = false) Boolean soloStockBajo,
            @RequestParam(required = false) String codigoBarra,
            Model model,
            RedirectAttributes redirectAttributes) {
        // Búsqueda solo por código de barras: ir al producto si existe
        if (codigoBarra != null && !codigoBarra.isBlank()
                && (nombre == null || nombre.isBlank())
                && (marca == null || marca.isBlank())
                && (rubro == null || rubro.isBlank())
                && !Boolean.TRUE.equals(soloStockBajo)) {
            return productoService.buscarPorCodigoBarra(codigoBarra.trim())
                    .map(p -> "redirect:/productos/" + p.getId())
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("codigoNoEncontrado", codigoBarra.trim());
                        return "redirect:/productos";
                    });
        }
        List<Producto> productos = productoService.listarConFiltros(nombre, marca, rubro, soloStockBajo);
        boolean filtrosAplicados = (nombre != null && !nombre.isBlank()) || (marca != null && !marca.isBlank())
                || (rubro != null && !rubro.isBlank()) || Boolean.TRUE.equals(soloStockBajo);
        model.addAttribute("productos", productos);
        model.addAttribute("marcas", productoService.listarMarcas());
        model.addAttribute("rubros", productoService.listarRubros());
        model.addAttribute("filtroNombre", nombre != null ? nombre : "");
        model.addAttribute("filtroMarca", marca != null ? marca : "");
        model.addAttribute("filtroRubro", rubro != null ? rubro : "");
        model.addAttribute("filtroSoloStockBajo", Boolean.TRUE.equals(soloStockBajo));
        model.addAttribute("filtrosAplicados", filtrosAplicados);
        model.addAttribute("titulo", "Productos");
        return "producto-listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Agregar producto");
        return "producto-form";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        return productoService.buscarPorId(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", "Editar: " + p.getNombre());
                    return "producto-form";
                })
                .orElse("redirect:/productos");
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        return productoService.buscarPorId(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", p.getNombre());
                    return "producto-detalle";
                })
                .orElse("redirect:/stock");
    }
}
