package com.kiosco.Delcole.config;

import com.kiosco.Delcole.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Crea el usuario admin (admin / admin) automáticamente si no existe ningún usuario.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(ApplicationArguments args) {
        usuarioService.crearAdminSiNoExiste();
        log.info("Verificado: usuario admin existe o fue creado.");
    }
}
