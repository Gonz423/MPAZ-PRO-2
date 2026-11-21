package com.mpaz.usuarios.repository;

import com.mpaz.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByRol(String rol);
    List<Usuario> findByCurso(String curso);
    boolean existsByCorreo(String correo);
}
