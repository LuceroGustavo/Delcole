package com.kiosco.Delcole.service;

import com.kiosco.Delcole.model.Rol;
import com.kiosco.Delcole.model.Usuario;
import com.kiosco.Delcole.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD_PLAIN = "admin";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username);
    }

    /** Crea el usuario admin por defecto si no existe ningún usuario en el sistema. */
    @Transactional
    public void crearAdminSiNoExiste() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        if (usuarioRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }
        Usuario admin = Usuario.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD_PLAIN))
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
        usuarioRepository.save(admin);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /** Cambia la contraseña del usuario actual. Devuelve null si ok, mensaje de error si falla. */
    @Transactional
    public String cambiarPassword(String username, String contraseñaActual, String contraseñaNueva) {
        Usuario u = usuarioRepository.findByUsernameAndActivoTrue(username).orElse(null);
        if (u == null) return "Usuario no encontrado.";
        if (!passwordEncoder.matches(contraseñaActual, u.getPassword())) return "Contraseña actual incorrecta.";
        if (contraseñaNueva == null || contraseñaNueva.length() < 4) return "La nueva contraseña debe tener al menos 4 caracteres.";
        u.setPassword(passwordEncoder.encode(contraseñaNueva));
        usuarioRepository.save(u);
        return null;
    }

    /** Crea un nuevo usuario (solo ADMIN). Devuelve null si ok, mensaje de error si falla. */
    @Transactional
    public String crearUsuario(String username, String password, Rol rol) {
        if (username == null || username.isBlank()) return "El usuario es obligatorio.";
        if (usuarioRepository.existsByUsername(username.trim())) return "Ese nombre de usuario ya existe.";
        if (password == null || password.length() < 4) return "La contraseña debe tener al menos 4 caracteres.";
        Usuario u = Usuario.builder()
                .username(username.trim())
                .password(passwordEncoder.encode(password))
                .rol(rol != null ? rol : Rol.VENTAS_CARGA)
                .activo(true)
                .huellaHabilitada(false)
                .build();
        usuarioRepository.save(u);
        return null;
    }

    @Transactional
    public void actualizarHuellaHabilitada(String username, boolean habilitada) {
        usuarioRepository.findByUsernameAndActivoTrue(username).ifPresent(u -> {
            u.setHuellaHabilitada(habilitada);
            usuarioRepository.save(u);
        });
    }
}
