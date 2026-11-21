package com.mpaz.usuarios.config;

import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        // Usuario Docente predeterminado
        repo.save(Usuario.builder()
                .nombre("Prof. Andrea Valenzuela")
                .correo("docente@mpaz.cl")
                .password(passwordEncoder.encode("mpaz2026"))
                .rol("DOCENTE")
                .curso("2A")
                .activo(true)
                .build());

        // Usuario Estudiante predeterminado
        repo.save(Usuario.builder()
                .nombre("Martina González")
                .correo("estudiante@mpaz.cl")
                .password(passwordEncoder.encode("mpaz2026"))
                .rol("ESTUDIANTE")
                .curso("2A")
                .activo(true)
                .build());
    }
}
