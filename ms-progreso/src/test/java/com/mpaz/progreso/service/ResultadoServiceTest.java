package com.mpaz.progreso.service;

import com.mpaz.progreso.dto.ResultadoRequest;
import com.mpaz.progreso.dto.ResultadoResponse;
import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.exception.RecursoNoEncontradoException;
import com.mpaz.progreso.model.Resultado;
import com.mpaz.progreso.repository.ResultadoRepository;
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
class ResultadoServiceTest {

    private static final long AHORA = 1_700_000_000_000L;

    @Mock ResultadoRepository repo;
    ResultadoService service;

    @BeforeEach
    void setUp() {
        service = new ResultadoService(repo, Clock.fixed(Instant.ofEpochMilli(AHORA), ZoneOffset.UTC));
    }

    private static Resultado resultado(Long id, int nota, String materia) {
        return Resultado.builder().id(id).estudianteId(1L).ejercicioId(100L + id).nota(nota)
                .aciertos(1).totalPreguntas(1).intentos(1).materiaId(materia).fechaRealizado(1L).build();
    }

    private static ResultadoRequest request(Integer aciertos, Integer total) {
        return new ResultadoRequest(1L, 101L, 9, aciertos, total, "lenguaje");
    }

    @Test
    @DisplayName("registrar un ejercicio nuevo crea el resultado con 1 intento y la fecha actual")
    void registrarNuevo() {
        when(repo.findByEstudianteIdAndEjercicioId(1L, 101L)).thenReturn(Optional.empty());
        when(repo.save(any(Resultado.class))).thenAnswer(inv -> {
            Resultado r = inv.getArgument(0);
            r.setId(50L);
            return r;
        });

        ResultadoResponse r = service.registrar(request(null, null));

        assertThat(r.id()).isEqualTo(50L);
        assertThat(r.intentos()).isEqualTo(1);
        assertThat(r.fechaRealizado()).isEqualTo(AHORA);
        assertThat(r.aciertos()).isZero();
        assertThat(r.totalPreguntas()).isEqualTo(1);
    }

    @Test
    @DisplayName("registrar un ejercicio ya realizado actualiza el mismo registro y suma un intento")
    void registrarExistente() {
        Resultado existente = resultado(1L, 5, "lenguaje");
        existente.setIntentos(2);
        when(repo.findByEstudianteIdAndEjercicioId(1L, 101L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        ResultadoResponse r = service.registrar(request(3, 4));

        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.intentos()).isEqualTo(3);
        assertThat(r.nota()).isEqualTo(9);
        assertThat(r.aciertos()).isEqualTo(3);
        assertThat(r.totalPreguntas()).isEqualTo(4);
    }

    @Test
    @DisplayName("listar y listarPorEstudiante devuelven DTOs")
    void listar() {
        when(repo.findAll()).thenReturn(List.of(resultado(1L, 10, "lenguaje"), resultado(2L, 8, "matematica")));
        when(repo.findByEstudianteId(1L)).thenReturn(List.of(resultado(1L, 10, "lenguaje")));

        assertThat(service.listar()).hasSize(2);
        assertThat(service.listarPorEstudiante(1L)).extracting(ResultadoResponse::nota).containsExactly(10);
    }

    @Test
    @DisplayName("obtener, actualizar y eliminar con id inexistente lanzan RecursoNoEncontradoException")
    void inexistente() {
        when(repo.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(9L)).isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> service.actualizar(9L, request(1, 1))).isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> service.eliminar(9L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(repo, never()).delete(any());
    }

    @Test
    @DisplayName("obtener, actualizar y eliminar con id existente operan sobre el registro")
    void existente() {
        Resultado r = resultado(1L, 5, "lenguaje");
        when(repo.findById(1L)).thenReturn(Optional.of(r));
        when(repo.save(r)).thenReturn(r);

        assertThat(service.obtener(1L).nota()).isEqualTo(5);
        assertThat(service.actualizar(1L, request(1, 1)).nota()).isEqualTo(9);
        service.eliminar(1L);
        verify(repo).delete(r);
    }

    @Test
    @DisplayName("resumenPorMateria calcula promedio y porcentaje de avance")
    void resumen() {
        when(repo.findByEstudianteIdAndMateriaId(1L, "lenguaje"))
                .thenReturn(List.of(resultado(1L, 10, "lenguaje"), resultado(2L, 6, "lenguaje")));

        ResumenProgresoDto dto = service.resumenPorMateria(1L, "lenguaje");

        assertThat(dto.promedioNota()).isEqualTo(8.0);
        assertThat(dto.ejerciciosRealizados()).isEqualTo(2);
        assertThat(dto.porcentajeCompletado()).isEqualTo(20);
    }

    @Test
    @DisplayName("resumenPorMateria sin resultados devuelve promedio 0 y porcentaje tope 100")
    void resumenLimites() {
        when(repo.findByEstudianteIdAndMateriaId(1L, "historia")).thenReturn(List.of());
        assertThat(service.resumenPorMateria(1L, "historia").promedioNota()).isZero();

        List<Resultado> muchos = java.util.stream.LongStream.rangeClosed(1, 12)
                .mapToObj(i -> resultado(i, 7, "ciencias")).toList();
        when(repo.findByEstudianteIdAndMateriaId(1L, "ciencias")).thenReturn(muchos);
        assertThat(service.resumenPorMateria(1L, "ciencias").porcentajeCompletado()).isEqualTo(100);
    }
}
