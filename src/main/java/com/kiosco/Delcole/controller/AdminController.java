package com.kiosco.Delcole.controller;

import com.kiosco.Delcole.model.Rol;
import com.kiosco.Delcole.model.Usuario;
import com.kiosco.Delcole.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String admin(Model model, @AuthenticationPrincipal UserDetails user) {
        if (user == null) return "redirect:/login";
        Usuario current = usuarioService.buscarPorUsername(user.getUsername()).orElse(null);
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarioActual", current);
        model.addAttribute("huellaActiva", current != null && Boolean.TRUE.equals(current.getHuellaHabilitada()));
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("titulo", "Administración");
        return "admin";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String contraseñaActual,
            @RequestParam String contraseñaNueva,
            @RequestParam String contraseñaNueva2,
            RedirectAttributes ra) {
        if (user == null) return "redirect:/login";
        if (!contraseñaNueva.equals(contraseñaNueva2)) {
            ra.addFlashAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
            return "redirect:/admin#cambiar-password";
        }
        String error = usuarioService.cambiarPassword(user.getUsername(), contraseñaActual, contraseñaNueva);
        if (error != null) {
            ra.addFlashAttribute("errorPassword", error);
            return "redirect:/admin#cambiar-password";
        }
        ra.addFlashAttribute("okPassword", "Contraseña actualizada.");
        return "redirect:/admin#cambiar-password";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam Rol rol,
            RedirectAttributes ra) {
        if (!password.equals(password2)) {
            ra.addFlashAttribute("errorUsuario", "Las contraseñas no coinciden.");
            return "redirect:/admin#crear-usuario";
        }
        String error = usuarioService.crearUsuario(username, password, rol);
        if (error != null) {
            ra.addFlashAttribute("errorUsuario", error);
            return "redirect:/admin#crear-usuario";
        }
        ra.addFlashAttribute("okUsuario", "Usuario creado.");
        return "redirect:/admin#crear-usuario";
    }

    @PostMapping("/huella")
    public String toggleHuella(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "false") boolean habilitada,
            RedirectAttributes ra) {
        if (user != null) {
            usuarioService.actualizarHuellaHabilitada(user.getUsername(), habilitada);
            ra.addFlashAttribute("okHuella", habilitada ? "Ingreso por huella activado (en la app móvil)." : "Ingreso por huella desactivado.");
        }
        return "redirect:/admin#huella";
    }
}
