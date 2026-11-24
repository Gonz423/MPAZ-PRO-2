package com.mpaz.contenidos.service;

import com.mpaz.contenidos.dto.EjercicioRequest;
import com.mpaz.contenidos.dto.EjercicioResponse;
import com.mpaz.contenidos.exception.RecursoNoEncontradoException;
import com.mpaz.contenidos.model.Ejercicio;
import com.mpaz.contenidos.repository.EjercicioRepository;
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
class EjercicioServiceTest {

    @Mock EjercicioRepository repo;
    @InjectMocks EjercicioService service;

    private static Ejercicio ejercicio(Long id) {
        return Ejercicio.builder().id(id).materiaId("lenguaje").unidadId(1L).titulo("Ejercicio " + id)
                .pregunta("¿Pregunta?").opciones("[\"A\",\"B\"]").respuestaCorrectaIndex(1)
                .disponible(true).esTarea(false).build();
    }

    private static EjercicioRequest request(Integer indice, Boolean disponible, Boolean esTarea) {
        return new EjercicioRequest("matematica", 2L, "Sumas", "OA 3", "2002", "¿3+4?", "[\"6\",\"7\"]",
                indice, disponible, esTarea, null);
    }

    @Test
    @DisplayName("listar devuelve todos los ejercicios")
    void listar() {
        when(repo.findAll()).thenReturn(List.of(ejercicio(1L), ejercicio(2L)));
        assertThat(service.listar()).hasSize(2);
    }

    @Test
    @DisplayName("listarPorUnidad, listarPorMateria y listarTareas delegan en el repositorio")
    void filtros() {
        when(repo.findByUnidadId(1L)).thenReturn(List.of(ejercicio(1L)));
        when(repo.findByMateriaId("lenguaje")).thenReturn(List.of(ejercicio(2L)));
        when(repo.findByEsTareaTrue()).thenReturn(List.of());

        assertThat(service.listarPorUnidad(1L)).extracting(EjercicioResponse::id).containsExactly(1L);
        assertThat(service.listarPorMateria("lenguaje")).extracting(EjercicioResponse::id).containsExactly(2L);
        assertThat(service.listarTareas()).isEmpty();
    }

    @Test
    @DisplayName("obtener con id inexistente lanza RecursoNoEncontradoException")
    void obtenerInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("obtener con id existente mapea todos los campos")
    void obtenerExistente() {
        when(repo.findById(1L)).thenReturn(Optional.of(ejercicio(1L)));

        EjercicioResponse r = service.obtener(1L);

        assertThat(r.titulo()).isEqualTo("Ejercicio 1");
        assertThat(r.respuestaCorrectaIndex()).isEqualTo(1);
        assertThat(r.opciones()).isEqualTo("[\"A\",\"B\"]");
    }

    @Test
    @DisplayName("crear con opcionales nulos aplica los valores por defecto")
    void crearConDefaults() {
        when(repo.save(any(Ejercicio.class))).thenAnswer(inv -> {
            Ejercicio e = inv.getArgument(0);
            e.setId(7L);
            return e;
        });

        EjercicioResponse r = service.crear(request(null, null, null));

        assertThat(r.id()).isEqualTo(7L);
        assertThat(r.respuestaCorrectaIndex()).isZero();
        assertThat(r.disponible()).isTrue();
        assertThat(r.esTarea()).isFalse();
    }

    @Test
    @DisplayName("actualizar reemplaza todos los campos del ejercicio")
    void actualizar() {
        Ejercicio existente = ejercicio(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        EjercicioResponse r = service.actualizar(1L, request(1, false, true));

        assertThat(r.materiaId()).isEqualTo("matematica");
        assertThat(r.unidadId()).isEqualTo(2L);
        assertThat(r.disponible()).isFalse();
        assertThat(r.esTarea()).isTrue();
        assertThat(r.codigoGuia()).isEqualTo("2002");
    }

    @Test
    @DisplayName("eliminar borra el ejercicio existente")
    void eliminar() {
        Ejercicio existente = ejercicio(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        service.eliminar(1L);

        verify(repo).delete(existente);
    }
}
