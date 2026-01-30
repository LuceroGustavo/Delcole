package com.kiosco.Delcole.controller;

import com.kiosco.Delcole.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/marcas-rubros")
@RequiredArgsConstructor
public class MarcasRubrosController {

    private final ProductoService productoService;

    @GetMapping
    public String listar(Model model) {
        List<String> marcas = productoService.listarMarcas();
        List<String> rubros = productoService.listarRubros();
        model.addAttribute("marcas", marcas);
        model.addAttribute("rubros", rubros);
        model.addAttribute("titulo", "Marcas y Rubros");
        return "marcas-rubros";
    }

    @PostMapping("/marca/crear")
    public String crearMarca(@RequestParam String nombre, RedirectAttributes ra) {
        if (nombre == null || nombre.isBlank()) {
            ra.addFlashAttribute("errorMarca", "Ingresá el nombre de la marca.");
            return "redirect:/marcas-rubros#marcas";
        }
        String error = productoService.crearMarca(nombre.trim());
        if (error != null) {
            ra.addFlashAttribute("errorMarca", error);
            return "redirect:/marcas-rubros#marcas";
        }
        ra.addFlashAttribute("okMarca", "Marca \"" + nombre.trim() + "\" creada. Aparecerá en el listado y al cargar productos.");
        return "redirect:/marcas-rubros#marcas";
    }

    @PostMapping("/marca/renombrar")
    public String renombrarMarca(
            @RequestParam String vieja,
            @RequestParam String nueva,
            RedirectAttributes ra) {
        if (vieja == null || vieja.isBlank() || nueva == null || nueva.isBlank()) {
            ra.addFlashAttribute("errorMarca", "Ingresá el nombre actual y el nuevo.");
            return "redirect:/marcas-rubros#marcas";
        }
        int n = productoService.renombrarMarca(vieja.trim(), nueva.trim());
        ra.addFlashAttribute("okMarca", "Marca actualizada en " + n + " producto(s).");
        return "redirect:/marcas-rubros#marcas";
    }

    @PostMapping("/marca/eliminar")
    public String eliminarMarca(@RequestParam String marca, RedirectAttributes ra) {
        if (marca == null || marca.isBlank()) {
            ra.addFlashAttribute("errorMarca", "Seleccioná una marca.");
            return "redirect:/marcas-rubros#marcas";
        }
        int n = productoService.quitarMarca(marca.trim());
        ra.addFlashAttribute("okMarca", "Marca quitada de " + n + " producto(s). Quedan como \"Sin marca\".");
        return "redirect:/marcas-rubros#marcas";
    }

    @PostMapping("/rubro/crear")
    public String crearRubro(@RequestParam String nombre, RedirectAttributes ra) {
        if (nombre == null || nombre.isBlank()) {
            ra.addFlashAttribute("errorRubro", "Ingresá el nombre del rubro.");
            return "redirect:/marcas-rubros#rubros";
        }
        String error = productoService.crearRubro(nombre.trim());
        if (error != null) {
            ra.addFlashAttribute("errorRubro", error);
            return "redirect:/marcas-rubros#rubros";
        }
        ra.addFlashAttribute("okRubro", "Rubro \"" + nombre.trim() + "\" creado. Aparecerá en el listado y al cargar productos.");
        return "redirect:/marcas-rubros#rubros";
    }

    @PostMapping("/rubro/renombrar")
    public String renombrarRubro(
            @RequestParam String viejo,
            @RequestParam String nuevo,
            RedirectAttributes ra) {
        if (viejo == null || viejo.isBlank() || nuevo == null || nuevo.isBlank()) {
            ra.addFlashAttribute("errorRubro", "Ingresá el nombre actual y el nuevo.");
            return "redirect:/marcas-rubros#rubros";
        }
        int n = productoService.renombrarRubro(viejo.trim(), nuevo.trim());
        ra.addFlashAttribute("okRubro", "Rubro actualizado en " + n + " producto(s).");
        return "redirect:/marcas-rubros#rubros";
    }

    @PostMapping("/rubro/eliminar")
    public String eliminarRubro(@RequestParam String rubro, RedirectAttributes ra) {
        if (rubro == null || rubro.isBlank()) {
            ra.addFlashAttribute("errorRubro", "Seleccioná un rubro.");
            return "redirect:/marcas-rubros#rubros";
        }
        int n = productoService.quitarRubro(rubro.trim());
        ra.addFlashAttribute("okRubro", "Rubro quitado de " + n + " producto(s). Quedan como \"Sin rubro\".");
        return "redirect:/marcas-rubros#rubros";
    }
}
