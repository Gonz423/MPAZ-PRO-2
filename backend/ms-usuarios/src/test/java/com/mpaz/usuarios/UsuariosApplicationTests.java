package com.mpaz.usuarios;

import com.mpaz.usuarios.dto.LoginRequest;
import com.mpaz.usuarios.dto.LoginResponse;
import com.mpaz.usuarios.dto.RegisterRequest;
import com.mpaz.usuarios.dto.UsuarioResponse;
import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import com.mpaz.usuarios.security.JwtService;
import com.mpaz.usuarios.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
class UsuariosApplicationTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Cargar contexto de la aplicación")
    void contextLoads() {
        Assertions.assertNotNull(usuarioService);
        Assertions.assertNotNull(usuarioRepository);
    }

    @Test
    @DisplayName("Verificar datos semilla cargados por DataInitializer")
    void verificarSemillas() {
        Assertions.assertTrue(usuarioRepository.count() >= 3);
        Assertions.assertTrue(usuarioRepository.existsByUsernameIgnoreCase("estudiante"));
        Assertions.assertTrue(usuarioRepository.existsByUsernameIgnoreCase("docente"));
        Assertions.assertTrue(usuarioRepository.existsByUsernameIgnoreCase("utp"));
    }

    @Test
    @DisplayName("Validar contraseñas cifradas con BCrypt")
    void verificarBCryptPassword() {
        Usuario estudiante = usuarioRepository.findByUsernameIgnoreCase("estudiante")
                .orElseThrow();
        Assertions.assertTrue(passwordEncoder.matches("1234", estudiante.getPassword()));
        Assertions.assertNotEquals("1234", estudiante.getPassword());
    }

    @Test
    @DisplayName("Login exitoso y generación de token JWT")
    void testLoginExitoso() {
        LoginRequest req = LoginRequest.builder()
                .username("estudiante")
                .password("1234")
                .build();

        LoginResponse resp = usuarioService.login(req);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.getToken());
        Assertions.assertEquals("estudiante", resp.getUsername());
        Assertions.assertEquals("ESTUDIANTE", resp.getRol());

        Assertions.assertTrue(jwtService.isTokenValid(resp.getToken()));
        Assertions.assertEquals("estudiante", jwtService.extractUsername(resp.getToken()));
    }

    @Test
    @DisplayName("Login fallido con contraseña incorrecta")
    void testLoginFallido() {
        LoginRequest req = LoginRequest.builder()
                .username("estudiante")
                .password("password_incorrecta")
                .build();

        Assertions.assertThrows(BadCredentialsException.class, () -> usuarioService.login(req));
    }

    @Test
    @DisplayName("Registro de nuevo usuario")
    void testRegistroUsuario() {
        RegisterRequest req = RegisterRequest.builder()
                .username("alumno_nuevo")
                .password("claveSegura123")
                .nombreCompleto("Juan Perez")
                .email("juan.perez@escuela.cl")
                .rol(Rol.ESTUDIANTE)
                .curso("2° Básico A")
                .build();

        UsuarioResponse resp = usuarioService.register(req);

        Assertions.assertNotNull(resp.getId());
        Assertions.assertEquals("alumno_nuevo", resp.getUsername());
        Assertions.assertTrue(resp.getActivo());

        Usuario guardado = usuarioRepository.findById(resp.getId()).orElseThrow();
        Assertions.assertTrue(passwordEncoder.matches("claveSegura123", guardado.getPassword()));
    }

    @Test
    @DisplayName("Filtrar usuarios por rol")
    void testFiltrarPorRol() {
        List<UsuarioResponse> docentes = usuarioService.getUsuariosByRol("DOCENTE");
        Assertions.assertFalse(docentes.isEmpty());
        Assertions.assertTrue(docentes.stream().allMatch(u -> u.getRol() == Rol.DOCENTE));
    }

    @Test
    @DisplayName("Borrado lógico de usuario")
    void testBorradoLogico() {
        Usuario user = usuarioRepository.findByUsernameIgnoreCase("utp").orElseThrow();
        UsuarioResponse resp = usuarioService.deactivateUsuario(user.getId());

        Assertions.assertFalse(resp.getActivo());
    }
}
