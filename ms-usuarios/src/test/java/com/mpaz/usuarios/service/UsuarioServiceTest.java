package com.mpaz.usuarios.service;

import com.mpaz.usuarios.dto.LoginRequest;
import com.mpaz.usuarios.dto.LoginResponse;
import com.mpaz.usuarios.dto.RegisterRequest;
import com.mpaz.usuarios.dto.UsuarioResponse;
import com.mpaz.usuarios.dto.UsuarioUpdateRequest;
import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import com.mpaz.usuarios.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository repo;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;
    @InjectMocks UsuarioService service;

    private static Usuario usuario(Long id, String username, Rol rol, boolean activo) {
        return Usuario.builder().id(id).username(username).password("HASH").nombreCompleto("Nombre " + username)
                .email(username + "@marcelapaz.cl").rol(rol).curso("2° Básico A").activo(activo).build();
    }

    // ---------- login ----------

    @Test
    @DisplayName("login con credenciales correctas devuelve token y datos del perfil")
    void loginExitoso() {
        Usuario u = usuario(1L, "estudiante", Rol.ESTUDIANTE, true);
        when(repo.findByUsernameIgnoreCase("estudiante")).thenReturn(Optional.of(u));
        when(encoder.matches("1234", "HASH")).thenReturn(true);
        when(jwtService.generateToken(u)).thenReturn("TOKEN");

        LoginResponse r = service.login(new LoginRequest("  estudiante ", "1234"));

        assertThat(r.getToken()).isEqualTo("TOKEN");
        assertThat(r.getRol()).isEqualTo("ESTUDIANTE");
        assertThat(r.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login acepta el correo electronico como identificador")
    void loginPorEmail() {
        Usuario u = usuario(2L, "docente", Rol.DOCENTE, true);
        when(repo.findByUsernameIgnoreCase("docente@marcelapaz.cl")).thenReturn(Optional.empty());
        when(repo.findByEmailIgnoreCase("docente@marcelapaz.cl")).thenReturn(Optional.of(u));
        when(encoder.matches("1234", "HASH")).thenReturn(true);
        when(jwtService.generateToken(u)).thenReturn("T");

        assertThat(service.login(new LoginRequest("docente@marcelapaz.cl", "1234")).getUsername()).isEqualTo("docente");
    }

    @Test
    @DisplayName("login con usuario inexistente o clave incorrecta lanza BadCredentialsException")
    void loginFallido() {
        when(repo.findByUsernameIgnoreCase("nadie")).thenReturn(Optional.empty());
        when(repo.findByEmailIgnoreCase("nadie")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.login(new LoginRequest("nadie", "x"))).isInstanceOf(BadCredentialsException.class);

        when(repo.findByUsernameIgnoreCase("estudiante")).thenReturn(Optional.of(usuario(1L, "estudiante", Rol.ESTUDIANTE, true)));
        when(encoder.matches("mala", "HASH")).thenReturn(false);
        assertThatThrownBy(() -> service.login(new LoginRequest("estudiante", "mala"))).isInstanceOf(BadCredentialsException.class);

        when(repo.findByUsernameIgnoreCase("")).thenReturn(Optional.empty());
        when(repo.findByEmailIgnoreCase("")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.login(new LoginRequest(null, "x"))).isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("login de un usuario inactivo devuelve 401")
    void loginInactivo() {
        when(repo.findByUsernameIgnoreCase("utp")).thenReturn(Optional.of(usuario(3L, "utp", Rol.UTP, false)));
        when(encoder.matches("1234", "HASH")).thenReturn(true);

        assertThatThrownBy(() -> service.login(new LoginRequest("utp", "1234")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    // ---------- register ----------

    @Test
    @DisplayName("register cifra la clave y guarda al usuario como activo")
    void registerExitoso() {
        when(repo.existsByUsernameIgnoreCase("nuevo")).thenReturn(false);
        when(repo.existsByEmail("nuevo@x.cl")).thenReturn(false);
        when(encoder.encode("clave")).thenReturn("CIFRADA");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UsuarioResponse r = service.register(new RegisterRequest(" nuevo ", "clave", " Juan ", " nuevo@x.cl ", Rol.DOCENTE, " 2°A "));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("CIFRADA");
        assertThat(r.getId()).isEqualTo(10L);
        assertThat(r.getUsername()).isEqualTo("nuevo");
        assertThat(r.getEmail()).isEqualTo("nuevo@x.cl");
        assertThat(r.getRol()).isEqualTo(Rol.DOCENTE);
        assertThat(r.getActivo()).isTrue();
    }

    @Test
    @DisplayName("register sin email, rol ni curso usa los valores por defecto")
    void registerDefaults() {
        when(repo.existsByUsernameIgnoreCase("min")).thenReturn(false);
        when(encoder.encode("c")).thenReturn("X");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse r = service.register(new RegisterRequest("min", "c", "Min", " ", null, null));

        assertThat(r.getEmail()).isNull();
        assertThat(r.getRol()).isEqualTo(Rol.ESTUDIANTE);
        assertThat(r.getCurso()).isNull();
    }

    @Test
    @DisplayName("register con username o email duplicado devuelve 400")
    void registerDuplicado() {
        when(repo.existsByUsernameIgnoreCase("estudiante")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("estudiante", "c", "N", null, null, null)))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("usuario ya está registrado");

        when(repo.existsByUsernameIgnoreCase("otro")).thenReturn(false);
        when(repo.existsByEmail("e@x.cl")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("otro", "c", "N", "e@x.cl", null, null)))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("correo");
        verify(repo, never()).save(any());
    }

    // ---------- consultas ----------

    @Test
    @DisplayName("consultas de usuarios devuelven DTOs sin contraseña")
    void consultas() {
        Usuario e = usuario(1L, "estudiante", Rol.ESTUDIANTE, true);
        Usuario d = usuario(2L, "docente", Rol.DOCENTE, false);
        when(repo.findByActivoTrue()).thenReturn(List.of(e));
        when(repo.findAll()).thenReturn(List.of(e, d));
        when(repo.findByRolAndActivoTrue(Rol.DOCENTE)).thenReturn(List.of(d));
        when(repo.findById(1L)).thenReturn(Optional.of(e));

        assertThat(service.getUsuariosActivos()).hasSize(1);
        assertThat(service.getAllUsuarios()).hasSize(2);
        assertThat(service.getUsuariosByRol("docente")).extracting(UsuarioResponse::getRol).containsExactly(Rol.DOCENTE);
        assertThat(service.getUsuarioById(1L).getUsername()).isEqualTo("estudiante");
    }

    @Test
    @DisplayName("getUsuarioById, update y deactivate con id inexistente devuelven 404")
    void noEncontrado() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUsuarioById(99L)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.updateUsuario(99L, new UsuarioUpdateRequest())).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.deactivateUsuario(99L)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("getUsuariosByRol con rol desconocido lanza IllegalArgumentException")
    void rolInvalido() {
        assertThatThrownBy(() -> service.getUsuariosByRol("DIRECTOR")).isInstanceOf(IllegalArgumentException.class);
    }

    // ---------- update / deactivate ----------

    @Test
    @DisplayName("updateUsuario modifica solo los campos enviados y cifra la nueva clave")
    void update() {
        Usuario u = usuario(1L, "estudiante", Rol.ESTUDIANTE, true);
        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.existsByEmail("nuevo@x.cl")).thenReturn(false);
        when(encoder.encode("nueva")).thenReturn("NUEVO_HASH");
        when(repo.save(u)).thenReturn(u);

        UsuarioResponse r = service.updateUsuario(1L,
                new UsuarioUpdateRequest(" Mateo S. ", " nuevo@x.cl ", Rol.DOCENTE, " 2°B ", false, "nueva"));

        assertThat(r.getNombreCompleto()).isEqualTo("Mateo S.");
        assertThat(r.getEmail()).isEqualTo("nuevo@x.cl");
        assertThat(r.getRol()).isEqualTo(Rol.DOCENTE);
        assertThat(r.getCurso()).isEqualTo("2°B");
        assertThat(r.getActivo()).isFalse();
        assertThat(u.getPassword()).isEqualTo("NUEVO_HASH");
    }

    @Test
    @DisplayName("updateUsuario con email vacio lo elimina y sin cambios conserva los datos")
    void updateParcial() {
        Usuario u = usuario(1L, "estudiante", Rol.ESTUDIANTE, true);
        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.existsByEmail("")).thenReturn(false);
        when(repo.save(u)).thenReturn(u);

        UsuarioResponse r = service.updateUsuario(1L, new UsuarioUpdateRequest(" ", "", null, null, null, " "));

        assertThat(r.getEmail()).isNull();
        assertThat(r.getNombreCompleto()).isEqualTo("Nombre estudiante");
        assertThat(u.getPassword()).isEqualTo("HASH");
        verify(encoder, never()).encode(any());
    }

    @Test
    @DisplayName("updateUsuario con un email ya usado por otro usuario devuelve 400")
    void updateEmailDuplicado() {
        when(repo.findById(1L)).thenReturn(Optional.of(usuario(1L, "estudiante", Rol.ESTUDIANTE, true)));
        when(repo.existsByEmail("docente@marcelapaz.cl")).thenReturn(true);

        UsuarioUpdateRequest req = new UsuarioUpdateRequest(null, "docente@marcelapaz.cl", null, null, null, null);
        assertThatThrownBy(() -> service.updateUsuario(1L, req)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("deactivateUsuario hace borrado logico")
    void deactivate() {
        Usuario u = usuario(1L, "estudiante", Rol.ESTUDIANTE, true);
        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(u)).thenReturn(u);

        assertThat(service.deactivateUsuario(1L).getActivo()).isFalse();
    }
}
