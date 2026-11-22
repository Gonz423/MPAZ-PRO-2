package com.mpaz.usuarios.service;

import com.mpaz.usuarios.dto.*;
import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import com.mpaz.usuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario inactivo o deshabilitado");
        }

        String token = jwtService.generateToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .curso(usuario.getCurso())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public UsuarioResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (usuarioRepository.existsByEmail(request.getEmail().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado");
            }
        }

        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto().trim())
                .email(request.getEmail() != null && !request.getEmail().isBlank() ? request.getEmail().trim() : null)
                .rol(request.getRol() != null ? request.getRol() : Rol.ESTUDIANTE)
                .curso(request.getCurso() != null ? request.getCurso().trim() : null)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente: {} con ID {}", saved.getUsername(), saved.getId());
        return UsuarioResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosByRol(String rolStr) {
        Rol rol = Rol.fromString(rolStr);
        return usuarioRepository.findByRolAndActivoTrue(rol).stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponse updateUsuario(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

        if (request.getNombreCompleto() != null && !request.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(request.getNombreCompleto().trim());
        }

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está en uso");
            }
            usuario.setEmail(newEmail.isBlank() ? null : newEmail);
        }

        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }

        if (request.getCurso() != null) {
            usuario.setCurso(request.getCurso().trim());
        }

        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Usuario con ID {} actualizado exitosamente", updated.getId());
        return UsuarioResponse.fromEntity(updated);
    }

    @Transactional
    public UsuarioResponse deactivateUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

        usuario.setActivo(false);
        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario con ID {} desactivado (borrado lógico)", saved.getId());
        return UsuarioResponse.fromEntity(saved);
    }
}
