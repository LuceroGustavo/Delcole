package com.kiosco.Delcole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("titulo", "Inicio");
        return "home";
    }

    @GetMapping("/vender")
    public String vender(Model model) {
        model.addAttribute("titulo", "Vender");
        return "vender";
    }

    @GetMapping("/stock")
    public String stock(Model model) {
        model.addAttribute("titulo", "Stock");
        return "stock";
    }

    @GetMapping("/caja")
    public String caja(Model model) {
        model.addAttribute("titulo", "Cierre de caja");
        return "caja";
    }

    @GetMapping("/finanzas")
    public String finanzas(Model model) {
        model.addAttribute("titulo", "Finanzas");
        return "finanzas";
    }

    @GetMapping("/escanear")
    public String escanear(Model model) {
        model.addAttribute("titulo", "Escanear producto");
        return "escanear";
    }
}
