package com.kiosco.Delcole.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kiosco.Delcole.model.Producto;
import com.kiosco.Delcole.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductoService productoService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Producto> productos = productoService.listarActivos();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Inicio");
        return "home";
    }

    @GetMapping("/escanear")
    public String escanear(Model model) {
        model.addAttribute("titulo", "Escanear producto");
        return "escanear";
    }
}
