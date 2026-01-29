package com.kiosco.Delcole.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoWebController {

    private final ProductoService productoService;

    @GetMapping
    public String listar(Model model) {
        List<Producto> productos = productoService.listarActivos();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Productos");
        return "producto-listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Agregar producto");
        return "producto-form";
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
