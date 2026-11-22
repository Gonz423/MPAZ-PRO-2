package com.mpaz.progreso.config;

import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.model.Resultado;
import com.mpaz.progreso.repository.ObservacionRepository;
import com.mpaz.progreso.repository.ResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ResultadoRepository resultadoRepo;
    private final ObservacionRepository observacionRepo;

    @Override
    public void run(String... args) {
        if (resultadoRepo.count() > 0) return;

        // Resultados iniciales del estudiante 1
        resultadoRepo.save(Resultado.builder()
                .estudianteId(1L)
                .ejercicioId(101L)
                .materiaId("lenguaje")
                .nota(10)
                .aciertos(1)
                .totalPreguntas(1)
                .intentos(1)
                .fechaRealizado(System.currentTimeMillis())
                .build());

        resultadoRepo.save(Resultado.builder()
                .estudianteId(1L)
                .ejercicioId(201L)
                .materiaId("matematica")
                .nota(8)
                .aciertos(1)
                .totalPreguntas(1)
                .intentos(1)
                .fechaRealizado(System.currentTimeMillis())
                .build());

        // Observaciones iniciales del docente
        observacionRepo.save(Observacion.builder()
                .docenteId(1L)
                .estudianteId(1L)
                .contenido("Martina demuestra excelente comprensión en lecturas guiadas y fábulas.")
                .fecha(System.currentTimeMillis())
                .build());
    }
}
