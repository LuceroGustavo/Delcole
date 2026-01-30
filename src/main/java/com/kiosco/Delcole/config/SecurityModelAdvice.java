package com.kiosco.Delcole.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Añade atributos de seguridad al modelo para las vistas (ej. ocultar Finanzas si no es ADMIN).
 */
@ControllerAdvice
public class SecurityModelAdvice {

    @ModelAttribute
    public void addSecurityAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null
                && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("usuarioLogueado", auth != null && auth.isAuthenticated() ? auth.getName() : null);
    }
}
