package com.mpaz.usuarios.config;

import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("Base de datos ya contiene usuarios. Se omite carga inicial.");
            return;
        }

        log.info("Inicializando datos semilla en ms-usuarios...");

        // 1. Estudiante de prueba
        Usuario estudiante = Usuario.builder()
                .username("estudiante")
                .password(passwordEncoder.encode("1234"))
                .nombreCompleto("Mateo Silva")
                .email("estudiante@marcelapaz.cl")
                .rol(Rol.ESTUDIANTE)
                .curso("2° Básico A")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        usuarioRepository.save(estudiante);

        // 2. Docente de prueba
        Usuario docente = Usuario.builder()
                .username("docente")
                .password(passwordEncoder.encode("1234"))
                .nombreCompleto("Profesora Marcela")
                .email("docente@marcelapaz.cl")
                .rol(Rol.DOCENTE)
                .curso("2° Básico A")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        usuarioRepository.save(docente);

        // 3. UTP de prueba
        Usuario utp = Usuario.builder()
                .username("utp")
                .password(passwordEncoder.encode("1234"))
                .nombreCompleto("Jefe UTP")
                .email("utp@marcelapaz.cl")
                .rol(Rol.UTP)
                .curso("Coordinación Académica")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        usuarioRepository.save(utp);

        log.info("Datos semilla creados exitosamente: estudiante (ID: {}), docente (ID: {}), utp (ID: {})",
                estudiante.getId(), docente.getId(), utp.getId());
    }
}
