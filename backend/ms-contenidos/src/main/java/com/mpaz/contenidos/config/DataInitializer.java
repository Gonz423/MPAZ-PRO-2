package com.mpaz.contenidos.config;

import com.mpaz.contenidos.model.Ejercicio;
import com.mpaz.contenidos.model.Unidad;
import com.mpaz.contenidos.repository.EjercicioRepository;
import com.mpaz.contenidos.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UnidadRepository unidadRepo;
    private final EjercicioRepository ejercicioRepo;

    @Override
    public void run(String... args) {
        if (unidadRepo.count() > 0) return;

        // Unidades base por asignatura
        Unidad uLenguaje = unidadRepo.save(Unidad.builder()
                .materiaId("lenguaje")
                .numero(1)
                .titulo("Lectura y comprensión")
                .objetivoAprendizaje("OA 3, OA 6 · Comprensión de textos, fábulas y valores")
                .build());

        Unidad uMatematica = unidadRepo.save(Unidad.builder()
                .materiaId("matematica")
                .numero(1)
                .titulo("Números y Operaciones hasta 100")
                .objetivoAprendizaje("OA 1, OA 3 · Conteo de 2 en 2, sumas y restas con material concreto")
                .build());

        Unidad uCiencias = unidadRepo.save(Unidad.builder()
                .materiaId("ciencias")
                .numero(1)
                .titulo("Cuerpo Humano y Hábitats")
                .objetivoAprendizaje("OA 1, OA 3 · Función de órganos vitales y animales nativos")
                .build());

        Unidad uHistoria = unidadRepo.save(Unidad.builder()
                .materiaId("historia")
                .numero(1)
                .titulo("Mi Entorno y Pueblos Originarios")
                .objetivoAprendizaje("OA 1, OA 3 · Lectura de planos y tradiciones de Chile")
                .build());

        // Ejercicios Lenguaje
        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("lenguaje")
                .unidadId(uLenguaje.getId())
                .titulo("Lectura Guiada: El Zorro y la Cigüeña")
                .objetivoAprendizaje("OA 3: Demostrar comprensión extrayendo información e identificando valores.")
                .codigoGuia("1001")
                .pregunta("¿Qué valor importante aprendemos al compartir con nuestros amigos?")
                .opciones("[\"Compartir y jugar juntos con respeto\",\"Quedarse solo sin hablar con nadie\",\"Pelear y discutir con los compañeros\"]")
                .respuestaCorrectaIndex(0)
                .disponible(true)
                .esTarea(false)
                .build());

        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("lenguaje")
                .unidadId(uLenguaje.getId())
                .titulo("Juego de Rimas y Sonidos de Palabras")
                .objetivoAprendizaje("OA 6: Reconocer rimas, aliteraciones y juegos sonoros de la lengua oral.")
                .codigoGuia("1002")
                .pregunta("¿Qué palabra rima con la palabra 'CANCION'?")
                .opciones("[\"Mesa\",\"Corazón\",\"Árbol\"]")
                .respuestaCorrectaIndex(1)
                .disponible(true)
                .esTarea(true)
                .build());

        // Ejercicios Matemática
        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("matematica")
                .unidadId(uMatematica.getId())
                .titulo("Conteo Divertido de 2 en 2 hasta 20")
                .objetivoAprendizaje("OA 1: Contar números de 2 en 2.")
                .codigoGuia("2001")
                .pregunta("Si contamos de 2 en 2: 2, 4, 6... ¿cuál número viene a continuación?")
                .opciones("[\"7\",\"8\",\"9\"]")
                .respuestaCorrectaIndex(1)
                .disponible(true)
                .esTarea(false)
                .build());

        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("matematica")
                .unidadId(uMatematica.getId())
                .titulo("Sumas con Manzanas y Frutas")
                .objetivoAprendizaje("OA 3: Demostrar adición con material concreto.")
                .codigoGuia("2002")
                .pregunta("En un canasto hay 3 manzanas y agregamos 4 manzanas más, ¿cuántas hay en total?")
                .opciones("[\"6 manzanas\",\"7 manzanas\",\"8 manzanas\"]")
                .respuestaCorrectaIndex(1)
                .disponible(true)
                .esTarea(false)
                .build());

        // Ejercicios Ciencias
        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("ciencias")
                .unidadId(uCiencias.getId())
                .titulo("El Corazón y los Pulmones")
                .objetivoAprendizaje("OA 1: Ubicación y función de órganos vitales.")
                .codigoGuia("3001")
                .pregunta("¿Cuál es la función principal de nuestro corazón en el cuerpo humano?")
                .opciones("[\"Bombear la sangre a todo nuestro cuerpo\",\"Ayudarnos a respirar el aire fresco\",\"Sostener la cabeza erguida\"]")
                .respuestaCorrectaIndex(0)
                .disponible(true)
                .esTarea(false)
                .build());

        // Ejercicios Historia
        ejercicioRepo.save(Ejercicio.builder()
                .materiaId("historia")
                .unidadId(uHistoria.getId())
                .titulo("Mi Escuela y mi Barrio en el Plano")
                .objetivoAprendizaje("OA 1: Lectura de planos con simbología básica.")
                .codigoGuia("4001")
                .pregunta("¿Para qué sirve un plano en nuestra escuela Marcela Paz?")
                .opciones("[\"Para ubicarnos y saber dónde están las salas y patios\",\"Para ver películas los fines de semana\",\"Para comprar útiles escolares\"]")
                .respuestaCorrectaIndex(0)
                .disponible(true)
                .esTarea(false)
                .build());
    }
}
