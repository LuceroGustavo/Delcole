package com.kiosco.Delcole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) Boolean error,
            @RequestParam(value = "logout", required = false) Boolean logout,
            Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("mensajeError", "Usuario o contraseña incorrectos.");
        }
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("mensajeOk", "Sesión cerrada correctamente.");
        }
        return "login";
    }
}
