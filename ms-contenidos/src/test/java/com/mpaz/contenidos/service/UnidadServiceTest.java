package com.mpaz.contenidos.service;

import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.exception.RecursoNoEncontradoException;
import com.mpaz.contenidos.model.Unidad;
import com.mpaz.contenidos.repository.UnidadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnidadServiceTest {

    @Mock UnidadRepository repo;
    @InjectMocks UnidadService service;

    private static Unidad unidad(Long id, String materia) {
        return Unidad.builder().id(id).materiaId(materia).numero(1).titulo("Unidad " + id).objetivoAprendizaje("OA 1").build();
    }

    @Test
    @DisplayName("listar devuelve todas las unidades como DTO")
    void listar() {
        when(repo.findAll()).thenReturn(List.of(unidad(1L, "lenguaje"), unidad(2L, "matematica")));

        List<UnidadResponse> lista = service.listar();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).titulo()).isEqualTo("Unidad 1");
    }

    @Test
    @DisplayName("listarPorMateria filtra por materia")
    void listarPorMateria() {
        when(repo.findByMateriaId("ciencias")).thenReturn(List.of(unidad(3L, "ciencias")));

        assertThat(service.listarPorMateria("ciencias")).extracting(UnidadResponse::materiaId).containsExactly("ciencias");
    }

    @Test
    @DisplayName("obtener con id existente devuelve la unidad")
    void obtenerExistente() {
        when(repo.findById(1L)).thenReturn(Optional.of(unidad(1L, "lenguaje")));

        assertThat(service.obtener(1L).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtener con id inexistente lanza RecursoNoEncontradoException")
    void obtenerInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crear guarda la entidad y devuelve el DTO con id")
    void crear() {
        when(repo.save(any(Unidad.class))).thenAnswer(inv -> {
            Unidad u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UnidadResponse r = service.crear(new UnidadRequest("historia", 2, "Pueblos originarios", "OA 2"));

        assertThat(r.id()).isEqualTo(10L);
        assertThat(r.materiaId()).isEqualTo("historia");
        assertThat(r.numero()).isEqualTo(2);
    }

    @Test
    @DisplayName("actualizar modifica los campos de la unidad existente")
    void actualizar() {
        Unidad existente = unidad(1L, "lenguaje");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        UnidadResponse r = service.actualizar(1L, new UnidadRequest("lenguaje", 3, "Nuevo titulo", "OA 9"));

        assertThat(r.titulo()).isEqualTo("Nuevo titulo");
        assertThat(r.numero()).isEqualTo(3);
        assertThat(r.objetivoAprendizaje()).isEqualTo("OA 9");
    }

    @Test
    @DisplayName("eliminar borra la unidad existente")
    void eliminar() {
        Unidad existente = unidad(1L, "lenguaje");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        service.eliminar(1L);

        verify(repo).delete(existente);
    }

    @Test
    @DisplayName("eliminar con id inexistente no borra nada")
    void eliminarInexistente() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(5L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(repo, never()).delete(any());
    }
}
