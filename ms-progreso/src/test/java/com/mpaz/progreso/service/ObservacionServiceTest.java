package com.mpaz.progreso.service;

import com.mpaz.progreso.dto.ObservacionRequest;
import com.mpaz.progreso.dto.ObservacionResponse;
import com.mpaz.progreso.exception.RecursoNoEncontradoException;
import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.repository.ObservacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservacionServiceTest {

    private static final long AHORA = 1_700_000_000_000L;

    @Mock ObservacionRepository repo;
    ObservacionService service;

    @BeforeEach
    void setUp() {
        service = new ObservacionService(repo, Clock.fixed(Instant.ofEpochMilli(AHORA), ZoneOffset.UTC));
    }

    private static Observacion obs(Long id) {
        return Observacion.builder().id(id).docenteId(2L).estudianteId(1L).contenido("Buen avance").fecha(1L).build();
    }

    @Test
    @DisplayName("listar, listarPorEstudiante y listarPorDocente devuelven DTOs")
    void listar() {
        when(repo.findAll()).thenReturn(List.of(obs(1L), obs(2L)));
        when(repo.findByEstudianteId(1L)).thenReturn(List.of(obs(1L)));
        when(repo.findByDocenteId(2L)).thenReturn(List.of());

        assertThat(service.listar()).hasSize(2);
        assertThat(service.listarPorEstudiante(1L)).extracting(ObservacionResponse::contenido).containsExactly("Buen avance");
        assertThat(service.listarPorDocente(2L)).isEmpty();
    }

    @Test
    @DisplayName("crear asigna la fecha actual")
    void crear() {
        when(repo.save(any(Observacion.class))).thenAnswer(inv -> {
            Observacion o = inv.getArgument(0);
            o.setId(3L);
            return o;
        });

        ObservacionResponse r = service.crear(new ObservacionRequest(2L, 1L, "Lee con fluidez"));

        assertThat(r.id()).isEqualTo(3L);
        assertThat(r.fecha()).isEqualTo(AHORA);
        assertThat(r.docenteId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("actualizar solo cambia el contenido y conserva autor y fecha")
    void actualizar() {
        Observacion existente = obs(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        ObservacionResponse r = service.actualizar(1L, new ObservacionRequest(99L, 99L, "Nuevo texto"));

        assertThat(r.contenido()).isEqualTo("Nuevo texto");
        assertThat(r.docenteId()).isEqualTo(2L);
        assertThat(r.fecha()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtener y eliminar operan sobre la observacion existente")
    void obtenerYEliminar() {
        Observacion existente = obs(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        assertThat(service.obtener(1L).id()).isEqualTo(1L);
        service.eliminar(1L);
        verify(repo).delete(existente);
    }

    @Test
    @DisplayName("obtener con id inexistente lanza RecursoNoEncontradoException")
    void inexistente() {
        when(repo.findById(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(8L)).isInstanceOf(RecursoNoEncontradoException.class);
    }
}
