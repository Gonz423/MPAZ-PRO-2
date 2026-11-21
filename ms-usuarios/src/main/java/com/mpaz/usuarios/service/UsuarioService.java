package com.mpaz.usuarios.service;

import com.mpaz.usuarios.dto.*;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.repository.UsuarioRepository;
import com.mpaz.usuarios.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Usuario registrar(RegistroRequest req) {
        if (repo.existsByCorreo(req.getCorreo()))
            throw new RuntimeException("Ya existe un usuario con ese correo");
        return repo.save(Usuario.builder()
                .nombre(req.getNombre()).correo(req.getCorreo())
                .password(passwordEncoder.encode(req.getPassword()))
                .rol(req.getRol().toUpperCase()).curso(req.getCurso()).activo(true).build());
    }

    public LoginResponse login(LoginRequest req) {
        Usuario u = repo.findByCorreo(req.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));
        if (!u.getActivo() || !passwordEncoder.matches(req.getPassword(), u.getPassword()))
            throw new RuntimeException("Credenciales invalidas");
        return new LoginResponse(jwtUtil.generarToken(u.getCorreo(), u.getRol()), u.getRol(), u.getNombre());
    }

    public List<Usuario> listarTodos()              { return repo.findAll(); }
    public List<Usuario> listarPorRol(String rol)   { return repo.findByRol(rol.toUpperCase()); }
    public List<Usuario> listarPorCurso(String c)   { return repo.findByCurso(c); }
    public Usuario obtenerPorId(Long id)             { return repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado")); }

    public Usuario actualizar(Long id, RegistroRequest req) {
        Usuario u = obtenerPorId(id);
        u.setNombre(req.getNombre()); u.setCurso(req.getCurso());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        return repo.save(u);
    }

    public void eliminar(Long id) { obtenerPorId(id); repo.deleteById(id); }
}
